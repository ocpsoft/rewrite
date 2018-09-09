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

package com.ocpsoft.pretty.faces.config;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.NoSuchElementException;

import com.ocpsoft.pretty.faces.config.spi.ClassLoaderConfigurationProvider;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class MockClassLoader extends URLClassLoader
{
   private URL[] urls = null;

   public MockClassLoader(URL... urls)
   {
      super(new URL[0], Thread.currentThread().getContextClassLoader());
      this.urls = urls;
   }

   @Override
   public Enumeration<URL> getResources(final String name) throws IOException
   {
      if (ClassLoaderConfigurationProvider.PRETTY_CONFIG_RESOURCE.equals(name))
      {
         return enumeration(urls);
      }
      else
      {
         return super.getResources(name);
      }
   }

   private <T> Enumeration<T> enumeration(@SuppressWarnings("unchecked") final T... elements)
   {
      return new Enumeration<T>()
      {
         private int index = 0;

         @Override
         public boolean hasMoreElements()
         {
            return index < elements.length;
         }

         @Override
         public T nextElement()
         {
            if (!hasMoreElements())
            {
               throw new NoSuchElementException("No more elements exist.");
            }
            return elements[index++];
         }
      };
   }
}
