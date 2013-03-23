/*
 * Copyright 2013 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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
package org.ocpsoft.rewrite.servlet;

import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.servlet.spi.RequestParameterProvider;

/**
 * An {@link HttpServletRequestWrapper} for the {@link Rewrite} framework
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class RewriteWrappedRequest extends HttpServletRequestWrapper
{
   /**
    * Get the current {@link RewriteWrappedRequest}
    */
   public static RewriteWrappedRequest getCurrentInstance(ServletRequest request)
   {
      RewriteWrappedRequest wrapper = (RewriteWrappedRequest) request.getAttribute(RewriteWrappedRequest.class
               .getName());
      return wrapper;
   }

   /**
    * Set the current {@link RewriteWrappedRequest}
    */
   protected static void setCurrentInstance(final RewriteWrappedRequest instance)
   {
      instance.setAttribute(RewriteWrappedRequest.class.getName(), instance);
   }

   /**
    * Create a new {@link RewriteWrappedRequest}
    */
   public RewriteWrappedRequest(HttpServletRequest request)
   {
      super(request);
   }

   /**
    * Get the current {@link Map} of modifiable {@link HttpServletRequest} parameters.
    * 
    * @see {@link RequestParameterProvider}
    */
   abstract public Map<String, String[]> getModifiableParameters();
}
