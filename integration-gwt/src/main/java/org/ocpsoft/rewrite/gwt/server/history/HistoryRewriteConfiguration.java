package org.ocpsoft.rewrite.gwt.server.history;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;

import org.ocpsoft.logging.Logger;

import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.Method;
import org.ocpsoft.rewrite.servlet.config.Query;
import org.ocpsoft.rewrite.servlet.config.Response;
import org.ocpsoft.rewrite.servlet.util.ContextUtil;

public class HistoryRewriteConfiguration extends HttpConfigurationProvider
{
   private static final String DISABLED_PARAM = "org.ocpsoft.rewrite.gwt.history.disableCookies";
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
            String contextPath = context.getContextPath();
            if (contextPath == null || contextPath.isEmpty())
               contextPath = "/";

            config = ConfigurationBuilder
                     .begin()
                     .addRule()
                     .perform(Response
                              .addCookie(new Cookie("org.ocpsoft.rewrite.gwt.history.contextPath", contextPath)))

                     .addRule()
                     .when(Method.isHead().and(
                              Query.parameterExists("org.ocpsoft.rewrite.gwt.history.contextPath")))
                     .perform(Response.setStatus(200).and(
                              Response.addHeader("org.ocpsoft.rewrite.gwt.history.contextPath",
                                       contextPath)));
         }
      }
      return config;
   }

   public int priority()
   {
      return -5;
   }
}
