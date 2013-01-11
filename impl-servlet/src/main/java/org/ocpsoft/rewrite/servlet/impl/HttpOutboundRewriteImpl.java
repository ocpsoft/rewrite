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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;
import org.ocpsoft.urlbuilder.Address;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class HttpOutboundRewriteImpl extends BaseHttpRewrite implements HttpOutboundServletRewrite
{
   private Address address;
   private final Address originalAddress;

   public HttpOutboundRewriteImpl(final HttpServletRequest request,
            final HttpServletResponse response, ServletContext servletContext,
            final Address address)
   {
      super(request, response, servletContext);
      this.address = address;
      this.originalAddress = address;
   }

   @Override
   public Address getOutboundAddress()
   {
      return address;
   }

   @Override
   public void setOutboundAddress(final Address address)
   {
      this.address = address;
   }

   @Override
   public String toString()
   {
      return "OutboundRewrite [flow=" + flow + ", outboundURL=" + getOutboundAddress() + ", dispatchResource="
               + dispatchResource + "]";
   }

   @Override
   public Address getAddress()
   {
      return getOutboundAddress();
   }

   @Override
   public Address getOriginalOutboundAddress()
   {
      return originalAddress;
   }
}
