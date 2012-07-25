package org.ocpsoft.rewrite.showcase.composite;

import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.servlet.config.EncodeQuery;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.Redirect;
import org.ocpsoft.rewrite.servlet.config.rule.Join;

public class CompositeRewriteConfiguration extends HttpConfigurationProvider
{
   @Override
   public Configuration getConfiguration(final ServletContext context)
   {

      return ConfigurationBuilder
               .begin()

               /*
                * Combine all query parameters into one encoded parameter.
                * If hacking is detected, redirect to the hackers page.
                */
               .addRule()
               .perform(EncodeQuery.params().to("c")
                        .onChecksumFailure(Redirect.temporary(context.getContextPath() + "/hacker")))

               /*
                * Show the index page at '/'
                */
               .addRule(Join.path("/").to("/index.xhtml"))
               .addRule(Join.path("/hacker").to("/hacker.xhtml"))

      ;
   }

   @Override
   public int priority()
   {
      return 0;
   }
}