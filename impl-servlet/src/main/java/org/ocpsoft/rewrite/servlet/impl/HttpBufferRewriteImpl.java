package org.ocpsoft.rewrite.servlet.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.urlbuilder.Address;

public class HttpBufferRewriteImpl extends BaseHttpRewrite implements HttpServletRewrite
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
   public Address getAddress()
   {
      return delegate.getAddress();
   }
}
