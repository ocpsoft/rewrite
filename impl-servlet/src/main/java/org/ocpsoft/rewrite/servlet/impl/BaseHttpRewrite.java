package org.ocpsoft.rewrite.servlet.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.servlet.event.BaseRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.urlbuilder.Address;
import org.ocpsoft.urlbuilder.AddressBuilder;

/**
 * Base class for Http {@link Rewrite} events.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class BaseHttpRewrite extends BaseRewrite<HttpServletRequest, HttpServletResponse> implements
         HttpServletRewrite
{

   /*
    * For caching and performance purposes only.
    */
   private String requestContextPath;
   private Address address;

   public BaseHttpRewrite(HttpServletRequest request, HttpServletResponse response)
   {
      super(request, response);
   }

   @Override
   public String getContextPath()
   {
      if (this.requestContextPath == null)
         this.requestContextPath = getRequest().getContextPath();
      return this.requestContextPath;
   }

   @Override
   public Address getAddress()
   {
      return getInboundAddress();
   }

   @Override
   public Address getInboundAddress()
   {
      if (this.address == null)
      {
         this.address = AddressBuilder.begin()
                  .protocol(getRequest().getScheme())
                  .host(getRequest().getServerName())
                  .port(getRequest().getServerPort())
                  .pathEncoded(getRequest().getRequestURI())
                  .queryLiteral(getRequest().getQueryString()).build();
      }
      return this.address;
   }

}
