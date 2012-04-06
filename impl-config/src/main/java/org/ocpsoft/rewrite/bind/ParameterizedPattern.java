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
package org.ocpsoft.rewrite.bind;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.bind.parse.CaptureType;
import org.ocpsoft.rewrite.bind.parse.CapturingGroup;
import org.ocpsoft.rewrite.bind.parse.ParseTools;
import org.ocpsoft.rewrite.bind.util.Maps;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.Constraint;
import org.ocpsoft.rewrite.param.Parameter;
import org.ocpsoft.rewrite.param.Transform;

/**
 * An {@link org.ocpsoft.rewrite.param.Parameterized} regular expression {@link Pattern}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ParameterizedPattern
{
   public interface Transposition
   {
      public Bindable<?> getBindable(RegexCapture capture);
   }

   private static final String DEFAULT_PARAMETER_PATTERN = ".*";
   private Pattern pattern;
   private final char[] chars;
   private final Map<String, RegexCapture> params = new LinkedHashMap<String, RegexCapture>();

   /**
    * Create a new {@link ParameterizedPattern} instance with the default
    * {@link org.ocpsoft.rewrite.bind.parse.CaptureType#BRACE} and parameter pattern of ".*".
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

               String parameterName = new String(group.getCaptured());
               RegexCapture parameter = new RegexCapture(group,
                        parameterIndex++);
               parameter.matches(parameterPattern);
               params.put(parameterName, parameter);

               break;

            default:
               break;
            }

            cursor++;
         }
      }
   }

   /**
    * Get all {@link org.ocpsoft.rewrite.param.Parameter} instances detected during expression parsing.
    */
   public Map<String, RegexCapture> getParameters()
   {
      return params;
   }

   /**
    * Use this expression to build a {@link String} from the given pattern. Extract needed values from registered
    * {@link Binding} instances.
    */
   public String build(final Rewrite event, final EvaluationContext context,
            final Map<String, ? extends Bindable<?>> parameters)
   {
      return buildUnsafe(extractBoundValues(event, context, new Transposition() {
         @Override
         @SuppressWarnings("rawtypes")
         public Bindable<?> getBindable(RegexCapture capture)
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
         public Bindable<?> getBindable(RegexCapture capture)
         {
            return new DefaultBindable().bindsTo(Evaluation.property(capture.getName()));
         }
      }));
   }

   /**
    * Use this expression's pattern to build a {@link String} from the given values. Enforces that the number of values
    * passed must equal the number of expression parameters.
    */
   public String buildUnsafe(final Object... values)
   {
      if ((values == null) || (params.size() != values.length))
      {
         throw new IllegalArgumentException("Must supply [" + params.size() + "] values to build output string.");
      }

      StringBuilder builder = new StringBuilder();
      CapturingGroup last = null;

      int index = 0;
      for (Entry<String, RegexCapture> entry : params.entrySet())
      {
         RegexCapture param = entry.getValue();
         CapturingGroup capture = param.getCapture();

         if ((last != null) && (last.getEnd() < capture.getStart()))
         {
            builder.append(Arrays.copyOfRange(chars, last.getEnd() + 1, capture.getStart()));
         }
         else if ((last == null) && (capture.getStart() > 0))
         {
            builder.append(Arrays.copyOfRange(chars, 0, capture.getStart()));
         }

         builder.append(values[index]);

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
    * Use this expression to build a {@link String} from the given pattern and values.
    */
   public String buildUnsafe(final Map<String, List<Object>> values)
   {
      if ((values == null) || (params.size() != values.size()))
      {
         throw new IllegalArgumentException("Must supply [" + params.size() + "] values to build output string.");
      }

      StringBuilder builder = new StringBuilder();
      CapturingGroup last = null;
      Map<String, Integer> pointers = new LinkedHashMap<String, Integer>();

      for (Entry<String, RegexCapture> entry : params.entrySet())
      {
         RegexCapture param = entry.getValue();
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

   /**
    * Return true if this expression matches the given {@link String}.
    */
   public boolean matches(final Rewrite event, final EvaluationContext context, final String value)
   {
      Matcher matcher = getMatcher(value);
      boolean result = matcher.matches();
      if (result == true)
      {
         int group = 1;
         PARAMS: for (Entry<String, RegexCapture> entry : params.entrySet())
         {
            RegexCapture param = entry.getValue();
            String matched = matcher.group(group++);
            for (Constraint<String> c : param.getConstraints()) {
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

   public Matcher getMatcher(final String value)
   {
      if (pattern == null)
      {
         StringBuilder patternBuilder = new StringBuilder();

         CapturingGroup last = null;
         for (Entry<String, RegexCapture> entry : params.entrySet())
         {
            RegexCapture param = entry.getValue();
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
            patternBuilder.append(new char[] { '\\', 'Q' });
            patternBuilder.append(chars);
            patternBuilder.append(new char[] { '\\', 'E' });
         }

         pattern = Pattern.compile(patternBuilder.toString());
      }

      Matcher matcher = pattern.matcher(value);
      return matcher;
   }

   /**
    * Parses the given string if it matches this expression. Returns a {@link org.ocpsoft.rewrite.param.Parameter}-value
    * map of parsed values.
    */
   public Map<RegexCapture, String[]> parse(final Rewrite event, final EvaluationContext context,
            final String path)
   {
      Map<RegexCapture, String[]> values = new LinkedHashMap<RegexCapture, String[]>();

      Matcher matcher = getMatcher(path);
      if (matcher.matches())
      {
         for (RegexCapture param : params.values()) {
            String capturedValue = applyTransforms(event, context, param, matcher.group(param.getIndex() + 1));
            Maps.addArrayValue(values, param, capturedValue);
         }
      }
      return values;
   }

   private String applyTransforms(final Rewrite event, final EvaluationContext context,
            RegexCapture param,
            String value)
   {
      String result = value;
      for (Transform<String> t : param.getTransforms()) {
         result = t.transform(event, context, value);
      }
      return result;
   }

   /**
    * Get the {@link org.ocpsoft.rewrite.param.Parameter} with the given name. Return null if no such
    * {@link org.ocpsoft.rewrite.param.Parameter} exists.
    */
   public RegexCapture getParameter(final String name)
   {
      return params.get(name);
   }

   /**
    * Extract bound values from configured {@link Binding} instances. Return a {@link Map} of the extracted key-value
    * pairs.
    */
   private Map<String, List<Object>> extractBoundValues(final Rewrite event, final EvaluationContext context,
            final Transposition transpose)
   {
      Map<String, List<Object>> result = new LinkedHashMap<String, List<Object>>();

      for (Entry<String, RegexCapture> entry : params.entrySet())
      {
         String name = entry.getKey();
         RegexCapture capture = entry.getValue();

         List<Object> values = Bindings.performRetrieval(event, context, transpose.getBindable(capture));

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
