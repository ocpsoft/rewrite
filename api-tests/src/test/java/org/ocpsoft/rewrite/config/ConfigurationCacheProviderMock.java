package org.ocpsoft.rewrite.config;

import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.servlet.config.HttpConfigurationCacheProvider;


public class ConfigurationCacheProviderMock extends HttpConfigurationCacheProvider
{
   private static final String KEY = ConfigurationCacheProviderMock.class.getName() + "_cachedConfig";

   @Override
   public Configuration getConfiguration(ServletContext context)
   {
      return (Configuration) context.getAttribute(KEY);
   }

   @Override
   public void setConfiguration(ServletContext context, Configuration configuration)
   {
      context.setAttribute(KEY, configuration);
   }

   @Override
   public int priority()
   {
      return 0;
   }
}
