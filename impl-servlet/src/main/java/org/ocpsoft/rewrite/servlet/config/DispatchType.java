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
package org.ocpsoft.rewrite.servlet.config;

import java.util.List;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.rewrite.bind.Bindable;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.Bindings;
import org.ocpsoft.rewrite.bind.DefaultBindable;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * Responsible for asserting on the {@link HttpServletRequest#getDispatcherType()} property.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class DispatchType extends HttpCondition implements Bindable<DispatchType>
{
   private final DispatcherType type;

   @SuppressWarnings("rawtypes")
   private final DefaultBindable<?> bindable = new DefaultBindable();

   private DispatchType(final DispatcherType type)
   {
      this.type = type;
   }

   @Override
   public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      if (this.type.equals(event.getRequest().getDispatcherType()))
      {
         Bindings.enqueueSubmission(event, context, bindable, type);
         return true;
      }
      return false;
   }

   /**
    * Return a {@link DispatchType} condition that ensures the current {@link HttpServletRewrite} is of
    * {@link DispatcherType#FORWARD}
    */
   public static DispatchType isForward()
   {
      return new DispatchType(DispatcherType.FORWARD);
   }

   /**
    * Return a {@link DispatchType} condition that ensures the current {@link HttpServletRewrite} is of
    * {@link DispatcherType#REQUEST}
    */
   public static DispatchType isRequest()
   {
      return new DispatchType(DispatcherType.REQUEST);
   }

   /**
    * Return a {@link DispatchType} condition that ensures the current {@link HttpServletRewrite} is of
    * {@link DispatcherType#ERROR}
    */
   public static DispatchType isError()
   {
      return new DispatchType(DispatcherType.ERROR);
   }

   /**
    * Return a {@link DispatchType} condition that ensures the current {@link HttpServletRewrite} is of
    * {@link DispatcherType#ASYNC}
    */
   public static DispatchType isAsync()
   {
      return new DispatchType(DispatcherType.ASYNC);
   }

   /**
    * Return a {@link DispatchType} condition that ensures the current {@link HttpServletRewrite} is of
    * {@link DispatcherType#INCLUDE}
    */
   public static DispatchType isInclude()
   {
      return new DispatchType(DispatcherType.INCLUDE);
   }

   @Override
   public String toString()
   {
      return type == null ? "unknown" : type.toString();
   }

   @Override
   public DispatchType bindsTo(final Binding binding)
   {
      bindable.bindsTo(binding);
      return this;
   }

   @Override
   public List<Binding> getBindings()
   {
      return bindable.getBindings();
   }

}