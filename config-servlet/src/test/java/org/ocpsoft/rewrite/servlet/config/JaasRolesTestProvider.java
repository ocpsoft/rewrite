package org.ocpsoft.rewrite.servlet.config;

import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Direction;

public class JaasRolesTestProvider extends HttpConfigurationProvider
{

   @Override
   public Configuration getConfiguration(final ServletContext context)
   {
      Configuration config = ConfigurationBuilder
               .begin()

               .addRule()
               .when(JAASRoles.required("admin").and(Direction.isInbound())
                        .and(Path.matches("/admin/{tail}")))
               .perform(SendStatus.code(200))
               .where("tail").matches(".*");

      return config;
   }

   @Override
   public int priority()
   {
      return 0;
   }

}
