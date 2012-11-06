package org.ocpsoft.rewrite.servlet;

import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public abstract class RewriteWrappedRequest extends HttpServletRequestWrapper
{
   public static RewriteWrappedRequest getCurrentInstance(ServletRequest request)
   {
      RewriteWrappedRequest wrapper = (RewriteWrappedRequest) request.getAttribute(RewriteWrappedRequest.class
               .getName());
      return wrapper;
   }

   protected static void setCurrentInstance(final RewriteWrappedRequest instance)
   {
      instance.setAttribute(RewriteWrappedRequest.class.getName(), instance);
   }

   public RewriteWrappedRequest(HttpServletRequest request)
   {
      super(request);
   }

   abstract public Map<String, String[]> getModifiableParameters();
}
