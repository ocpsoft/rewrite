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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.Bindings;
import org.ocpsoft.rewrite.bind.Evaluation;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.ParameterizedPatternParser;
import org.ocpsoft.rewrite.param.ParameterizedPatternParserParameter;
import org.ocpsoft.rewrite.param.RegexParameterizedPatternBuilder;
import org.ocpsoft.rewrite.param.RegexParameterizedPatternParser;
import org.ocpsoft.rewrite.servlet.config.bind.Request;
import org.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * A {@link org.ocpsoft.rewrite.config.Condition} that inspects the value of
 * {@link org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite#getRequestPath()}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class URL extends HttpCondition implements IURL
{
   private final ParameterizedPatternParser expression;
   private final ParameterStore<URLParameter> parameters = new ParameterStore<URLParameter>();

   private URL(final String pattern)
   {
      Assert.notNull(pattern, "URL must not be null.");
      this.expression = new RegexParameterizedPatternParser(".*", pattern);

      for (ParameterizedPatternParserParameter parameter : expression.getParameterMap().values()) {
         where(parameter.getName()).bindsTo(Evaluation.property(parameter.getName()));
      }
   }

   /**
    * Inspect the current request URL, comparing against the given pattern.
    * <p>
    * The given pattern may be parameterized using the following format:
    * <p>
    * <b>INBOUND:</b><br>
    * <code>
    *    /context-path/{param}?foo={bar} <br>
    *    /context-path/{param}/{param2}?foo={bar}&cab={caz} <br>
    *    ... and so on
    * </code> <b>OUTBOUND:</b><br>
    * http://domain.com/context-path/{param}?foo={bar} <br>
    * /context-path/{param}/{param2}?foo={bar}&cab={caz} <br>
    * ... and so on
    * <p>
    * By default, matching parameter values are bound only to the {@link org.ocpsoft.rewrite.context.EvaluationContext}.
    * See also {@link #where(String)}
    */
   public static URL matches(final String pattern)
   {
      return new URL(pattern);
   }

   public static URL captureIn(final String param)
   {
      URL path = new URL("{" + param + "}");
      return path;
   }

   /**
    * Bind each URL parameter to the corresponding request parameter by name. By default, matching values are bound only
    * to the {@link org.ocpsoft.rewrite.context.EvaluationContext}.
    * <p>
    * See also {@link #where(String)}
    */
   @Override
   public URL withRequestBinding()
   {
      for (ParameterizedPatternParserParameter capture : expression.getParameterMap().values()) {
         where(capture.getName()).bindsTo(Request.parameter(capture.getName()));
      }
      return this;
   }

   @Override
   public URLParameter where(final String param)
   {
      return parameters.where(param, new URLParameter(this, expression.getParameter(param)));
   }

   @Override
   public URLParameter where(final String param, final Binding binding)
   {
      return where(param).bindsTo(binding);
   }

   @Override
   public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      String requestURL = null;

      if (event instanceof HttpOutboundServletRewrite)
      {
         requestURL = ((HttpOutboundServletRewrite) event).getOutboundURL();
         if (requestURL.startsWith(event.getContextPath()))
         {
            requestURL = requestURL.substring(event.getContextPath().length());
         }
      }
      else
      {
         HttpServletRequest request = event.getRequest();
         requestURL = event.getContextPath() + event.getURL();
         requestURL = request.getScheme() + "://" + request.getServerName()
                  + (request.getServerPort() != 80 ? ":" + request.getServerPort() : "") + requestURL;
      }

      if (expression.matches(event, context, requestURL))
      {
         Map<ParameterizedPatternParserParameter, String[]> parameters = expression.parse(event, context, requestURL);

         for (ParameterizedPatternParserParameter capture : parameters.keySet()) {
            if (!Bindings.enqueueSubmission(event, context, where(capture.getName()), parameters.get(capture)))
               return false;
         }
         return true;
      }
      return false;
   }

   /**
    * Get the underlying {@link RegexParameterizedPatternBuilder} for this {@link URL}
    * <p>
    * See also: {@link #where(String)}
    */
   public ParameterizedPatternParser getSchemeExpression()
   {
      return expression;
   }

   @Override
   public String toString()
   {
      return expression.toString();
   }

   @Override
   public ParameterizedPatternParser getPathExpression()
   {
      return expression;
   }
}