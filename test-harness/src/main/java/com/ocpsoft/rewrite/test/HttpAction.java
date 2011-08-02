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
package com.ocpsoft.rewrite.test;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class HttpAction<T extends HttpRequest>
{
   private final HttpClient client;
   private final T request;
   private final HttpResponse response;
   private final HttpContext context;
   private final String baseUrl;

   public HttpAction(final HttpClient httpClient, final T httpGet, final HttpContext context,
            final HttpResponse response, final String baseUrl)
   {
      this.client = httpClient;
      this.request = httpGet;
      this.context = context;
      this.response = response;
      this.baseUrl = baseUrl;
   }

   /**
    * Return the current full URL.
    */
   public String getCurrentURL()
   {
      HttpUriRequest currentReq = (HttpUriRequest) context.getAttribute(
               ExecutionContext.HTTP_REQUEST);
      HttpHost currentHost = (HttpHost) context.getAttribute(
               ExecutionContext.HTTP_TARGET_HOST);
      String currentUrl = currentHost.toURI() + currentReq.getURI();

      if (currentUrl.startsWith(baseUrl))
      {
         currentUrl = currentUrl.substring(baseUrl.length());
      }

      return currentUrl;
   }

   /**
    * Return the current URL excluding host or context root.
    */
   public String getRelativeURL()
   {
      if (!getCurrentURL().startsWith(getHost()))
      {
         throw new IllegalStateException("Cannot get relative URL for address outside context root [" + getHost()
                  + "]");
      }
      return getCurrentURL().substring(getHost().length());
   }

   /**
    * Return the URL up to and including the host and port.
    */
   public String getHost()
   {
      HttpHost currentHost = (HttpHost) context.getAttribute(
               ExecutionContext.HTTP_TARGET_HOST);

      return currentHost.toURI();
   }

   public HttpClient getClient()
   {
      return client;
   }

   public T getRequest()
   {
      return request;
   }

   public HttpContext getContext()
   {
      return context;
   }

   public HttpResponse getResponse()
   {
      return response;
   }
}
