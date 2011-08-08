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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.ocpsoft.rewrite.EvaluationContext;
import com.ocpsoft.rewrite.config.Condition;
import com.ocpsoft.rewrite.servlet.config.parameters.DefaultBindable;
import com.ocpsoft.rewrite.servlet.config.parameters.ParameterBinding;
import com.ocpsoft.rewrite.servlet.config.parameters.binding.Bindings;
import com.ocpsoft.rewrite.servlet.config.parameters.binding.Evaluation;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import com.ocpsoft.rewrite.servlet.util.Maps;
import com.ocpsoft.rewrite.servlet.util.QueryStringBuilder;
import com.ocpsoft.rewrite.util.Assert;

/**
 * A {@link Condition} that inspects the value of {@link HttpServletRewrite#getRequestQueryString()}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class QueryString extends HttpCondition
{
   @SuppressWarnings({ "rawtypes", "unchecked" })
   protected final DefaultBindable<?, ParameterBinding> bindable = new DefaultBindable();

   /**
    * Bind the values of this {@link QueryString} query to the given {@link ParameterBinding}.
    */
   public QueryString bindsTo(final ParameterBinding binding)
   {
      this.bindable.bindsTo(binding);
      return this;
   }

   /**
    * Return a new {@link Condition} matching against the entire {@link HttpServletRequest#getQueryString()}
    * <p>
    * This value may be bound.
    * <p>
    * See also: {@link #bindsTo(ParameterBinding)}
    */
   public static QueryString matches(final String pattern)
   {
      Assert.notNull(pattern, "URL pattern must not be null.");

      return new QueryString() {
         @Override
         public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
         {
            String queryString = event.getRequestQueryString();
            if (Pattern.compile(pattern).matcher(queryString == null ? "" : queryString).matches())
            {
               List<String> values = new ArrayList<String>();
               values.add(queryString);
               Bindings.evaluateCondition(event, context, bindable, values.toArray(new String[] {}));
               return true;
            }
            return false;
         }
      };
   }

   /**
    * Return a new {@link Condition} matching against the existence of specific parameters within
    * {@link HttpServletRequest#getQueryString()}
    * <p>
    * The values of all matching parameters may be bound. By default, matching values are bound to the
    * {@link EvaluationContext}.
    * <p>
    * See also: {@link #bindsTo(ParameterBinding)}
    */
   public static QueryString parameterExists(final String nameRegex)
   {
      Assert.notNull(nameRegex, "Parameter name pattern must not be null.");

      return new QueryString() {
         @Override
         @SuppressWarnings({ "rawtypes", "unchecked" })
         public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
         {
            Pattern pattern = Pattern.compile(nameRegex);

            QueryStringBuilder queryString = null;

            queryString = QueryStringBuilder.build(event.getURL());

            List<String> values = new ArrayList<String>();
            Map<DefaultBindable, String[]> map = new LinkedHashMap<DefaultBindable, String[]>();
            for (String name : queryString.getParameterNames()) {
               if (pattern.matcher(name).matches())
               {
                  String[] temp = queryString.getParameterValues(name);
                  DefaultBindable tempBindable = new DefaultBindable();
                  tempBindable.bindsTo(Evaluation.property(name));
                  map.put(tempBindable, temp);

                  values.addAll(Arrays.asList(temp));
               }
            }

            map.put(bindable, values.toArray(new String[] {}));

            if (!values.isEmpty())
               Bindings.evaluateCondition(event, context, map);

            return !values.isEmpty();
         }
      };
   }

   /**
    * Return a new {@link Condition} matching against the existence of a parameter values within
    * {@link HttpServletRequest#getQueryString()}
    * <p>
    * The values of all matching parameter values may be bound. By default, matching values are bound to the
    * {@link EvaluationContext}.
    * <p>
    * See also: {@link #bindsTo(ParameterBinding)}
    */
   public static QueryString valueExists(final String valueRegex)
   {
      Assert.notNull(valueRegex, "Parameter value pattern must not be null.");

      return new QueryString() {
         @Override
         @SuppressWarnings({ "rawtypes", "unchecked" })
         public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
         {
            Pattern pattern = Pattern.compile(valueRegex);
            QueryStringBuilder queryString = QueryStringBuilder.build(event.getRequestQueryString());

            List<String> values = new ArrayList<String>();
            Map<DefaultBindable, String[]> map = new LinkedHashMap<DefaultBindable, String[]>();
            for (String name : queryString.getParameterNames()) {

               List<String> paramValues = Arrays.asList(queryString.getParameterValues(name));
               DefaultBindable tempBindable = new DefaultBindable();
               tempBindable.bindsTo(Evaluation.property(name));
               for (String value : paramValues) {

                  if (pattern.matcher(value).matches())
                  {
                     Maps.addArrayValue(map, tempBindable, value);
                     values.add(value);
                  }
               }
            }

            if (!values.isEmpty())
               Bindings.evaluateCondition(event, context, map);

            return !values.isEmpty();
         }
      };
   }

}