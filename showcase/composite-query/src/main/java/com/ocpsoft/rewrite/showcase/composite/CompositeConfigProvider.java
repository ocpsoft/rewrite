package com.ocpsoft.rewrite.showcase.composite;

import javax.servlet.ServletContext;

import com.ocpsoft.rewrite.config.Configuration;
import com.ocpsoft.rewrite.config.ConfigurationBuilder;
import com.ocpsoft.rewrite.servlet.config.EncodeQuery;
import com.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import com.ocpsoft.rewrite.servlet.config.Redirect;
import com.ocpsoft.rewrite.servlet.config.rule.Join;

public class CompositeConfigProvider extends HttpConfigurationProvider
{
   @Override
   public Configuration getConfiguration(final ServletContext context)
   {
      EncodeQuery encodeQuery = EncodeQuery.params().to("c");
      encodeQuery.onChecksumFailure(Redirect.temporary(context.getContextPath() + "/hacker"));

      return ConfigurationBuilder.begin()

               /*
                * Combine all query parameters into one encoded parameter.
                * If hacking is detected, redirect to the hackers page.
                */
               .defineRule().perform(encodeQuery)

               /*
                * Show the index page at '/'
                */
               .addRule(Join.path("/").to("/index.xhtml"))
               .addRule(Join.path("/hacker").to("/hacker.xhtml"));
   }

   @Override
   public int priority()
   {
      return 0;
   }
}