/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
