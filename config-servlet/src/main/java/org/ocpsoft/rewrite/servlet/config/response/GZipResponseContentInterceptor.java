package org.ocpsoft.rewrite.servlet.config.response;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import org.ocpsoft.common.util.Streams;
import org.ocpsoft.rewrite.exception.RewriteException;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * A {@link ResponseContentInterceptor} that compresses the response output to GZip format and sets the proper response
 * headers.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class GZipResponseContentInterceptor implements ResponseContentInterceptor
{
   @Override
   public void intercept(HttpServletRewrite event, ResponseContent buffer, ResponseContentInterceptorChain chain)
   {
      /*
       * Do our work after all other interceptors.
       */
      chain.proceed();

      byte[] contents = buffer.getContents();
      ByteArrayOutputStream result = new ByteArrayOutputStream();
      try {
         GZIPOutputStream gzip = new GZIPOutputStream(result);
         Streams.copy(new ByteArrayInputStream(contents), gzip);
         gzip.close();

         contents = result.toByteArray();
         buffer.setContents(contents);

         event.getResponse().setContentLength(contents.length);
         event.getResponse().addHeader("Content-Encoding", "gzip");
      }
      catch (IOException e) {
         throw new RewriteException("Failed to GZIP compress output content: ", e);
      }

   }
}
