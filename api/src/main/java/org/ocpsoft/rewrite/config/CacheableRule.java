package org.ocpsoft.rewrite.config;

/**
 * A {@link Rule} capable of providing internal {@link ConfigurationElement} instances for caching purposes.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface CacheableRule extends Rule
{
   /**
    * Get an {@link Condition} representing the logic this {@link Rule} uses during calls to
    * {@link #evaluate(org.ocpsoft.rewrite.event.Rewrite, org.ocpsoft.rewrite.context.EvaluationContext)}
    */
   public Condition getCondition();

   /**
    * Get an {@link Operation} representing the logic this {@link Rule} uses during calls to
    * {@link #perform(org.ocpsoft.rewrite.event.Rewrite, org.ocpsoft.rewrite.context.EvaluationContext)}
    */
   public Operation getOperation();

   /**
    * Get an {@link Operation} representing the logic this {@link Rule} uses during calls to
    * {@link #otherwise(org.ocpsoft.rewrite.event.Rewrite, org.ocpsoft.rewrite.context.EvaluationContext)}
    */
   public Operation getOtherwise();
}
