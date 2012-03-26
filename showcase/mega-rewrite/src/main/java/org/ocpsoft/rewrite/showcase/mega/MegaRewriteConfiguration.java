package org.ocpsoft.rewrite.showcase.mega;

import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.servlet.config.DispatchType;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.rule.Join;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.config.Not;

public class MegaRewriteConfiguration extends HttpConfigurationProvider
{
   @Override
   public Configuration getConfiguration(final ServletContext context)
   {
      return ConfigurationBuilder
               .begin()

               .addRule(Join.path("/{one}/{two}/{three}").to("/{one}/{two}/{three}.xhtml")
                        .when(Not.any(Path.matches(".*javax.faces.resource.*"))
                                 .and(DispatchType.isRequest().or(Direction.isOutbound()))))

               .addRule(Join.path("/{one}/{two}").to("/{one}/{two}.xhtml")
                        .when(Not.any(Path.matches(".*javax.faces.resource.*"))
                                 .and(DispatchType.isRequest().or(Direction.isOutbound()))))

               .addRule(Join.path("/{one}").to("/{one}.xhtml")
                        .when(Not.any(Path.matches(".*javax.faces.resource.*"))
                                 .and(DispatchType.isRequest().or(Direction.isOutbound()))))

               .addRule(Join.path("/").to("/index.xhtml"));
   }

   @Override
   public int priority()
   {
      return 0;
   }
}