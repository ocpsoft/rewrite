package com.ocpsoft.rewrite.mock;

import com.ocpsoft.rewrite.bind.Binding;
import com.ocpsoft.rewrite.context.EvaluationContext;
import com.ocpsoft.rewrite.event.Rewrite;

public class MockBinding implements Binding
{

   private boolean submitted;
   private boolean validated;
   private boolean converted;
   private boolean extracted;
   private Object value;

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

   public boolean isSubmitted()
   {
      return submitted;
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
      if (value.getClass().isArray())
      {
         Object[] temp = (Object[]) value;
         if (temp.length > 0)
         {
            this.value = temp[0];
         }
      }
      else
         this.value = value;

      submitted = true;
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

   public Object getBoundValue()
   {
      return value;
   }
}