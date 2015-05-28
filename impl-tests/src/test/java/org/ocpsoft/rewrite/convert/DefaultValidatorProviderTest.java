package org.ocpsoft.rewrite.convert;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.instance.DefaultValidatorProvider;
import org.ocpsoft.rewrite.param.Validator;

public class DefaultValidatorProviderTest
{
   @Test
   public void testCreateRewriteValidator()
   {
      Validator<?> validator = new DefaultValidatorProvider().getByValidatorType(TestValidator.class);
      assertNotNull(validator);
      assertTrue(validator instanceof TestValidator);
   }

   @Test
   public void testUnsupportedType()
   {
      Validator<?> validator = new DefaultValidatorProvider().getByValidatorType(BigDecimal.class);
      assertNull(validator);
   }

   @Test
   public void testSomeIdentifier()
   {
      Validator<?> validator = new DefaultValidatorProvider().getByValidatorId("something");
      assertNull(validator);
   }

   @Test
   public void testByTargetType()
   {
      Validator<?> validator = new DefaultValidatorProvider().getByTargetType(Integer.class);
      assertNull(validator);
   }

   public static class TestValidator implements Validator<Object>
   {
      @Override
      public boolean isValid(Rewrite event, EvaluationContext context, Object value)
      {
         return false;
      }
   }
}
