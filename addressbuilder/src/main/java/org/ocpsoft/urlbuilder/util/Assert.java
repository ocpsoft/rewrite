/*
 * Copyright 2012 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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
 * Utility methods for making precondition/postcondition assertions.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public final class Assert
{
   private Assert()
   {}

   /**
    * Throw an exception if the given {@link Object} is not null.
    */
   public static void isNull(final Object object, final String message) throws IllegalArgumentException
   {
      if (object != null)
      {
         throw new IllegalArgumentException(message);
      }
   }

   /**
    * Throw an exception if the given {@link Object} is null.
    */
   public static void notNull(final Object object, final String message) throws IllegalArgumentException
   {
      if (object == null)
      {
         throw new IllegalArgumentException(message);
      }
   }

   /**
    * Throw an exception if the given value is not true.
    */
   public static void assertTrue(final boolean value, final String message) throws IllegalArgumentException
   {
      if (value != true)
      {
         throw new IllegalArgumentException(message);
      }
   }

   /**
    * Throw an exception if the given value is not false.
    */
   public static void assertFalse(final boolean value, final String message) throws IllegalArgumentException
   {
      if (value != false)
      {
         throw new IllegalArgumentException(message);
      }
   }
}