/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.ocpsoft.rewrite.prettyfaces;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.urlbuilder.Address;
import org.ocpsoft.urlbuilder.AddressBuilder;
import org.ocpsoft.urlbuilder.util.Encoder;

import com.ocpsoft.pretty.faces.config.rewrite.Redirect;
import com.ocpsoft.pretty.faces.config.rewrite.RewriteRule;
import com.ocpsoft.pretty.faces.rewrite.RewriteEngine;
import com.ocpsoft.pretty.faces.url.QueryString;
import com.ocpsoft.pretty.faces.url.URL;
import com.ocpsoft.pretty.faces.util.StringUtils;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public class InboundRewriteRuleAdaptor implements Rule
{
   private final RewriteRule rule;

   public InboundRewriteRuleAdaptor(final RewriteRule rule)
   {
      this.rule = rule;
   }

   @Override
   public String getId()
   {
      return toString();
   }

   @Override
   public boolean evaluate(final Rewrite event, final EvaluationContext context)
   {
      if (event instanceof HttpInboundServletRewrite && rule.isInbound() && PFUtil.isRewritingEnabled(event))
      {
         HttpServletRewrite httpRewrite = (HttpServletRewrite) event;
         String path = httpRewrite.getInboundAddress().getPath();

         String url = URL.build(path).decode().toURL()
                  + QueryString.build(httpRewrite.getInboundAddress().getQuery()).toQueryString();

         String contextPath = httpRewrite.getContextPath();
         if (!contextPath.equals("/") && url.startsWith(contextPath))
            url = url.substring(contextPath.length());

         if (rule.matches(url))
         {
            return true;
         }
      }
      return false;
   }

   @Override
   public void perform(final Rewrite event, final EvaluationContext context)
   {
      RewriteEngine engine = new RewriteEngine();
      HttpServletRewrite httpRewrite = (HttpServletRewrite) event;
      String originalUrl = httpRewrite.getInboundAddress().getPath();

      originalUrl = URL.build(originalUrl).decode().toURL()
               + QueryString.build(httpRewrite.getInboundAddress().getQuery()).toQueryString();

      String contextPath = ((HttpServletRewrite) event).getContextPath();
      originalUrl = StringUtils.removePathPrefix(contextPath, originalUrl);

      String newUrl = engine.processInbound(((HttpServletRewrite) event).getRequest(),
               ((HttpServletRewrite) event).getResponse(), rule, originalUrl);

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
             * Add context path.
             */
            redirectURL = contextPath + newUrl;
         }
         else if (StringUtils.isNotBlank(rule.getUrl()))
         {
            /*
             * This is a custom location - don't add context path, just
             * redirect to the encoded URL
             */
            redirectURL = newUrl.trim();
         }


         if (redirectURL != null)
         {
            if (redirectURL.contains(" ")) {
                String[] parts = redirectURL.split(Pattern.quote("?"), 2);
                String[] encodedParts = new String[parts.length];
                encodedParts[0] = Encoder.path(parts[0]);

                if (parts.length > 1) {
                    encodedParts[1] = Encoder.query(parts[1]);

                    StringBuilder joiner = new StringBuilder(encodedParts[0]);
                    joiner.append("?").append(encodedParts[1]);

                    redirectURL = joiner.toString();
                }
                else
                {
                    redirectURL = encodedParts[0];
                }
            }

            try
            {
                URI uri = new URI(redirectURL);
                if (uri.getScheme() == null && uri.getHost() != null) {
                    String path = uri.getPath();
                    if (StringUtils.hasLeadingSlash(path))
                    {
                        path = path.substring(1);
                    }

                    Address encodedRedirectAddress
                        = AddressBuilder.begin()
                                        .scheme(uri.getScheme())
                                        .domain(uri.getHost())
                                        .port(uri.getPort())
                                        .path("/{p}")
                                        .setEncoded("p", path)
                                        .queryLiteral(QueryString.build(uri.getQuery()).toQueryString())
                                        .anchor(uri.getRawFragment())
                                        .buildLiteral();
                    redirectURL = encodedRedirectAddress.toString();
                }
            }
            catch (URISyntaxException e)
            {
                throw new IllegalArgumentException(
                        "[" + redirectURL + "] is not a valid URL fragment. Consider encoding relevant portions of the URL with ["
                                 + Encoder.class
                                 + "], or use the provided builder pattern to specify part encoding.", e);
            }

            if (Redirect.PERMANENT.equals(rule.getRedirect()))
               ((HttpInboundServletRewrite) event).redirectPermanent(redirectURL);
            if (Redirect.TEMPORARY.equals(rule.getRedirect()))
               ((HttpInboundServletRewrite) event).redirectTemporary(redirectURL);
         }
      }
      else if (!originalUrl.equals(newUrl))
      {
         PFUtil.setRewriteOccurred(event);
         ((HttpInboundServletRewrite) event).forward(newUrl);
      }
   }

   @Override
   public String toString()
   {
      return "InboundRewriteRuleAdaptor [rule=" + rule + "]";
   }
}
