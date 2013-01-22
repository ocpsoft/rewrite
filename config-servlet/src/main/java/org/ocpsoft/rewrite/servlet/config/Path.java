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

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.ParameterizedPatternParameter;
import org.ocpsoft.rewrite.param.ParameterizedPatternParser;
import org.ocpsoft.rewrite.param.RegexParameterizedPatternBuilder;
import org.ocpsoft.rewrite.param.RegexParameterizedPatternParser;
import org.ocpsoft.rewrite.servlet.config.bind.Request;
import org.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.servlet.util.URLBuilder;

/**
 * A {@link org.ocpsoft.rewrite.config.Condition} that inspects the value of
 * {@link org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite#getRequestPath()}
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Path extends HttpCondition implements Parameterized<ParameterizedPatternParameter, String>
{
   private final ParameterizedPatternParser expression;
   private boolean withRequestBinding = false;

   private Path(final String pattern)
   {
      Assert.notNull(pattern, "Path must not be null.");
      this.expression = new RegexParameterizedPatternParser("[^/]+", pattern);
   }

   /**
    * Inspect the current request URL, comparing against the given pattern.
    * <p>
    * The given pattern may be parameterized using the following format:
    * <p>
    * <code>
    *    /example/{param} <br>
    *    /example/{value}/sub/{value2} <br>
    *    ... and so on
    * </code>
    * <p>
    * By default, matching parameter values are bound only to the {@link org.ocpsoft.rewrite.context.EvaluationContext}.
    * See also {@link #where(String)}
    */
   public static Path matches(final String pattern)
   {
      return new Path(pattern);
   }

   public static Path captureIn(final String param)
   {
      Path path = new Path("{" + param + "}");
      path.expression.getParameterStore().get(param).matches(".*");
      return path;
   }

   /**
    * Bind each path parameter to the corresponding request parameter by name. By default, matching values are bound
    * only to the {@link org.ocpsoft.rewrite.context.EvaluationContext}.
    * <p>
    * See also {@link #where(String)}
    */
   public Path withRequestBinding()
   {
      if (!withRequestBinding)
      {
         for (ParameterizedPatternParameter parameter : expression.getParameterStore().values()) {
            parameter.bindsTo(Request.parameter(parameter.getName()));
         }
         withRequestBinding = true;
      }
      return this;
   }

   @Override
   public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      String url = null;

      if (event instanceof HttpOutboundServletRewrite)
         url = ((HttpOutboundServletRewrite) event).getOutboundAddress().getPath();
      else
         url = URLBuilder.createFrom(event.getInboundAddress().getPath()).decode().toPath();

      if (url.startsWith(event.getContextPath()))
         url = url.substring(event.getContextPath().length());

      return (expression.matches(event, context, url));
   }

   /**
    * Get the underlying {@link RegexParameterizedPatternBuilder} for this {@link Path}
    * <p>
    * See also: {@link #where(String)}
    */
   public ParameterizedPatternParser getPathExpression()
   {
      return expression;
   }

   @Override
   public String toString()
   {
      return expression.toString();
   }

   @Override
   public ParameterStore<ParameterizedPatternParameter> getParameterStore()
   {
      return expression.getParameterStore();
   }
}