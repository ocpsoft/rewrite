/*
 * Copyright 2012 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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

import java.util.ArrayList;
import java.util.List;

import org.ocpsoft.rewrite.servlet.config.response.ResponseBuffer;
import org.ocpsoft.rewrite.servlet.config.response.ResponseInterceptor;
import org.ocpsoft.rewrite.servlet.config.response.ResponseInterceptorChain;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ResponseInterceptorChainImpl implements ResponseInterceptorChain
{
   private List<ResponseInterceptor> interceptors;

   public ResponseInterceptorChainImpl(List<ResponseInterceptor> responseInterceptors)
   {
      this.interceptors = new ArrayList<ResponseInterceptor>(responseInterceptors);
   }

   @Override
   public void next(HttpServletRewrite event, ResponseBuffer buffer, ResponseInterceptorChain chain)
   {
      ResponseInterceptor interceptor = interceptors.remove(0);
      interceptor.intercept(event, buffer, chain);
   }

   public void begin(HttpServletRewrite event, ResponseBuffer buffer)
   {
      interceptors.add(new ResponseInterceptor() {
         @Override
         public void intercept(HttpServletRewrite event, ResponseBuffer buffer, ResponseInterceptorChain chain)
         {
         }
      });
      
      next(event, buffer, this);
   }
}
