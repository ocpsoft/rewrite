package org.ocpsoft.rewrite.servlet.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ocpsoft.common.util.Strings;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.servlet.event.BaseRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.servlet.util.QueryStringBuilder;
import org.ocpsoft.rewrite.servlet.util.URLBuilder;

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
   private String requestPath;
   private String requestQueryString;
   private String requestQueryStringSeparator;
   private String requestUrl;

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
   public String getRequestPath()
   {
      if (this.requestPath == null)
      {
         String url = getRequest().getRequestURI();
         if (url.startsWith(getContextPath()))
         {
            url = url.substring(getContextPath().length());
         }

         this.requestPath = URLBuilder.createFrom(url).decode().toURL();
      }
      return this.requestPath;
   }

   @Override
   public String getRequestQueryStringSeparator()
   {
      if (this.requestQueryStringSeparator == null)
      {
         String queryString = getRequestQueryString();
         if ((queryString != null) && !queryString.isEmpty())
            this.requestQueryStringSeparator = "?";
         else
            return this.requestQueryStringSeparator = "";
      }
      return this.requestQueryStringSeparator;
   }

   @Override
   public String getRequestQueryString()
   {
      if (this.requestQueryString == null)
      {
         String query = getRequest().getQueryString();
         this.requestQueryString = Strings.isNullOrEmpty(query) ? "" : QueryStringBuilder.createFromEncoded(query)
                  .decode().toQueryString().substring(1);
      }
      return this.requestQueryString;
   }

   @Override
   public String getRequestURL()
   {
      if (this.requestUrl == null)
         this.requestUrl = getRequestPath() + getRequestQueryStringSeparator() + getRequestQueryString();
      return this.requestUrl;
   }
}
