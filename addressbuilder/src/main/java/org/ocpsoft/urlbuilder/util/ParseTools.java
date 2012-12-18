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
package org.ocpsoft.urlbuilder.util;

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
   public static CapturingGroup balancedCapture(final CharSequence path, final int startPos, final int endPos,
            final CaptureType type)
   {
      Assert.notNull(path, "Character input array must not be null.");
      Assert.assertTrue(startPos >= 0, "Start position must be greater than zero.");
      Assert.assertTrue(startPos < (path.length() - 1),
               "Start position must be at least one character before the array upper bound.");
      Assert.assertTrue(endPos > 0, "End position must be greater than zero.");
      Assert.assertTrue(endPos < path.length(), "End position must be less than or equal to the array upper bound.");
      Assert.assertTrue(startPos < endPos, "Start position must be less than end position.");

      Assert.assertTrue(path.charAt(startPos) == type.getBegin(),
               "Character at starting position was [" + path.charAt(startPos)
                        + "] but expected [" + type.getBegin() + "]");

      if ((startPos > 0) && (path.charAt(startPos - 1) == ESCAPE_CHAR))
      {
         if ((startPos == 1) || ((startPos > 1) && (path.charAt(startPos - 2) != ESCAPE_CHAR)))
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
         if (path.charAt(cursor) == term)
         {
            if (!isEscaped(path, cursor))
               depth--;
         }
         else if (path.charAt(cursor) == begin)
         {
            if (!isEscaped(path, cursor))
               depth++;
         }

         if (depth == 0)
         {
            break;
         }
         cursor++;
      }

      return new CapturingGroup(path, startPos, cursor);
   }

   /**
    * Return true if the character at the given cursor is escaped; otherwise, return false.
    */
   public static boolean isEscaped(final CharSequence path, final int cursor)
   {
      Assert.notNull(path, "Character input array must not be null.");
      Assert.assertTrue(cursor >= 0, "Start position must be greater than zero.");
      Assert.assertTrue(cursor < (path.length()),
               "Start position must be within the array upper bound.");

      if ((cursor > 0) && (path.charAt(cursor - 1) == ESCAPE_CHAR))
      {
         if ((cursor == 1) || ((cursor > 1) && (path.charAt(cursor - 2) != ESCAPE_CHAR)))
         {
            return true;
         }
      }
      return false;
   }
}
