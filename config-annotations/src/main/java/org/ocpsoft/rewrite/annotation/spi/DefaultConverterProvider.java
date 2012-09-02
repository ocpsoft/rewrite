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
package org.ocpsoft.rewrite.annotation.spi;

import org.ocpsoft.rewrite.bind.Converter;

/**
 * Default implementation of {@link ConverterProvider} that builds {@link Converter} instances by creating an instance
 * of the supplied class using the default constructor.
 * 
 * @author Christian Kaltepoth
 */
public class DefaultConverterProvider implements ConverterProvider
{

   @Override
   public Converter<?> getByType(Class<?> converterClass)
   {

      if (Converter.class.isAssignableFrom(converterClass)) {

         try {
            return (Converter<?>) converterClass.newInstance();
         }
         catch (InstantiationException e) {
            throw new IllegalArgumentException(e);
         }
         catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
         }

      }

      return null;

   }

}
