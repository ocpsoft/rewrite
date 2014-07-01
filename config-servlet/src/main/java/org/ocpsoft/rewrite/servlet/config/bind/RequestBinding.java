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
package org.ocpsoft.rewrite.servlet.config.bind;

import java.util.Map;

import javax.servlet.ServletRequest;

import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.Parameter;
import org.ocpsoft.rewrite.servlet.RewriteWrappedRequest;
import org.ocpsoft.rewrite.servlet.event.ServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.util.Maps;

/**
 * A {@link Binding} responsible for storing and retrieving {@link Parameter} values in the
 * {@link ServletRequest#setAttribute(String, Object)} and {@link ServletRequest#getParameterMap()} contexts.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class RequestBinding implements Binding
{
   /**
    * Create a {@link Binding} to the {@link ServletRequest#setAttribute(String, Object)} map.
    */
   public static RequestBinding attribute(final String property)
   {
      return new RequestAttributeBinding(property) {
         @Override
         public String toString()
         {
            return "RequestBinding.attribute(\"" + property + "\")";
         }
      };
   }

   /**
    * Create a {@link Binding} to the {@link ServletRequest#getParameterMap()}.
    */
   public static RequestBinding parameter(final String property)
   {
      return new RequestParameterBinding(property) {
         @Override
         public String toString()
         {
            return "RequestBinding.parameter(\"" + property + "\")";
         }
      };
   }

   @Override
   public boolean supportsRetrieval()
   {
      return true;
   }

   @Override
   public boolean supportsSubmission()
   {
      return true;
   }

   private static class RequestParameterBinding extends RequestBinding
   {
      private final String name;

      public RequestParameterBinding(final String parameter)
      {
         this.name = parameter;
      }

      @Override
      public Object submit(final Rewrite event, final EvaluationContext context, final Object value)
      {
         ServletRequest request = ((ServletRewrite<?, ?>) event).getRequest();
         RewriteWrappedRequest wrapper = RewriteWrappedRequest.getCurrentInstance(request);

         Map<String, String[]> modifiableParameters = wrapper.getModifiableParameters();
         if (value != null)
         {
            if (value.getClass().isArray())
            {
               Object[] values = (Object[]) value;
               for (Object object : values) {
                  Maps.addArrayValue(modifiableParameters, name, object.toString());
               }
            }
            else
            {
               Maps.addArrayValue(modifiableParameters, name, value.toString());
            }
         }
         else
         {
            Maps.addArrayValue(modifiableParameters, name, null);
         }

         return null;
      }

      @Override
      public Object retrieve(final Rewrite event, final EvaluationContext context)
      {
         return ((HttpServletRewrite) event).getRequest().getParameter(name);
      }
   }

   private static class RequestAttributeBinding extends RequestBinding
   {
      private final String name;

      public RequestAttributeBinding(final String attribute)
      {
         this.name = attribute;
      }

      @Override
      public Object submit(final Rewrite event, final EvaluationContext context, final Object value)
      {
         ((HttpServletRewrite) event).getRequest().setAttribute(name, value);
         return null;
      }

      @Override
      public Object retrieve(final Rewrite event, final EvaluationContext context)
      {
         if (event instanceof HttpServletRewrite)
            return ((HttpServletRewrite) event).getRequest().getParameter(name);

         else
            return null;
      }

   }
}
