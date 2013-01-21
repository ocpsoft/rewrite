package org.ocpsoft.rewrite.config;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

public interface Otherwise extends ConfigurationElement
{
   /**
    * Execute the configured alternate {@link Operation} instance, if any.
    */
   public void otherwise(Rewrite event, EvaluationContext context);
}
