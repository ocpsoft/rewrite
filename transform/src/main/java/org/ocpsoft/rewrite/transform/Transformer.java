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

/**
 * Implementations are able to transform streamed data. Classes implementing this interface can be added
 * to {@link Transform} to modify the intercepted response stream from the container.
 * 
 * @see StringTransformer
 * @author Christian Kaltepoth
 */
public interface Transformer
{
   
   /**
    * Transform the given input stream and write it to the output stream.
    */
   void transform(InputStream input, OutputStream output) throws IOException;


}
