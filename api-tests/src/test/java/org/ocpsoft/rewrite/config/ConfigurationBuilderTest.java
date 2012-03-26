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

import junit.framework.Assert;

import org.junit.Test;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.InboundRewrite;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.mock.MockEvaluationContext;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ConfigurationBuilderTest
{
   private boolean performed = false;

   @Test
   public void testBuildConfiguration()
   {
      Configuration config = ConfigurationBuilder.begin().defineRule()
               .when(And.all(Direction.isInbound(), new True()))
               .perform(operation);

      Rule rule = config.getRules().get(0);
      InboundRewrite rewrite = new MockInboundRewrite();
      EvaluationContext context = new MockEvaluationContext();
      if (rule.evaluate(rewrite, context))
      {
         rule.perform(rewrite, context);
      }
      Assert.assertTrue(performed);
   }

   Operation operation = new Operation() {
      @Override
      public void perform(final Rewrite event, final EvaluationContext context)
      {
         performed = true;
      }
   };
}
