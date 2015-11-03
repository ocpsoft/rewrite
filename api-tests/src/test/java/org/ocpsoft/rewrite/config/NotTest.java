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

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.mock.MockEvaluationContext;
import org.ocpsoft.rewrite.test.MockRewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class NotTest
{
   @Test
   public void testNotFalseIsTrue()
   {
      Condition condition = Not.any(new False());
      Assert.assertTrue(condition.evaluate(new MockRewrite(), new MockEvaluationContext()));
   }

   @Test
   public void testNotTrueIsFalse()
   {
      Condition condition = Not.any(new True());
      Assert.assertFalse(condition.evaluate(new MockRewrite(), new MockEvaluationContext()));
   }

   @Test
   public void testNotCountOdd()
   {
      Condition assertCountOdd = new Condition()
      {
         @Override
         public boolean evaluate(Rewrite event, EvaluationContext context)
         {
            Assert.assertEquals(1, Conditions.getNegationCount(context));
            Assert.assertTrue(Conditions.isNegated(context));
            return true;
         }
      };
      Condition condition = Not.any(assertCountOdd);
      Assert.assertFalse(condition.evaluate(new MockRewrite(), new MockEvaluationContext()));
   }

   @Test
   public void testNotCountEvent()
   {
      Condition assertCountOdd = new Condition()
      {
         @Override
         public boolean evaluate(Rewrite event, EvaluationContext context)
         {
            Assert.assertEquals(3, Conditions.getNegationCount(context));
            Assert.assertTrue(Conditions.isNegated(context));
            return true;
         }
      };
      Condition assertCountEven = new Condition()
      {
         @Override
         public boolean evaluate(Rewrite event, EvaluationContext context)
         {
            Assert.assertEquals(4, Conditions.getNegationCount(context));
            Assert.assertFalse(Conditions.isNegated(context));
            return true;
         }
      };
      Condition condition = Not.any(Not.any(Not.any(And.all(assertCountOdd, Not.any(assertCountEven)))));
      Assert.assertTrue(condition.evaluate(new MockRewrite(), new MockEvaluationContext()));
   }
}
