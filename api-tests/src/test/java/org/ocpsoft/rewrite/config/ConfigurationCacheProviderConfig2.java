package org.ocpsoft.rewrite.config;

import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.SendStatus;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public class ConfigurationCacheProviderConfig2 implements ConfigurationProvider<Object>
{
   private static int accessCount = 200;

   @Override
   public int priority()
   {
      return 1;
   }

   @Override
   public boolean handles(final Object payload)
   {
      return true;
   }

   @Override
   public Configuration getConfiguration(final Object context)
   {
      accessCount++;

      try {
         Thread.sleep(1000);
      }
      catch (InterruptedException e) {
      }

      return ConfigurationBuilder.begin().addRule()
               .when(Path.matches("/cache2"))
               .perform(SendStatus.code(accessCount));
   }

}
