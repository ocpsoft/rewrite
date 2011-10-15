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
package com.ocpsoft.rewrite.cdi;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;

import org.jboss.solder.beanManager.BeanManagerAware;

import com.ocpsoft.common.spi.ServiceLocator;
import com.ocpsoft.logging.Logger;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class CdiServiceLocator extends BeanManagerAware implements ServiceLocator
{
   Logger log = Logger.getLogger(CdiServiceLocator.class);

   @Override
   @SuppressWarnings("unchecked")
   public <T> Collection<Class<T>> locate(final Class<T> type)
   {
      List<Class<T>> result = new ArrayList<Class<T>>();

      BeanManager manager = getBeanManager();

      @SuppressWarnings("serial")
      Set<Bean<?>> beans = manager.getBeans(type, new Annotation[] { new AnnotationLiteral<Any>() {} });

      for (Bean<?> bean : beans) {
         result.add((Class<T>) bean.getBeanClass());
      }

      return result;
   }

}
