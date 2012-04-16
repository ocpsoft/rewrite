package org.ocpsoft.rewrite.servlet.event;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.mock.MockEvaluationContext;
import org.ocpsoft.rewrite.servlet.event.BaseRewrite.Flow;
import org.ocpsoft.rewrite.test.MockServletRewrite;

public class SubflowTaskTest
{
   private ServletRewrite<?, ?> rewrite;

   @Before
   public void before()
   {
      rewrite = new MockServletRewrite(null, null);
   }

   @Test
   public void testSubflowRestoresOriginalFlowState()
   {
      rewrite.setFlow(Flow.REDIRECT_PERMANENT);

      Flow result = SubflowTask.perform(rewrite, new MockEvaluationContext(), new SubflowTask() {

         @Override
         public void performInSubflow(ServletRewrite<?, ?> event, EvaluationContext context)
         {
            Assert.assertEquals(Flow.REDIRECT_PERMANENT, rewrite.getFlow());
            event.setFlow(Flow.HANDLED);
         }
      });

      Assert.assertEquals(Flow.HANDLED, result);
      Assert.assertEquals(Flow.REDIRECT_PERMANENT, rewrite.getFlow());
   }

   @Test
   public void testSubflowRestoresOriginalFlowStateWithInitializedFlow()
   {
      rewrite.setFlow(Flow.REDIRECT_PERMANENT);

      Flow result = SubflowTask.perform(rewrite, new MockEvaluationContext(), Flow.INCLUDE, new SubflowTask() {

         @Override
         public void performInSubflow(ServletRewrite<?, ?> event, EvaluationContext context)
         {
            Assert.assertEquals(Flow.INCLUDE, rewrite.getFlow());
            event.setFlow(Flow.HANDLED);
         }
      });

      Assert.assertEquals(Flow.HANDLED, result);
      Assert.assertEquals(Flow.REDIRECT_PERMANENT, rewrite.getFlow());
   }

   @Test
   public void testSubflowRestoresOriginalFlowStateAfterException()
   {
      rewrite.setFlow(Flow.REDIRECT_PERMANENT);

      Flow result = null;
      try {
         result = SubflowTask.perform(rewrite, new MockEvaluationContext(), new SubflowTask() {

            @Override
            public void performInSubflow(ServletRewrite<?, ?> event, EvaluationContext context)
            {
               Assert.assertEquals(Flow.REDIRECT_PERMANENT, rewrite.getFlow());
               event.setFlow(Flow.HANDLED);
               throw new RuntimeException();
            }

         });
         Assert.fail();
      }
      catch (RuntimeException e) {}

      Assert.assertNull(result);
      Assert.assertEquals(Flow.REDIRECT_PERMANENT, rewrite.getFlow());
   }

   @Test
   public void testSubflowRestoresOriginalFlowStateAfterExceptionWithInitializedFlow()
   {
      rewrite.setFlow(Flow.REDIRECT_PERMANENT);

      Flow result = null;
      try {
         result = SubflowTask.perform(rewrite, new MockEvaluationContext(), Flow.INCLUDE, new SubflowTask() {

            @Override
            public void performInSubflow(ServletRewrite<?, ?> event, EvaluationContext context)
            {
               Assert.assertEquals(Flow.INCLUDE, rewrite.getFlow());
               event.setFlow(Flow.HANDLED);
               throw new RuntimeException();
            }

         });
         Assert.fail();
      }
      catch (RuntimeException e) {}

      Assert.assertNull(result);
      Assert.assertEquals(Flow.REDIRECT_PERMANENT, rewrite.getFlow());
   }

}
