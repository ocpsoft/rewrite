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
package org.ocpsoft.rewrite.el;

import java.util.Iterator;

import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.el.spi.BeanNameResolver;

/**
 * This implementation of {@link Expression} is able to automatically build the EL expression from the type of the bean.
 * 
 * @author Christian Kaltepoth
 */
class TypeBasedExpression implements Expression
{

   private static Logger log = Logger.getLogger(TypeBasedExpression.class);

   private final Class<?> clazz;
   private final String component;

   private String cachedExpression;

   /**
    * Standard way to create a {@link TypeBasedExpression}. The constructor requires the caller to supply the type of
    * the bean and the component, which may be a field or method name.
    */
   public TypeBasedExpression(Class<?> clazz, String component)
   {
      this.clazz = clazz;
      this.component = component;
   }

   @Override
   public String getExpression()
   {

      // lazily perform the lookup and cache the result
      if (cachedExpression == null) {
         cachedExpression = lookupBeanName();
      }

      return cachedExpression;

   }

   @SuppressWarnings("unchecked")
   private String lookupBeanName()
   {

      // load the available SPI implementations
      Iterator<BeanNameResolver> iterator = ServiceLoader.load(BeanNameResolver.class).iterator();
      while (iterator.hasNext()) {
         BeanNameResolver resolver = iterator.next();

         // check if this implementation is able to tell the name
         String beanName = resolver.getBeanName(clazz);

         if (log.isTraceEnabled()) {
            log.trace("Service provider [{}] returned [{}] for class [{}]", new Object[] {
                     resolver.getClass().getSimpleName(), beanName, clazz.getName()
            });
         }

         // the first result is accepted
         if (beanName != null) {

            // create the complete EL expression including the component
            String el = new StringBuilder()
                     .append(beanName).append('.').append(component)
                     .toString();

            if (log.isTraceEnabled()) {
               log.debug("Creation of EL expression for component [{}] of class [{}] successful: {}", new Object[] {
                        component, clazz.getName(), el
               });
            }

            return el;
         }

      }

      throw new IllegalStateException("Unable to obtain EL name for bean of type [" + clazz.getName()
               + "] from any of the SPI implementations. You should conside placing a @"
               + ELBeanName.class.getSimpleName() + " on the class.");

   }

   @Override
   public String toString()
   {
      return getExpression();
   }

}
