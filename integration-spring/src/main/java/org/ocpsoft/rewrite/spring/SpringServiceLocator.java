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

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.ocpsoft.common.spi.ServiceLocator;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

/**
 * {@link ServiceLocator} implementation for Spring.
 * 
 * @author Christian Kaltepoth
 */
public class SpringServiceLocator implements ServiceLocator
{

   @Override
   @SuppressWarnings("unchecked")
   public <T> Collection<Class<T>> locate(Class<T> clazz)
   {
      Set<Class<T>> result = new LinkedHashSet<Class<T>>();

      // use the Spring API to obtain the WebApplicationContext
      WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();

      // may be null if Spring hasn't started yet
      if (context != null) {

         // ask spring about SPI implementations
         Map<String, T> beans = context.getBeansOfType(clazz);

         // add the implementations Class objects to the result set
         for (T type : beans.values()) {
            result.add((Class<T>) type.getClass());
         }

      }

      return result;
   }

}
