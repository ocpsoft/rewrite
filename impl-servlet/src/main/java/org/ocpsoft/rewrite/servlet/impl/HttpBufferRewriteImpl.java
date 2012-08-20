package org.ocpsoft.rewrite.servlet.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ocpsoft.rewrite.servlet.event.BaseRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

public class HttpBufferRewriteImpl extends BaseRewrite<HttpServletRequest, HttpServletResponse> implements HttpServletRewrite
{
   private HttpInboundRewriteImpl delegate;

   public HttpBufferRewriteImpl(HttpServletRequest request, HttpServletResponse response)
   {
      super(request, response);
      this.delegate = new HttpInboundRewriteImpl(request, response);
   }

   @Override
   public String getContextPath()
   {
      return delegate.getContextPath();
   }

   @Override
   public String getRequestPath()
   {
      return delegate.getRequestPath();
   }

   @Override
   public String getRequestQueryString()
   {
      return delegate.getRequestQueryString();
   }

   @Override
   public String getRequestQueryStringSeparator()
   {
      return delegate.getRequestQueryStringSeparator();
   }
   
   @Override
   public String getURL()
   {
      return delegate.getURL();
   }
}
