package org.ocpsoft.rewrite.annotation.spi;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;
import org.ocpsoft.rewrite.bind.Converter;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

public class DefaultConverterProviderTest
{

   @Test
   public void testCreateRewriteConverter()
   {
      Converter<?> converter = new DefaultConverterProvider().getByType(TestConverter.class);
      assertNotNull(converter);
      assertTrue(converter instanceof TestConverter);
   }

   @Test
   public void testUnsupportedType()
   {
      Converter<?> converter = new DefaultConverterProvider().getByType(BigDecimal.class);
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
