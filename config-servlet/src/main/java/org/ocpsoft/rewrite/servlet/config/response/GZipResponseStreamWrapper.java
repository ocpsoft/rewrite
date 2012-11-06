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
   public class FlushableGZIPOutputStream extends GZIPOutputStream
   {
      public FlushableGZIPOutputStream(OutputStream out) throws IOException
      {
         super(out);
      }

      @Override
      public void flush() throws IOException
      {
         super.flush();
      }

      @Override
      public void close() throws IOException
      {
         finish();
         super.close();
      }

   }

   @Override
   public OutputStream wrap(HttpServletRewrite rewrite, OutputStream outputStream)
   {
      rewrite.getResponse().addHeader("Content-Encoding", "gzip");

      try {
         GZIPOutputStream stream = new FlushableGZIPOutputStream(outputStream);
         return stream;
      }
      catch (IOException e) {
         throw new RewriteException("Could not wrap OutputStream", e);
      }
   }
}
