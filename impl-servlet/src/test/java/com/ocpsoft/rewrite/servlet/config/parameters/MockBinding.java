package com.ocpsoft.rewrite.servlet.config.parameters;

import com.ocpsoft.rewrite.bind.Binding;
import com.ocpsoft.rewrite.context.EvaluationContext;
import com.ocpsoft.rewrite.event.Rewrite;

public class MockBinding implements Binding
{

   private boolean bound;
   private boolean validated;
   private boolean converted;
   private boolean extracted;

   @Override
   public boolean validates(final Rewrite event, final EvaluationContext context, final Object value)
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

   public boolean isBound()
   {
      return bound;
   }

   public boolean isValidated()
   {
      return validated;
   }

   public boolean isExtracted()
   {
      return extracted;
   }

   @Override
   public Object submit(final Rewrite event, final EvaluationContext context, final Object value)
   {
      bound = true;
      return null;
   }

   @Override
   public Object retrieve(final Rewrite event, final EvaluationContext context)
   {
      extracted = true;
      return new Object();
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
}