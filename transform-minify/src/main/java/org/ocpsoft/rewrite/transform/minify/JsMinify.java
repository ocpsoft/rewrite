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
package org.ocpsoft.rewrite.transform.minify;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;

import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.transform.Transformer;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

/**
 * 
 * A {@link Transformer} implementation that compresses JavaScript using yuicompressor.
 * 
 * @author Christian Kaltepoth
 */
public class JsMinify extends Minify implements Transformer
{
   JsMinify()
   {}

   @Override
   public void transform(HttpServletRewrite event, InputStream input, OutputStream output) throws IOException
   {

      // prepare input reader
      Reader reader = new InputStreamReader(input, getCharset());
      JavaScriptCompressor compressor = new JavaScriptCompressor(reader, null);

      // write compressed output
      OutputStreamWriter writer = new OutputStreamWriter(output, getCharset());
      compressor.compress(writer, 100, true, false, false, false);
      writer.flush();
   }

}
