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

import java.util.Collections;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.InboundRewrite;
import org.ocpsoft.rewrite.param.Parameter;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.ParameterValueStore;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.ParameterizedPatternParser;
import org.ocpsoft.rewrite.param.RegexParameterizedPatternParser;
import org.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.servlet.util.QueryStringBuilder;

/**
 * A {@link org.ocpsoft.rewrite.config.Condition} that inspects the value of
 * {@link org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite#getRequestQueryString()}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class Query extends HttpCondition implements Parameterized
{
   /**
    * Return a new {@link org.ocpsoft.rewrite.config.Condition} matching against the entire
    * {@link HttpServletRequest#getQueryString()}
    * <p>
    * This value may be bound using <code>{param}</code> statements.
    * <p>
    * See also: {@link #bindsTo(Binding)}
    */
   public static Query matches(final String query)
   {
      Assert.notNull(query, "URL pattern must not be null.");

      return new Query() {
         private ParameterStore store;
         final ParameterizedPatternParser pattern = new RegexParameterizedPatternParser(query);

         @Override
         public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
         {
            String queryString = null;
            if (event instanceof InboundRewrite)
            {
               queryString = event.getInboundAddress().getQuery();
            }
            else if (event instanceof HttpOutboundServletRewrite)
            {
               queryString = ((HttpOutboundServletRewrite) event).getOutboundAddress().getQuery();
            }

            if (pattern.matches(event, context, queryString == null ? "" : queryString))
            {
               ParameterValueStore values = (ParameterValueStore) context.get(ParameterValueStore.class);
               for (Entry<Parameter<?>, String> entry : pattern.parse(event, context, query).entrySet()) {
                  values.submit(store.get(entry.getKey().getName()), entry.getValue());
               }
               return true;
            }
            return false;
         }

         @Override
         public String toString()
         {
            return "Query.matches(" + query + ")";
         }

         @Override
         public Set<String> getRequiredParameterNames()
         {
            return pattern.getRequiredParameterNames();
         }

         @Override
         public void setParameterStore(ParameterStore store)
         {
            pattern.setParameterStore(store);
            this.store = store;
         }
      };
   }

   /**
    * Return a new {@link org.ocpsoft.rewrite.config.Condition} matching against the existence of specific parameters
    * within {@link HttpServletRequest#getQueryString()}
    * <p>
    * The values of all matching parameters may be bound. By default, matching values are bound to the
    * {@link org.ocpsoft.rewrite.context.EvaluationContext}, and are stored by parameter name.
    * <p>
    * See also: {@link #bindsTo(Binding)}
    */
   public static Query parameterExists(final String name)
   {
      Assert.notNull(name, "Parameter name pattern must not be null.");

      return new Query() {
         final ParameterizedPatternParser pattern = new RegexParameterizedPatternParser("{" + name + "}");
         final String parameterName = name;

         @Override
         public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
         {
            QueryStringBuilder queryString = QueryStringBuilder.createFromEncoded(event.getAddress().getQuery())
                     .decode();

            for (String name : queryString.getParameterNames()) {
               String[] parameterValues = queryString.getParameterValues(name);
               for (String value : parameterValues) {

                  if (parameterName.equals(name) && pattern.matches(value))
                  {
                     ParameterStore store = (ParameterStore) context.get(ParameterStore.class);
                     ParameterValueStore values = (ParameterValueStore) context.get(ParameterValueStore.class);
                     return values.submit(store.get(parameterName), value);
                  }

               }
            }

            return false;
         }

         @Override
         public String toString()
         {
            return "Query.parameterExists(" + name + ")";
         }

         @Override
         public Set<String> getRequiredParameterNames()
         {
            return pattern.getRequiredParameterNames();
         }

         @Override
         public void setParameterStore(ParameterStore store)
         {
            pattern.setParameterStore(store);
         }
      };
   }

   /**
    * Return a new {@link org.ocpsoft.rewrite.config.Condition} matching against the existence of a parameter values
    * within {@link HttpServletRequest#getQueryString()}
    * <p>
    * The values of all matching parameter values may be bound. By default, matching values are bound to the
    * {@link EvaluationContext}.
    * <p>
    * See also: {@link #bindsTo(Binding)}
    */
   public static Query valueExists(final String valuePattern)
   {
      Assert.notNull(valuePattern, "Parameter value pattern must not be null.");

      return new Query() {
         final Pattern pattern = Pattern.compile(valuePattern);

         @Override
         public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
         {
            QueryStringBuilder queryString = QueryStringBuilder.createFromEncoded(event.getAddress().getQuery())
                     .decode();

            for (String name : queryString.getParameterNames()) {

               for (String value : queryString.getParameterValues(name)) {
                  if (value != null && pattern.matcher(value).matches())
                  {
                     return true;
                  }
               }
            }

            return false;
         }

         @Override
         public String toString()
         {
            return "Query.valueExists(" + valuePattern + ")";
         }

         @Override
         public Set<String> getRequiredParameterNames()
         {
            return Collections.emptySet();
         }

         @Override
         public void setParameterStore(ParameterStore store)
         {}
      };
   }

}