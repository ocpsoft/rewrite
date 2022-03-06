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
package org.ocpsoft.rewrite.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.ocpsoft.rewrite.exception.RewriteException;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public class HttpAction
{
   private final OkHttpClient client;
   private final Request request;
   private final Response response;
   private final String baseUrl;
   private final String contextPath;
   private String responseContent;

   public HttpAction(final OkHttpClient client, final Request request,
                     final Response response, final String baseUrl, final String contextPath)
   {
      this.client = client;
      this.request = request;
      this.response = response;
      this.baseUrl = baseUrl;
      this.contextPath = contextPath;
   }

   /**
    * Return the current full URL.
    */
   public String getCurrentURL()
   {
      String currentUrl = response.request().url().toString();

      if (currentUrl.startsWith(baseUrl))
      {
         currentUrl = currentUrl.substring(baseUrl.length());
      }

      return currentUrl;
   }

   /**
    * Return the current URL excluding host or context root.
    */
   public String getCurrentContextRelativeURL()
   {
      if (!getCurrentURL().startsWith(getContextPath()))
      {
         throw new IllegalStateException("Cannot get relative URL for address outside context root [" + getCurrentURL()
                  + "]");
      }
      return getCurrentURL().substring(getContextPath().length());
   }

   /**
    * Return the URL up to and including the host and port.
    */
   public String getHost()
   {
      return request.url().host();
   }

   public OkHttpClient getClient()
   {
      return client;
   }

   public Request getRequest()
   {
      return request;
   }

   public Response getResponse()
   {
      return response;
   }

   public String getContextPath()
   {
      return contextPath;
   }

   public List<String> getResponseHeaderValues(final String name)
   {
      return response.headers(name);
   }

   public int getStatusCode()
   {
      return response.code();
   }

   public String getResponseContent()
   {
      if (responseContent == null)
      {
         try {
            ResponseBody entity = getResponse().body();
            if (entity != null)
            {
               responseContent = entity.string();
            }
         }
         catch (Exception e) {
            throw new RewriteException("Could not stringify response InputStream", e);
         }
      }
      return responseContent;
   }

   /**
    * Return a {@link String} containing the contents of the given {@link InputStream}
    */
   public static String toString(final InputStream stream)
   {
      StringBuilder out = new StringBuilder();
      try {
         final char[] buffer = new char[0x10000];
         Reader in = new InputStreamReader(stream, "UTF-8");
         int read;
         do {
            read = in.read(buffer, 0, buffer.length);
            if (read > 0) {
               out.append(buffer, 0, read);
            }
         }
         while (read >= 0);
      }
      catch (UnsupportedEncodingException e) {
         throw new RuntimeException(e);
      }
      catch (IOException e) {
         throw new RuntimeException(e);
      }
      return out.toString();
   }
}
