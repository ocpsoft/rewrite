package org.ocpsoft.rewrite.showcase.access;

import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.servlet.config.DispatchType;
import org.ocpsoft.rewrite.servlet.config.Domain;
import org.ocpsoft.rewrite.servlet.config.Forward;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.Lifecycle;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.rule.Join;

import java.time.LocalDateTime;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class AccessRewriteConfiguration extends HttpConfigurationProvider
{
   @Override
   public Configuration getConfiguration(final ServletContext context)
   {
      return ConfigurationBuilder.begin()
               /*
                * Enable the root menu.
                */
               .addRule(Join.path("/").to("/index.xhtml").withInboundCorrection())

               /*
                * Domain based access control (only grants access to specific domains)
                */
               .addRule(Join.path("/domain").to("/domain.xhtml").withInboundCorrection())
               .when(Direction.isInbound().and(Domain.matches("localhost").or(Domain.matches("{*}rhcloud{*}"))))

               /*
                * Time based access control (only grants access during the first half of each minute)
                */
               .addRule(Join.path("/timer").to("/timer.xhtml").withInboundCorrection())
               .when(Direction.isInbound().and(timeGranted))

               .addRule()
               .when(Direction.isInbound()
                        .and(DispatchType.isForward())
                        .and(Path.matches("/timer.xhtml"))
                        .andNot(timeGranted)
                        .and(DispatchType.isRequest()))
               .perform(Lifecycle.handled())

               .addRule()
               .when(Direction.isInbound()
                        .and(DispatchType.isRequest())
                        .andNot(Path.matches("{*}javax.faces.resource{*}")))
               .perform(Forward.to("/accessDenied.xhtml"));

   }

   private final Condition timeGranted = new Condition() {
      @Override
      public boolean evaluate(Rewrite event, EvaluationContext context)
      {
         return LocalDateTime.now().getSecond() < 30;
      }
   };

   @Override
   public int priority()
   {
      return 10;
   }

}
