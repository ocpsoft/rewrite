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
package org.ocpsoft.rewrite.faces;

import javax.faces.bean.ManagedBean;

import org.ocpsoft.rewrite.el.spi.BeanNameResolver;

/**
 * Implementation of {@link BeanNameResolver} that checks for {@link ManagedBean} annotations on the class.
 * 
 * @author Christian Kaltepoth
 */
public class FacesBeanNameResolver implements BeanNameResolver
{

   @Override
   public String getBeanName(Class<?> clazz)
   {
      ManagedBean annotation = clazz.getAnnotation(ManagedBean.class);
      if (annotation != null) {

         // name is set using the annotation
         if (annotation.name().length() > 0) {
            return annotation.name();
         }

         // no name, so name is auto generated
         else {
            String className = clazz.getSimpleName();
            return Character.toLowerCase(className.charAt(0)) + className.substring(1);
         }

      }

      // no @ManagedBean annotation -> not a JSF bean
      return null;

   }

}
