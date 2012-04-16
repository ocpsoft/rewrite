package org.ocpsoft.rewrite.servlet.event;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.event.BaseRewrite.Flow;

/**
 * Defines a task to be performed within a sub-{@link Flow} of a given {@link ServletRewrite}
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public abstract class SubflowTask
{

   /**
    * Perform the given {@link SubflowTask} using the given {@link ServletRewrite} event. If modified, the original
    * {@link Flow} will be restored after task completion.
    *
    * @param event the {@link ServletRewrite} event used in the given {@link SubflowTask}
    *
    * @return the resultant {@link Flow} from the {@link SubflowTask} execution.
    */
   public static Flow perform(ServletRewrite<?, ?> rewrite, EvaluationContext context,
            SubflowTask subflowTask)
   {
      return perform(rewrite, context, rewrite.getFlow(), subflowTask);
   }

   /**
    * Perform the given {@link SubflowTask} using the given {@link ServletRewrite} event initialized with a new
    * {@link Flow}. The original {@link Flow} will be restored after task completion.
    *
    * @param event the {@link ServletRewrite} event used in the given {@link SubflowTask}
    * @param subflow the {@link Flow} to be us as the initial state for the given {@link SubflowTask}
    *
    * @return the resultant {@link Flow} from the {@link SubflowTask} execution.
    */
   public static Flow perform(ServletRewrite<?, ?> event, EvaluationContext context, Flow subflow,
            SubflowTask subflowTask)
   {
      Flow flow = event.getFlow();
      try
      {
         event.setFlow(subflow);
         subflowTask.performInSubflow(event, context);
         return event.getFlow();
      }
      finally
      {
         event.setFlow(flow);
      }
   }

   /**
    * The task to be performed in a sub-{@link Flow}.
    */
   public abstract void performInSubflow(ServletRewrite<?, ?> event, EvaluationContext context);

}
