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

import java.util.Map.Entry;
import java.util.Set;

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.config.ConfigurationRuleParameterBuilder;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.InboundRewrite;
import org.ocpsoft.rewrite.param.Parameter;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.ParameterValueStore;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.ParameterizedPattern;
import org.ocpsoft.rewrite.param.ParameterizedPatternParser;
import org.ocpsoft.rewrite.param.ParameterizedPatternResult;
import org.ocpsoft.rewrite.param.RegexParameterizedPatternParser;
import org.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.servlet.util.QueryStringBuilder;
import org.ocpsoft.urlbuilder.Address;

/**
 * A {@link Condition} that inspects the value of {@link HttpServletRewrite#getRequestQueryString()}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@SuppressWarnings("deprecation")
public abstract class Query extends HttpCondition implements Parameterized
{
   /**
    * Create a {@link Condition} matching the current {@link Address#getQuery()}.
    * <p>
    * The given pattern may be parameterized:
    * <p>
    * <code>
    *    ?param=value <br>
    *    ?param={param} <br>
    *    ?param={param}&foo=bar <br>
    *    ...
    * </code>
    * <p>
    * 
    * @param pattern {@link ParameterizedPattern} matching the query string of the current {@link Address}.
    * 
    * @see ConfigurationRuleParameterBuilder#where(String)
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

            if (pattern.parse(queryString == null ? "" : queryString).submit(event, context))
            {
               ParameterValueStore values = (ParameterValueStore) context.get(ParameterValueStore.class);
               for (Entry<Parameter<?>, String> entry : pattern.parse(query).getParameters(context).entrySet()) {
                  values.submit(event, context, store.get(entry.getKey().getName()), entry.getValue());
               }
               return true;
            }
            return false;
         }

         @Override
         public String toString()
         {
            return "Query.matches(\"" + query + "\")";
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
    * Create a {@link Condition} asserting the existence of specific parameter names within the current
    * {@link Address#getQuery()}
    * <p>
    * The given parameter name is automatically parameterized:
    * <p>
    * <code>
    *    Query.paramterExists("param") <br>
    *    ...<br/>
    *    Forward.to("/{param}/page.jsp")
    * </code>
    * <p>
    * 
    * @param pattern name of the {@link Parameter} matching query parameter names within the current
    *           {@link Address#getQuery()}.
    * 
    * @see ConfigurationRuleParameterBuilder#where(String)
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
               if (parameterName.equals(name))
               {
                  if (parameterValues == null || (parameterValues.length == 0))
                  {
                     return pattern.parse("").matches();
                  }
                  else
                  {
                     for (String value : parameterValues) {

                        ParameterizedPatternResult parseResult = pattern.parse(value);
                        if (parseResult.matches())
                        {
                           return parseResult.submit(event, context);
                        }
                     }
                  }
               }
            }

            return false;
         }

         @Override
         public String toString()
         {
            return "Query.parameterExists(\"" + name + "\")";
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
    * Create a {@link Condition} asserting the existence of specific parameter values within the current
    * {@link Address#getQuery()}.
    * 
    * @param pattern {@link ParameterizedPattern} matching query parameter values of the current
    *           {@link Address#getQuery()}.
    * 
    * @see ConfigurationRuleParameterBuilder#where(String)
    */
   public static Query valueExists(final String valuePattern)
   {
      Assert.notNull(valuePattern, "Parameter value pattern must not be null.");

      return new Query() {
         final ParameterizedPatternParser pattern = new RegexParameterizedPatternParser(valuePattern);

         @Override
         public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
         {
            QueryStringBuilder queryString = QueryStringBuilder.createFromEncoded(event.getAddress().getQuery())
                     .decode();

            for (String name : queryString.getParameterNames()) {

               for (String value : queryString.getParameterValues(name)) {
                  if (value != null && pattern.parse(value).submit(event, context))
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
            return "Query.valueExists(\"" + valuePattern + "\")";
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

}