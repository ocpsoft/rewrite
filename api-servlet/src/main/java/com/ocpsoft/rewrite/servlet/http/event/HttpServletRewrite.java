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
package com.ocpsoft.rewrite.servlet.http.event;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ocpsoft.rewrite.servlet.event.ServletRewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface HttpServletRewrite extends
         ServletRewrite<HttpServletRequest, HttpServletResponse>
{
   /**
    * Return the application context root {@link HttpServletRequest#getContextPath()}
    */
   public String getContextPath();

   /**
    * Portion of the request URL representing request path within the application. The context path is not included, and
    * should be retrieved using {@link #getContextPath()}
    */
   public String getRequestURL();

   /**
    * Portion of the request URL representing the query string.
    */
   public String getRequestQueryString();

   /**
    * Portion of the request URL joining the {@link #getRequestURL()} and {@link #getRequestQueryString()}. If
    * {@link #getRequestQueryString()} is valued, this will return "?"; otherwise, if {@link #getRequestQueryString()}
    * is empty, this too will return an empty string.
    */
   String getRequestQueryStringSeparator();

   /**
    * Return the full URL including query string. Note that for {@link HttpInboundServletRewrite} events, this method
    * returns {@link #getRequestURL()} + {@link #getRequestQueryStringSeparator()} + {@link #getRequestQueryString()};
    * for {@link HttpOutboundServletRewrite} events, this method returns
    * {@link HttpOutboundServletRewrite#getOutboundURL()}
    */
   public String getURL();
}
