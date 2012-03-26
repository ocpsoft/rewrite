package org.ocpsoft.rewrite.servlet.util;

import javax.servlet.ServletContext;

import org.ocpsoft.common.util.Assert;

/**
 * Utility method for easily interacting with the {@link ServletContext}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ContextUtil
{
   /**
    * Retrieve the named context-parameter from the given {@link ServletContext}. If the parameter is not defined,
    * return false, otherwise return the value as a boolean. If the value cannot be converted to a boolean type, return
    * false.
    */
   public static boolean getInitParamBoolean(ServletContext context, String name)
   {
      boolean result = false;
      String value = getInitParam(context, name, null);
      if ("true".equalsIgnoreCase(value))
      {
         result = true;
      }
      return result;
   }

   /**
    * Retrieve the named context-parameter from the given {@link ServletContext}. If the parameter is not defined,
    * return null.
    */
   public static String getInitParam(ServletContext context, String name)
   {
      return getInitParam(context, name, null);
   }

   /**
    * Retrieve the named context-parameter from the given {@link ServletContext}. If the parameter is not defined,
    * return the default value instead.
    */
   public static String getInitParam(ServletContext context, String name, String deflt)
   {
      Assert.notNull(context, "Servlet context must not be null.");
      Assert.notNull(context, "Cookie name must not be null.");

      String value = context.getInitParameter(name);

      if (value == null)
      {
         value = deflt;
      }

      return value;
   }
}
