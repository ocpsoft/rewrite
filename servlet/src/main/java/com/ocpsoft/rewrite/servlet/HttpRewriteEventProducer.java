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

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ocpsoft.rewrite.event.MutableRewriteEvent;
import com.ocpsoft.rewrite.spi.RewriteEventProducer;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class HttpRewriteEventProducer implements RewriteEventProducer
{
   @Override
   public int priority()
   {
      return 0;
   }

   @Override
   public MutableRewriteEvent<?, ?> createRewriteEvent(final ServletRequest request,
            final ServletResponse response)
   {
      if ((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse))
         return new HttpRewriteEventImpl((HttpServletRequest) request, (HttpServletResponse) response);

      return null;
   }

}
