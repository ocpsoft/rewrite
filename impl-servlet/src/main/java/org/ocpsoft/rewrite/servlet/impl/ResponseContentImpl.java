/*
 * Copyright 2012 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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
package org.ocpsoft.rewrite.servlet.impl;

import java.nio.charset.Charset;

import org.ocpsoft.rewrite.servlet.config.response.ResponseContent;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ResponseContentImpl implements ResponseContent
{

   private byte[] contents;
   private Charset charset;

   public ResponseContentImpl(byte[] contents, Charset charset)
   {
      this.contents = contents;
      this.charset = charset;
   }

   @Override
   public byte[] getContents()
   {
      return contents;
   }

   @Override
   public void setContents(byte[] contents)
   {
      this.contents = contents;
   }

   @Override
   public Charset getCharset()
   {
      return charset;
   }

   @Override
   public void setCharset(Charset charset)
   {
      this.charset = charset;
   }

   @Override
   public String toString()
   {
      return "ResponseBufferImpl [contents=" + new String(contents, charset) + ", charset=" + charset + "]";
   }

}
