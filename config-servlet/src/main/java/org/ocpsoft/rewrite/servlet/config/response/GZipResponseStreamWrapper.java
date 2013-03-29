/*
 * Copyright 2013 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import org.ocpsoft.rewrite.exception.RewriteException;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * A {@link ResponseStreamWrapper} that compresses the response output to GZip format and sets the proper response
 * headers.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class GZipResponseStreamWrapper implements ResponseStreamWrapper
{
   public final static String STREAM_KEY = GZipResponseStreamWrapper.class.getName() + "_STREAM";

   @Override
   public OutputStream wrap(final HttpServletRewrite rewrite, OutputStream outputStream)
   {
      rewrite.getResponse().setHeader("Content-Encoding", "gzip");

      try {
         GZIPOutputStream stream = new GZIPOutputStream(outputStream);
         rewrite.getRequest().setAttribute(STREAM_KEY, stream);
         return stream;
      }
      catch (IOException e) {
         throw new RewriteException("Could not wrap OutputStream", e);
      }
   }

   @Override
   public void finish(HttpServletRewrite rewrite)
   {
      try {
         GZIPOutputStream stream = (GZIPOutputStream) rewrite.getRequest().getAttribute(STREAM_KEY);
         if (stream != null)
         {
            stream.flush();
            stream.finish();
         }
      }
      catch (IOException e) {
         throw new RewriteException("Could not finish GZip Encoding", e);
      }
   }
}
