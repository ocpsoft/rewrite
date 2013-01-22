package org.ocpsoft.rewrite.showcase.access;

import javax.servlet.ServletContext;

import org.joda.time.DateTime;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.config.jodatime.JodaTime;
import org.ocpsoft.rewrite.config.jodatime.TimeCondition;
import org.ocpsoft.rewrite.servlet.config.DispatchType;
import org.ocpsoft.rewrite.servlet.config.Forward;
import org.ocpsoft.rewrite.servlet.config.Hostname;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.Lifecycle;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.rule.Join;

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
               .when(Direction.isInbound().andNot(Hostname.matches("localhost").or(Hostname.matches("{1}rhcloud{2}"))))
               .where("1").matches(".*").where("2").matches(".*")

               /*
                * Time based access control (only grants access during the first half of each minute)
                */
               .addRule(Join.path("/timer").to("/timer.xhtml").withInboundCorrection())
               .when(Direction.isInbound().andNot(JodaTime.matches(timeGranted)))

               .addRule()
               .when(Direction.isInbound()
                        .and(DispatchType.isForward())
                        .and(Path.matches("/timer.xhtml"))
                        .andNot(JodaTime.matches(timeGranted))
                        .and(DispatchType.isRequest()))
               .perform(Lifecycle.handled())

               .addRule()
               .when(Direction.isInbound()
                        .and(DispatchType.isRequest())
                        .andNot(Path.matches("{1}javax.faces.resource{2}")))
               .perform(Forward.to("/accessDenied.xhtml"))
               .where("1").matches(".*").where("2").matches(".*");

   }

   private final TimeCondition timeGranted = new TimeCondition() {
      @Override
      public boolean matches(final DateTime time)
      {
         return time.getSecondOfMinute() < 30;
      }
   };

   @Override
   public int priority()
   {
      return 0;
   }

}
