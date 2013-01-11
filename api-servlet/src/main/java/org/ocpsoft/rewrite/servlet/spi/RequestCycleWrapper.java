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
package org.ocpsoft.rewrite.servlet.spi;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.ocpsoft.common.pattern.Specialized;
import org.ocpsoft.common.pattern.Weighted;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * Provides an opportunity for extensions to wrap the {@link ServletRequest} and {@link ServletResponse} object on each
 * request-response cycle.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface RequestCycleWrapper<IN extends ServletRequest, OUT extends ServletResponse>
         extends Weighted, Specialized<Rewrite>
{
   /**
    * Wrap the inbound {@link IN} object.
    */
   IN wrapRequest(IN request, OUT response, ServletContext servletContext);

   /**
    * Wrap the outbound {@link OUT} object.
    */
   OUT wrapResponse(IN request, OUT response, ServletContext servletContext);
}
