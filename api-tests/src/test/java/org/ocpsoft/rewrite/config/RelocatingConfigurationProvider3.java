package org.ocpsoft.rewrite.config;

import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.SendStatus;


/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class RelocatingConfigurationProvider3 implements ConfigurationProvider<Object>
{

   @Override
   public int priority()
   {
      return 10;
   }

   @Override
   public boolean handles(final Object payload)
   {
      return true;
   }

   @Override
   public Configuration getConfiguration(final Object context)
   {
      return ConfigurationBuilder.begin()
               .addRule()
               .when(Path.matches("/priority"))
               .perform(SendStatus.code(203))
               
               .addRule()
               .when(Path.matches("/priority2"))
               .perform(SendStatus.code(203))
               
               .addRule()
               .when(Path.matches("/priority3"))
               .perform(SendStatus.code(203))
               
               .addRule()
               .when(Path.matches("/priority4"))
               .perform(SendStatus.code(203))
               .withPriority(-1)
               ;
   }

}
