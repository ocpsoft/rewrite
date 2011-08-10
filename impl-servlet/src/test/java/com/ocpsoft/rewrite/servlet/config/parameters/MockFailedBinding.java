package com.ocpsoft.rewrite.servlet.config.parameters;

import com.ocpsoft.rewrite.config.Operation;
import com.ocpsoft.rewrite.context.EvaluationContext;
import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

public class MockFailedBinding implements ParameterBinding
{
   private boolean validated;
   private boolean converted;

   @Override
   public boolean validates(final HttpServletRewrite event, final EvaluationContext context, final Object value)
   {
      validated = true;
      return true;
   }

   @Override
   public Object convert(final HttpServletRewrite event, final EvaluationContext context, final Object value)
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
   public Operation getOperation(final HttpServletRewrite event, final EvaluationContext context, final Object value)
   {
      return new Operation() {
         @Override
         public void perform(final Rewrite event, final EvaluationContext context)
         {
            throw new RuntimeException("Binding failed (expected)");
         }
      };
   }

   @Override
   public Object extractBoundValue(final HttpServletRewrite event, final EvaluationContext context)
   {
      throw new RuntimeException("Binding extraction failed (expected)");
   }
}