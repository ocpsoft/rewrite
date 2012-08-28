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
package org.ocpsoft.rewrite.servlet.config.response;

import javax.servlet.ServletResponse;

import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * An intercepter that operates on the fully buffered {@link ServletResponse#getOutputStream()} before flushing to the
 * client, once the control of the application has been returned to Rewrite.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ResponseContentInterceptor
{
   /**
    * Perform modifications on the fully buffered {@link ServletResponse#getOutputStream()} contents. 
    */
   void intercept(HttpServletRewrite event, ResponseContent buffer, ResponseContentInterceptorChain chain);
}
