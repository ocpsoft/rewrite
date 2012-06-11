package org.ocpsoft.rewrite.transform.cache;

import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationCacheProvider;

public class ServletContextConfigurationCacheProvider extends HttpConfigurationCacheProvider
{

   @Override
   public Configuration getConfiguration(ServletContext context)
   {
      return (Configuration) context.getAttribute(this.getClass().getName());
   }

   @Override
   public void setConfiguration(ServletContext context, Configuration configuration)
   {
      context.setAttribute(this.getClass().getName(), configuration);
   }

   @Override
   public int priority()
   {
      return 0;
   }

}
