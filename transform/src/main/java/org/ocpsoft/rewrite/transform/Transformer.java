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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * Implementations of this interface are able to transform streamed data. Classes implementing this interface can be
 * added via {@link Transposition} to modify an intercepted {@link HttpServletResponse#getOutputStream()}.
 * 
 * @see StringTransformer
 * @author Christian Kaltepoth
 */
public interface Transformer
{

   /**
    * Transposition the given {@link InputStream} and write it to the given {@link OutputStream}.
    */
   void transform(HttpServletRewrite event, InputStream input, OutputStream output) throws IOException;

}
