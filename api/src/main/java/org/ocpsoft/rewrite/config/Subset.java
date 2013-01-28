package org.ocpsoft.rewrite.config;

import java.util.Collections;
import java.util.List;

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

public class Subset extends DefaultOperationBuilder implements CompositeOperation
{
   private Configuration config;

   private Subset(Configuration config)
   {
      Assert.notNull(config, "Configuration must not be null.");
      this.config = config;
   }

   public static Subset when(Configuration config)
   {
      return new Subset(config);
   }

   /*
    * Executors
    */

   @Override
   public void perform(Rewrite event, EvaluationContext context)
   {
      // int providerCount = providers.size();
      // for (int i = 0; i < providerCount; i++)
      // {
      // RewriteProvider<ServletContext, Rewrite> provider = providers.get(i);
      // if (provider.handles(event))
      // {
      // provider.rewrite(event);
      //
      // if (event.getFlow().is(BaseRewrite.Flow.HANDLED))
      // {
      // log.debug("Event flow marked as HANDLED. No further processing will occur.");
      // break;
      // }
      // }
      // }
   }

   /*
    * Getters
    */

   @Override
   public List<Operation> getOperations()
   {
      return Collections.emptyList();
   }

}
