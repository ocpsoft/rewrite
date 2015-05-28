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
package org.ocpsoft.rewrite.config.jodatime;

import org.junit.Assert;

import org.joda.time.DateTime;
import org.junit.Test;

import org.ocpsoft.rewrite.mock.MockEvaluationContext;
import org.ocpsoft.rewrite.mock.MockRewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class JodaTimeTest
{

   @Test
   public void testTimeConstraintNotNull()
   {
      JodaTime jodaTime = JodaTime.matches(new TimeCondition() {
         @Override
         public boolean matches(final DateTime time)
         {
            return time != null;
         }
      });

      Assert.assertTrue(jodaTime.evaluate(new MockRewrite(), new MockEvaluationContext()));
   }

   @Test
   public void testTimeConstraintFalseEvaluatesToFalse()
   {
      JodaTime jodaTime = JodaTime.matches(new TimeCondition() {
         @Override
         public boolean matches(final DateTime time)
         {
            return false;
         }
      });

      Assert.assertFalse(jodaTime.evaluate(new MockRewrite(), new MockEvaluationContext()));
   }

}
