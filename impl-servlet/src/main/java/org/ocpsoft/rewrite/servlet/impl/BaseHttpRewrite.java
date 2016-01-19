/*
 * Copyright 2013 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ocpsoft.rewrite.event.Flow;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.servlet.event.BaseRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.urlbuilder.Address;
import org.ocpsoft.urlbuilder.AddressBuilder;
import org.ocpsoft.urlbuilder.util.Encoder;

/**
 * Base class for Http {@link Rewrite} events.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class BaseHttpRewrite extends BaseRewrite<HttpServletRequest, HttpServletResponse> implements
         HttpServletRewrite
{
   private static final Pattern JSESSIONID_PATTERN = Pattern.compile("(?i)^(.*);jsessionid=[\\w\\.\\-\\+]+(.*)");
   private static final String JSESSIONID_REPLACEMENT = "$1$2";

   /*
    * For caching and performance purposes only.
    */
   private String requestContextPath;
   private Address address;

   public BaseHttpRewrite(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext)
   {
      super(request, response, servletContext);
   }

   @Override
   public String getContextPath()
   {
      if (this.requestContextPath == null)
         this.requestContextPath = getRequest().getContextPath();
      return this.requestContextPath;
   }

   @Override
   public Address getAddress()
   {
      return getInboundAddress();
   }

   @Override
   public Address getInboundAddress()
   {
      if (this.address == null)
      {
         String requestURI = getRequest().getRequestURI();

         Matcher sessionIdMatcher = JSESSIONID_PATTERN.matcher(requestURI);
         if (sessionIdMatcher.matches())
         {
            requestURI = sessionIdMatcher.replaceFirst(JSESSIONID_REPLACEMENT);
         }

         // for forwards the requestURI isn't URL encoded as we expect it
         // see: https://github.com/ocpsoft/rewrite/issues/165
         if (getRequest().getAttribute("javax.servlet.forward.request_uri") != null) {
            requestURI = Encoder.path(requestURI);
         }

         this.address = AddressBuilder.begin()
                  .scheme(getRequest().getScheme())
                  .domain(getRequest().getServerName())
                  .port(getRequest().getServerPort())
                  .path(requestURI)
                  .queryLiteral(getRequest().getQueryString()).buildLiteral();
      }
      return this.address;
   }

   @Override
   public void setFlow(Flow flow)
   {
      this.flow = flow;
   }
}
