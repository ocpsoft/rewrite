package org.ocpsoft.rewrite.convert;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.instance.DefaultConverterProvider;
import org.ocpsoft.rewrite.param.Converter;

public class DefaultConverterProviderTest
{
   @Test
   public void testCreateRewriteConverter()
   {
      Converter<?> converter = new DefaultConverterProvider().getByConverterType(TestConverter.class);
      assertNotNull(converter);
      assertTrue(converter instanceof TestConverter);
   }

   @Test
   public void testUnsupportedType()
   {
      Converter<?> converter = new DefaultConverterProvider().getByConverterType(BigDecimal.class);
      assertNull(converter);
   }

   @Test
   public void testSomeIdentifier()
   {
      Converter<?> converter = new DefaultConverterProvider().getByConverterId("something");
      assertNull(converter);
   }

   @Test
   public void testByTargetType()
   {
      Converter<?> converter = new DefaultConverterProvider().getByTargetType(Integer.class);
      assertNull(converter);
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
