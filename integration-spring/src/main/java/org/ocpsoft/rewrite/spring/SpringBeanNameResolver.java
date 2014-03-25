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
package org.ocpsoft.rewrite.spring;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.el.spi.BeanNameResolver;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

/**
 * {@link BeanNameResolver} implementation for Spring.
 * 
 * @author Christian Kaltepoth
 */
public class SpringBeanNameResolver implements BeanNameResolver
{

   private final Logger log = Logger.getLogger(SpringBeanNameResolver.class);

   @Override
   public String getBeanName(Class<?> clazz)
   {

      // try to obtain the WebApplicationContext using ContextLoader
      WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
      if (context == null) {
         throw new IllegalStateException("Unable to get current WebApplicationContext");
      }

      // obtain a map of bean names
      Set<String> beanNames = resolveBeanNames(context, clazz);

      // no beans of that type, nothing we can do
      if (beanNames == null || beanNames.size() == 0) {
         return null;
      }

      // more than one result -> warn the user
      else if (beanNames.size() > 1) {
         log.warn("Spring knows more than one bean of type [{}]", clazz.getName());
         return null;
      }

      // exactly one result -> we got a name
      else {
         return beanNames.iterator().next();
      }

   }

   /**
    * Will ignore scoped proxy target bean names.
    * 
    * @see https://github.com/ocpsoft/rewrite/issues/170
    */
   private Set<String> resolveBeanNames(ListableBeanFactory beanFactory, Class<?> clazz)
   {

      final Set<String> result = new HashSet<String>();

      Map<String, ?> beanMap = beanFactory.getBeansOfType(clazz);
      if (beanMap != null) {
         for (String name : beanMap.keySet()) {
            if (name != null && !name.startsWith("scopedTarget.")) {
               result.add(name);
            }
         }
      }

      return result;

   }

}
