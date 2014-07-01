package org.ocpsoft.rewrite.config;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * An {@link OperationBuilder} that does nothing.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public final class NoOp extends DefaultOperationBuilder
{
   @Override
   public void perform(Rewrite event, EvaluationContext context)
   {}

   @Override
   public String toString()
   {
      return "new NoOp()";
   }
}