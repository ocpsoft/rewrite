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
package org.ocpsoft.rewrite.transform;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.ocpsoft.common.util.Streams;

public abstract class StringTransformer implements Transformer
{

   private static final Charset UTF8 = Charset.forName("UTF8");

   public abstract String transform(String input);

   @Override
   public void transform(InputStream inputStream, OutputStream outputStream) throws IOException
   {

      // read input stream and store it in a string
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      Streams.copy(inputStream, bos);
      String input = new String(bos.toByteArray(), Charset.forName("UTF-8"));

      // perform internal transformation
      String output = transform(input);

      // write result to the output stream
      outputStream.write(output.getBytes(UTF8));

   }

}
