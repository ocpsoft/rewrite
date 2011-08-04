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
package com.ocpsoft.rewrite.servlet.config;

import javax.servlet.DispatcherType;

import com.ocpsoft.rewrite.EvaluationContext;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class DispatchType extends HttpCondition
{
   private final DispatcherType type;

   public DispatchType(final DispatcherType type)
   {
      this.type = type;
   }

   @Override
   public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      return this.type.equals(event.getRequest().getDispatcherType());
   }

   /**
    * Return a {@link DispatchType} condition that ensures the current {@link HttpServletRewrite} if of
    * {@link DispatcherType#FORWARD}
    */
   public DispatchType isForward()
   {
      return new DispatchType(DispatcherType.FORWARD);
   }

   /**
    * Return a {@link DispatchType} condition that ensures the current {@link HttpServletRewrite} if of
    * {@link DispatcherType#REQUEST}
    */
   public DispatchType isRequest()
   {
      return new DispatchType(DispatcherType.REQUEST);
   }

   /**
    * Return a {@link DispatchType} condition that ensures the current {@link HttpServletRewrite} if of
    * {@link DispatcherType#ERROR}
    */
   public DispatchType isError()
   {
      return new DispatchType(DispatcherType.ERROR);
   }

   /**
    * Return a {@link DispatchType} condition that ensures the current {@link HttpServletRewrite} if of
    * {@link DispatcherType#ASYNC}
    */
   public DispatchType isAsync()
   {
      return new DispatchType(DispatcherType.ASYNC);
   }

   /**
    * Return a {@link DispatchType} condition that ensures the current {@link HttpServletRewrite} if of
    * {@link DispatcherType#INCLUDE}
    */
   public DispatchType isInclude()
   {
      return new DispatchType(DispatcherType.INCLUDE);
   }

}