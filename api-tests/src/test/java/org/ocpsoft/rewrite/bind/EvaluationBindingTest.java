package org.ocpsoft.rewrite.bind;

import javax.servlet.http.HttpServletRequest;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.mock.MockEvaluationContext;
import org.ocpsoft.rewrite.servlet.impl.HttpInboundRewriteImpl;
import org.ocpsoft.rewrite.servlet.impl.HttpOutboundRewriteImpl;

public class EvaluationBindingTest
{
   private Rewrite inbound;
   private Rewrite outbound;
   private HttpServletRequest request;

   @Before
   public void before()
   {
      request = Mockito.mock(HttpServletRequest.class);
      Mockito.when(request.getServerName())
      .thenReturn("example.com");

      inbound = new HttpInboundRewriteImpl(request, null);
      outbound = new HttpOutboundRewriteImpl(request, null, "http://example.com:8080/path?query=value");
   }

   @Test(expected = IllegalArgumentException.class)
   public void testCannotAccessNonexistentEvaluationContextPropertyInbound() throws Exception
   {
      MockEvaluationContext context = new MockEvaluationContext();
      Evaluation.property("property").retrieve(inbound, context);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testCannotAccessNonexistentEvaluationContextPropertyOutbound() throws Exception
   {
      MockEvaluationContext context = new MockEvaluationContext();
      Evaluation.property("property").retrieve(outbound, context);
   }

   @Test
   public void testCanAccessEvaluationContextPropertyInbound() throws Exception
   {
      MockEvaluationContext context = new MockEvaluationContext();
      try {
         Evaluation.property("property").retrieve(inbound, context);
         Assert.fail();
      }
      catch (IllegalArgumentException e) {}

      Evaluation.property("property").submit(inbound, context, "Foo");
      Object value = Evaluation.property("property").retrieve(inbound, context);

      Assert.assertEquals("Foo", value);
   }

   @Test
   public void testCanAccessEvaluationContextPropertyOutbound() throws Exception
   {
      MockEvaluationContext context = new MockEvaluationContext();
      try {
         Evaluation.property("property").retrieve(outbound, context);
         Assert.fail();
      }
      catch (IllegalArgumentException e) {}

      Evaluation.property("property").submit(outbound, context, "Foo");
      Object value = Evaluation.property("property").retrieve(outbound, context);

      Assert.assertEquals("Foo", value);
   }

   @Test
   public void testCanAccessEvaluationContextConvertedPropertyInbound() throws Exception
   {
      MockEvaluationContext context = new MockEvaluationContext();

      Converter<String> converter = new Converter<String>() {
         @Override
         public String convert(Rewrite event, EvaluationContext context, Object value)
         {
            return "Bar";
         }
      };

      Evaluation.property("property").convertedBy(converter).submit(inbound, context, "Foo");
      Assert.assertEquals("Foo", Evaluation.property("property").retrieve(inbound, context));
      Assert.assertEquals("Bar", Evaluation.property("property").retrieveConverted(inbound, context));
   }

   @Test
   public void testCanAccessEvaluationContextConvertedPropertyOutbound() throws Exception
   {
      MockEvaluationContext context = new MockEvaluationContext();

      Converter<String> converter = new Converter<String>() {
         @Override
         public String convert(Rewrite event, EvaluationContext context, Object value)
         {
            return "Bar";
         }
      };

      Evaluation.property("property").convertedBy(converter).submit(outbound, context, "Foo");
      Assert.assertEquals("Foo", Evaluation.property("property").retrieve(outbound, context));
      Assert.assertEquals("Bar", Evaluation.property("property").retrieveConverted(outbound, context));
   }

}
