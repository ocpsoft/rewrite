package org.ocpsoft.rewrite.param;

import junit.framework.Assert;

import org.junit.Test;
import org.ocpsoft.rewrite.bind.Converter;
import org.ocpsoft.rewrite.bind.Validator;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.mock.MockEvaluationContext;
import org.ocpsoft.rewrite.mock.MockRewrite;

public class ParameterBuilderTest
{

   protected boolean validated;
   protected boolean converted;

   @Test
   public void testCanValidateAndConvertWithoutBinding()
   {
      Validator<String> validator = new Validator<String>() {
         @Override
         public boolean validate(Rewrite event, EvaluationContext context, String value)
         {
            validated = true;
            return false;
         }
      };

      Converter<String> converter = new Converter<String>() {

         @Override
         public String convert(Rewrite event, EvaluationContext context, Object value)
         {
            converted = true;
            return null;
         }
      };

      Parameter<?, ?> parameter = new MockParameter("foo").validatedBy(validator).convertedBy(converter);

      Object value = parameter.convert(new MockRewrite(), new MockEvaluationContext(), new Object());
      boolean valid = parameter.validates(new MockRewrite(), new MockEvaluationContext(), value);

      Assert.assertTrue(validated);
      Assert.assertFalse(valid);
      Assert.assertTrue(converted);
      Assert.assertNull(value);
   }

}
