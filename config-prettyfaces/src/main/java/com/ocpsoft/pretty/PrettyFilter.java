/*
 * Copyright 2010 Lincoln Baxter, III
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ocpsoft.pretty;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ocpsoft.pretty.faces.config.mapping.UrlMapping;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ocpsoft.pretty.faces.config.PrettyConfig;
import com.ocpsoft.pretty.faces.config.PrettyConfigurator;
import com.ocpsoft.pretty.faces.config.mapping.PathParameter;
import com.ocpsoft.pretty.faces.config.reload.PrettyConfigReloader;
import com.ocpsoft.pretty.faces.config.rewrite.Redirect;
import com.ocpsoft.pretty.faces.config.rewrite.RewriteRule;
import com.ocpsoft.pretty.faces.rewrite.RewriteEngine;
import com.ocpsoft.pretty.faces.servlet.PrettyFacesWrappedRequest;
import com.ocpsoft.pretty.faces.servlet.PrettyFacesWrappedResponse;
import com.ocpsoft.pretty.faces.url.QueryString;
import com.ocpsoft.pretty.faces.url.URL;
import com.ocpsoft.pretty.faces.util.StringUtils;

/**
 * @author Lincoln Baxter, III <lincoln@ocpsoft.com>
 */
public class PrettyFilter implements Filter
{
   private static final Log log = LogFactory.getLog(PrettyFilter.class);

   private static final String REWRITE_OCCURRED_KEY = "com.ocpsoft.pretty.rewrite";

   private static final String URL_MAPPING_FORWARD_KEY = "com.ocpsoft.pretty.url_mapping_forward";

   private ServletContext servletContext;

   private PrettyConfigReloader reloader = new PrettyConfigReloader();

   /**
    * Determine if the current request is mapped using PrettyFaces. If it is, process the pattern, storing parameters
    * into the request map, then forward the request to the specified viewId.
    */
   public void doFilter(final ServletRequest req, final ServletResponse resp, final FilterChain chain)
            throws IOException, ServletException
   {
      HttpServletRequest request = (HttpServletRequest) req;

      // let PrettyConfigReloader reload the configuration if required
      if(!PrettyContext.isInstantiated(request)) {
         reloader.onNewRequest(servletContext);
      }

      HttpServletResponse response = new PrettyFacesWrappedResponse(request.getContextPath(), request,
               (HttpServletResponse) resp, getConfig());
      req.setAttribute(PrettyContext.CONFIG_KEY, getConfig());

      PrettyContext context = PrettyContext.newDetachedInstance(request);

      rewrite(request, response);

      if (resp.isCommitted())
      {
         log.trace("Rewrite occurred, reponse is committed - ending request.");
      }
      else
      {
         URL url = context.getRequestURL();

         /*
          * This code will be executed even for the requests already forwarded by PrettyFaces.
          * As some patterns may also match viewIds to which PrettyFaces forwards, we lookup
          * the matching URL mapping only if we are not already in a forward.
          * (Fix for #128)
          */
         UrlMapping mapping = null;
         if (!isUrlMappingForward(req))
         {
            mapping = getConfig().getMappingForUrl(url);
         }

         if (mapping != null)
         {
            context.setCurrentMapping(mapping);
            PrettyContext.setCurrentContext(request, context); // set

            String viewId = context.getCurrentViewId();
            if (!response.isCommitted())
            {
               if (context.shouldProcessDynaview())
               {
                  log.trace("Forwarding mapped request [" + url.toURL() + "] to dynaviewId [" + viewId + "]");
                  setUrlMappingForward(request);
                  req.getRequestDispatcher(context.getDynaViewId()).forward(req, response);
               }
               else
               {
                  List<PathParameter> params = context.getCurrentMapping().getPatternParser().parse(url);
                  QueryString query = QueryString.build(params);

                  ServletRequest wrappedRequest = new PrettyFacesWrappedRequest(request,
                              query.getParameterMap());

                  log.trace("Sending mapped request [" + url.toURL() + "] to resource [" + viewId + "]");
                  if (url.toURL().equals(viewId))
                  {
                     chain.doFilter(wrappedRequest, response);
                  }
                  else
                  {
                     setUrlMappingForward(request);
                     req.getRequestDispatcher(viewId).forward(wrappedRequest, response);
                  }
               }
            }
         }
         else
         {
            log.trace("Request is not mapped using PrettyFaces. Continue.");
            chain.doFilter(req, response);
         }
      }
   }

   /**
    * Apply the given list of {@link RewriteRule}s to the URL (in order,) perform a redirect/forward if required.
    * Canonicalization is only invoked if it has not previously been invoked on this request. This method operates on
    * the requestUri, excluding contextPath.
    * 
    * @return True if forward/redirect occurred, false if not.
    */
   private void rewrite(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException
   {
      /*
       * FIXME Refactor this horrible method.
       */
      if (!rewriteOccurred(req) && !isUrlMappingForward(req))
      {
         RewriteEngine rewriteEngine = new RewriteEngine();
         
         /*
          * Get the URL from a new detached instance of PrettyContext
          * so that we get the forwarded URL instead of the original one
          * if the filter is called for a forwarded request.
          */
         URL url = PrettyContext.newDetachedInstance(req).getRequestURL();

         String queryString = req.getQueryString();
         if ((queryString != null) && !"".equals(queryString))
         {
            queryString = "?" + queryString;
         }
         else if (queryString == null)
         {
            queryString = "";
         }

         // TODO test this now that query string is included in rewrites
         String originalUrl = url.toURL() + queryString;
         String newUrl = originalUrl;
         for (RewriteRule rule : getConfig().getGlobalRewriteRules())
         {
            if (rule.matches(newUrl))
            {
               newUrl = rewriteEngine.processInbound(req, resp, rule, newUrl);
               if (!Redirect.CHAIN.equals(rule.getRedirect()))
               {

                  /*
                   * An HTTP redirect has been triggered; issue one if we have a URL or if the current URL has been
                   * modified.
                   */

                  String redirectURL = null;

                  /*
                   * The rewrite changed the URL and no 'url' attribute has been set for the rule.
                   */
                  if (StringUtils.isBlank(rule.getUrl()) && !originalUrl.equals(newUrl))
                  {

                     /*
                      * Add context path and encode request using encodeRedirectURL().
                      */
                     redirectURL = resp.encodeRedirectURL(req.getContextPath() + newUrl);
                  }
                  else if (StringUtils.isNotBlank(rule.getUrl()))
                  {

                     /*
                      * This is a custom location - don't call encodeRedirectURL() and don't add context path, just
                      * redirect to the encoded URL
                      */
                     redirectURL = newUrl.trim();

                  }

                  // we have to send a redirect
                  if (redirectURL != null)
                  {

                     // try to encode the redirect target
                     String encodedRedirectURL = encodeUrlWithQueryString(redirectURL);

                     // send redirect
                     resp.setHeader("Location", encodedRedirectURL);
                     resp.setStatus(rule.getRedirect().getStatus());
                     resp.flushBuffer();
                     break;

                  }

               }
            }
         }

         if (!originalUrl.equals(newUrl) && !resp.isCommitted())
         {
            /*
             * The URL was modified, but no redirect occurred; forward instead.
             */
            setRewriteOccurred(req); // make sure we don't get here twice
            req.getRequestDispatcher(newUrl).forward(req, resp);
         }

      }
   }

   /**
    * Helper method that encodes an URL including a query string. If encoding is not possible due to errors related to
    * the input URL, the method will return the unencoded input URL.
    * 
    * @param url URL to encode (may contain a query string)
    * @return encoded representation of the URL
    */
   private String encodeUrlWithQueryString(final String url)
   {

      // we must take care of URISyntax exceptions
      try
      {

         // split the input into the base URL and the query string.
         String[] urlParts = url.split("\\?", 2);

         // use the URI class to encode the base part of the url
         String baseUrlEncoded = new URI(urlParts[0]).toASCIIString();

         // is there a query string
         if ((urlParts.length > 1) && StringUtils.isNotBlank(urlParts[1]))
         {
            // return encoded base URL + the encoded query string
            return baseUrlEncoded + QueryString.build(urlParts[1]).toQueryString();
         }
         else
         {
            // no query string -> just return the encoded URL
            return baseUrlEncoded;
         }

      }
      catch (URISyntaxException e)
      {
         // warn and return input URL
         log.warn("Failed to encode URL '" + url + "': " + e.getMessage());
         return url;

      }

   }

   private void setRewriteOccurred(final ServletRequest req)
   {
      req.setAttribute(REWRITE_OCCURRED_KEY, true);
   }

   private boolean rewriteOccurred(final ServletRequest req)
   {
      return Boolean.TRUE.equals(req.getAttribute(REWRITE_OCCURRED_KEY));
   }

   private void setUrlMappingForward(final ServletRequest req)
   {
      req.setAttribute(URL_MAPPING_FORWARD_KEY, true);
   }

   private boolean isUrlMappingForward(final ServletRequest req)
   {
      return Boolean.TRUE.equals(req.getAttribute(URL_MAPPING_FORWARD_KEY));
   }

   public PrettyConfig getConfig()
   {
      if ((servletContext == null) || (servletContext.getAttribute(PrettyContext.CONFIG_KEY) == null))
      {
         log.warn("PrettyFilter is not registered in web.xml, but is registered with JSF "
                  + "Navigation and Action handlers -- this could cause unpredictable behavior.");
         return new PrettyConfig();
      }
      return (PrettyConfig) servletContext.getAttribute(PrettyContext.CONFIG_KEY);
   }

   /**
    * Load and cache configurations
    */
   public void init(final FilterConfig filterConfig) throws ServletException
   {
      log.info("PrettyFilter starting up...");
      servletContext = filterConfig.getServletContext();

      PrettyConfigurator configurator = new PrettyConfigurator(servletContext);
      configurator.configure();

      log.info("PrettyFilter initialized.");
   }

   public void destroy()
   {
      log.info("PrettyFilter shutting down...");
   }
}