package org.ocpsoft.rewrite.servlet.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
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
   private static final Pattern JSESSIONID_PATTERN = Pattern.compile("(?i)^(.*);jsessionid=[\\w\\.\\-]+(.*)");
   private static final String JSESSIONID_REPLACEMENT = "$1$2";

   /*
    * For caching and performance purposes only.
    */
   private String requestContextPath;
   private Address address;

   public BaseHttpRewrite(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext)
   {
      super(request, response, servletContext);
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
         String requestURI = getRequest().getRequestURI();

         Matcher sessionIdMatcher = JSESSIONID_PATTERN.matcher(requestURI);
         if (sessionIdMatcher.matches())
         {
            requestURI = sessionIdMatcher.replaceFirst(JSESSIONID_REPLACEMENT);
         }

         this.address = AddressBuilder.begin()
                  .scheme(getRequest().getScheme())
                  .domain(getRequest().getServerName())
                  .port(getRequest().getServerPort())
                  .pathEncoded(requestURI)
                  .queryLiteral(getRequest().getQueryString()).build();
      }
      return this.address;
   }
}
