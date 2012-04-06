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
package org.ocpsoft.rewrite.bind;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.mock.MockBinding;
import org.ocpsoft.rewrite.mock.MockEvaluationContext;
import org.ocpsoft.rewrite.mock.MockRewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class BindingsTest
{
   private Rewrite rewrite;

   @Before
   public void before()
   {
      rewrite = new MockRewrite();
   }

   @Test
   @SuppressWarnings("rawtypes")
   public void testEnqueueSubmissionsExecutesEvaluationEagerly() throws Exception
   {
      MockEvaluationContext context = new MockEvaluationContext();

      Assert.assertTrue(Bindings.enqueueSubmission(rewrite, context,
               new DefaultBindable().bindsTo(Evaluation.property("lincoln")), "baxter"));
      Assert.assertEquals("baxter", Evaluation.property("lincoln").retrieve(rewrite, context));
   }

   @Test
   @SuppressWarnings("rawtypes")
   public void testEnqueueEvaluationProcessesConversionAndValidation() throws Exception
   {
      MockEvaluationContext context = new MockEvaluationContext();
      Validator validator = new Validator() {
         @Override
         public boolean validate(Rewrite event, EvaluationContext context, Object value)
         {
            return "baxter III".equals(value);
         }
      };

      Assert.assertFalse(Bindings.enqueueSubmission(rewrite, context,
               new DefaultBindable().bindsTo(Evaluation.property("lincoln").validatedBy(validator)), "baxter"));
   }

   /*
    * Equals
    */
   @Test
   public void testRetrieveEqualsValue()
   {
      MockBinding mockBinding = new MockBinding("v");
      Condition condition = Bindings.equals("v", mockBinding);
      MockEvaluationContext context = new MockEvaluationContext();
      Assert.assertTrue(condition.evaluate(rewrite, context));
   }

   @Test
   public void testRetrieveEqualsRetrieve()
   {
      MockBinding left = new MockBinding("v");
      MockBinding right = new MockBinding("v");
      Condition condition = Bindings.equals(left, right);
      MockEvaluationContext context = new MockEvaluationContext();
      Assert.assertTrue(condition.evaluate(rewrite, context));
   }

   @Test
   public void testRetrieveDoesNotEqualDifferentRetrieve()
   {
      MockBinding left = new MockBinding("v");
      MockBinding right = new MockBinding("o");
      Condition condition = Bindings.equals(left, right);
      MockEvaluationContext context = new MockEvaluationContext();
      Assert.assertFalse(condition.evaluate(rewrite, context));
   }

   @Test
   public void testRetrieveEqualsSubmit()
   {
      MockBinding left = new MockBinding("v");
      MockBinding right = new MockBinding(null, "v");
      Condition condition = Bindings.equals(left, right, "o");
      MockEvaluationContext context = new MockEvaluationContext();
      Assert.assertTrue(condition.evaluate(rewrite, context));
   }

   @Test
   public void testRetrieveDoesNotEqualDifferentSubmit()
   {
      MockBinding left = new MockBinding("v");
      MockBinding right = new MockBinding(null, "o");
      Condition condition = Bindings.equals(left, right, "o");
      MockEvaluationContext context = new MockEvaluationContext();
      Assert.assertFalse(condition.evaluate(rewrite, context));
   }

   @Test
   public void testSubmitEqualsValue()
   {
      MockBinding mockBinding = new MockBinding(null, "Return");
      Condition condition = Bindings.equals("Return", mockBinding, "Return");
      MockEvaluationContext context = new MockEvaluationContext();
      Assert.assertTrue(condition.evaluate(rewrite, context));
   }

   @Test
   public void testSubmitEqualsSubmit()
   {
      MockBinding left = new MockBinding(null, "v");
      MockBinding right = new MockBinding(null, "v");
      Condition condition = Bindings.equals(left, "1", right, "2");
      MockEvaluationContext context = new MockEvaluationContext();
      Assert.assertTrue(condition.evaluate(rewrite, context));
   }

   @Test
   public void testSubmitDoesNotEqualDifferentSubmit()
   {
      MockBinding left = new MockBinding(null, "v");
      MockBinding right = new MockBinding(null, "o");
      Condition condition = Bindings.equals(left, "1", right, "2");
      MockEvaluationContext context = new MockEvaluationContext();
      Assert.assertFalse(condition.evaluate(rewrite, context));
   }

   /*
    * Not Equals
    */
   @Test
   public void testRetrieveNotEqualsValue()
   {
      MockBinding mockBinding = new MockBinding("v");
      Condition condition = Bindings.notEquals("v", mockBinding);
      MockEvaluationContext context = new MockEvaluationContext();
      Assert.assertFalse(condition.evaluate(rewrite, context));
   }

   @Test
   public void testRetrieveNotEqualsRetrieve()
   {
      MockBinding left = new MockBinding("v");
      MockBinding right = new MockBinding("v");
      Condition condition = Bindings.notEquals(left, right);
      MockEvaluationContext context = new MockEvaluationContext();
      Assert.assertFalse(condition.evaluate(rewrite, context));
   }

   @Test
   public void testRetrieveDoesNotEqualDifferentRetrieveReturnsTrue()
   {
      MockBinding left = new MockBinding("v");
      MockBinding right = new MockBinding("o");
      Condition condition = Bindings.notEquals(left, right);
      MockEvaluationContext context = new MockEvaluationContext();
      Assert.assertTrue(condition.evaluate(rewrite, context));
   }

   @Test
   public void testRetrieveNotEqualsSubmit()
   {
      MockBinding left = new MockBinding("v");
      MockBinding right = new MockBinding(null, "v");
      Condition condition = Bindings.notEquals(left, right, "o");
      MockEvaluationContext context = new MockEvaluationContext();
      Assert.assertFalse(condition.evaluate(rewrite, context));
   }

   @Test
   public void testRetrieveDoesNotEqualDifferentSubmitReturnsTrue()
   {
      MockBinding left = new MockBinding("v");
      MockBinding right = new MockBinding(null, "o");
      Condition condition = Bindings.notEquals(left, right, "o");
      MockEvaluationContext context = new MockEvaluationContext();
      Assert.assertTrue(condition.evaluate(rewrite, context));
   }

   @Test
   public void testSubmitNotEqualsValue()
   {
      MockBinding mockBinding = new MockBinding(null, "Return");
      Condition condition = Bindings.notEquals("Return", mockBinding, "Return");
      MockEvaluationContext context = new MockEvaluationContext();
      Assert.assertFalse(condition.evaluate(rewrite, context));
   }

   @Test
   public void testSubmitNotEqualsSubmit()
   {
      MockBinding left = new MockBinding(null, "v");
      MockBinding right = new MockBinding(null, "v");
      Condition condition = Bindings.notEquals(left, "1", right, "2");
      MockEvaluationContext context = new MockEvaluationContext();
      Assert.assertFalse(condition.evaluate(rewrite, context));
   }

   @Test
   public void testSubmitDoesNotEqualDifferentSubmitReturnsTrue()
   {
      MockBinding left = new MockBinding(null, "v");
      MockBinding right = new MockBinding(null, "o");
      Condition condition = Bindings.notEquals(left, "1", right, "2");
      MockEvaluationContext context = new MockEvaluationContext();
      Assert.assertTrue(condition.evaluate(rewrite, context));
   }
}
