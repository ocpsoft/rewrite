package org.ocpsoft.rewrite.servlet.event;

import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Flow;
import org.ocpsoft.rewrite.mock.MockEvaluationContext;
import org.ocpsoft.rewrite.servlet.event.BaseRewrite.ServletRewriteFlow;
import org.ocpsoft.rewrite.test.MockServletRewrite;

public class SubflowTaskTest
{
   private ServletRewrite<?, ?> rewrite;

   @Before
   public void before()
   {
      rewrite = new MockServletRewrite(null, null, null);
   }

   @Test
   public void testSubflowRestoresOriginalFlowState()
   {
      rewrite.setFlow(ServletRewriteFlow.REDIRECT_PERMANENT);

      Flow result = SubflowTask.perform(rewrite, new MockEvaluationContext(), new SubflowTask() {

         @Override
         public void performInSubflow(ServletRewrite<?, ?> event, EvaluationContext context)
         {
            Assert.assertEquals(ServletRewriteFlow.REDIRECT_PERMANENT, rewrite.getFlow());
            event.setFlow(ServletRewriteFlow.HANDLED);
         }
      });

      Assert.assertEquals(ServletRewriteFlow.HANDLED, result);
      Assert.assertEquals(ServletRewriteFlow.REDIRECT_PERMANENT, rewrite.getFlow());
   }

   @Test
   public void testSubflowRestoresOriginalFlowStateWithInitializedFlow()
   {
      rewrite.setFlow(ServletRewriteFlow.REDIRECT_PERMANENT);

      Flow result = SubflowTask.perform(rewrite, new MockEvaluationContext(), ServletRewriteFlow.INCLUDE,
               new SubflowTask() {

                  @Override
                  public void performInSubflow(ServletRewrite<?, ?> event, EvaluationContext context)
                  {
                     Assert.assertEquals(ServletRewriteFlow.INCLUDE, rewrite.getFlow());
                     event.setFlow(ServletRewriteFlow.HANDLED);
                  }
               });

      Assert.assertEquals(ServletRewriteFlow.HANDLED, result);
      Assert.assertEquals(ServletRewriteFlow.REDIRECT_PERMANENT, rewrite.getFlow());
   }

   @Test
   public void testSubflowRestoresOriginalFlowStateAfterException()
   {
      rewrite.setFlow(ServletRewriteFlow.REDIRECT_PERMANENT);

      Flow result = null;
      try {
         result = SubflowTask.perform(rewrite, new MockEvaluationContext(), new SubflowTask() {

            @Override
            public void performInSubflow(ServletRewrite<?, ?> event, EvaluationContext context)
            {
               Assert.assertEquals(ServletRewriteFlow.REDIRECT_PERMANENT, rewrite.getFlow());
               event.setFlow(ServletRewriteFlow.HANDLED);
               throw new RuntimeException();
            }

         });
         Assert.fail();
      }
      catch (RuntimeException e) {}

      Assert.assertNull(result);
      Assert.assertEquals(ServletRewriteFlow.REDIRECT_PERMANENT, rewrite.getFlow());
   }

   @Test
   public void testSubflowRestoresOriginalFlowStateAfterExceptionWithInitializedFlow()
   {
      rewrite.setFlow(ServletRewriteFlow.REDIRECT_PERMANENT);

      Flow result = null;
      try {
         result = SubflowTask.perform(rewrite, new MockEvaluationContext(), ServletRewriteFlow.INCLUDE,
                  new SubflowTask() {

                     @Override
                     public void performInSubflow(ServletRewrite<?, ?> event, EvaluationContext context)
                     {
                        Assert.assertEquals(ServletRewriteFlow.INCLUDE, rewrite.getFlow());
                        event.setFlow(ServletRewriteFlow.HANDLED);
                        throw new RuntimeException();
                     }

                  });
         Assert.fail();
      }
      catch (RuntimeException e) {}

      Assert.assertNull(result);
      Assert.assertEquals(ServletRewriteFlow.REDIRECT_PERMANENT, rewrite.getFlow());
   }

}
