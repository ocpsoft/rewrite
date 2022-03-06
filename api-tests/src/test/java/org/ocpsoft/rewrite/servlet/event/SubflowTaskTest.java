package org.ocpsoft.rewrite.servlet.event;

import org.junit.Before;
import org.junit.Test;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Flow;
import org.ocpsoft.rewrite.mock.MockEvaluationContext;
import org.ocpsoft.rewrite.servlet.event.BaseRewrite.ServletRewriteFlow;
import org.ocpsoft.rewrite.test.MockServletRewrite;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

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
            assertThat(rewrite.getFlow()).isEqualTo(ServletRewriteFlow.REDIRECT_PERMANENT);
            event.setFlow(ServletRewriteFlow.HANDLED);
         }
      });

      assertThat(result).isEqualTo(ServletRewriteFlow.HANDLED);
      assertThat(rewrite.getFlow()).isEqualTo(ServletRewriteFlow.REDIRECT_PERMANENT);
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
                     assertThat(rewrite.getFlow()).isEqualTo(ServletRewriteFlow.INCLUDE);
                     event.setFlow(ServletRewriteFlow.HANDLED);
                  }
               });

      assertThat(result).isEqualTo(ServletRewriteFlow.HANDLED);
      assertThat(rewrite.getFlow()).isEqualTo(ServletRewriteFlow.REDIRECT_PERMANENT);
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
               assertThat(rewrite.getFlow()).isEqualTo(ServletRewriteFlow.REDIRECT_PERMANENT);
               event.setFlow(ServletRewriteFlow.HANDLED);
               throw new RuntimeException();
            }

         });
         fail("");
      }
      catch (RuntimeException e) {}

      assertThat(result).isNull();
      assertThat(rewrite.getFlow()).isEqualTo(ServletRewriteFlow.REDIRECT_PERMANENT);
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
                        assertThat(rewrite.getFlow()).isEqualTo(ServletRewriteFlow.INCLUDE);
                        event.setFlow(ServletRewriteFlow.HANDLED);
                        throw new RuntimeException();
                     }

                  });
         fail("");
      }
      catch (RuntimeException e) {}

      assertThat(result).isNull();
      assertThat(rewrite.getFlow()).isEqualTo(ServletRewriteFlow.REDIRECT_PERMANENT);
   }

}
