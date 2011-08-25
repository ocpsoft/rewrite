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
package com.ocpsoft.rewrite.bind;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ocpsoft.rewrite.config.Condition;
import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.mock.MockBinding;
import com.ocpsoft.rewrite.mock.MockEvaluationContext;
import com.ocpsoft.rewrite.mock.MockRewrite;

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
   public void testPathAttemptsToBindParameters()
   {
      MockBinding mockBinding = new MockBinding("Value");
      Condition condition = Bindings.equals("Value", mockBinding);
      MockEvaluationContext context = new MockEvaluationContext();
      Assert.assertTrue(condition.evaluate(rewrite, context));
   }
}
