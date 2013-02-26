package org.ocpsoft.rewrite.mock;

import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.Parameter;

public class MockFailedBinding implements Binding
{

   @Override
   public Object retrieve(final Rewrite event, final EvaluationContext context, Parameter<?> parameter)
   {
      throw new RuntimeException("Binding extraction failed (expected)");
   }

   @Override
   public boolean supportsRetrieval()
   {
      return true;
   }

   @Override
   public boolean supportsSubmission()
   {
      return true;
   }

   @Override
   public Object submit(final Rewrite event, final EvaluationContext context, Parameter<?> parameter, final Object value)
   {
      throw new RuntimeException("Binding failed (expected)");
   }
}