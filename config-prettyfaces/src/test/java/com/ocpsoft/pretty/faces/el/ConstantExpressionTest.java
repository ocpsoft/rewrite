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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConstantExpressionTest
{

   @Test
   public void testSimpleConstantExpression()
   {

      PrettyExpression expr = new ConstantExpression("#{someBean.someProperty}");
      assertThat(expr.getELExpression()).isEqualTo("#{someBean.someProperty}");

   }

   @Test
   public void testConstantExpressionEqualsAndHashCode()
   {

      PrettyExpression expr1 = new ConstantExpression("#{someBean.someProperty}");
      PrettyExpression expr2 = new ConstantExpression("#{someBean.someProperty}");

      assertThat(expr1.equals(expr2)).isTrue();
      assertThat(expr2.hashCode()).isEqualTo(expr1.hashCode());

   }

}
