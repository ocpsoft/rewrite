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
package org.ocpsoft.rewrite.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.ocpsoft.rewrite.annotation.api.Parameter;

public class ParameterImpl implements Parameter
{
   private final Method method;
   private final Class<?> type;
   private final List<Annotation> annotations;
   private final int index;

   public ParameterImpl(Method method, Class<?> type, Annotation[] annotations, int index)
   {
      this.method = method;
      this.type = type;
      this.annotations = Arrays.asList(annotations);
      this.index = index;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends Annotation> T getAnnotation(Class<T> type)
   {
      for (Annotation a : annotations) {
         if (type.equals(a.annotationType()))
            return (T) a;
      }
      return null;
   }

   @Override
   public Annotation[] getAnnotations()
   {
      return annotations.toArray(new Annotation[] {});
   }

   @Override
   public Annotation[] getDeclaredAnnotations()
   {
      // TODO is it possible to distinguish this? Currently returns all annotations, declared or inherited.
      return getAnnotations();
   }

   @Override
   public boolean isAnnotationPresent(Class<? extends Annotation> type)
   {
      return getAnnotation(type) != null;
   }

   @Override
   public Method getDeclaringMethod()
   {
      return method;
   }

   @Override
   public int getIndex()
   {
      return index;
   }

   @Override
   public Class<?> getType()
   {
      return type;
   }

}
