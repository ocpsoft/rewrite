/*
 * Copyright 2011 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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
package com.ocpsoft.rewrite.servlet.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;

import com.ocpsoft.rewrite.exception.RewriteException;
import com.ocpsoft.rewrite.servlet.event.RewriteEventBase;
import com.ocpsoft.rewrite.servlet.http.HttpInboundRewriteEvent;
import com.ocpsoft.rewrite.servlet.util.QueryString;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class HttpInboundRewriteEventImpl extends RewriteEventBase<HttpServletRequest, HttpServletResponse>
         implements HttpInboundRewriteEvent
{
   Logger log = Logger.getLogger(HttpInboundRewriteEventImpl.class);

   public HttpInboundRewriteEventImpl(final HttpServletRequest request, final HttpServletResponse response)
   {
      super(request, response);
   }

   @Override
   public void redirectTemporary(final String location)
   {
      log.debug("Temporary Redirect (302) requested: [" + location + "]");
      redirect(location, HttpServletResponse.SC_MOVED_TEMPORARILY);
   }

   @Override
   public void redirectPermanent(final String location)
   {
      log.debug("Permanent Redirect (301) requested: [" + location + "]");
      redirect(location, HttpServletResponse.SC_MOVED_PERMANENTLY);
   }

   @Override
   public void sendErrorCode(final int code)
   {
      sendErrorCode(code, null);
   }

   @Override
   public void sendErrorCode(final int code, final String message)
   {
      HttpServletResponse response = getResponse();
      if (response.isCommitted())
      {
         throw new IllegalStateException("Response is already committed. Cannot issue redirect.");
      }

      try
      {
         if (message == null)
            response.sendError(code);
         else
            response.sendError(code, message);

         abort();
      }
      catch (IOException e)
      {
         throw new RewriteException("Could not send HTTP error code.", e);
      }
   }

   private void redirect(String location, final int code)
   {
      HttpServletResponse response = getResponse();
      if (response.isCommitted())
      {
         throw new IllegalStateException("Response is already committed. Cannot issue redirect.");
      }

      try
      {
         dispatchResource = location;
         location = encodeRedirectUrl(response, location);
         response.setStatus(code);
         response.setHeader("Location", location);
         response.flushBuffer();
         abort();
      }
      catch (IOException e)
      {
         throw new RewriteException();
      }
   }

   private String encodeRedirectUrl(final HttpServletResponse response, final String url)
   {
      try
      {
         // The base URL and Query String require different encoding rules.
         String[] urlParts = url.split("\\?", 2);

         String baseUrlEncoded = new URI(urlParts[0]).toASCIIString();

         if ((urlParts.length > 1) && (urlParts[1] != null) && !"".equals(urlParts[1]))
         {
            return response.encodeRedirectURL(baseUrlEncoded + QueryString.build(urlParts[1]).toQueryString());
         }
         else
         {
            return response.encodeRedirectURL(baseUrlEncoded);
         }
      }
      catch (URISyntaxException e)
      {
         log.warn("Failed to encode URL '" + url + "': " + e.getMessage());
         return response.encodeRedirectURL(url);
      }
   }

   @Override
   public String getContextPath()
   {
      return getRequest().getContextPath();
   }

   @Override
   public String getRequestURL()
   {
      return getRequest().getRequestURI().substring(getContextPath().length());
   }

   @Override
   public String getRequestQueryString()
   {
      return getRequest().getQueryString() == null ? "" : getRequest().getQueryString();
   }
}
