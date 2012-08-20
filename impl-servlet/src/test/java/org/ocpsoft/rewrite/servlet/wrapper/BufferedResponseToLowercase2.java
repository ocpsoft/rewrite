package org.ocpsoft.rewrite.servlet.wrapper;

import org.ocpsoft.rewrite.servlet.config.response.ResponseBuffer;
import org.ocpsoft.rewrite.servlet.config.response.ResponseInterceptor;
import org.ocpsoft.rewrite.servlet.config.response.ResponseInterceptorChain;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

public class BufferedResponseToLowercase2 implements ResponseInterceptor
{
   @Override
   public void intercept(HttpServletRewrite event, ResponseBuffer buffer, ResponseInterceptorChain chain)
   {
      buffer.setContents(new String(buffer.getContents(), buffer.getCharset()).replaceAll("uppercase", "lowercase")
               .getBytes());
      chain.next(event, buffer, chain);
   }
}
