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
package org.ocpsoft.rewrite.util;

import java.util.Arrays;

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.param.RegexParameterizedPattern.CaptureType;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public abstract class ParseTools
{
   private static final char ESCAPE_CHAR = '\\';

   /**
    * Return the index of the terminating character in the group, excluding markers.
    */
   public static CapturingGroup balancedCapture(final char[] chars, final int startPos, final int endPos,
            final CaptureType type)
   {
      Assert.notNull(chars, "Character input array must not be null.");
      Assert.assertTrue(startPos >= 0, "Start position must be greater than zero.");
      Assert.assertTrue(startPos < (chars.length - 1),
               "Start position must be at least one character before the array upper bound.");
      Assert.assertTrue(endPos > 0, "End position must be greater than zero.");
      Assert.assertTrue(endPos < chars.length, "End position must be less than or equal to the array upper bound.");
      Assert.assertTrue(startPos < endPos, "Start position must be less than end position.");

      Assert.assertTrue(chars[startPos] == type.getBegin(), "Character at starting position was [" + chars[startPos]
               + "] but expected [" + type.getBegin() + "]");

      if ((startPos > 0) && (chars[startPos - 1] == ESCAPE_CHAR))
      {
         if ((startPos == 1) || ((startPos > 1) && (chars[startPos - 2] != ESCAPE_CHAR)))
         {
            throw new IllegalArgumentException(
                     "Character at starting position is escaped, and cannot be used in capturing a group.");
         }
      }

      int depth = 1;

      char begin = type.getBegin();
      char term = type.getEnd();

      int cursor = startPos + 1;
      while ((cursor < endPos) && (depth > 0))
      {
         if (chars[cursor] == term)
         {
            if (!isEscaped(chars, cursor))
               depth--;
         }
         else if (chars[cursor] == begin)
         {
            if (!isEscaped(chars, cursor))
               depth++;
         }

         if (depth == 0)
         {
            break;
         }
         cursor++;
      }

      return new CapturingGroup(chars, startPos, cursor);
   }

   /**
    * Return true if the character at the given cursor is escaped; otherwise, return false.
    */
   public static boolean isEscaped(final char[] chars, final int cursor)
   {
      Assert.notNull(chars, "Character input array must not be null.");
      Assert.assertTrue(cursor >= 0, "Start position must be greater than zero.");
      Assert.assertTrue(cursor < (chars.length),
               "Start position must be within the array upper bound.");

      if ((cursor > 0) && (chars[cursor - 1] == ESCAPE_CHAR))
      {
         if ((cursor == 1) || ((cursor > 1) && (chars[cursor - 2] != ESCAPE_CHAR)))
         {
            return true;
         }
      }
      return false;
   }

   public static class CapturingGroup
   {
      private final char[] chars;
      private final int start;
      private final int end;

      public CapturingGroup(final char[] chars, final int start, final int end)
      {
         this.chars = chars;
         this.start = start;
         this.end = end;
      }

      public int getStart()
      {
         return start;
      }

      public int getEnd()
      {
         return end;
      }

      public char[] getCaptured()
      {
         return Arrays.copyOfRange(chars, start + 1, end);
      }

      @Override
      public String toString()
      {
         return "CapturingGroup [start=" + start + ", end=" + end + "]";
      }
   }
}
