package org.ocpsoft.rewrite.mock;

import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

public class MockFailedBinding implements Binding
{
   private boolean validated;
   private boolean converted;

   @Override
   public boolean validate(final Rewrite event, final EvaluationContext context, final Object value)
   {
      validated = true;
      return true;
   }

   @Override
   public Object convert(final Rewrite event, final EvaluationContext context, final Object value)
   {
      converted = true;
      return value;
   }

   public boolean isConverted()
   {
      return converted;
   }

   public boolean isValidated()
   {
      return validated;
   }

   @Override
   public Object retrieve(final Rewrite event, final EvaluationContext context)
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
   public Object submit(final Rewrite event, final EvaluationContext context, final Object value)
   {
      throw new RuntimeException("Binding failed (expected)");
   }
}