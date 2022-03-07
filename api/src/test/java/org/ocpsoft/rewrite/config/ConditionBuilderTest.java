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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ConditionBuilderTest
{

   @Test
   public void testTrueAndTrueIsTrue()
   {
      assertThat(new True().and(new True()).evaluate(null, null)).isTrue();
   }

   @Test
   public void testTrueAndFalseIsFalse()
   {
      assertThat(new True().and(new False()).evaluate(null, null)).isFalse();
   }

   @Test
   public void testFalseAndTrueIsFalse()
   {
      assertThat(new False().and(new True()).evaluate(null, null)).isFalse();
   }

   @Test
   public void testFalseAndFalseIsFalse()
   {
      assertThat(new False().and(new False()).evaluate(null, null)).isFalse();
   }

   @Test
   public void testTrueAndNotTrueIsFalse()
   {
      assertThat(new True().andNot(new True()).evaluate(null, null)).isFalse();
   }

   @Test
   public void testTrueAndNotFalseIsTrue()
   {
      assertThat(new True().andNot(new False()).evaluate(null, null)).isTrue();
   }

   @Test
   public void testTrueOrTrueIsTrue()
   {
      assertThat(new True().or(new True()).evaluate(null, null)).isTrue();
   }

   @Test
   public void testTrueOrFalseIsTrue()
   {
      assertThat(new True().or(new False()).evaluate(null, null)).isTrue();
   }

   @Test
   public void testFalseOrTrueIsTrue()
   {
      assertThat(new False().or(new True()).evaluate(null, null)).isTrue();
   }

   @Test
   public void testFalseOrFalseIsFalse()
   {
      assertThat(new False().or(new False()).evaluate(null, null)).isFalse();
   }

   @Test
   public void testTrueOrNotTrueIsTrue()
   {
      assertThat(new True().orNot(new True()).evaluate(null, null)).isTrue();
   }

   @Test
   public void testTrueOrNotFalseIsTrue()
   {
      assertThat(new True().orNot(new False()).evaluate(null, null)).isTrue();
   }

   @Test
   public void testFalseOrNotTrueIsFalse()
   {
      assertThat(new False().orNot(new True()).evaluate(null, null)).isFalse();
   }

   @Test
   public void testFalseOrNotFalseIsTrue()
   {
      assertThat(new False().orNot(new False()).evaluate(null, null)).isTrue();
   }
}
