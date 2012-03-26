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
package org.ocpsoft.rewrite.showcase.rest;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class XMLUtil
{

   public static <T> void streamFromObject(final Class<T> type, final T object, final ServletOutputStream outputStream)
   {
      try {
         Marshaller marshaller = JAXBContext.newInstance(type)
                  .createMarshaller();
         marshaller.setProperty("jaxb.formatted.output", true);
         marshaller.marshal(object, outputStream);

      }
      catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   @SuppressWarnings("unchecked")
   public static <T> T streamToObject(final Class<T> type, final ServletInputStream inputStream)
   {
      try {
         Unmarshaller unmarshaller = JAXBContext.newInstance(type).createUnmarshaller();
         T result = (T) unmarshaller.unmarshal(inputStream);
         return result;
      }
      catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

}
