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

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.Bindings;
import org.ocpsoft.rewrite.bind.ParameterizedPattern;
import org.ocpsoft.rewrite.bind.ParameterizedPattern.RegexParameter;
import org.ocpsoft.rewrite.bind.RegexConditionParameterBuilder;
import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.ConditionParameterBuilder;
import org.ocpsoft.rewrite.param.ParameterizedCondition;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * A {@link Condition} that inspects values returned by {@link HttpServletRequest#getParameterMap()}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class RequestParameter extends HttpCondition implements
ParameterizedCondition<ConditionParameterBuilder<RegexConditionParameterBuilder, String>, String>
{

   private final ParameterizedPattern name;
   private final ParameterizedPattern value;

   private RequestParameter(final String name, final String value)
   {
      Assert.notNull(name, "Header name pattern cannot be null.");
      Assert.notNull(value, "Header value pattern cannot be null.");
      this.name = new ParameterizedPattern(name);
      this.value = new ParameterizedPattern(value);
   }

   /**
    * Return a {@link Header} condition that matches against both header name and values.
    * <p>
    * See also: {@link HttpServletRequest#getHeader(String)}
    * 
    * @param name Regular expression matching the header name
    * @param value Regular expression matching the header value
    */
   public static RequestParameter matches(final String name, final String value)
   {
      return new RequestParameter(name, value);
   }

   public static RequestParameter matchesAll(final String name, final String value)
   {
      return new AllRequestParameters(name, value);
   }

   /**
    * Return a {@link Header} condition that matches only against the existence of a header with a name matching the
    * given pattern. The header value is ignored.
    * <p>
    * See also: {@link HttpServletRequest#getHeader(String)}
    * 
    * @param name Regular expression matching the header name
    */
   public static RequestParameter exists(final String name)
   {
      return new RequestParameter(name, "{" + RequestParameter.class.getName() + "_value}");
   }

   /**
    * Return a {@link Header} condition that matches only against the existence of a header with value matching the
    * given pattern. The header name is ignored.
    * 
    * @param value Regular expression matching the header value
    */
   public static RequestParameter valueExists(final String value)
   {
      return new RequestParameter("{" + RequestParameter.class.getName() + "_name}", value);
   }

   @Override
   public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      HttpServletRequest request = event.getRequest();
      for (String parameter : Collections.list(request.getParameterNames()))
      {
         if (name.matches(event, context, parameter) && matchesValue(event, context, request, parameter))
         {
            Map<RegexParameter, String[]> parameters = name.parse(event, context, parameter);
            parameters = value.parse(event, context, parameter);

            if (Bindings.enqueuePreOperationSubmissions(event, context, parameters)
                     && Bindings.enqueuePreOperationSubmissions(event, context, parameters))
            {
               return true;
            }
         }
      }
      return false;
   }

   private boolean matchesValue(Rewrite event, EvaluationContext context, final HttpServletRequest request,
            final String header)
   {
      for (String contents : Arrays.asList(request.getParameterValues(header)))
      {
         if (value.matches(event, context, contents))
         {
            return true;
         }
      }
      return false;
   }

   @Override
   public RegexConditionParameterBuilder where(String param)
   {
      return new RegexConditionParameterBuilder(this, name.getParameter(param), value.getParameter(param));
   }

   @Override
   public RegexConditionParameterBuilder where(String param, String pattern)
   {
      return where(param).matches(pattern);
   }

   @Override
   public RegexConditionParameterBuilder where(String param, String pattern,
            Binding binding)
   {
      return where(param, pattern).bindsTo(binding);
   }

   @Override
   public RegexConditionParameterBuilder where(String param, Binding binding)
   {
      return where(param, binding);
   }

   ParameterizedPattern getName()
   {
      return name;
   }

   ParameterizedPattern getValue()
   {
      return value;
   }

   public static class AllRequestParameters extends RequestParameter
   {
      public AllRequestParameters(String name, String value)
      {
         super(name, value);
      }

      @Override
      public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
      {
         boolean result = false;
         HttpServletRequest request = event.getRequest();
         for (String name : Collections.list(request.getParameterNames()))
         {
            if (getName().matches(event, context, name))
            {
               if (matchesValues(event, context, request, name))
               {
                  Map<RegexParameter, String[]> parameters = getName().parse(event, context, name);
                  parameters = getValue().parse(event, context, name);

                  result = true;

                  if (!(Bindings.enqueuePreOperationSubmissions(event, context, parameters)
                           && Bindings.enqueuePreOperationSubmissions(event, context, parameters)))
                  {
                     return false;
                  }
               }
               else
                  return false;
            }
         }
         return result;
      }

      private boolean matchesValues(Rewrite event, EvaluationContext context, final HttpServletRequest request,
               final String name)
      {
         for (String contents : Arrays.asList(request.getParameterValues(name)))
         {
            if (!getValue().matches(event, context, contents))
            {
               return false;
            }
         }
         return true;
      }
   }
}