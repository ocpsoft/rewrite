package org.ocpsoft.rewrite.config;


/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class NullValueConfigurationProvider implements ConfigurationProvider<Object>
{

   @Override
   public int priority()
   {
      return 0;
   }

   @Override
   public boolean handles(final Object payload)
   {
      return true;
   }

   @Override
   public Configuration getConfiguration(final Object context)
   {
      return null;
   }

}
