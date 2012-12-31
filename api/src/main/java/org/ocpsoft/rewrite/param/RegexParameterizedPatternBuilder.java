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
import org.ocpsoft.rewrite.util.ParseTools.CaptureType;
import org.ocpsoft.rewrite.util.ParseTools.CapturingGroup;

/**
 * An {@link org.ocpsoft.rewrite.param.Parameterized} regular expression {@link Pattern}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class RegexParameterizedPatternBuilder implements ParameterizedPatternBuilder
{
   private interface Transposition
   {
      public Bindable<?> getBindable(RegexGroup capture);
   }

   private static final String DEFAULT_PARAMETER_PATTERN = ".*";
   private String pattern;
   private final char[] chars;
   private final List<RegexGroup> groups = new ArrayList<RegexGroup>();
   private final ParameterStore<ParameterizedPatternBuilderParameter> parameters = new ParameterStore<ParameterizedPatternBuilderParameter>();

   private RegexParameterizedPatternParser parser = null;

   RegexParameterizedPatternBuilder(String pattern, RegexParameterizedPatternParser parser)
   {
      this(pattern);
      this.parser = parser;
   }

   /**
    * Create a new {@link RegexParameterizedPatternBuilder} instance with the default
    * {@link org.ocpsoft.rewrite.bind.parse.CaptureType#BRACE} and parameter compiledPattern of ".*".
    */
   public RegexParameterizedPatternBuilder(final String pattern)
   {
      this(CaptureType.BRACE, DEFAULT_PARAMETER_PATTERN, pattern);
   }

   /**
    * Create a new {@link RegexParameterizedPatternBuilder} instance with the default {@link CaptureType#BRACE}.
    */
   public RegexParameterizedPatternBuilder(final String parameterPattern, final String pattern)
   {
      this(CaptureType.BRACE, parameterPattern, pattern);
   }

   /**
    * Create a new {@link RegexParameterizedPatternBuilder} instance with the default parameter regex of ".*".
    */
   public RegexParameterizedPatternBuilder(final CaptureType type, final String pattern)
   {
      this(type, DEFAULT_PARAMETER_PATTERN, pattern);
   }

   /**
    * Create a new {@link RegexParameterizedPatternBuilder} instance.
    */
   public RegexParameterizedPatternBuilder(final CaptureType type, final String parameterPattern, final String pattern)
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
                        new ParameterizedPatternBuilderParameter(this, parameterName).matches(parameterPattern));

               break;

            default:
               break;
            }

            cursor++;
         }
      }
   }

   @Override
   public Map<String, ParameterizedPatternBuilderParameter> getParameterMap()
   {
      return Collections.unmodifiableMap(parameters);
   }

   @Override
   public String build(final Rewrite event, final EvaluationContext context,
            final Map<String, ? extends Bindable<?>> parameters)
   {
      return build(extractBoundValues(event, context, new Transposition() {
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
    * Use this expression to build a {@link String} from the given compiledPattern. Extract needed values from the
    * {@link EvaluationContext}.
    */
   public String build(final Rewrite event, final EvaluationContext context)
   {
      return build(extractBoundValues(event, context, new Transposition() {
         @Override
         @SuppressWarnings("rawtypes")
         public Bindable<?> getBindable(RegexGroup capture)
         {
            return new DefaultBindable().bindsTo(Evaluation.property(capture.getName()));
         }
      }));
   }

   @Override
   public String build(final Object... values)
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
   public String build(final Map<String, List<String>> values)
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

   private String applyTransforms(final Rewrite event, final EvaluationContext context,
            ParameterizedPatternBuilderParameter param, String value)
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
   private Map<String, List<String>> extractBoundValues(final Rewrite event, final EvaluationContext context,
            final Transposition transpose)
   {
      Map<String, List<String>> result = new LinkedHashMap<String, List<String>>();

      for (RegexGroup group : groups)
      {
         List<Object> values = Bindings.performRetrieval(event, context, transpose.getBindable(group));

         for (Object boundValue : values) {

            String name = group.getName();
            if (boundValue.getClass().isArray())
            {
               for (Object temp : (Object[]) boundValue)
               {
                  Maps.addListValue(result, name,
                           applyTransforms(event, context, parameters.get(name)
                                    , temp == null ? null : temp.toString()));
               }
            }
            else
            {
               Maps.addListValue(result, name,
                        applyTransforms(event, context, parameters.get(name),
                                 boundValue == null ? null : boundValue.toString()));
            }
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
   public ParameterizedPatternBuilderParameter where(String param)
   {
      return parameters.get(param);
   }

   @Override
   public ParameterizedPatternBuilderParameter getParameter(String param)
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

   @Override
   public String getPattern()
   {
      return pattern;
   }

   @Override
   public ParameterizedPatternParser getParser()
   {
      if (parser == null)
      {
         parser = new RegexParameterizedPatternParser(pattern, this);
      }
      return parser;
   }
}
