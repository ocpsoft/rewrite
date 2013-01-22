package org.ocpsoft.rewrite.config;

import java.util.List;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

public class Subset extends DefaultOperationBuilder implements CompositeCondition, CompositeOperation
{
   public static Subset when(Condition condition)
   {
      throw new IllegalStateException("not implemented");
   }

   public static ConfigurationRuleBuilder when(Rule rule)
   {
      throw new IllegalStateException("not implemented");
   }

   @Override
   public boolean evaluate(Rewrite event, EvaluationContext context)
   {
      throw new IllegalStateException("not implemented");
   }

   @Override
   public void perform(Rewrite event, EvaluationContext context)
   {
      throw new IllegalStateException("not implemented");
   }

   @Override
   public List<Operation> getOperations()
   {
      throw new IllegalStateException("not implemented");
   }

   @Override
   public List<Condition> getConditions()
   {
      throw new IllegalStateException("not implemented");
   }

   public Subset perform(Operation condition)
   {
      throw new IllegalStateException("not implemented");
   }

   public Subset otherwise(Operation condition)
   {
      throw new IllegalStateException("not implemented");
   }

}
