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
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ocpsoft.common.services.NonEnriching;

import org.ocpsoft.rewrite.servlet.event.OutboundServletRewrite;
import org.ocpsoft.rewrite.servlet.spi.OutboundRewriteProducer;
import org.ocpsoft.urlbuilder.Address;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class HttpOutboundRewriteProducer implements
         OutboundRewriteProducer<HttpServletRequest, HttpServletResponse, Address>,
         NonEnriching
{
   @Override
   public int priority()
   {
      return 0;
   }

   @Override
   public boolean handles(Address address)
   {
      return address instanceof Address;
   }

   @Override
   public OutboundServletRewrite<HttpServletRequest, HttpServletResponse, Address> createOutboundRewrite(
            final ServletRequest request, final ServletResponse response, ServletContext servletContext, Address payload)
   {
      return new HttpOutboundRewriteImpl((HttpServletRequest) request, (HttpServletResponse) response, servletContext,
               payload);
   }

}
