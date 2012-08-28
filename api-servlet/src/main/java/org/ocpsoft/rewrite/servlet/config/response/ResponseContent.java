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
package org.ocpsoft.rewrite.servlet.config.response;

import java.nio.charset.Charset;

import javax.servlet.http.HttpServletResponse;

/**
 * Holds contents of the fully completed {@link HttpServletResponse}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ResponseContent
{
   /**
    * Get the contents of this buffer. 
    */
   public byte[] getContents();

   /**
    * Set the contents of this buffer.
    */
   public void setContents(byte[] contents);

   /**
    * Get the {@link Charset} with which response output is encoded.
    */
   public Charset getCharset();
   
   /**
    * Set the {@link Charset} with which response output will be encoded.
    */
   public void setCharset(Charset charset);
}
