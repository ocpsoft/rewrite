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
package org.ocpsoft.rewrite.servlet.impl;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.exception.RewriteException;
import org.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import org.ocpsoft.urlbuilder.Address;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class HttpInboundRewriteImpl extends BaseHttpRewrite implements HttpInboundServletRewrite
{
   Logger log = Logger.getLogger(HttpInboundRewriteImpl.class);

   public HttpInboundRewriteImpl(final HttpServletRequest request, final HttpServletResponse response,
            final ServletContext servletContext)
   {
      super(request, response, servletContext);
   }

   @Override
   public void redirectTemporary(final String location)
   {
      log.debug("Temporary Redirect (302) requested: [" + location + "]");

      HttpServletResponse response = getResponse();
      if (response.isCommitted())
      {
         throw new IllegalStateException("Response is already committed. Cannot issue redirect.");
      }

      dispatchResource = encodeRedirectUrl(response, location);
      this.flow = ServletRewriteFlow.REDIRECT_TEMPORARY;
   }

   @Override
   public void redirectPermanent(final String location)
   {
      log.debug("Permanent Redirect (301) requested: [" + location + "]");

      HttpServletResponse response = getResponse();
      if (response.isCommitted())
      {
         throw new IllegalStateException("Response is already committed. Cannot issue redirect.");
      }

      dispatchResource = encodeRedirectUrl(response, location);
      this.flow = ServletRewriteFlow.REDIRECT_PERMANENT;
   }

   private String encodeRedirectUrl(final HttpServletResponse response, final String url)
   {
      return response.encodeRedirectURL(url);
   }

   @Override
   public void sendStatusCode(final int code)
   {
      HttpServletResponse response = getResponse();
      if (response.isCommitted())
      {
         throw new IllegalStateException("Response is already committed. Cannot send codes.");
      }

      try
      {
         response.setStatus(code);
         response.flushBuffer();
         abort();
      }
      catch (IOException e)
      {
         throw new RewriteException("Could not send HTTP status code.", e);
      }
   }

   @Override
   public void sendStatusCode(final int code, final String message)
   {
      try
      {
         getResponse().getWriter().write("<h1>" + message + "</h1>");
         sendStatusCode(code);
      }
      catch (IOException e)
      {
         throw new RewriteException("Could not send HTTP status code.", e);
      }
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
         throw new IllegalStateException("Response is already committed. Cannot send codes.");
      }

      try
      {
         if (message == null)
         {
            response.sendError(code);
         }
         else
         {
            response.sendError(code, message);
         }

         abort();
      }
      catch (IOException e)
      {
         throw new RewriteException("Could not send HTTP error code.", e);
      }
   }

   @Override
   public Address getAddress()
   {
      return getInboundAddress();
   }

   @Override
   public String toString()
   {
      return "InboundRewrite [" + getRequest().getMethod() + " url=" + getAddress() + ", flow=" + getFlow()
               + ", dispatchResource=" + getDispatchResource()
               + "]";
   }
}
