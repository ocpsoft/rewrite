/*
 * Copyright 2010 Lincoln Baxter, III
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
package com.ocpsoft.pretty.faces.el;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ConstantExpressionTest
{

   @Test
   public void testSimpleConstantExpression()
   {

      PrettyExpression expr = new ConstantExpression("#{someBean.someProperty}");
      assertEquals("#{someBean.someProperty}", expr.getELExpression());

   }

   @Test
   public void testConstantExpressionEqualsAndHashCode()
   {

      PrettyExpression expr1 = new ConstantExpression("#{someBean.someProperty}");
      PrettyExpression expr2 = new ConstantExpression("#{someBean.someProperty}");

      assertTrue(expr1.equals(expr2));
      assertEquals(expr1.hashCode(), expr2.hashCode());

   }

}
