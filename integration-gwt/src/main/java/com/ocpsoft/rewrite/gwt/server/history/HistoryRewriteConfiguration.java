package com.ocpsoft.rewrite.gwt.server.history;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;

import com.ocpsoft.logging.Logger;
import com.ocpsoft.rewrite.config.Configuration;
import com.ocpsoft.rewrite.config.ConfigurationBuilder;
import com.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import com.ocpsoft.rewrite.servlet.config.Method;
import com.ocpsoft.rewrite.servlet.config.Path;
import com.ocpsoft.rewrite.servlet.config.QueryString;
import com.ocpsoft.rewrite.servlet.config.Response;
import com.ocpsoft.rewrite.servlet.util.ContextUtil;

public class HistoryRewriteConfiguration extends HttpConfigurationProvider
{
   private static final String DISABLED_PARAM = "com.ocpsoft.rewrite.gwt.history.disableCookies";
   public static Logger log = Logger.getLogger(HistoryRewriteConfiguration.class);

   Configuration config = null;

   public Configuration getConfiguration(ServletContext context)
   {
      if (!ContextUtil.getInitParamBoolean(context, DISABLED_PARAM))
      {
         if (log.isDebugEnabled())
            log.debug(DISABLED_PARAM + " [false]");

         if (config == null)
         {
            config = ConfigurationBuilder
                     .begin()
                     .defineRule()
                     .perform(Response.addCookie(new Cookie("com.ocpsoft.rewrite.gwt.history.contextPath", context
                              .getContextPath())))

                     .defineRule()
                     .when(Method.isHead().and(
                              QueryString.parameterExists("com.ocpsoft.rewrite.gwt.history.contextPath")))
                     .perform(Response.setCode(200).and(
                              Response.addHeader("com.ocpsoft.rewrite.gwt.history.contextPath",
                                       context.getContextPath())));
         }
      }
      return config;
   }

   public int priority()
   {
      return -5;
   }
}
