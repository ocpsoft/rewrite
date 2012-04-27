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

import org.ocpsoft.rewrite.el.spi.BeanNameResolver;

/**
 * A default implementation of {@link BeanNameResolver} that checks for an {@link ELBeanName} annotation on the type.
 * 
 * @author Christian Kaltepoth
 */
public class DefaultBeanNameResolver implements BeanNameResolver
{

   @Override
   public String getBeanName(Class<?> clazz)
   {
      ELBeanName annotation = clazz.getAnnotation(ELBeanName.class);
      if (annotation != null) {
         return annotation.value();
      }
      return null;
   }

}
