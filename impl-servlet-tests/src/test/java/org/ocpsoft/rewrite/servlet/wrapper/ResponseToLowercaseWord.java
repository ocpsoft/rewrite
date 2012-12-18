package org.ocpsoft.rewrite.servlet.wrapper;

import org.ocpsoft.rewrite.servlet.config.response.ResponseContent;
import org.ocpsoft.rewrite.servlet.config.response.ResponseContentInterceptor;
import org.ocpsoft.rewrite.servlet.config.response.ResponseContentInterceptorChain;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

public class ResponseToLowercaseWord implements ResponseContentInterceptor
{
   @Override
   public void intercept(HttpServletRewrite event, ResponseContent buffer, ResponseContentInterceptorChain chain)
   {
      buffer.setContents(new String(buffer.getContents(), buffer.getCharset()).replaceAll("uppercase", "lowercase")
               .getBytes());
      chain.proceed();
   }
}
