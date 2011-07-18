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
package com.ocpsoft.rewrite.servlet.event;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.servlet.event.BaseRewrite.Flow;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface ServletRewrite<IN extends ServletRequest, OUT extends ServletResponse>
         extends Rewrite
{
   public IN getRequest();

   public OUT getResponse();

   /**
    * Marks the current {@link ServletRewrite} as handled and terminates further handling.
    */
   public void abort();

   /**
    * Marks the {@link ServletRewrite} as handled and proceeds with the rest of the handlers.
    */
   public void proceed();

   /**
    * Marks the {@link ServletRewrite} as handled and terminates further handling.
    */
   public void handled();

   /**
    * Get the current {@link Flow} state.
    */
   public Flow getFlow();
}
