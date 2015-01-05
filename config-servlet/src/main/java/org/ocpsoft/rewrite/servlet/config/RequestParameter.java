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
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.config.ConfigurationRuleParameterBuilder;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.ParameterizedPattern;
import org.ocpsoft.rewrite.param.ParameterizedPatternParser;
import org.ocpsoft.rewrite.param.RegexParameterizedPatternParser;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * A {@link Condition} that inspects values returned by {@link HttpServletRequest#getParameterMap()}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class RequestParameter extends HttpCondition implements Parameterized
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
    * Create a {@link Condition} that matches against both request parameter names and values.
    * <p>
    * Parameter name and value expressions may be parameterized:
    * <p>
    * <code>
    * RequestParameter.matches("username", "guest")<br/>
    * RequestParameter.matches("username", "{name}")<br/>
    * RequestParameter.matches("{anything}", "{value}")<br/>
    * </code>
    * 
    * @param name {@link ParameterizedPattern} matching the request parameter name.
    * @param value {@link ParameterizedPattern} matching the request parameter value.
    * 
    * @see ConfigurationRuleParameterBuilder#where(String) {@link HttpServletRequest#getParameterMap()}
    */
   public static RequestParameter matches(final String name, final String value)
   {
      return new RequestParameter(name, value) {
         @Override
         public String toString()
         {
            return "RequestParameter.matches(\"" + name + "\", \"" + value + "\")";
         }
      };
   }

   public static RequestParameter matchesAll(final String name, final String value)
   {
      return new AllRequestParameters(name, value) {
         @Override
         public String toString()
         {
            return "RequestParameter.matchesAll(\"" + name + "\", \"" + value + "\")";
         }
      };
   }

   /**
    * Creates a {@link RequestParameter} condition that will capture the value of the the given request parameter if it
    * exists so you can bind to it using <code>.where()</code>.
    * 
    * @param name The name of the request parameter
    */
   public static RequestParameter captureValue(final String name)
   {
      return new RequestParameter(name, "{" + name + "}") {

         @Override
         public String toString()
         {
            return "RequestParameter.captureValue(\"" + name + "\")";
         }

         @Override
         public boolean evaluateHttp(HttpServletRewrite event, EvaluationContext context)
         {
            super.evaluateHttp(event, context);
            return true;
         }

      };
   }

   /**
    * Create a {@link Condition} that matches against the existence of a request parameter with a name matching the
    * given pattern. The parameter value is ignored.
    * <p>
    * Parameter name expressions may be parameterized:
    * <p>
    * <code>
    * RequestParameter.exists("username")<br/>
    * RequestParameter.exists("{name}")<br/>
    * ...
    * </code>
    * 
    * @param name {@link ParameterizedPattern} matching the request parameter name.
    * 
    * @see ConfigurationRuleParameterBuilder#where(String) {@link HttpServletRequest#getParameterMap()}
    */
   public static RequestParameter exists(final String name)
   {
      return new RequestParameter(name, "{" + RequestParameter.class.getName() + "_" + name + "_value}") {
         @Override
         public String toString()
         {
            return "RequestParameter.exists(\"" + name + "\")";
         }
      };
   }

   /**
    * Create a {@link Condition} that matches only against the existence of a request parameter value matching the given
    * pattern. The parameter name is ignored.
    * <p>
    * Parameter value expressions may be parameterized:
    * <p>
    * <code>
    * RequestParameter.valueExists("guest")<br/>
    * RequestParameter.valueExists("{username}")<br/>
    * ...
    * </code>
    * 
    * @param name {@link ParameterizedPattern} matching the request parameter name.
    * 
    * @see ConfigurationRuleParameterBuilder#where(String) {@link HttpServletRequest#getParameterMap()}
    */
   public static RequestParameter valueExists(final String value)
   {
      return new RequestParameter("{" + RequestParameter.class.getName() + "_" + value + "_name}", value) {
         @Override
         public String toString()
         {
            return "RequestParameter.valueExists(\"" + value + "\")";
         }
      };
   }

   @Override
   public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      HttpServletRequest request = event.getRequest();
      Enumeration<?> parameterNames = request.getParameterNames();
      while (parameterNames.hasMoreElements())
      {
         String parameter = parameterNames.nextElement().toString();
         if (name.parse(parameter).submit(event, context) && matchesValue(event, context, request, parameter))
         {
            return true;
         }
      }
      return false;
   }

   private boolean matchesValue(Rewrite event, EvaluationContext context, final HttpServletRequest request,
            final String parameterName)
   {
      for (String contents : Arrays.asList(request.getParameterValues(parameterName)))
      {
         if (value.parse(contents).submit(event, context))
         {
            return true;
         }
      }
      return false;
   }

   /**
    * Get the {@link ParameterizedPattern} of the request parameter name.
    */
   public ParameterizedPatternParser getNameExpression()
   {
      return name;
   }

   /**
    * Get the {@link ParameterizedPattern} of the request parameter value.
    */
   public ParameterizedPatternParser getValueExpression()
   {
      return value;
   }

   private abstract static class AllRequestParameters extends RequestParameter
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
            if (getNameExpression().parse(name).submit(event, context))
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
            if (!getValueExpression().parse(contents).submit(event, context))
            {
               return false;
            }
         }
         return true;
      }
   }

   @Override
   public Set<String> getRequiredParameterNames()
   {
      Set<String> result = new LinkedHashSet<String>();
      result.addAll(name.getRequiredParameterNames());
      result.addAll(value.getRequiredParameterNames());
      return result;
   }

   @Override
   public void setParameterStore(ParameterStore store)
   {
      name.setParameterStore(store);
      value.setParameterStore(store);
   }
}