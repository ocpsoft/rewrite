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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ocpsoft.common.util.Assert;
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
public class RegexParameterizedPatternParser implements ParameterizedPatternParser
{
   private static final char[] REGEX_ESCAPE_END = new char[] { '\\', 'E' };
   private static final char[] REGEX_ESCAPE_BEGIN = new char[] { '\\', 'Q' };
   private static final String DEFAULT_PARAMETER_PATTERN = ".*";
   private Pattern compiledPattern;
   private final String pattern;
   private final char[] chars;
   private final List<RegexGroup> groups = new ArrayList<RegexGroup>();
   private RegexParameterizedPatternBuilder builder;
   private String defaultParameterPattern;
   private ParameterStore store;
   private CaptureType type;

   RegexParameterizedPatternParser(RegexParameterizedPatternBuilder builder,
            String defaultParameterPattern, String pattern)
   {
      this(defaultParameterPattern, pattern);
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
   public RegexParameterizedPatternParser(final CaptureType type, final String defaultParameterPattern,
            final String pattern)
   {
      Assert.notNull(pattern, "Pattern must not be null");

      this.defaultParameterPattern = defaultParameterPattern;
      this.pattern = pattern;
      this.chars = pattern.toCharArray();
      this.type = type;
      this.groups.addAll(getGroups(type, chars));
   }

   protected static List<RegexGroup> getGroups(final CaptureType type, final char[] chars)
   {
      List<RegexGroup> groups = new ArrayList<RegexGroup>();
      int cursor = 0;
      try {

         if (chars.length > 0)
         {
            int parameterIndex = 0;
            while (cursor < chars.length)
            {
               char c = chars[cursor];
               if (!ParseTools.isEscaped(chars, cursor))
               {
                  if (c == '{')
                  {
                     int startPos = cursor;
                     CapturingGroup group = ParseTools.balancedCapture(chars, startPos, chars.length - 1, type);
                     cursor = group.getEnd();

                     groups.add(new RegexGroup(group, parameterIndex++));
                  }
                  else if (c == '\\')
                  {
                     if (chars.length - 1 > cursor)
                     {
                        if (chars[cursor + 1] != type.getBegin()
                                 && chars[cursor + 1] != type.getEnd()
                                 && chars[cursor + 1] != '\\')
                        {
                           throw new ParameterizedPatternSyntaxException("Illegal escape sequence at index " + cursor,
                                    new String(chars), cursor);
                        }
                     }
                     else if (chars.length - 1 == cursor)
                     {
                        throw new ParameterizedPatternSyntaxException(
                                 "Illegal partial escape sequence at index [" + cursor + "] of [" + new String(chars)
                                          + "]", new String(chars), cursor);
                     }
                  }
               }

               cursor++;
            }
         }
      }
      catch (ParameterizedPatternSyntaxException e)
      {
         throw e;
      }
      catch (Exception e)
      {
         throw new ParameterizedPatternSyntaxException("Error parsing parameterized pattern due to: " + e.getMessage(),
                  new String(chars), cursor);
      }
      return groups;
   }

   @Override
   public ParameterizedPatternResult parse(final String value)
   {
      return new RegexParameterizedPatternMatchResult(groups, getCompiledPattern(store).matcher(value));
   }

   public Pattern getCompiledPattern(ParameterStore store)
   {
      if (compiledPattern == null)
      {
         StringBuilder patternBuilder = new StringBuilder();

         CapturingGroup last = null;
         for (RegexGroup group : groups)
         {
            CapturingGroup capture = group.getCapture();

            if ((last != null) && (last.getEnd() < capture.getStart() - 1))
            {
               patternBuilder.append(REGEX_ESCAPE_BEGIN);
               String literal = String.valueOf(Arrays.copyOfRange(chars, last.getEnd() + 1, capture.getStart()));
               patternBuilder.append(unescape(literal));
               patternBuilder.append(REGEX_ESCAPE_END);
            }
            else if ((last == null) && (capture.getStart() > 0))
            {
               patternBuilder.append(REGEX_ESCAPE_BEGIN);
               String literal = String.valueOf(Arrays.copyOfRange(chars, 0, capture.getStart()));
               patternBuilder.append(unescape(literal));
               patternBuilder.append(REGEX_ESCAPE_END);
            }

            patternBuilder.append('(');

            StringBuilder parameterPatternBuilder = new StringBuilder();
            if (store != null)
            {
               if (store.contains(group.getName()))
               {
                  Iterator<Constraint<String>> iterator = store.get(group.getName()).getConstraints().iterator();
                  while (iterator.hasNext())
                  {
                     Constraint<String> constraint = iterator.next();
                     if (constraint instanceof RegexConstraint)
                     {
                        if (iterator.hasNext())
                           parameterPatternBuilder.append("(?=");
                        else
                           parameterPatternBuilder.append("(?:");
                        parameterPatternBuilder.append(sanitizePattern(constraint));
                        parameterPatternBuilder.append(")");
                     }
                  }
               }
            }

            if (parameterPatternBuilder.length() > 0)
               patternBuilder.append(parameterPatternBuilder);
            else
               patternBuilder.append(defaultParameterPattern);

            patternBuilder.append(')');

            last = capture;
         }

         if ((last != null) && (last.getEnd() < chars.length))
         {
            patternBuilder.append(REGEX_ESCAPE_BEGIN);
            String literal = String.valueOf(Arrays.copyOfRange(chars, last.getEnd() + 1, chars.length));
            patternBuilder.append(unescape(literal));
            patternBuilder.append(REGEX_ESCAPE_END);
         }
         else if (last == null)
         {
            patternBuilder.append(REGEX_ESCAPE_BEGIN);
            patternBuilder.append(unescape(String.valueOf(chars)));
            patternBuilder.append(REGEX_ESCAPE_END);
         }

         compiledPattern = Pattern.compile(patternBuilder.toString());
      }
      return compiledPattern;
   }

   private String sanitizePattern(Constraint<String> constraint)
   {
      StringBuilder result = new StringBuilder();
      String regex = ((RegexConstraint) constraint).getPattern();
      char[] pattern = regex.toCharArray();
      for (int i = 0; i < pattern.length; i++)
      {
         result.append(pattern[i]);
         if (pattern[i] == '(' && !ParseTools.isEscaped(pattern, i))
         {
            if (pattern[i + 1] != '?')
            {
               result.append("?:");
            }
         }
      }
      return result.toString();
   }

   private String unescape(String literal)
   {
      String result = literal.replace("\\\\", "\\");
      result = result.replace("\\" + type.getBegin(), String.valueOf(type.getBegin()));
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
   public ParameterizedPatternBuilder getBuilder()
   {
      if (builder == null)
      {
         builder = new RegexParameterizedPatternBuilder(pattern, this);
         builder.setParameterStore(store);
      }
      return builder;
   }

   static class RegexGroup
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

   private static class RegexParameterizedPatternMatchResult implements ParameterizedPatternResult
   {
      private Matcher matcher;
      private List<RegexGroup> groups;
      private Boolean matched;

      public RegexParameterizedPatternMatchResult(List<RegexGroup> groups, Matcher matcher)
      {
         this.groups = groups;
         this.matcher = matcher;
      }

      /**
       * Returns <code>true</code> if the {@link ParameterizedPattern} matched the input value, <code>false</code> if
       * not.
       */
      @Override
      public boolean matches()
      {
         if (matched == null)
         {
            matched = matcher.matches();
         }
         return matched;
      }

      @Override
      public Map<Parameter<?>, String> getParameters(EvaluationContext context)
      {
         Map<Parameter<?>, String> values = new LinkedHashMap<Parameter<?>, String>();
         ParameterStore store = DefaultParameterStore.getInstance(context);

         if (matcher.matches())
         {
            for (RegexGroup group : groups)
            {
               values.put(store.get(group.getName()), matcher.group(group.getIndex() + 1));
            }
         }
         return values;
      }

      @Override
      public boolean isValid(Rewrite event, EvaluationContext context)
      {
         if (matches())
         {
            ParameterStore store = DefaultParameterStore.getInstance(context);

            int index = 1;
            for (RegexGroup group : groups)
            {
               String value = matcher.group(index++);
               Parameter<?> param = store.get(group.getName());

               ParameterValueStore valueStore = DefaultParameterValueStore.getInstance(context);
               if (!valueStore.isValid(event, context, param, value))
               {
                  return false;
               }
            }

            return true;
         }
         return false;
      }

      @Override
      public boolean submit(Rewrite event, EvaluationContext context)
      {
         if (matches())
         {
            ParameterStore store = DefaultParameterStore.getInstance(context);

            int index = 1;
            for (RegexGroup group : groups)
            {
               String value = matcher.group(index++);
               Parameter<?> param = store.get(group.getName());

               ParameterValueStore valueStore = DefaultParameterValueStore.getInstance(context);
               if (!valueStore.submit(event, context, param, value))
               {
                  return false;
               }
            }

            return true;
         }
         return false;
      }

   }
}
