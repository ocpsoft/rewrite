package org.ocpsoft.rewrite.param;

import org.junit.Test;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.mock.MockEvaluationContext;
import org.ocpsoft.rewrite.test.MockRewrite;

import static org.assertj.core.api.Assertions.assertThat;

public class ParameterBuilderConfiguredByTest
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

      Parameter<?> parameter = new DefaultParameter("foo").configuredBy(validator).configuredBy(converter);

      Object value = parameter.getConverter().convert(new MockRewrite(), new MockEvaluationContext(), new Object());

      @SuppressWarnings("unchecked")
      boolean valid = ((Validator<Object>) parameter.getValidator()).isValid(new MockRewrite(),
               new MockEvaluationContext(), value);

      assertThat(validated).isTrue();
      assertThat(valid).isFalse();
      assertThat(converted).isTrue();
      assertThat(value).isNull();
   }

}
