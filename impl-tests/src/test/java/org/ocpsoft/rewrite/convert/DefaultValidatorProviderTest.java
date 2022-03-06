package org.ocpsoft.rewrite.convert;

import java.math.BigDecimal;

import org.junit.Test;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.instance.DefaultValidatorProvider;
import org.ocpsoft.rewrite.param.Validator;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultValidatorProviderTest
{
   @Test
   public void testCreateRewriteValidator()
   {
      Validator<?> validator = new DefaultValidatorProvider().getByValidatorType(TestValidator.class);
      assertThat(validator).isNotNull();
      assertThat(validator).isInstanceOf(TestValidator.class);
   }

   @Test
   public void testUnsupportedType()
   {
      Validator<?> validator = new DefaultValidatorProvider().getByValidatorType(BigDecimal.class);
      assertThat(validator).isNull();
   }

   @Test
   public void testSomeIdentifier()
   {
      Validator<?> validator = new DefaultValidatorProvider().getByValidatorId("something");
      assertThat(validator).isNull();
   }

   @Test
   public void testByTargetType()
   {
      Validator<?> validator = new DefaultValidatorProvider().getByTargetType(Integer.class);
      assertThat(validator).isNull();
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
