/*
 * Copyright 2010 Lincoln Baxter, III
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
package com.ocpsoft.pretty.faces.el.processor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ocpsoft.pretty.faces.config.mapping.PathParameter;
import com.ocpsoft.pretty.faces.el.ConstantExpression;

public class Injected implements PathParameterProcessor
{
   private static final String REGEX = "\\#\\{\\s*:?\\s*([\\w\\.]+)\\s*\\}";
   public static final Pattern pattern = Pattern.compile(REGEX);

   public PathParameter process(final PathParameter param)
   {
      PathParameter result = param.copy();

      Matcher matcher = pattern.matcher(param.getExpression().getELExpression());
      if (matcher.matches())
      {
         String el = matcher.group(1);
         result.setExpression(new ConstantExpression("#{" + el + "}"));
         result.setExpressionIsPlainText(false);
      }
      return result;
   }

   public String getRegex()
   {
      return REGEX;
   }

}
