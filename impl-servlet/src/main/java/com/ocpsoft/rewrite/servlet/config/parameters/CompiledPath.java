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
package com.ocpsoft.rewrite.servlet.config.parameters;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ocpsoft.rewrite.servlet.config.Path;
import com.ocpsoft.rewrite.servlet.parse.CaptureType;
import com.ocpsoft.rewrite.servlet.parse.CapturingGroup;
import com.ocpsoft.rewrite.servlet.parse.ParseTools;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class CompiledPath
{
   private Pattern pattern;
   private final char[] chars;
   private final Map<String, PathParameter> params = new LinkedHashMap<String, PathParameter>();

   public CompiledPath(final Path path, final String pattern)
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

               params.put(new String(group.getCaptured()), new PathParameter(path, group));
               break;

            default:
               break;
            }

            cursor++;
         }
      }
   }

   public Map<String, PathParameter> getParameters()
   {
      return params;
   }

   public boolean matches(final String path)
   {
      if (pattern == null)
      {
         StringBuilder patternBuilder = new StringBuilder();

         CapturingGroup last = null;
         for (Entry<String, PathParameter> entry : params.entrySet()) {
            PathParameter param = entry.getValue();
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
   public Map<PathParameter, String> parseEncoded(final String path)
   {
      Map<PathParameter, String> values = new LinkedHashMap<PathParameter, String>();

      String temp = path;
      if (matches(path))
      {
         CapturingGroup last = null;
         for (Entry<String, PathParameter> entry : params.entrySet()) {
            PathParameter param = entry.getValue();
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

   public PathParameter getParameter(final String name)
   {
      return params.get(name);
   }
}
