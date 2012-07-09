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
package org.ocpsoft.rewrite.transform;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.ocpsoft.common.util.Streams;
import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.config.HttpOperation;
import org.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.transform.cache.CacheKeyStrategy;
import org.ocpsoft.rewrite.transform.cache.CachedTransformation;
import org.ocpsoft.rewrite.transform.cache.DefaultTransformationCache;
import org.ocpsoft.rewrite.transform.cache.RequestPathCacheKeyStrategy;
import org.ocpsoft.rewrite.transform.cache.TransformationCache;
import org.ocpsoft.rewrite.transform.resolve.ResourceResolver;
import org.ocpsoft.rewrite.transform.resolve.WebResourceResolver;
import org.ocpsoft.rewrite.transform.resource.Resource;

public class Transform extends HttpOperation
{

   private static final String PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";

   private final Logger log = Logger.getLogger(Transform.class);

   private ResourceResolver resolver = WebResourceResolver.identity();

   private Pipeline pipeline = new Pipeline();

   private TransformationCache cache = new DefaultTransformationCache();

   private CacheKeyStrategy cacheKeyFactory = new RequestPathCacheKeyStrategy();

   public static Transform with(Class<? extends Transformer> transformerType) {
      return new Transform().apply(transformerType);
   }
   
   public Transform apply(Class<? extends Transformer> transformerType)
   {
      try {
         return apply(transformerType.newInstance());
      }
      catch (InstantiationException e) {
         throw new IllegalArgumentException(e);
      }
      catch (IllegalAccessException e) {
         throw new IllegalArgumentException(e);
      }
   }

   public Transform apply(Class<? extends Transformer> first, Class<? extends Transformer> second,
            Class<? extends Transformer> third)
   {
      apply(first);
      apply(second);
      apply(third);
      return this;
   }

   public Transform apply(Class<? extends Transformer> first, Class<? extends Transformer> second)
   {
      apply(first);
      apply(second);
      return this;
   }

   /*
    * invoking this one will create compiler warnings :(
    */
   public Transform apply(Class<? extends Transformer>... transformerTypes)
   {
      for (Class<? extends Transformer> transformerType : transformerTypes) {
         apply(transformerType);
      }
      return this;
   }

   public Transform apply(Transformer... transformers)
   {
      for (Transformer transformer : transformers) {
         pipeline.add(transformer);
      }
      return this;
   }

   public Transform cacheWith(TransformationCache cache)
   {
      this.cache = cache;
      return this;
   }

   public Transform resolvedBy(ResourceResolver resourceResolver)
   {
      this.resolver = resourceResolver;
      return this;
   }

   public Transform cacheKeyStrategy(CacheKeyStrategy cacheKeyFactory)
   {
      this.cacheKeyFactory = cacheKeyFactory;
      return this;
   }

   @Override
   public void performHttp(HttpServletRewrite event, EvaluationContext context)
   {

      // rendering effects only inbound requests
      if (event instanceof HttpInboundServletRewrite) {

         HttpInboundServletRewrite inboundRewrite = (HttpInboundServletRewrite) event;
         HttpServletResponse response = inboundRewrite.getResponse();

         // IO errors must be handled here
         try {

            // try to load the underlying resource
            Resource resource = resolver.getResource(event, context);

            // proceed only if requested resource has been found
            if (resource != null) {

               // We may send a 304 response if the client already has a current version
               if (clientHasAlreadyTheLatestVersion(inboundRewrite, resource)) {

                  // send 304 meaning 'resource didn't change since the last time you requested it'
                  inboundRewrite.sendStatusCode(304);

               }

               // we must send the content to the client
               else {

                  // is the result of the transformation available from the cache?
                  Serializable key = cacheKeyFactory.create(inboundRewrite);
                  CachedTransformation cacheEntry = cache.get(key);

                  // holds the result of the transformation written to the client at a later stage
                  byte[] result = null;

                  // use the cached version of it is up to data
                  if (cacheEntry != null && cacheEntry.getTimestamp() >= resource.getLastModified()) {

                     if (log.isDebugEnabled()) {
                        log.debug("Found cached transformation for: {}", key);
                     }

                     result = cacheEntry.getData();

                  }

                  // no cached version available, we have to perform the full transformation
                  else {

                     if (log.isDebugEnabled()) {
                        log.debug("No cached transformation found. Starting transformation process...");
                     }

                     long start = System.currentTimeMillis();

                     // run transformation and store it in a byte array
                     ByteArrayOutputStream bos = new ByteArrayOutputStream();
                     pipeline.transform(resource.getInputStream(), bos);
                     result = bos.toByteArray();

                     if (log.isDebugEnabled()) {
                        log.debug("Transformation finished in: {}ms", System.currentTimeMillis() - start);
                     }

                     // store the result for later requests
                     cache.put(key, new CachedTransformation(result, resource.getLastModified()));

                  }

                  if (log.isDebugEnabled()) {
                     log.debug("Writing {} bytes back to the client.", result.length);
                  }

                  // send 'Last-Modified' date if available
                  if (resource.getLastModified() > 0) {
                     // round up to the next second because resource modification times have milliseconds
                     response.setDateHeader("Last-Modified", resource.getLastModified() + 1000);
                  }

                  // write the data to the client
                  Streams.copy(new ByteArrayInputStream(result), response.getOutputStream());
                  response.flushBuffer();

               }

               // the application doesn't need to process the request anymore
               inboundRewrite.abort();

            }
         }
         catch (IOException e) {
            log.error("Failed to render resource", e);
         }

      }

   }

   private boolean clientHasAlreadyTheLatestVersion(HttpInboundServletRewrite rewrite, Resource resource)
   {

      // we can only send a 304 if there is a modification of the resouce
      if (resource.getLastModified() > 0) {

         long ifModifiedSince = 0;

         // check for a 'If-Modified-Since' header
         String headerValue = rewrite.getRequest().getHeader("If-Modified-Since");
         if (headerValue != null && headerValue.trim().length() > 0) {

            // try to parse the RFC1123 date
            try {
               ifModifiedSince = new SimpleDateFormat(PATTERN_RFC1123, Locale.ENGLISH).parse(headerValue).getTime();
            }
            catch (ParseException e) {
               // invalid header format -> ignore it
            }

         }

         // if the 'If-Modified-Since' date AFTER the last modification date of the resouce?
         if (ifModifiedSince > 0 && ifModifiedSince > resource.getLastModified()) {
            return true;
         }

      }
      return false;

   }

}
