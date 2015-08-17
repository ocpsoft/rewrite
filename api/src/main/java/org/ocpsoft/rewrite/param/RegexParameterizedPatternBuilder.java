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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.exception.ParameterizationException;
import org.ocpsoft.rewrite.param.RegexParameterizedPatternParser.RegexGroup;
import org.ocpsoft.rewrite.util.ParameterUtils;
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
   private final String defaultParameterPattern;
   private ParameterStore store;

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
      this.chars = pattern.toCharArray();
      this.groups.addAll(RegexParameterizedPatternParser.getGroups(type, chars));
   }

   @Override
   public String build(final Rewrite event, final EvaluationContext context)
            throws ParameterizationException
   {
      return build(event, context, null);
   }

   @Override
   public String build(final Rewrite event, final EvaluationContext context,
            final Transposition<String> transposition)
            throws ParameterizationException
   {
      return build(extractBoundValues(event, context, transposition));
   }

   @Override
   public String build(final List<Object> values) throws ParameterizationException
   {
      if ((values == null && groups.size() != 0) || (groups.size() != values.size()))
      {
         throw new ParameterizationException("Must supply [" + groups.size() + "] values to build output string.");
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
   public String build(final Map<String, Object> values) throws ParameterizationException
   {
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

         if (!values.containsKey(param.getName()))
            throw new ParameterizationException("No value supplied for parameter [" + param.getName()
                     + "] when building pattern [" + getPattern() + "].");

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

      // if (!getParser().matches(result))
      // {
      // throw new IllegalStateException("Generated a value that does not match constraints.");
      // }

      return result;
   }

   /**
    * Extract bound values from configured {@link Binding} instances. Return a {@link Map} of the extracted key-value
    * pairs. Before storing the values in the map, this method applies the supplied {@link Transposition} instance.
    */
   private Map<String, Object> extractBoundValues(final Rewrite event, final EvaluationContext context,
            Transposition<String> transposition)
   {
      Map<String, Object> result = new LinkedHashMap<String, Object>();

      for (RegexGroup group : groups)
      {
         Parameter<?> parameter = store.get(group.getName());
         Object value = null;

         // TODO TEST ME!!!
         if (context.getState().isEvaluating())
            value = ((ParameterValueStore) context.get(ParameterValueStore.class)).retrieve(parameter);

         if (value == null || context.getState().isPerforming())
         {
            Object retrieved = ParameterUtils.performRetrieval(event, context, parameter);
            if (retrieved != null)
               value = retrieved;
         }

         if (value == null)
            throw new ParameterizationException("The value of required parameter [" + group.getName() + "] was null.");

         if (transposition != null)
            value = transposition.transpose(event, context, value.toString());

         result.put(group.getName(), value);
      }
      return result;
   }

   @Override
   public String toString()
   {
      return new String(chars);
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
         parser.setParameterStore(store);
      }
      return parser;
   }

   @Override
   public Set<String> getRequiredParameterNames()
   {
      Set<String> result = new LinkedHashSet<String>();
      for (RegexGroup group : groups)
      {
         result.add(group.getName());
      }
      return result;
   }

   @Override
   public void setParameterStore(ParameterStore store)
   {
      this.store = store;
   }

   @Override
   public boolean isParameterComplete(Rewrite event, EvaluationContext context)
   {
      try
      {
         extractBoundValues(event, context, null);
         return true;
      }
      catch (ParameterizationException e)
      {
         return false;
      }
   }
}
