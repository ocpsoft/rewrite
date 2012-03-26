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
package org.ocpsoft.rewrite.config;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ConditionBuilderTest
{

   @Test
   public void testTrueAndTrueIsTrue()
   {
      Assert.assertTrue(new True().and(new True()).evaluate(null, null));
   }

   @Test
   public void testTrueAndFalseIsFalse()
   {
      Assert.assertFalse(new True().and(new False()).evaluate(null, null));
   }

   @Test
   public void testFalseAndTrueIsFalse()
   {
      Assert.assertFalse(new False().and(new True()).evaluate(null, null));
   }

   @Test
   public void testFalseAndFalseIsFalse()
   {
      Assert.assertFalse(new False().and(new False()).evaluate(null, null));
   }

   @Test
   public void testTrueAndNotTrueIsFalse()
   {
      Assert.assertFalse(new True().andNot(new True()).evaluate(null, null));
   }

   @Test
   public void testTrueAndNotFalseIsTrue()
   {
      Assert.assertTrue(new True().andNot(new False()).evaluate(null, null));
   }

   @Test
   public void testTrueOrTrueIsTrue()
   {
      Assert.assertTrue(new True().or(new True()).evaluate(null, null));
   }

   @Test
   public void testTrueOrFalseIsTrue()
   {
      Assert.assertTrue(new True().or(new False()).evaluate(null, null));
   }

   @Test
   public void testFalseOrTrueIsTrue()
   {
      Assert.assertTrue(new False().or(new True()).evaluate(null, null));
   }

   @Test
   public void testFalseOrFalseIsFalse()
   {
      Assert.assertFalse(new False().or(new False()).evaluate(null, null));
   }

   @Test
   public void testTrueOrNotTrueIsTrue()
   {
      Assert.assertTrue(new True().orNot(new True()).evaluate(null, null));
   }

   @Test
   public void testTrueOrNotFalseIsTrue()
   {
      Assert.assertTrue(new True().orNot(new False()).evaluate(null, null));
   }

   @Test
   public void testFalseOrNotTrueIsFalse()
   {
      Assert.assertFalse(new False().orNot(new True()).evaluate(null, null));
   }

   @Test
   public void testFalseOrNotFalseIsTrue()
   {
      Assert.assertTrue(new False().orNot(new False()).evaluate(null, null));
   }
}
