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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.Bindings;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
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

   private static final String DEFAULT_PARAMETER_PATTERN = ".*";
   private final String pattern;
   private final char[] chars;
   private final List<RegexGroup> groups = new ArrayList<RegexGroup>();
   private String defaultParameterPattern;
   private ParameterStore parameters;

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
   public RegexParameterizedPatternBuilder(final CaptureType type, final String defaultParameterPattern,
            final String pattern)
   {
      Assert.notNull(pattern, "Pattern must not be null");

      this.defaultParameterPattern = defaultParameterPattern;

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

               break;

            default:
               break;
            }

            cursor++;
         }
      }
   }

   /**
    * Use this expression to build a {@link String} from the given compiledPattern. Extract needed values from the
    * {@link EvaluationContext}.
    */
   @Override
   public String build(final Rewrite event, final EvaluationContext context, final Transform<String> transform)
   {
      return build(extractBoundValues(event, context, transform));
   }

   @Override
   public String build(final List<Object> values)
   {
      if ((values == null && groups.size() != 0) || (groups.size() != values.size()))
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

         builder.append(values.get(index++));

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

      String result = builder.toString();

      // if (!getParser().matches(result))
      // {
      // throw new IllegalStateException("Generated a value that does not match constraints.");
      // }

      return result;
   }

   @Override
   public String build(final Map<String, Object> values)
   {
      if ((values == null) || (groups.size() != values.size()))
      {
         throw new IllegalArgumentException("Must supply [" + groups.size() + "] values to build output string.");
      }

      StringBuilder builder = new StringBuilder();
      CapturingGroup last = null;

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

         builder.append(values.get(param.getName()));

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

      String result = builder.toString();

//      if (!getParser().matches(result))
//      {
//         throw new IllegalStateException("Generated a value that does not match constraints.");
//      }

      return result;
   }

   /**
    * Extract bound values from configured {@link Binding} instances. Return a {@link Map} of the extracted key-value
    * pairs. Before storing the values in the map, this method applies the supplied {@link Transform} instance.
    */
   private Map<String, Object> extractBoundValues(final Rewrite event, final EvaluationContext context,
            Transform<String> transform)
   {
      Map<String, Object> result = new LinkedHashMap<String, Object>();

      for (RegexGroup group : groups)
      {
         Parameter<?> parameter = parameters.get(group.getName());
         Object value = Bindings.performRetrieval(event, context, parameter);

         if (value == null)
            throw new IllegalStateException("Required parameter [" + group.getName() + "] value was null.");

         result.put(group.getName(), transform.transform(event, context, value.toString()));
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
      return pattern;
   }

   @Override
   public ParameterizedPatternParser getParser()
   {
      if (parser == null)
      {
         parser = new RegexParameterizedPatternParser(this, defaultParameterPattern, pattern);
         parser.setParameterStore(parameters);
      }
      return parser;
   }

   @Override
   public Set<String> getRequiredParameterNames()
   {
      Set<String> result = new HashSet<String>();
      for (RegexGroup group : groups) {
         result.add(group.getName());
      }
      return result;
   }

   @Override
   public void setParameterStore(ParameterStore store)
   {
      this.parameters = store;
   }
}
