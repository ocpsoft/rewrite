package com.ocpsoft.rewrite.showcase.mega;

import javax.servlet.ServletContext;

import com.ocpsoft.rewrite.config.Configuration;
import com.ocpsoft.rewrite.config.ConfigurationBuilder;
import com.ocpsoft.rewrite.config.Direction;
import com.ocpsoft.rewrite.config.Not;
import com.ocpsoft.rewrite.servlet.config.DispatchType;
import com.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import com.ocpsoft.rewrite.servlet.config.Path;
import com.ocpsoft.rewrite.servlet.config.rule.Join;

public class CompositeConfigProvider extends HttpConfigurationProvider
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