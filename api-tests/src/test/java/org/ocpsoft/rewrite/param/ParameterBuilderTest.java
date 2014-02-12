package org.ocpsoft.rewrite.param;


import org.junit.Assert;
import org.junit.Test;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.mock.MockEvaluationContext;
import org.ocpsoft.rewrite.test.MockRewrite;

public class ParameterBuilderTest
{

   protected boolean validated;
   protected boolean converted;

   @Test
   public void testCanValidateAndConvertWithoutBinding()
   {
      Validator<String> validator = new Validator<String>() {
         @Override
         public boolean isValid(Rewrite event, EvaluationContext context, String value)
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

      Parameter<?> parameter = new DefaultParameter("foo").validatedBy(validator).convertedBy(converter);

      Object value = parameter.getConverter().convert(new MockRewrite(), new MockEvaluationContext(), new Object());

      @SuppressWarnings("unchecked")
      boolean valid = ((Validator<Object>) parameter.getValidator()).isValid(new MockRewrite(),
               new MockEvaluationContext(), value);

      Assert.assertTrue(validated);
      Assert.assertFalse(valid);
      Assert.assertTrue(converted);
      Assert.assertNull(value);
   }

}
