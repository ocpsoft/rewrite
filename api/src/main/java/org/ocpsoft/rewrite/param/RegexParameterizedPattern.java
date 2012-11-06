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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.bind.Bindable;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.Bindings;
import org.ocpsoft.rewrite.bind.DefaultBindable;
import org.ocpsoft.rewrite.bind.Evaluation;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.util.Maps;
import org.ocpsoft.rewrite.util.ParseTools;
import org.ocpsoft.rewrite.util.ParseTools.CapturingGroup;

/**
 * An {@link org.ocpsoft.rewrite.param.Parameterized} regular expression {@link Pattern}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class RegexParameterizedPattern implements ParameterizedPattern
{
   public interface Transposition
   {
      public Bindable<?> getBindable(RegexGroup capture);
   }

   private static final String DEFAULT_PARAMETER_PATTERN = ".*";
   private Pattern pattern;
   private final char[] chars;
   private final List<RegexGroup> groups = new ArrayList<RegexGroup>();
   private final ParameterStore<PatternParameter> parameters = new ParameterStore<PatternParameter>();

   /**
    * Create a new {@link RegexParameterizedPattern} instance with the default
    * {@link org.ocpsoft.rewrite.bind.parse.CaptureType#BRACE} and parameter pattern of ".*".
    */
   public RegexParameterizedPattern(final String pattern)
   {
      this(CaptureType.BRACE, DEFAULT_PARAMETER_PATTERN, pattern);
   }

   /**
    * Create a new {@link RegexParameterizedPattern} instance with the default {@link CaptureType#BRACE}.
    */
   public RegexParameterizedPattern(final String parameterPattern, final String pattern)
   {
      this(CaptureType.BRACE, parameterPattern, pattern);
   }

   /**
    * Create a new {@link RegexParameterizedPattern} instance with the default parameter regex of ".*".
    */
   public RegexParameterizedPattern(final CaptureType type, final String pattern)
   {
      this(type, DEFAULT_PARAMETER_PATTERN, pattern);
   }

   /**
    * Create a new {@link RegexParameterizedPattern} instance.
    */
   public RegexParameterizedPattern(final CaptureType type, final String parameterPattern, final String pattern)
   {
      Assert.notNull(pattern, "Pattern must not be null");

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
               parameters.where(parameterName, new PatternParameter(this, parameterName).matches(parameterPattern));

               break;

            default:
               break;
            }

            cursor++;
         }
      }
   }

   @Override
   public Map<String, PatternParameter> getParameterMap()
   {
      return Collections.unmodifiableMap(parameters);
   }

   @Override
   public String build(final Rewrite event, final EvaluationContext context,
            final Map<String, ? extends Bindable<?>> parameters)
   {
      return buildUnsafe(extractBoundValues(event, context, new Transposition() {
         @Override
         @SuppressWarnings("rawtypes")
         public Bindable<?> getBindable(RegexGroup capture)
         {
            Bindable<?> bindable = parameters.get(capture.getName());
            if (bindable != null) {
               return bindable;
            }
            return new DefaultBindable().bindsTo(Evaluation.property(capture.getName()));
         }
      }));
   }

   /**
    * Use this expression to build a {@link String} from the given pattern. Extract needed values from the
    * {@link EvaluationContext}.
    */
   public String build(final Rewrite event, final EvaluationContext context)
   {
      return buildUnsafe(extractBoundValues(event, context, new Transposition() {
         @Override
         @SuppressWarnings("rawtypes")
         public Bindable<?> getBindable(RegexGroup capture)
         {
            return new DefaultBindable().bindsTo(Evaluation.property(capture.getName()));
         }
      }));
   }

   @Override
   public String buildUnsafe(final Object... values)
   {
      if ((values == null) || (groups.size() != values.length))
      {
         throw new IllegalArgumentException("Must supply [" + groups.size() + "] values to build output string.");
      }

      StringBuilder builder = new StringBuilder();
      CapturingGroup last = null;

      int index = 0;
      for (RegexGroup param : groups)
      {
         CapturingGroup capture = param.getCapture();

         if ((last != null) && (last.getEnd() < capture.getStart()))
         {
            builder.append(Arrays.copyOfRange(chars, last.getEnd() + 1, capture.getStart()));
         }
         else if ((last == null) && (capture.getStart() > 0))
         {
            builder.append(Arrays.copyOfRange(chars, 0, capture.getStart()));
         }

         builder.append(values[index++]);

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

   @Override
   public String buildUnsafe(final Map<String, List<Object>> values)
   {
      if ((values == null) || (groups.size() != values.size()))
      {
         throw new IllegalArgumentException("Must supply [" + groups.size() + "] values to build output string.");
      }

      StringBuilder builder = new StringBuilder();
      CapturingGroup last = null;
      Map<String, Integer> pointers = new LinkedHashMap<String, Integer>();

      for (RegexGroup param : groups)
      {
         CapturingGroup capture = param.getCapture();

         if ((last != null) && (last.getEnd() < capture.getStart()))
         {
            builder.append(Arrays.copyOfRange(chars, last.getEnd() + 1, capture.getStart()));
         }
         else if ((last == null) && (capture.getStart() > 0))
         {
            builder.append(Arrays.copyOfRange(chars, 0, capture.getStart()));
         }

         String name = param.getName();
         int index = pointers.get(name) == null ? 0 : pointers.get(name);
         builder.append(Maps.getListValue(values, name, index));
         pointers.put(name, index++);

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
      if (pattern == null)
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

         pattern = Pattern.compile(patternBuilder.toString());
      }

      Matcher matcher = pattern.matcher(value);
      return matcher;
   }

   @Override
   public Map<PatternParameter, String[]> parse(String value)
   {
      Map<PatternParameter, String[]> values = new LinkedHashMap<PatternParameter, String[]>();

      Matcher matcher = getMatcher(value);
      if (matcher.matches())
      {
         for (RegexGroup group : groups) {
            Maps.addArrayValue(values, parameters.get(group.getName()), matcher.group(group.getIndex() + 1));
         }
      }
      return values;
   }

   public Map<PatternParameter, String[]> parse(final Rewrite event, final EvaluationContext context,
            final String value)
   {
      Map<PatternParameter, String[]> values = new LinkedHashMap<PatternParameter, String[]>();

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
            PatternParameter param, String value)
   {
      String result = value;
      for (Transform<String> t : param.getTransforms()) {
         result = t.transform(event, context, value);
      }
      return result;
   }

   /**
    * Extract bound values from configured {@link Binding} instances. Return a {@link Map} of the extracted key-value
    * pairs.
    */
   private Map<String, List<Object>> extractBoundValues(final Rewrite event, final EvaluationContext context,
            final Transposition transpose)
   {
      Map<String, List<Object>> result = new LinkedHashMap<String, List<Object>>();

      for (RegexGroup group : groups)
      {
         List<Object> values = Bindings.performRetrieval(event, context, transpose.getBindable(group));

         for (Object boundValue : values) {

            if (boundValue.getClass().isArray())
               for (Object temp : (Object[]) boundValue)
               {
                  Maps.addListValue(result, group.getName(), temp);
               }
            else
               Maps.addListValue(result, group.getName(), boundValue);
         }

      }
      return result;
   }

   @Override
   public List<String> getParameterNames()
   {
      ArrayList<String> result = new ArrayList<String>();
      for (RegexGroup group : groups) {
         result.add(group.getName());
      }
      return result;
   }

   @Override
   public String toString()
   {
      return new String(chars);
   }

   @Override
   public PatternParameter where(String param)
   {
      return parameters.get(param);
   }

   @Override
   public PatternParameter where(String param, Binding binding)
   {
      return where(param).bindsTo(binding);
   }

   @Override
   public PatternParameter getParameter(String param)
   {
      return where(param);
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

   public enum CaptureType
   {
      BRACE('{', '}'), BRACKET('[', ']'), PAREN('(', ')'), REGEX('/', '/');

      private char begin;
      private char end;

      private CaptureType(final char begin, final char end)
      {
         this.begin = begin;
         this.end = end;
      }

      public char getBegin()
      {
         return begin;
      }

      public char getEnd()
      {
         return end;
      }
   }
}
