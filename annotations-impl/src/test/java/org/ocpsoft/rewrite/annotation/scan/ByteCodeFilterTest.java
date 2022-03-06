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
package org.ocpsoft.rewrite.annotation.scan;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ByteCodeFilterTest
{

   private ByteCodeFilter filter;

   @Before
   public void setup()
   {
      Set<Class<? extends Annotation>> types = new HashSet<Class<? extends Annotation>>();
      types.add(TestAnnotation.class);
      filter = new ByteCodeFilter(types);
   }

   @After
   public void teardown()
   {
      filter = null;
   }

   @Test
   public void testInvalidClass() throws IOException
   {
      assertThat(filter.accept(new ByteArrayInputStream(new byte[]{
              // some random bytes
              (byte) 0x12, (byte) 0x34, (byte) 0xd3, (byte) 0x45,
              (byte) 0x13, (byte) 0x35, (byte) 0xd4, (byte) 0x46,
              (byte) 0x14, (byte) 0x36, (byte) 0xd5, (byte) 0x47,
              (byte) 0x15, (byte) 0x37, (byte) 0xd6, (byte) 0x48
      }))).isFalse();
   }

   @Test
   public void testJdk14Class() throws IOException
   {
      assertThat(filter.accept(new ByteArrayInputStream(new byte[]{
              (byte) 0xca, (byte) 0xfe, (byte) 0xba, (byte) 0xbe, // magic
              (byte) 0x00, (byte) 0x00, // minor
              (byte) 0x00, (byte) 48 // major 48 = JDK14
      }))).isFalse();
   }

   @Test
   public void testNoAnnotationsOnClass() throws IOException
   {
      assertThat(filter.accept(getByteCodeInputStream(ClassWithoutAnnotations.class))).isFalse();
   }

   @Test
   public void testUrlMappingAnnotationOnClass() throws IOException
   {
      assertThat(filter.accept(getByteCodeInputStream(ClassWithUrlMapping.class))).isTrue();
   }

   @Test
   public void testClassWithLongValueInConstantPool() throws IOException
   {
      assertThat(filter.accept(getByteCodeInputStream(ClassWithLongValueInConstantPool.class))).isFalse();
   }

   @Test
   public void testUrlActionOnMethod() throws IOException
   {
      assertThat(filter.accept(getByteCodeInputStream(ClassWithUrlAction.class))).isTrue();
   }

   private InputStream getByteCodeInputStream(Class<?> clazz)
   {
      String name = clazz.getName().replace(".", "/") + ".class";
      InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
      assertThat(stream).as("Cannot find class file: " + name).isNotNull();
      return stream;
   }

   /**
    * Class without any PrettyFaces annotations
    */
   public static class ClassWithoutAnnotations
   {

      private String someProperty;

      public String getSomeProperty()
      {
         return someProperty;
      }

   }

   /**
    * Class with some {@link Long} constant in the constant pool
    */
   public class ClassWithLongValueInConstantPool
   {
      public final static long value = 9223342036854775807l;
   }

   /**
    * Class with a relevant annotation on the type
    */
   @TestAnnotation
   public static class ClassWithUrlMapping
   {

      private String someProperty;

      public String getSomeProperty()
      {
         return someProperty;
      }

   }

   /**
    * Class with a relevant annotation on a method
    */
   public static class ClassWithUrlAction
   {

      private String someProperty;

      @TestAnnotation
      public void urlAction()
      {
         // nothing
      }

      public String getSomeProperty()
      {
         return someProperty;
      }

   }

}
