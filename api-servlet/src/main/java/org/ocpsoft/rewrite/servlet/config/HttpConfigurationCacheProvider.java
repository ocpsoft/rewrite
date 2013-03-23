package org.ocpsoft.rewrite.servlet.config;

import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.spi.ConfigurationCacheProvider;

/**
 * Configuration cache provider for HTTP/Servlet environments.
 * 
 * @see org.ocpsoft.rewrite.config.ConfigurationProvider
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public abstract class HttpConfigurationCacheProvider implements ConfigurationCacheProvider<ServletContext>
{
   @Override
   public boolean handles(Object payload)
   {
      return payload instanceof ServletContext;
   }
}
