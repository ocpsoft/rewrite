package org.ocpsoft.rewrite.showcase.access;

import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.Constraint;
import org.ocpsoft.rewrite.servlet.config.Header;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.SendStatus;
import org.ocpsoft.rewrite.servlet.config.URL;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class InputValidationRewriteConfiguration extends HttpConfigurationProvider
{
   @Override
   public Configuration getConfiguration(final ServletContext context)
   {
      return ConfigurationBuilder.begin()

               .addRule()
               .when(Direction.isInbound()
                        .and(URL.matches("{badthings}")
                                 .or(Header.exists("{badthings}"))
                                 .or(Header.valueExists("{badthings}")
                                 )
                        )
               )
               .perform(SendStatus.error(403, "Forbidden")) // or take some protective action
               .where("badthings").constrainedBy(selectedCharacters);

   }

   private Constraint<String> selectedCharacters = new Constraint<String>() {
      @Override
      public boolean isSatisfiedBy(Rewrite event, EvaluationContext context, String value)
      {
         return value.matches(".*[+%'\"^$#\\\\(\\\\)*<>].*");
         // Don't forget unicode!
      }
   };

   @Override
   public int priority()
   {
      return Integer.MIN_VALUE;
   }

}
