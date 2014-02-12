package org.ocpsoft.rewrite.param;

import java.util.Set;

import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ParameterizedOperationConfigurationProvider implements ConfigurationProvider<Object>
{

   public class ParameterizedOperation implements Operation, Parameterized
   {

      @Override
      public void perform(Rewrite event, EvaluationContext context)
      {}

      @Override
      public Set<String> getRequiredParameterNames()
      {
         return null;
      }

      @Override
      public void setParameterStore(ParameterStore store)
      {}

   }

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
      return ConfigurationBuilder.begin().addRule().perform(new ParameterizedOperation());
   }

}
