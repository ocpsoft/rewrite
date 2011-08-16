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
package com.ocpsoft.rewrite.bind;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ocpsoft.rewrite.bind.parse.CaptureType;
import com.ocpsoft.rewrite.bind.parse.CapturingGroup;
import com.ocpsoft.rewrite.bind.parse.ParseTools;
import com.ocpsoft.rewrite.bind.util.Maps;
import com.ocpsoft.rewrite.context.EvaluationContext;
import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.param.Parameter;
import com.ocpsoft.rewrite.param.Parameterized;

/**
 * An {@link Parameterized} regular expression {@link Pattern}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ParameterizedPattern
{
   private static final String DEFAULT_PARAMETER_PATTERN = ".*";
   private Pattern pattern;
   private final char[] chars;
   private final Map<String, RegexParameter> params = new LinkedHashMap<String, RegexParameter>();

   /**
    * Create a new {@link ParameterizedPattern} instance with the default {@link CaptureType#BRACE} and parameter
    * pattern of ".*".
    */
   public ParameterizedPattern(final String pattern)
   {
      this(CaptureType.BRACE, DEFAULT_PARAMETER_PATTERN, pattern);
   }

   /**
    * Create a new {@link ParameterizedPattern} instance with the default {@link CaptureType#BRACE}.
    */
   public ParameterizedPattern(final String parameterPattern, final String pattern)
   {
      this(CaptureType.BRACE, parameterPattern, pattern);
   }

   /**
    * Create a new {@link ParameterizedPattern} instance with the default parameter regex of ".*".
    */
   public ParameterizedPattern(final CaptureType type, final String pattern)
   {
      this(type, DEFAULT_PARAMETER_PATTERN, pattern);
   }

   /**
    * Create a new {@link ParameterizedPattern} instance.
    */
   public ParameterizedPattern(final CaptureType type, final String parameterPattern, final String pattern)
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
               CapturingGroup group = ParseTools.balancedCapture(chars, startPos, chars.length - 1, type);
               cursor = group.getEnd();

               params.put(new String(group.getCaptured()), new RegexParameter(group).matches(parameterPattern));
               break;

            default:
               break;
            }

            cursor++;
         }
      }
   }

   /**
    * Get all {@link Parameter} instances detected during expression parsing.
    */
   @SuppressWarnings({ "unchecked", "rawtypes" })
   public Map<String, Parameter<String>> getParameters()
   {
      return (Map) params;
   }

   /**
    * Use this expression to build a {@link String} from the given pattern and values.
    */
   public String build(final Rewrite event, final EvaluationContext context)
   {
      return build(extractBoundValues(event, context));
   }

   /**
    * Use this expression to build a {@link String} from the given pattern and values. Enforces that the number of keys
    * passed must equal the number of expression parameters.
    */
   public String build(final Map<String, List<Object>> values)
   {

      if ((values == null) || (params.size() != values.size()))
      {
         throw new IllegalArgumentException("Must supply [" + params.size() + "] values to build path.");
      }

      return buildUnsafe(values);
   }

   /**
    * Use this expression to build a {@link String} from the given pattern and values.
    */
   public String buildUnsafe(final Map<String, List<Object>> values)
   {
      StringBuilder builder = new StringBuilder();
      CapturingGroup last = null;
      for (Entry<String, RegexParameter> entry : params.entrySet())
      {
         RegexParameter param = entry.getValue();
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

   /**
    * Return true if this expression matches the given {@link String}.
    */
   public boolean matches(final String value)
   {
      if (pattern == null)
      {
         StringBuilder patternBuilder = new StringBuilder();

         CapturingGroup last = null;
         for (Entry<String, RegexParameter> entry : params.entrySet())
         {
            RegexParameter param = entry.getValue();
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

      return pattern.matcher(value).matches();
   }

   /**
    * Parses the given string if it matches this expression. Returns a {@link Parameter}-value map of parsed values.
    */
   public Map<Parameter<String>, String[]> parseEncoded(final String path)
   {
      Map<Parameter<String>, String[]> values = new LinkedHashMap<Parameter<String>, String[]>();

      String temp = path;
      if (matches(path))
      {
         CapturingGroup last = null;
         for (Entry<String, RegexParameter> entry : params.entrySet())
         {
            RegexParameter param = entry.getValue();
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

            // TODO this needs to handle URLDecoding
            if (segmentMatcher.matches())
            {
               String value = segmentMatcher.group(1);
               Maps.addArrayValue(values, param, value);
               temp = temp.substring(segmentMatcher.end(1));
            }

            last = capture;
         }
      }
      return values;
   }

   /**
    * Get the {@link Parameter} with the given name. Return null if no such {@link Parameter} exists.
    */
   public Parameter<String> getParameter(final String name)
   {
      return params.get(name);
   }

   /**
    * Extract bound values from configured {@link Binding} instances. Return a {@link Map} of the extracted key-value
    * pairs.
    */
   private Map<String, List<Object>> extractBoundValues(final Rewrite event, final EvaluationContext context)
   {
      Map<String, List<Object>> result = new LinkedHashMap<String, List<Object>>();

      for (Entry<String, RegexParameter> entry : params.entrySet())
      {
         String name = entry.getKey();
         Parameter<String> parameter = entry.getValue();

         List<Object> values = Bindings.performRetrieval(event, context, parameter);

         for (Object boundValue : values) {

            if (boundValue.getClass().isArray())
               for (Object temp : (Object[]) boundValue)
               {
                  Maps.addListValue(result, name, temp);
               }
            else
               Maps.addListValue(result, name, boundValue);
         }

      }
      return result;
   }

   /**
    * Get a {@link List} of all defined {@link Parameter} names.
    */
   public List<String> getParameterNames()
   {
      return new ArrayList<String>(params.keySet());
   }

   @Override
   public String toString()
   {
      return new String(chars);
   }

}
