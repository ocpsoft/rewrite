package org.ocpsoft.rewrite.servlet.impl;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.urlbuilder.Address;

public class HttpBufferRewriteImpl extends BaseHttpRewrite implements HttpServletRewrite
{
   private HttpInboundRewriteImpl delegate;

   public HttpBufferRewriteImpl(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext)
   {
      super(request, response, servletContext);
      this.delegate = new HttpInboundRewriteImpl(request, response, servletContext);
   }

   @Override
   public String getContextPath()
   {
      return delegate.getContextPath();
   }

   @Override
   public Address getAddress()
   {
      return delegate.getAddress();
   }

}
