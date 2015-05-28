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
package org.ocpsoft.rewrite.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.ocpsoft.rewrite.annotation.api.ClassContext;
import org.ocpsoft.rewrite.annotation.api.ClassVisitor;
import org.ocpsoft.rewrite.annotation.api.FieldContext;
import org.ocpsoft.rewrite.annotation.api.HandlerChain;
import org.ocpsoft.rewrite.annotation.spi.AnnotationHandler;

/**
 * Verify the behavior of {@link ClassVisitorImpl} in inheritance scenarios
 * 
 * @author Christian Kaltepoth
 */
public class ClassVisitorInheritanceTest
{

   private final TestHandler handler = new TestHandler();

   @SuppressWarnings("rawtypes")
   private final List handlers = Arrays.asList(handler);

   @SuppressWarnings("unchecked")
   private final ClassVisitor visitor = new ClassVisitorImpl(handlers, null);

   @Test
   public void classWithoutSuperClass()
   {

      visitor.visit(SimpleClass.class);

      /*
       * The hander visits the class and all the fields
       */
      assertThat(handler.getElements())
               .contains("SimpleClass")
               .contains("field");

   }

   @Test
   public void classWithSuperClass()
   {

      visitor.visit(SimpleSubClass.class);

      /*
       * The handler visits the class, all the fields (including the ones 
       * declared in the super class), but not the super class itself.
       */
      assertThat(handler.getElements())
               .contains("SimpleSubClass")
               .contains("field1")
               .contains("field2")
               .doesNotContain("SimpleSuperClass");

   }

   private static class TestHandler implements AnnotationHandler<Resource>
   {

      private final List<String> elements = new ArrayList<String>();

      @Override
      public int priority()
      {
         return 0;
      }

      @Override
      public Class<Resource> handles()
      {
         return Resource.class;
      }

      @Override
      public void process(ClassContext context, Resource annotation, HandlerChain chain)
      {
         if (context instanceof FieldContext) {
            FieldContext fieldContext = (FieldContext) context;
            elements.add(fieldContext.getJavaField().getName());
         }
         else if (context instanceof ClassContext) {

            ClassContext classContext = (ClassContext) context;
            elements.add(classContext.getJavaClass().getSimpleName());

            // trigger creation of a RuleBuilder
            context.getRuleBuilder();

         }
      }

      public List<String> getElements()
      {
         return elements;
      }

   }

   @Resource
   private static class SimpleClass
   {
      @Resource
      @SuppressWarnings("unused")
      private String field;
   }

   @Resource
   private static class SimpleSuperClass
   {
      @Resource
      @SuppressWarnings("unused")
      private String field2;
   }

   @Resource
   private static class SimpleSubClass extends SimpleSuperClass
   {
      @Resource
      @SuppressWarnings("unused")
      private String field1;
   }

}
