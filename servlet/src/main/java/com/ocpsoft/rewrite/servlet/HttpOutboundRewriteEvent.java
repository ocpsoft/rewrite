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
package com.ocpsoft.rewrite.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ocpsoft.rewrite.event.MutableOutboundRewriteEvent;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class HttpOutboundRewriteEvent implements MutableOutboundRewriteEvent<HttpServletRequest, HttpServletResponse>
{

   private String url;
   private final HttpServletResponse response;
   private final HttpServletRequest request;

   public HttpOutboundRewriteEvent(final HttpServletRequest request, final HttpServletResponse response,
            final String url)
   {
      super();
      this.url = url;
      this.response = response;
      this.request = request;
   }

   @Override
   public HttpServletRequest getRequest()
   {
      return request;
   }

   @Override
   public HttpServletResponse getResponse()
   {
      return response;
   }

   @Override
   public String getURL()
   {
      return url;
   }

   @Override
   public void setURL(final String url)
   {
      this.url = url;
   }
}
