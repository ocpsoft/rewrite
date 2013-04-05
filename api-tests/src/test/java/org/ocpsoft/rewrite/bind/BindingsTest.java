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
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.mock.MockEvaluationContext;
import org.ocpsoft.rewrite.param.DefaultParameter;
import org.ocpsoft.rewrite.param.Validator;
import org.ocpsoft.rewrite.test.MockRewrite;
import org.ocpsoft.rewrite.util.ParameterUtils;

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
   public void testEnqueueSubmissionsExecutesEvaluationEagerly() throws Exception
   {
      MockEvaluationContext context = new MockEvaluationContext();

      DefaultParameter parameter = new DefaultParameter("lincoln");
      Assert.assertTrue(ParameterUtils.enqueueSubmission(rewrite, context,
               parameter.bindsTo(Evaluation.property("lincoln")), "baxter"));
      Assert.assertEquals("baxter", Evaluation.property("lincoln").retrieve(rewrite, context));
   }

   @Test
   @SuppressWarnings("rawtypes")
   public void testEnqueueEvaluationProcessesConversionAndValidation() throws Exception
   {
      MockEvaluationContext context = new MockEvaluationContext();
      Validator validator = new Validator() {
         @Override
         public boolean isValid(Rewrite event, EvaluationContext context, Object value)
         {
            return "baxter III".equals(value);
         }
      };

      Assert.assertFalse(ParameterUtils.enqueueSubmission(rewrite, context,
               new DefaultParameter("lincoln").bindsTo(Evaluation.property("lincoln")).validatedBy(validator), "baxter"));
   }

}
