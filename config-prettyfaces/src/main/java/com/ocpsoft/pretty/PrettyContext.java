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
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.context.FacesContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ocpsoft.pretty.faces.config.PrettyConfig;
import com.ocpsoft.pretty.faces.config.PrettyConfigurator;
import com.ocpsoft.pretty.faces.config.mapping.UrlMapping;
import com.ocpsoft.pretty.faces.url.QueryString;
import com.ocpsoft.pretty.faces.url.URL;
import com.ocpsoft.pretty.faces.util.Assert;

/**
 * @author Lincoln Baxter, III <lincoln@ocpsoft.com>
 */
public class PrettyContext implements Serializable
{

   private static final String DEFAULT_ENCODING = "UTF-8";
   public static final String CONFIG_KEY = "com.ocpsoft.pretty.CONFIG_FILES";

   private static final Log log = LogFactory.getLog(PrettyContext.class);
   private static final long serialVersionUID = -4593906924975844541L;

   public static final String PRETTY_PREFIX = "pretty:";
   private static final String CONTEXT_REQUEST_KEY = "prettyContext";

   private static final Pattern JSESSIONID_PATTERN = Pattern.compile("(?i)^(.*);jsessionid=[\\w\\.\\-\\+]+(.*)");
   private static final String JSESSIONID_REPLACEMENT = "$1$2";

   private PrettyConfig config;

   private final String contextPath;
   private URL requestURL;
   private final QueryString requestQuery;

   private UrlMapping currentMapping;

   private boolean dynaviewProcessed = false;
   private boolean inNavigation = false;

   /**
    * Must create instance through the initialize() method
    */
   protected PrettyContext(final HttpServletRequest request)
   {
      Assert.notNull(request, "HttpServletRequest argument was null");

      // attribute is set by PrettyFacesRewriteLifecycleListener before
      config = (PrettyConfig) request.getAttribute(CONFIG_KEY);

      // not sure if this can happen any more, but we'll keep it for now
      if (config == null)
      {
         config = new PrettyConfig();
      }

      contextPath = request.getContextPath();
      String requestUrl = stripContextPath(request.getRequestURI());
      Matcher sessionIdMatcher = JSESSIONID_PATTERN.matcher(requestUrl);
      if (sessionIdMatcher.matches())
      {
         requestUrl = sessionIdMatcher.replaceFirst(JSESSIONID_REPLACEMENT);
      }

      String encoding = request.getCharacterEncoding() == null ? DEFAULT_ENCODING : request.getCharacterEncoding();

      requestURL = new URL(requestUrl);
      requestURL.setEncoding(encoding);
      requestURL = requestURL.decode();

      requestQuery = QueryString.build(request.getQueryString());

      log.trace("Initialized PrettyContext");
   }

   /**
    * Get the current PrettyFaces context object, or construct a new one if it
    * does not yet exist for this request. (Delegates to
    * {@link FacesContext#getCurrentInstance()} to retrieve the current
    * {@link HttpServletRequest} object)
    * 
    * @return current context instance
    */
   public static PrettyContext getCurrentInstance()
   {
      FacesContext context = FacesContext.getCurrentInstance();
      return getCurrentInstance(context);
   }

   /**
    * Get the current PrettyFaces context object, or construct a new one if it
    * does not yet exist for this request. (Delegates to {@link FacesContext} to
    * retrieve the current {@link HttpServletRequest} object)
    * 
    * @return current context instance
    */
   public static PrettyContext getCurrentInstance(final FacesContext context)
   {
      Assert.notNull(context, "FacesContext argument was null.");
      return getCurrentInstance((HttpServletRequest) context.getExternalContext().getRequest());
   }

   /**
    * Get the current {@link PrettyContext}, or construct a new one if it does
    * not yet exist for this request.
    * 
    * @return current context instance
    */
   public static PrettyContext getCurrentInstance(final HttpServletRequest request)
   {
      Assert.notNull(request, "HttpServletRequest argument was null");
      PrettyContext prettyContext = (PrettyContext) request.getAttribute(CONTEXT_REQUEST_KEY);
      if (prettyContext instanceof PrettyContext)
      {
         log.trace("Retrieved PrettyContext from Request");
         return prettyContext;
      }
      else
      {
         Assert.notNull(request, "HttpServletRequest argument was null");
         prettyContext = newDetachedInstance(request);
         log.trace("PrettyContext not found in Request - building new instance");
         setCurrentContext(request, prettyContext);
         return prettyContext;
      }
   }

   /**
    * Return true if there is an instantiated {@link PrettyContext} contained in
    * the current given request object.
    */
   public static boolean isInstantiated(final ServletRequest request)
   {
      Assert.notNull(request, "HttpServletRequest argument was null");
      PrettyContext prettyContext = (PrettyContext) request.getAttribute(CONTEXT_REQUEST_KEY);
      if (prettyContext instanceof PrettyContext)
      {
         return true;
      }
      else
      {
         return false;
      }
   }

   /**
    * Package private -- only {@link PrettyFilter} should be calling this method
    * -- it does not overwrite existing contexts in Request object
    */
   public static PrettyContext newDetachedInstance(final HttpServletRequest request)
   {
      Assert.notNull(request, "HttpServletRequest argument was null");

      PrettyContext prettyContext = new PrettyContext(request);
      return prettyContext;
   }

   /**
    * Package private -- only PrettyFilter or this class should be calling this
    * method -- it overwrites existing contexts in Request object
    */
   public static void setCurrentContext(final HttpServletRequest request, final PrettyContext prettyContext)
   {
      request.setAttribute(CONTEXT_REQUEST_KEY, prettyContext);
   }

   /**
    * Return the {@link URL} representing the path with which this request was
    * populated. (Does not include context-path.)
    */
   public URL getRequestURL()
   {
      return requestURL;
   }

   /**
    * Return the {@link QueryString} representing the query-string with which
    * this request was populated.
    * 
    * @return
    */
   public QueryString getRequestQueryString()
   {
      return requestQuery;
   }

   /**
    * Determine if this request URL is mapped by PrettyFaces
    */
   public boolean isPrettyRequest()
   {
      return getCurrentMapping() != null;
   }

   public String getContextPath()
   {
      return contextPath;
   }

   /**
    * If the given URL is prefixed with this request's context-path, return the
    * URI without the context path. Otherwise return the URI unchanged.
    */
   public String stripContextPath(String uri)
   {
      if (!contextPath.equals("/") && uri.startsWith(contextPath))
      {
         uri = uri.substring(contextPath.length());
      }
      return uri;
   }

   /**
    * Get the pretty-config.xml configurations as loaded by
    * {@link PrettyConfigurator} (This can be dynamically manipulated at runtime
    * in order to change or add any mappings.
    */
   public PrettyConfig getConfig()
   {
      return config;
   }

   void setConfig(final PrettyConfig config)
   {
      this.config = config;
   }

   /**
    * Get the {@link UrlMapping} representing the current request.
    */
   public UrlMapping getCurrentMapping()
   {
      if (currentMapping == null)
      {
         currentMapping = config.getMappingForUrl(requestURL);
      }
      return currentMapping;
   }

   /**
    * Called once from PrettyFilter to initialize the currentMapping property.
    * This leads to better performance because the filter already knows the mapping
    * and we won't have to do the mapping identification again in the getter for
    * the property. 
    */
   void setCurrentMapping(UrlMapping mapping)
   {
      currentMapping = mapping;
   }

   /**
    * Return the current viewId to which the current request will be forwarded
    * to JSF.
    */
   public String getCurrentViewId()
   {
      if (getCurrentMapping() != null)
      {
         return currentMapping.getViewId();
      }
      return "";
   }

   /**
    * Return whether or not this faces application is in the Navigation State
    */
   public boolean isInNavigation()
   {
      return inNavigation;
   }

   /**
    * Set whether or not to treat this request as if it is in the Navigation
    * State
    */
   public void setInNavigation(final boolean value)
   {
      inNavigation = value;
   }

   /**
    * @return True if the current mapping and request should trigger DynaView
    *         capabilities.
    */
   public boolean shouldProcessDynaview()
   {
      if (isPrettyRequest())
      {
         return getCurrentMapping().isDynaView() && !isDynaviewProcessed();
      }
      return false;
   }

   /**
    * Return true if the current request has already processed the DynaView
    * life-cycle.
    */
   public boolean isDynaviewProcessed()
   {
      return dynaviewProcessed;
   }

   public void setDynaviewProcessed(final boolean value)
   {
      dynaviewProcessed = value;
   }

   public String getDynaViewId()
   {
      return config.getDynaviewId();
   }

   /**
    * <p>
    * Sends an error response to the client using the specified HTTP status
    * code.
    * </p>
    * <p>
    * Please note that this method can only be called from within the JSF
    * lifecycle as it needs the {@link FacesContext} to obtain the 
    * {@link HttpServletResponse}. Please use
    * {@link #sendError(int, String, HttpServletResponse)} in all other
    * cases.
    * </p>
    * 
    * @param code the error status code
    * @see HttpServletResponse#sendError(int, String)
    */
   public void sendError(int code)
   {
      sendError(code, null);
   }

   /**
    * <p>
    * Sends an error response to the client using the specified HTTP status
    * code.
    * </p>
    * <p>
    * Please note that this method can only be called from within the JSF
    * lifecycle as it needs the {@link FacesContext} to obtain the 
    * {@link HttpServletResponse}. Please use
    * {@link #sendError(int, String, HttpServletResponse)} in all other
    * cases.
    * </p>
    * 
    * @param code the error status code
    * @param message the descriptive message
    * @see HttpServletResponse#sendError(int, String)
    */
   public void sendError(int code, String message)
   {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      Assert.notNull(facesContext, "FacesContext argument was null.");

      Object response = facesContext.getExternalContext().getResponse();
      if (response instanceof HttpServletResponse)
      {
         sendError(code, message, (HttpServletResponse) response);
         facesContext.responseComplete();
      }

   }

   /**
    * Sends an error response to the client using the specified HTTP status
    * code.
    * 
    * @param code the error status code
    * @param message the descriptive message
    * @param response
    * @see HttpServletResponse#sendError(int, String)
    */
   public void sendError(int code, String message, HttpServletResponse response)
   {
      Assert.notNull(response, "HttpServletResponse argument was null");
      try
      {
         if (message != null)
         {
            response.sendError(code, message);
         }
         else
         {
            response.sendError(code);
         }
      }
      catch (IOException e)
      {
         throw new IllegalStateException("Failed to send error code: " + code, e);
      }
   }

}
