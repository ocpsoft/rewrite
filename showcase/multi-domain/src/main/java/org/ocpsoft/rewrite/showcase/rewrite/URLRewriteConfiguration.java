package org.ocpsoft.rewrite.showcase.rewrite;

import javax.servlet.ServletContext;

import com.ocpsoft.rewrite.config.Configuration;
import com.ocpsoft.rewrite.config.ConfigurationBuilder;
import com.ocpsoft.rewrite.config.Direction;
import com.ocpsoft.rewrite.servlet.config.DispatchType;
import com.ocpsoft.rewrite.servlet.config.Forward;
import com.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import com.ocpsoft.rewrite.servlet.config.Path;
import com.ocpsoft.rewrite.servlet.config.rule.Join;
import com.ocpsoft.rewrite.servlet.config.rule.TrailingSlash;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class URLRewriteConfiguration extends HttpConfigurationProvider
{
   private static final String ENTITY_NAME = "[a-zA-Z$_0-9]+";

   @Override
   public Configuration getConfiguration(final ServletContext context)
   {
      return ConfigurationBuilder
               .begin()
               .addRule(TrailingSlash.remove())
               .addRule(Join.path("/")
                        .to("/index.xhtml"))
               .addRule(Join.path("/{domain}").where("domain").matches(ENTITY_NAME)
                        .to("/scaffold/{domain}/list.xhtml").withInboundCorrection())
               .addRule(Join.path("/{domain}/{id}").where("domain").matches(ENTITY_NAME).where("id").matches("\\d+")
                        .to("/scaffold/{domain}/view.xhtml").withInboundCorrection())
               .addRule(Join.path("/{domain}/create").where("domain").matches(ENTITY_NAME)
                        .to("/scaffold/{domain}/create.xhtml").withInboundCorrection())
               .addRule(Join.path("/404").to("/faces/404.html"))
               .addRule(Join.path("/error").to("/faces/500.html"))
               .defineRule()
               .when(Direction.isInbound().and(DispatchType.isRequest()).and(Path.matches(".*\\.xhtml"))
                        .andNot(Path.matches(".*javax\\.faces\\.resource.*")).andNot(Path.matches("/rfRes/.*")))
               .perform(Forward.to("/404"));
   }

   @Override
   public int priority()
   {
      return 1;
   }
}
