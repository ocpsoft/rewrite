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
package com.ocpsoft.pretty.faces.el;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import com.ocpsoft.pretty.faces.el.processor.PathParameterProcessor;

public class Expressions
{
   public static final String EL_REGEX;

   static
   {
      List<PathParameterProcessor> processors = new ArrayList<PathParameterProcessor>();
      processors.addAll(ExpressionProcessorRunner.processors);
      processors.addAll(ExpressionProcessorRunner.preProcessors);

      String result = "(";
      for (Iterator<PathParameterProcessor> iter = processors.iterator(); iter.hasNext();)
      {
         PathParameterProcessor p = iter.next();
         if (p.getRegex() != null)
         {
            result += "(" + p.getRegex() + ")";
            if (iter.hasNext())
            {
               result += "|";
            }
         }
      }
      result += ")";
      EL_REGEX = result;
   }

   /**
    * The pattern to match a String EL expression. Regex Group 0 contains the
    * entire expression, no more.
    */
   public static final Pattern elPattern = Pattern.compile(EL_REGEX);

   /**
    * Return true if the value is an EL expression.
    */
   public static boolean isEL(final String value)
   {
      if (value == null)
      {
         return false;
      }
      return elPattern.matcher(value).matches();
   }

   /**
    * Return true if the value contains an EL expression.
    */
   public static boolean containsEL(final String value)
   {
      if (value == null)
      {
         return false;
      }

      return elPattern.matcher(value).find();
   }
}
