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
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.servlet.RewriteLifecycleContext;
import org.ocpsoft.rewrite.servlet.event.BaseRewrite.Flow;
import org.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;
import org.ocpsoft.rewrite.servlet.spi.RewriteLifecycleListener;
import org.ocpsoft.rewrite.spi.RewriteProvider;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class HttpRewriteWrappedResponse extends HttpServletResponseWrapper
{
   private final HttpServletRequest request;

   public HttpRewriteWrappedResponse(final HttpServletRequest request, final HttpServletResponse response)
   {
      super(response);
      this.request = request;
   }

   public HttpServletRequest getRequest()
   {
      return request;
   }

   @Override
   public String encodeRedirectUrl(final String url)
   {
      return encodeRedirectURL(url);
   }

   @Override
   public String encodeUrl(final String url)
   {
      return encodeURL(url);
   }

   @Override
   public String encodeRedirectURL(final String url)
   {
      HttpOutboundServletRewrite event = new HttpOutboundRewriteImpl(request, this, url);
      rewrite(event);

      if (event.getFlow().is(Flow.ABORT_REQUEST))
      {
         return event.getOutboundURL();
      }

      return super.encodeRedirectURL(event.getOutboundURL());
   }

   @Override
   public String encodeURL(final String url)
   {
      HttpOutboundServletRewrite event = new HttpOutboundRewriteImpl(request, this, url);
      rewrite(event);

      if (event.getFlow().is(Flow.ABORT_REQUEST))
      {
         return event.getOutboundURL();
      }

      return super.encodeURL(event.getOutboundURL());
   }

   private void rewrite(final HttpOutboundServletRewrite event)
   {
      @SuppressWarnings("unchecked")
      RewriteLifecycleContext<ServletContext> context = (RewriteLifecycleContext<ServletContext>) request
               .getAttribute(RewriteLifecycleContext.CONTEXT_KEY);
      for (RewriteLifecycleListener<Rewrite> listener : context.getRewriteLifecycleListeners())
      {
         listener.beforeOutboundRewrite(event);
      }

      for (RewriteProvider<ServletContext, Rewrite> p : context.getRewriteProviders())
      {
         if (p.handles(event))
         {
            p.rewrite(event);
            if (event.getFlow().is(Flow.HANDLED))
            {
               break;
            }
         }
      }

      for (RewriteLifecycleListener<Rewrite> listener : context.getRewriteLifecycleListeners())
      {
         listener.afterOutboundRewrite(event);
      }
   }

   @Override
   public void addCookie(Cookie cookie)
   {
      // TODO Auto-generated method stub
      super.addCookie(cookie);
   }

   @Override
   public boolean containsHeader(String name)
   {
      // TODO Auto-generated method stub
      return super.containsHeader(name);
   }

   @Override
   public void sendError(int sc, String msg) throws IOException
   {
      // TODO Auto-generated method stub
      super.sendError(sc, msg);
   }

   @Override
   public void sendError(int sc) throws IOException
   {
      // TODO Auto-generated method stub
      super.sendError(sc);
   }

   @Override
   public void sendRedirect(String location) throws IOException
   {
      // TODO Auto-generated method stub
      super.sendRedirect(location);
   }

   @Override
   public void setDateHeader(String name, long date)
   {
      // TODO Auto-generated method stub
      super.setDateHeader(name, date);
   }

   @Override
   public void addDateHeader(String name, long date)
   {
      // TODO Auto-generated method stub
      super.addDateHeader(name, date);
   }

   @Override
   public void setHeader(String name, String value)
   {
      // TODO Auto-generated method stub
      super.setHeader(name, value);
   }

   @Override
   public void addHeader(String name, String value)
   {
      // TODO Auto-generated method stub
      super.addHeader(name, value);
   }

   @Override
   public void setIntHeader(String name, int value)
   {
      // TODO Auto-generated method stub
      super.setIntHeader(name, value);
   }

   @Override
   public void addIntHeader(String name, int value)
   {
      // TODO Auto-generated method stub
      super.addIntHeader(name, value);
   }

   @Override
   public void setStatus(int sc)
   {
      // TODO Auto-generated method stub
      super.setStatus(sc);
   }

   @Override
   public void setStatus(int sc, String sm)
   {
      // TODO Auto-generated method stub
      super.setStatus(sc, sm);
   }

   @Override
   public int getStatus()
   {
      // TODO Auto-generated method stub
      return super.getStatus();
   }

   @Override
   public String getHeader(String name)
   {
      // TODO Auto-generated method stub
      return super.getHeader(name);
   }

   @Override
   public Collection<String> getHeaders(String name)
   {
      // TODO Auto-generated method stub
      return super.getHeaders(name);
   }

   @Override
   public Collection<String> getHeaderNames()
   {
      // TODO Auto-generated method stub
      return super.getHeaderNames();
   }

   @Override
   public ServletResponse getResponse()
   {
      // TODO Auto-generated method stub
      return super.getResponse();
   }

   @Override
   public void setResponse(ServletResponse response)
   {
      // TODO Auto-generated method stub
      super.setResponse(response);
   }

   @Override
   public void setCharacterEncoding(String charset)
   {
      // TODO Auto-generated method stub
      super.setCharacterEncoding(charset);
   }

   @Override
   public String getCharacterEncoding()
   {
      // TODO Auto-generated method stub
      return super.getCharacterEncoding();
   }

   @Override
   public ServletOutputStream getOutputStream() throws IOException
   {
      // TODO Auto-generated method stub
      return super.getOutputStream();
   }

   @Override
   public PrintWriter getWriter() throws IOException
   {
      // TODO Auto-generated method stub
      return super.getWriter();
   }

   @Override
   public void setContentLength(int len)
   {
      // TODO Auto-generated method stub
      super.setContentLength(len);
   }

   @Override
   public void setContentType(String type)
   {
      // TODO Auto-generated method stub
      super.setContentType(type);
   }

   @Override
   public String getContentType()
   {
      // TODO Auto-generated method stub
      return super.getContentType();
   }

   @Override
   public void setBufferSize(int size)
   {
      // TODO Auto-generated method stub
      super.setBufferSize(size);
   }

   @Override
   public int getBufferSize()
   {
      // TODO Auto-generated method stub
      return super.getBufferSize();
   }

   @Override
   public void flushBuffer() throws IOException
   {
      // TODO Auto-generated method stub
      super.flushBuffer();
   }

   @Override
   public boolean isCommitted()
   {
      // TODO Auto-generated method stub
      return super.isCommitted();
   }

   @Override
   public void reset()
   {
      // TODO Auto-generated method stub
      super.reset();
   }

   @Override
   public void resetBuffer()
   {
      // TODO Auto-generated method stub
      super.resetBuffer();
   }

   @Override
   public void setLocale(Locale loc)
   {
      // TODO Auto-generated method stub
      super.setLocale(loc);
   }

   @Override
   public Locale getLocale()
   {
      // TODO Auto-generated method stub
      return super.getLocale();
   }

   @Override
   public boolean isWrapperFor(ServletResponse wrapped)
   {
      // TODO Auto-generated method stub
      return super.isWrapperFor(wrapped);
   }

   @Override
   public boolean isWrapperFor(Class wrappedType)
   {
      // TODO Auto-generated method stub
      return super.isWrapperFor(wrappedType);
   }


}
