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
package org.ocpsoft.rewrite.showcase.bookstore.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ejb.Stateless;

@Stateless
public class BookImageService
{

   public byte[] getBookImage(Long isbn)
   {

      String resource = "images/" + isbn + ".jpg";

      InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);

      if (stream == null) {
         return null;
      }

      try {
         ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         copy(stream, buffer);
         return buffer.toByteArray();
      }
      catch (IOException e) {
         throw new IllegalStateException(e);
      }

   }

   private static void copy(InputStream in, OutputStream out) throws IOException
   {
      byte[] buf = new byte[1024];
      int len = 0;
      while ((len = in.read(buf)) >= 0)
      {
         out.write(buf, 0, len);
      }

   }

}
