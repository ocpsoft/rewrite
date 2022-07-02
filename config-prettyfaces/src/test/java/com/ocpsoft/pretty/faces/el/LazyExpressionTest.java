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
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

public class LazyExpressionTest
{

   @Test
   public void testSimpleLazyExpression()
   {

      // prepare LazyBeanNameFinder mock
      LazyBeanNameFinder beanNameFinder = Mockito.mock(LazyBeanNameFinder.class);
      Mockito.when(beanNameFinder.findBeanName(SomeTestBean.class)).thenReturn("someTestBean");

      // create expression and evaluate it twice (to check LazyBeanNameFinder is called only once)
      PrettyExpression expr = new LazyExpression(beanNameFinder, SomeTestBean.class, "property");
      assertThat(expr.getELExpression()).isEqualTo("#{someTestBean.property}");
      assertThat(expr.getELExpression()).isEqualTo("#{someTestBean.property}");

      // verify mock
      Mockito.verify(beanNameFinder).findBeanName(SomeTestBean.class);

   }

   @Test(expected = IllegalStateException.class)
   public void testUnresolvableLazyExpression()
   {

      // prepare LazyBeanNameFinder mock
      LazyBeanNameFinder beanNameFinder = Mockito.mock(LazyBeanNameFinder.class);
      Mockito.when(beanNameFinder.findBeanName(SomeTestBean.class)).thenThrow(new IllegalStateException());

      // this call will fail, because LazyBeanNameFinder will throw an IllegalStateException
      PrettyExpression expr = new LazyExpression(beanNameFinder, SomeTestBean.class, "property");
      expr.getELExpression();
   }

   /**
    * Simple test class
    */
   public class SomeTestBean
   {

   }

}
