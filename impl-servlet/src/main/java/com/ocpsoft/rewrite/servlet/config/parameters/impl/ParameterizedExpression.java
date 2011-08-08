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
package com.ocpsoft.rewrite.servlet.config.parameters.impl;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ocpsoft.rewrite.EvaluationContext;
import com.ocpsoft.rewrite.servlet.config.parameters.Parameter;
import com.ocpsoft.rewrite.servlet.config.parameters.ParameterBinding;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import com.ocpsoft.rewrite.servlet.parse.CaptureType;
import com.ocpsoft.rewrite.servlet.parse.CapturingGroup;
import com.ocpsoft.rewrite.servlet.parse.ParseTools;
import com.ocpsoft.rewrite.servlet.util.Maps;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ParameterizedExpression
{
   private Pattern pattern;
   private final char[] chars;
   private final Map<String, Parameter> params = new LinkedHashMap<String, Parameter>();

   public ParameterizedExpression(final String pattern)
   {
      chars = pattern.toCharArray();

      if (chars.length > 0)
      {
         int cursor = 0;
         while (cursor < chars.length)
         {
            switch (chars[cursor])
            {
            case '{':
               int startPos = cursor;
               CapturingGroup group = ParseTools.balancedCapture(chars, startPos, chars.length - 1, CaptureType.BRACE);
               cursor = group.getEnd();

               params.put(new String(group.getCaptured()), new Parameter(group));
               break;

            default:
               break;
            }

            cursor++;
         }
      }
   }

   public Map<String, Parameter> getParameters()
   {
      return params;
   }

   public String build(final HttpServletRewrite event, final EvaluationContext context)
   {
      return build(extractBoundValues(event, context));
   }

   public String build(final Map<String, List<Object>> values)
   {
      StringBuilder builder = new StringBuilder();

      if ((values == null) || (params.size() != values.size()))
      {
         throw new IllegalArgumentException("Must supply [" + params.size() + "] values to build path.");
      }

      CapturingGroup last = null;
      for (Entry<String, Parameter> entry : params.entrySet()) {
         Parameter param = entry.getValue();
         CapturingGroup capture = param.getCapture();

         if ((last != null) && (last.getEnd() < capture.getStart()))
         {
            builder.append(Arrays.copyOfRange(chars, last.getEnd() + 1, capture.getStart()));
         }
         else if ((last == null) && (capture.getStart() > 0))
         {
            builder.append(Arrays.copyOfRange(chars, 0, capture.getStart()));
         }

         builder.append(Maps.popListValue(values, param.getName()));

         last = capture;
      }

      if ((last != null) && (last.getEnd() < chars.length))
      {
         builder.append(Arrays.copyOfRange(chars, last.getEnd() + 1, chars.length));
      }
      else if (last == null)
      {
         builder.append(chars);
      }

      return builder.toString();
   }

   public boolean matches(final String path)
   {
      if (pattern == null)
      {
         StringBuilder patternBuilder = new StringBuilder();

         CapturingGroup last = null;
         for (Entry<String, Parameter> entry : params.entrySet()) {
            Parameter param = entry.getValue();
            CapturingGroup capture = param.getCapture();

            if ((last != null) && (last.getEnd() < capture.getStart()))
            {
               patternBuilder.append(Arrays.copyOfRange(chars, last.getEnd() + 1, capture.getStart()));
            }
            else if ((last == null) && (capture.getStart() > 0))
            {
               patternBuilder.append(Arrays.copyOfRange(chars, 0, capture.getStart()));
            }

            patternBuilder.append('(');
            patternBuilder.append(param.getPattern());
            patternBuilder.append(')');

            last = capture;
         }

         if ((last != null) && (last.getEnd() < chars.length))
         {
            patternBuilder.append(Arrays.copyOfRange(chars, last.getEnd() + 1, chars.length));
         }
         else if (last == null)
         {
            patternBuilder.append(chars);
         }

         pattern = Pattern.compile(patternBuilder.toString());
      }

      return pattern.matcher(path).matches();
   }

   /**
    * Matches against the given URLEncoded path
    */
   public Map<Parameter, String> parseEncoded(final String path)
   {
      Map<Parameter, String> values = new LinkedHashMap<Parameter, String>();

      String temp = path;
      if (matches(path))
      {
         CapturingGroup last = null;
         for (Entry<String, Parameter> entry : params.entrySet()) {
            Parameter param = entry.getValue();
            CapturingGroup capture = param.getCapture();

            if ((last != null) && (last.getEnd() < capture.getStart()))
            {
               temp = temp.substring(capture.getStart() - (last.getEnd() + 1));
            }
            else if ((last == null) && (capture.getStart() > 0))
            {
               temp = temp.substring(capture.getStart());
            }

            StringBuilder segmentBuilder = new StringBuilder();
            segmentBuilder.append('(');
            segmentBuilder.append(param.getPattern());
            segmentBuilder.append(')');

            Pattern segmentPattern = Pattern.compile(segmentBuilder.toString() + ".*");
            Matcher segmentMatcher = segmentPattern.matcher(temp);

            if (segmentMatcher.matches())
            {
               String value = segmentMatcher.group(1);
               values.put(param, value);
               temp = temp.substring(segmentMatcher.end(1));
            }

            last = capture;
         }
         return values;
      }
      return null;
   }

   public Parameter getParameter(final String name)
   {
      return params.get(name);
   }

   public Map<String, List<Object>> extractBoundValues(final HttpServletRewrite event, final EvaluationContext context)
   {
      Map<String, List<Object>> result = new LinkedHashMap<String, List<Object>>();

      for (Entry<String, Parameter> entry : getParameters().entrySet()) {
         String name = entry.getKey();
         Parameter value = entry.getValue();

         // TODO need to do lots of error checking and handling here
         for (ParameterBinding binding : value.getBindings()) {
            Object boundValue = binding.extractBoundValue(event, context);
            Maps.addListValue(result, name, boundValue);
         }

         for (ParameterBinding binding : value.getOptionalBindings()) {
            Object boundValue = binding.extractBoundValue(event, context);
            Maps.addListValue(result, name, boundValue);
         }
      }
      return result;
   }
}
