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

import org.ocpsoft.rewrite.mock.MockEvaluationContext;
import org.ocpsoft.rewrite.test.MockRewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class OrTest
{
   @Test
   public void testTrueAndTrueIsTrue()
   {
      Assert.assertTrue(Or.any(new True(), new True()).evaluate(new MockRewrite(), new MockEvaluationContext()));
   }

   @Test
   public void testTrueAndFalseIsTrue()
   {
      Assert.assertTrue(Or.any(new True(), new False()).evaluate(new MockRewrite(), new MockEvaluationContext()));
   }

   @Test
   public void testFalseAndFalseIsFalse()
   {
      Assert.assertFalse(Or.any(new False(), new False()).evaluate(new MockRewrite(), new MockEvaluationContext()));
   }

   @Test
   public void testFlattensNestedOrs() throws Exception
   {
      Or or = Or.any(new False(), Or.any(new False(), new True()));
      Assert.assertEquals(3, or.getConditions().size());
      Assert.assertTrue(or.evaluate(new MockRewrite(), null));
      Assert.assertTrue(or.getConditions().get(0) instanceof False);
      Assert.assertTrue(or.getConditions().get(1) instanceof False);
      Assert.assertTrue(or.getConditions().get(2) instanceof True);
   }
}
