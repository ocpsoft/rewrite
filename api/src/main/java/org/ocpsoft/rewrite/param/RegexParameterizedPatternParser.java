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
package org.ocpsoft.rewrite.param;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.util.Maps;
import org.ocpsoft.rewrite.util.ParseTools;
import org.ocpsoft.rewrite.util.ParseTools.CaptureType;
import org.ocpsoft.rewrite.util.ParseTools.CapturingGroup;

/**
 * An {@link org.ocpsoft.rewrite.param.Parameterized} regular expression {@link Pattern}.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class RegexParameterizedPatternParser implements ParameterizedPatternParser
{
   private static final String DEFAULT_PARAMETER_PATTERN = ".*";
   private Pattern compiledPattern;
   private final String pattern;
   private final char[] chars;
   private final List<RegexGroup> groups = new ArrayList<RegexGroup>();
   private final ParameterStore<ParameterizedPatternParameter> parameters = new ParameterStore<ParameterizedPatternParameter>();
   private RegexParameterizedPatternBuilder builder;

   RegexParameterizedPatternParser(String pattern, RegexParameterizedPatternBuilder builder)
   {
      this(pattern);
      this.builder = builder;
   }

   /**
    * Create a new {@link RegexParameterizedPatternParser} instance with the default
    * {@link org.ocpsoft.rewrite.bind.parse.CaptureType#BRACE} and parameter pattern of ".*".
    */
   public RegexParameterizedPatternParser(final String pattern)
   {
      this(CaptureType.BRACE, DEFAULT_PARAMETER_PATTERN, pattern);
   }

   /**
    * Create a new {@link RegexParameterizedPatternParser} instance with the default {@link CaptureType#BRACE}.
    */
   public RegexParameterizedPatternParser(final String parameterPattern, final String pattern)
   {
      this(CaptureType.BRACE, parameterPattern, pattern);
   }

   /**
    * Create a new {@link RegexParameterizedPatternParser} instance with the default parameter regex of ".*".
    */
   public RegexParameterizedPatternParser(final CaptureType type, final String pattern)
   {
      this(type, DEFAULT_PARAMETER_PATTERN, pattern);
   }

   /**
    * Create a new {@link RegexParameterizedPatternParser} instance.
    */
   public RegexParameterizedPatternParser(final CaptureType type, final String parameterPattern, final String pattern)
   {
      Assert.notNull(pattern, "Pattern must not be null");

      this.pattern = pattern;
      chars = pattern.toCharArray();

      if (chars.length > 0)
      {
         int parameterIndex = 0;
         int cursor = 0;
         while (cursor < chars.length)
         {
            switch (chars[cursor])
            {
            case '{':
               int startPos = cursor;
               CapturingGroup group = ParseTools.balancedCapture(chars, startPos, chars.length - 1, type);
               cursor = group.getEnd();

               groups.add(new RegexGroup(group, parameterIndex++));
               String parameterName = new String(group.getCaptured());
               parameters.where(parameterName,
                        new ParameterizedPatternParameter(parameterName).matches(parameterPattern));

               break;

            default:
               break;
            }

            cursor++;
         }
      }
   }

   @Override
   public boolean matches(final Rewrite event, final EvaluationContext context, final String value)
   {
      Matcher matcher = getMatcher(value);
      boolean result = matcher.matches();
      if (result == true)
      {
         int group = 1;
         PARAMS: for (RegexGroup param : groups)
         {
            String matched = matcher.group(group++);

            for (Constraint<String> c : parameters.get(param.getName()).getConstraints()) {
               if (!c.isSatisfiedBy(event, context, matched))
               {
                  result = false;
                  break PARAMS;
               }
            }
         }
      }

      return result;
   }

   private Matcher getMatcher(final String value)
   {
      if (compiledPattern == null)
      {
         StringBuilder patternBuilder = new StringBuilder();

         CapturingGroup last = null;
         for (RegexGroup param : groups)
         {
            CapturingGroup capture = param.getCapture();

            if ((last != null) && (last.getEnd() < capture.getStart() - 1))
            {
               patternBuilder.append(new char[] { '\\', 'Q' });
               patternBuilder.append(Arrays.copyOfRange(chars, last.getEnd() + 1, capture.getStart()));
               patternBuilder.append(new char[] { '\\', 'E' });
            }
            else if ((last == null) && (capture.getStart() > 0))
            {
               patternBuilder.append(new char[] { '\\', 'Q' });
               patternBuilder.append(Arrays.copyOfRange(chars, 0, capture.getStart()));
               patternBuilder.append(new char[] { '\\', 'E' });
            }

            patternBuilder.append('(');
            patternBuilder.append(parameters.get(param.getName()).getPattern());
            patternBuilder.append(')');

            last = capture;
         }

         if ((last != null) && (last.getEnd() < chars.length))
         {
            patternBuilder.append(Arrays.copyOfRange(chars, last.getEnd() + 1, chars.length));
         }
         else if (last == null)
         {
            patternBuilder.append(new char[] { '\\', 'Q' });
            patternBuilder.append(chars);
            patternBuilder.append(new char[] { '\\', 'E' });
         }

         compiledPattern = Pattern.compile(patternBuilder.toString());
      }

      Matcher matcher = compiledPattern.matcher(value);
      return matcher;
   }

   @Override
   public Map<ParameterizedPatternParameter, String[]> parse(String value)
   {
      Map<ParameterizedPatternParameter, String[]> values = new LinkedHashMap<ParameterizedPatternParameter, String[]>();

      Matcher matcher = getMatcher(value);
      if (matcher.matches())
      {
         for (RegexGroup group : groups) {
            Maps.addArrayValue(values, parameters.get(group.getName()), matcher.group(group.getIndex() + 1));
         }
      }
      return values;
   }

   @Override
   public Map<ParameterizedPatternParameter, String[]> parse(final Rewrite event,
            final EvaluationContext context,
            final String value)
   {
      Map<ParameterizedPatternParameter, String[]> values = new LinkedHashMap<ParameterizedPatternParameter, String[]>();

      Matcher matcher = getMatcher(value);
      if (matcher.matches())
      {
         for (RegexGroup group : groups) {
            String capturedValue = applyTransforms(event, context, parameters.get(group.getName()),
                     matcher.group(group.getIndex() + 1));
            Maps.addArrayValue(values, parameters.get(group.getName()), capturedValue);
         }
      }
      return values;
   }

   private String applyTransforms(final Rewrite event, final EvaluationContext context,
            ParameterizedPatternParameter param, String value)
   {
      String result = value;
      for (Transform<String> t : param.getTransforms()) {
         result = t.transform(event, context, value);
      }
      return result;
   }

   @Override
   public String toString()
   {
      return new String(chars);
   }

   class RegexGroup
   {
      private final CapturingGroup capture;
      private final int index;

      public RegexGroup(final CapturingGroup capture, int index)
      {
         this.capture = capture;
         this.index = index;
      }

      public int getIndex()
      {
         return index;
      }

      public String getName()
      {
         return new String(capture.getCaptured());
      }

      public CapturingGroup getCapture()
      {
         return capture;
      }

      @Override
      public String toString()
      {
         return "RegexParameter [name=" + getName() + ", capture=" + capture + "]";
      }
   }

   @Override
   public String getPattern()
   {
      return compiledPattern.pattern();
   }

   @Override
   public ParameterizedPatternBuilder getBuilder()
   {
      if (builder == null)
      {
         builder = new RegexParameterizedPatternBuilder(pattern, this);
      }
      return builder;
   }

   @Override
   public ParameterStore<ParameterizedPatternParameter> getParameterStore()
   {
      return parameters;
   }
}
