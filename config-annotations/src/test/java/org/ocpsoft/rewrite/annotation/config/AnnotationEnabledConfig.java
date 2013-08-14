package org.ocpsoft.rewrite.annotation.config;

import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.annotation.RewriteConfiguration;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.SendStatus;

@RewriteConfiguration
public class AnnotationEnabledConfig implements ConfigurationProvider<ServletContext>
{
   @Override
   public int priority()
   {
      return 5;
   }

   @Override
   public boolean handles(Object payload)
   {
      return payload instanceof ServletContext;
   }

   @Override
   public Configuration getConfiguration(ServletContext context)
   {
      return ConfigurationBuilder.begin()
               .addRule()
               .when(Path.matches("/config-enabled-by-annotation"))
               .perform(SendStatus.code(299));
   }

}
