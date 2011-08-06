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
package com.ocpsoft.rewrite.servlet.parse;

import java.util.Arrays;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class CapturingGroup
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
