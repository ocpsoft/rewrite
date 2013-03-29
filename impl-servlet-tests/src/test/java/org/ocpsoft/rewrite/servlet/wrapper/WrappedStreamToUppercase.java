package org.ocpsoft.rewrite.servlet.wrapper;

import java.io.IOException;
import java.io.OutputStream;

import org.ocpsoft.rewrite.servlet.config.response.ResponseStreamWrapper;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

public class WrappedStreamToUppercase implements ResponseStreamWrapper
{
   @Override
   public OutputStream wrap(HttpServletRewrite rewrite, final OutputStream outputStream)
   {
      return new OutputStream() {

         @Override
         public void write(int b)
         {
            try {
               outputStream.write(Character.toUpperCase(b));
            }
            catch (IOException e) {
               e.printStackTrace();
            }
         }

         @Override
         public void write(byte[] bytes) throws IOException
         {
            outputStream.write(bytes);
         }

         @Override
         public void write(byte[] bytes, int off, int len)
         {
            try {
               for (int i = 0; i < bytes.length; i++) {
                  bytes[i] = (byte) Character.toUpperCase(bytes[i]);
               }
               outputStream.write(bytes, off, len);
            }
            catch (IOException e) {
               e.printStackTrace();
            }
         }

      };
   }

   @Override
   public void finish(HttpServletRewrite rewrite)
   {
   }

}
