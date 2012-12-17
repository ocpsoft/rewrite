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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.bind.Bindable;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.Bindings;
import org.ocpsoft.rewrite.bind.DefaultBindable;
import org.ocpsoft.rewrite.bind.Evaluation;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.InboundRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.servlet.util.QueryStringBuilder;
import org.ocpsoft.rewrite.util.Maps;

/**
 * A {@link org.ocpsoft.rewrite.config.Condition} that inspects the value of
 * {@link org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite#getRequestQueryString()}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class Query extends HttpCondition implements Bindable<Query>
{
   @SuppressWarnings({ "rawtypes" })
   protected final DefaultBindable<?> bindable = new DefaultBindable();

   /**
    * Bind the values of this {@link Query} query to the given {@link Binding}.
    */
   @Override
   public Query bindsTo(final Binding binding)
   {
      this.bindable.bindsTo(binding);
      return this;
   }

   @Override
   public List<Binding> getBindings()
   {
      return bindable.getBindings();
   }

   /**
    * Return a new {@link org.ocpsoft.rewrite.config.Condition} matching against the entire
    * {@link HttpServletRequest#getQueryString()}
    * <p>
    * This value may be bound using <code>{param}</code> statements.
    * <p>
    * See also: {@link #bindsTo(Binding)}
    */
   public static Query matches(final String queryPattern)
   {
      Assert.notNull(queryPattern, "URL pattern must not be null.");
      final Pattern pattern = Pattern.compile(queryPattern);

      return new Query() {
         @Override
         public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
         {
            String queryString = null;
            if (event instanceof InboundRewrite)
               queryString = event.getInboundAddress().getQuery();
            else if (event instanceof HttpOutboundServletRewrite)
            {
               queryString = QueryStringBuilder.extractQuery(getEncodedURL(event));
            }

            if (pattern.matcher(queryString == null ? "" : queryString).matches())
            {
               List<String> values = new ArrayList<String>();
               values.add(queryString);
               Bindings.enqueueSubmission(event, context, bindable, values.toArray(new String[] {}));
               return true;
            }
            return false;
         }
      };
   }

   /**
    * Temporary solution to get {@link HttpServletRewrite#getAddress()} in encoded form.
    */
   private static String getEncodedURL(HttpServletRewrite event)
   {
      if (event instanceof HttpInboundServletRewrite) {
         return event.getRequest().getRequestURI() + "?" + event.getRequest().getQueryString();
      }
      return event.getAddress().toString();
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
   public static Query parameterExists(final String nameRegex)
   {
      Assert.notNull(nameRegex, "Parameter name pattern must not be null.");
      final Pattern pattern = Pattern.compile(nameRegex);

      return new Query() {
         @Override
         @SuppressWarnings({ "rawtypes" })
         public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
         {
            QueryStringBuilder queryString = QueryStringBuilder.createFromEncoded(getEncodedURL(event)).decode();

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
               return Bindings.enqueuePreOperationSubmissions(event, context, map);

            return false;
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
   public static Query valueExists(final String valueRegex)
   {
      Assert.notNull(valueRegex, "Parameter value pattern must not be null.");
      final Pattern pattern = Pattern.compile(valueRegex);

      return new Query() {
         @Override
         @SuppressWarnings({ "rawtypes" })
         public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
         {
            QueryStringBuilder queryString = QueryStringBuilder.createFromEncoded(getEncodedURL(event)).decode();

            List<String> values = new ArrayList<String>();
            Map<DefaultBindable, String[]> map = new LinkedHashMap<DefaultBindable, String[]>();
            for (String name : queryString.getParameterNames()) {

               List<String> paramValues = Arrays.asList(queryString.getParameterValues(name));
               DefaultBindable tempBindable = new DefaultBindable();
               tempBindable.bindsTo(Evaluation.property(name));
               for (String value : paramValues) {

                  if (value != null && pattern.matcher(value).matches())
                  {
                     Maps.addArrayValue(map, tempBindable, value);
                     values.add(value);
                  }
               }
            }

            map.put(bindable, values.toArray(new String[] {}));

            if (!values.isEmpty())
               return Bindings.enqueuePreOperationSubmissions(event, context, map);

            return false;
         }
      };
   }

}