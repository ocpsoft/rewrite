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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.bind.Bindings;
import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.CompositeParameterStore;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.ParameterizedPatternParameter;
import org.ocpsoft.rewrite.param.ParameterizedPatternParser;
import org.ocpsoft.rewrite.param.RegexParameterizedPatternParser;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * A {@link Condition} that inspects values returned by {@link HttpServletRequest#getParameterMap()}
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class RequestParameter extends HttpCondition implements Parameterized<ParameterizedPatternParameter, String>
{
   private final ParameterizedPatternParser name;
   private final ParameterizedPatternParser value;

   private RequestParameter(final String name, final String value)
   {
      Assert.notNull(name, "Header name pattern cannot be null.");
      Assert.notNull(value, "Header value pattern cannot be null.");
      this.name = new RegexParameterizedPatternParser(name);
      this.value = new RegexParameterizedPatternParser(value);
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
      Enumeration<?> parameterNames = request.getParameterNames();
      while (parameterNames.hasMoreElements())
      {
         String parameter = parameterNames.nextElement().toString();
         if (name.matches(event, context, parameter) && matchesValue(event, context, request, parameter))
         {
            Map<ParameterizedPatternParameter, String[]> parameterValues = new HashMap<ParameterizedPatternParameter, String[]>();
            parameterValues.putAll(name.parse(event, context, parameter));
            parameterValues.putAll(value.parse(event, context, request.getParameter(parameter)));

            for (ParameterizedPatternParameter capture : parameterValues.keySet()) {
               if (!Bindings.enqueueSubmission(event, context, capture, parameterValues.get(capture)))
                  return false;
            }
            return true;
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

   public ParameterizedPatternParser getNameExpression()
   {
      return name;
   }

   public ParameterizedPatternParser getValueExpression()
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
         HttpServletRequest request = event.getRequest();
         Enumeration<?> parameterNames = request.getParameterNames();
         while (parameterNames.hasMoreElements())
         {
            String name = parameterNames.nextElement().toString();
            if (getNameExpression().matches(event, context, name))
            {
               if (matchesValues(event, context, request, name))
               {
                  return true;
               }
            }
         }
         return false;
      }

      private boolean matchesValues(Rewrite event, EvaluationContext context, final HttpServletRequest request,
               final String name)
      {
         for (String contents : Arrays.asList(request.getParameterValues(name)))
         {
            if (!getValueExpression().matches(event, context, contents))
            {
               return false;
            }
         }
         return true;
      }
   }

   @Override
   public String toString()
   {
      return "RequestParameter [name=" + name + ", value=" + value + "]";
   }

   @Override
   public ParameterStore<ParameterizedPatternParameter> getParameterStore()
   {
      return new CompositeParameterStore(name.getParameterStore(), value.getParameterStore());
   }
}