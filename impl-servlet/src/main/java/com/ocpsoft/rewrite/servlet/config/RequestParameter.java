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

import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.ocpsoft.rewrite.EvaluationContext;
import com.ocpsoft.rewrite.servlet.config.parameters.DefaultBindable;
import com.ocpsoft.rewrite.servlet.config.parameters.ParameterBinding;
import com.ocpsoft.rewrite.servlet.config.parameters.binding.Evaluation;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import com.ocpsoft.rewrite.util.Assert;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class RequestParameter extends HttpCondition
{
   private final Pattern name;
   private final Pattern value;

   private final DefaultBindable bindable = new DefaultBindable();

   private RequestParameter(final String nameRegex, final String valueRegex)
   {
      Assert.notNull(nameRegex, "Parameter name pattern cannot be null.");
      Assert.notNull(valueRegex, "Parameter value pattern cannot be null.");
      this.name = Pattern.compile(nameRegex);
      this.value = Pattern.compile(valueRegex);

      this.bindsTo(Evaluation.property(nameRegex));
   }

   private void bindsTo(final ParameterBinding binding)
   {
      this.bindable.bindsTo(binding);
   }

   /**
    * Return a {@link RequestParameter} condition that matches against both parameter name and values.
    * 
    * @param nameRegex Regular expression matching the parameter name
    * @param valueRegex Regular expression matching the parameter value
    */
   public static RequestParameter matches(final String nameRegex, final String valueRegex)
   {
      return new RequestParameter(nameRegex, valueRegex);
   }

   /**
    * Return a {@link RequestParameter} condition that matches only against the existence of a parameter with a name
    * matching the given pattern. The parameter value is ignored.
    * 
    * @param nameRegex Regular expression matching the parameter name
    */
   public static RequestParameter exists(final String nameRegex)
   {
      return new RequestParameter(nameRegex, ".*");
   }

   /**
    * Return a {@link RequestParameter} condition that matches only against the existence of a parameter with value
    * matching the given pattern. The parameter name is ignored.
    * 
    * @param valueRegex Regular expression matching the parameter value
    */
   public static RequestParameter valueExists(final String valueRegex)
   {
      return new RequestParameter(".*", valueRegex);
   }

   @Override
   public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      HttpServletRequest request = event.getRequest();
      for (String parameter : Collections.list(request.getParameterNames()))
      {
         if (name.matcher(parameter).matches() && matchesValue(request, parameter))
         {
            return true;
         }
      }
      return false;
   }

   private boolean matchesValue(final HttpServletRequest request, final String parameter)
   {
      for (String contents : Arrays.asList(request.getParameterValues(parameter)))
      {
         if (value.matcher(contents).matches())
         {
            return true;
         }
      }
      return false;
   }

}