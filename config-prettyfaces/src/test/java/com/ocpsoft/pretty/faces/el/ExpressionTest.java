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

import com.ocpsoft.pretty.faces.el.Expressions;

import static org.assertj.core.api.Assertions.assertThat;

public class ExpressionTest
{
   @Test
   public void testEmpty() throws Exception
   {
      assertThat(Expressions.isEL("")).isFalse();
      assertThat(Expressions.containsEL("")).isFalse();
   }

   @Test
   public void testNotAnExpression() throws Exception
   {
      assertThat(Expressions.isEL("Not an expression.")).isFalse();
      assertThat(Expressions.containsEL("Not an expression.")).isFalse();
   }

   @Test
   public void testEmbeddedExpression() throws Exception
   {
      assertThat(Expressions.isEL("This contains an #{expression}.")).isFalse();
      assertThat(Expressions.containsEL("This contains an #{expression}.")).isTrue();
   }

   @Test
   public void testErroneousExpressionLeft() throws Exception
   {
      assertThat(Expressions.isEL("#{expre{ssion}")).isFalse();
   }

   @Test
   public void testErroneousExpressionRight() throws Exception
   {
      assertThat(Expressions.isEL("#{expre}ssion}")).isFalse();
      assertThat(Expressions.containsEL("#{expre}ssion}")).isTrue();
      assertThat(Expressions.containsEL("This contains an #{expre}ssion}.")).isTrue();
   }

   @Test
   public void testIsExpression() throws Exception
   {
      assertThat(Expressions.isEL("#{this.is.an.expression}")).isTrue();
      assertThat(Expressions.containsEL("#{this.is.an.expression}")).isTrue();
   }
}
