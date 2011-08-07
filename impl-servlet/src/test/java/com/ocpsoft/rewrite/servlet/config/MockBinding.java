package com.ocpsoft.rewrite.servlet.config;

import com.ocpsoft.rewrite.EvaluationContext;
import com.ocpsoft.rewrite.config.Operation;
import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.servlet.config.parameters.ParameterBinding;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

public class MockBinding implements ParameterBinding
{

   private boolean bound;
   private boolean validated;
   private boolean converted;
   private boolean extracted;

   @Override
   public boolean validates(final HttpServletRewrite event, final EvaluationContext context, final Object value)
   {
      validated = true;
      return true;
   }

   @Override
   public Object convert(final HttpServletRewrite event, final EvaluationContext context, final String value)
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
   public Operation getOperation(final HttpServletRewrite event, final EvaluationContext context, final Object value)
   {
      return new Operation() {
         @Override
         public void perform(final Rewrite event, final EvaluationContext context)
         {
            bound = true;
         }
      };
   }

   @Override
   public Object extractBoundValue(final HttpServletRewrite event, final EvaluationContext context)
   {
      extracted = true;
      return new Object();
   }
}