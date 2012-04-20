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
package org.ocpsoft.rewrite.config.typesafe;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ocpsoft.rewrite.bind.Evaluation;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.mock.MockEvaluationContext;
import org.ocpsoft.rewrite.mock.MockRewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class TypesafeInvocationTest
{
   private Rewrite rewrite;
   private EvaluationContext context;

   @Before
   public void before()
   {
      rewrite = new MockRewrite();
      context = new MockEvaluationContext();

      DummyObject.bool = null;
      DummyObject.invoked = false;
      DummyObject.number = null;
      DummyObject.payload = null;
   }

   @Test
   public void testInvokeTypesafe()
   {
      Typesafe typesafe = Typesafe.method();
      typesafe.invoke(DummyObject.class).doSomething();

      Assert.assertFalse(DummyObject.invoked);
      typesafe.perform(rewrite, context);
      Assert.assertTrue(DummyObject.invoked);
   }

   @Test
   public void testInvokeTypesafePrimitiveByte()
   {
      Typesafe typesafe = Typesafe.method();
      DummyObject object = typesafe.invoke(DummyObject.class);
      object.doSomething(typesafe.param(byte.class));

      Assert.assertFalse(DummyObject.invoked);
      typesafe.perform(rewrite, context);
      Assert.assertTrue(DummyObject.invoked);
   }

   @Test
   public void testInvokeTypesafePrimitiveChar()
   {
      Typesafe typesafe = Typesafe.method();
      DummyObject object = typesafe.invoke(DummyObject.class);
      object.doSomething(typesafe.param(char.class));

      Assert.assertFalse(DummyObject.invoked);
      typesafe.perform(rewrite, context);
      Assert.assertTrue(DummyObject.invoked);
   }

   @Test
   public void testInvokeTypesafePrimitiveDouble()
   {
      Typesafe typesafe = Typesafe.method();
      DummyObject object = typesafe.invoke(DummyObject.class);
      object.doSomething(typesafe.param(double.class));

      Assert.assertFalse(DummyObject.invoked);
      typesafe.perform(rewrite, context);
      Assert.assertTrue(DummyObject.invoked);
   }

   @Test
   public void testInvokeTypesafePrimitiveFloat()
   {
      Typesafe typesafe = Typesafe.method();
      DummyObject object = typesafe.invoke(DummyObject.class);
      object.doSomething(typesafe.param(float.class));

      Assert.assertFalse(DummyObject.invoked);
      typesafe.perform(rewrite, context);
      Assert.assertTrue(DummyObject.invoked);
   }

   @Test
   public void testInvokeTypesafePrimitiveInt()
   {
      Typesafe typesafe = Typesafe.method();
      DummyObject object = typesafe.invoke(DummyObject.class);
      object.doSomething(typesafe.param(int.class));

      Assert.assertFalse(DummyObject.invoked);
      typesafe.perform(rewrite, context);
      Assert.assertTrue(DummyObject.invoked);
   }

   @Test
   public void testInvokeTypesafePrimitiveLong()
   {
      Typesafe typesafe = Typesafe.method();
      DummyObject object = typesafe.invoke(DummyObject.class);
      object.doSomething(typesafe.param(long.class));

      Assert.assertFalse(DummyObject.invoked);
      typesafe.perform(rewrite, context);
      Assert.assertTrue(DummyObject.invoked);
   }

   @Test
   public void testInvokeTypesafePrimitiveShort()
   {
      Typesafe typesafe = Typesafe.method();
      DummyObject object = typesafe.invoke(DummyObject.class);
      object.doSomething(typesafe.param(short.class));

      Assert.assertFalse(DummyObject.invoked);
      typesafe.perform(rewrite, context);
      Assert.assertTrue(DummyObject.invoked);
   }

   @Test
   public void testInvokeTypesafeWithNamedParams()
   {
      Evaluation.property("bool").submit(rewrite, context, true);
      Evaluation.property("int").submit(rewrite, context, 15);

      Typesafe typesafe = Typesafe.method();
      DummyObject object = typesafe.invoke(DummyObject.class);
      boolean param = typesafe.param(boolean.class, "bool");
      Integer param2 = typesafe.param(Integer.class, "int");

      object.doSomething(param, param2);

      Assert.assertFalse(DummyObject.invoked);
      Assert.assertNull(DummyObject.number);
      Assert.assertNull(DummyObject.bool);
      typesafe.perform(rewrite, context);
      Assert.assertEquals(15, DummyObject.number);
      Assert.assertEquals(true, DummyObject.bool);
      Assert.assertTrue(DummyObject.invoked);
   }

   @Test(expected = IllegalStateException.class)
   public void testInvokeTypesafeWithMissingParams()
   {
      Assert.assertFalse(DummyObject.invoked);
      Typesafe typesafe = Typesafe.method();
      DummyObject object = typesafe.invoke(DummyObject.class);
      boolean param = typesafe.param(boolean.class);

      object.doSomething(param, null);
      typesafe.perform(rewrite, context);
      Assert.fail();
   }
}
