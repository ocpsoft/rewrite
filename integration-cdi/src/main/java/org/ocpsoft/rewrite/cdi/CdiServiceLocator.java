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
package org.ocpsoft.rewrite.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;

import org.ocpsoft.common.spi.ServiceLocator;
import org.ocpsoft.rewrite.cdi.manager.BeanManagerAware;
import org.ocpsoft.rewrite.cdi.util.ParameterizedTypeImpl;
import org.ocpsoft.rewrite.cdi.util.WildcardTypeImpl;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:christian@kaltepoth.de">Christian Kaltepoth</a>
 */
public class CdiServiceLocator extends BeanManagerAware implements ServiceLocator
{

   @Override
   @SuppressWarnings("unchecked")
   public <T> Collection<Class<T>> locate(final Class<T> type)
   {
      List<Class<T>> result = new ArrayList<Class<T>>();

      // the BeanManager may be not available during Rewrite startup
      if (isBeanManagerAvailable()) {

         BeanManager manager = getBeanManager();

         Set<Bean<?>> beans = manager.getBeans(getRequiredType(type), new Annotation[] { new AnnotationLiteral<Any>() {
            private static final long serialVersionUID = -1896831901770051851L;
         } });

         for (Bean<?> bean : beans) {
            result.add((Class<T>) bean.getBeanClass());
         }

      }

      return result;

   }

   /**
    * Builds the correct "required type" including actual type arguments in case of parameterized types.
    * 
    * @see https://github.com/ocpsoft/rewrite/issues/137
    */
   private Type getRequiredType(Class<?> clazz)
   {

      TypeVariable<?>[] typeParameters = clazz.getTypeParameters();

      if (typeParameters.length > 0) {

         Type[] actualTypeParameters = new Type[typeParameters.length];
         for (int i = 0; i < typeParameters.length; i++) {
            actualTypeParameters[i] = new WildcardTypeImpl(new Type[] { Object.class }, new Type[] {});
         }

         return new ParameterizedTypeImpl(clazz, actualTypeParameters, null);

      }

      return clazz;
   }

}
