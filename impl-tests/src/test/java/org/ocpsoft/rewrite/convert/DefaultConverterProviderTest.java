package org.ocpsoft.rewrite.convert;

import java.math.BigDecimal;

import org.junit.Test;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.instance.DefaultConverterProvider;
import org.ocpsoft.rewrite.param.Converter;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultConverterProviderTest
{
   @Test
   public void testCreateRewriteConverter()
   {
      Converter<?> converter = new DefaultConverterProvider().getByConverterType(TestConverter.class);
      assertThat(converter).isNotNull();
      assertThat(converter).isInstanceOf(TestConverter.class);
   }

   @Test
   public void testUnsupportedType()
   {
      Converter<?> converter = new DefaultConverterProvider().getByConverterType(BigDecimal.class);
      assertThat(converter).isNull();
   }

   @Test
   public void testSomeIdentifier()
   {
      Converter<?> converter = new DefaultConverterProvider().getByConverterId("something");
      assertThat(converter).isNull();
   }

   @Test
   public void testByTargetType()
   {
      Converter<?> converter = new DefaultConverterProvider().getByTargetType(Integer.class);
      assertThat(converter).isNull();
   }

   public static class TestConverter implements Converter<Object>
   {
      @Override
      public Object convert(Rewrite event, EvaluationContext context, Object value)
      {
         return null;
      }
   }

}
