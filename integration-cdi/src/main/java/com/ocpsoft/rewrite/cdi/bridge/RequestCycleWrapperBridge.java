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
package com.ocpsoft.rewrite.cdi.bridge;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ocpsoft.rewrite.cdi.events.WrapRequest;
import com.ocpsoft.rewrite.cdi.events.WrapResponse;
import com.ocpsoft.rewrite.servlet.http.HttpRequestCycleWrapper;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class RequestCycleWrapperBridge extends HttpRequestCycleWrapper
{
   @Inject
   private BeanManager manager;

   @Override
   public HttpServletRequest wrapRequest(HttpServletRequest request, HttpServletResponse response)
   {
      WrapRequest wrap = new WrapRequest(request, response);
      manager.fireEvent(wrap);
      return wrap.getRequest() == null ? request : wrap.getRequest();
   }

   @Override
   public HttpServletResponse wrapResponse(HttpServletRequest request, HttpServletResponse response)
   {
      WrapResponse wrap = new WrapResponse(request, response);
      manager.fireEvent(wrap);
      return wrap.getResponse() == null ? response : wrap.getResponse();
   }

   @Override
   public int priority()
   {
      return 0;
   }

}
