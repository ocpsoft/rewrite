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

import java.io.FilterOutputStream;
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
   private GZIPOutputStream stream;
   private CountingOutputStream counter;

   @Override
   public OutputStream wrap(final HttpServletRewrite rewrite, OutputStream outputStream)
   {
      rewrite.getResponse().addHeader("Content-Encoding", "gzip");

      try {
         stream = new GZIPOutputStream(outputStream);
         counter = new CountingOutputStream(stream) {
            @Override
            public void flush() throws IOException
            {
               rewrite.getResponse().setHeader("Content-Length", String.valueOf(counter.getBytesWritten()));
               super.flush();
            }
         };
         return counter;
      }
      catch (IOException e) {
         throw new RewriteException("Could not wrap OutputStream", e);
      }
   }

   @Override
   public void finish(HttpServletRewrite rewrite)
   {
      try {
         counter.flush();
         stream.finish();
         stream.close();
      }
      catch (IOException e) {
         throw new RewriteException("Could not finish GZip Encoding", e);
      }
   }

   private class CountingOutputStream extends FilterOutputStream
   {
      private long written;

      public CountingOutputStream(OutputStream out)
      {
         super(out);
      }

      public long getBytesWritten()
      {
         return written;
      }

      @Override
      public void write(byte[] b, int off, int len) throws IOException
      {
         out.write(b, off, len);
         written += len;
      }

      @Override
      public void write(int b) throws IOException
      {
         out.write(b);
         written++;
      }
   }
}
