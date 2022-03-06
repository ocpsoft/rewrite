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
import org.ocpsoft.rewrite.test.MockRewrite;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class AndTest
{
   @Test
   public void testTrueAndTrueIsTrue()
   {
      assertThat(And.all(new True(), new True()).evaluate(new MockRewrite(), null)).isTrue();
   }

   @Test
   public void testTrueAndFalseIsFalse()
   {
      assertThat(And.all(new True(), new False()).evaluate(new MockRewrite(), null)).isFalse();
   }

   @Test
   public void testFalseAndFalseIsFalse()
   {
      assertThat(And.all(new False(), new False()).evaluate(new MockRewrite(), null)).isFalse();
   }

   @Test
   public void testFlattensNestedAnds() throws Exception
   {
      And and = And.all(new True(), And.all(new True(), new False()));
      assertThat(and.getConditions().size()).isEqualTo(3);
      assertThat(and.evaluate(new MockRewrite(), null)).isFalse();
      assertThat(and.getConditions().get(0) instanceof True).isTrue();
      assertThat(and.getConditions().get(1) instanceof True).isTrue();
      assertThat(and.getConditions().get(2) instanceof False).isTrue();
   }
}
