/*
 * Copyright 2011 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ocpsoft.rewrite.servlet.event;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Flow;
import org.ocpsoft.rewrite.servlet.event.BaseRewrite.ServletRewriteFlow;

/**
 * Defines a task to be performed within a sub-{@link ServletRewriteFlow} of a given {@link ServletRewrite}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public abstract class SubflowTask
{

   /**
    * Perform the given {@link SubflowTask} using the given {@link ServletRewrite} event. If modified, the original
    * {@link ServletRewriteFlow} will be restored after task completion.
    * 
    * @param event the {@link ServletRewrite} event used in the given {@link SubflowTask}
    * 
    * @return the resultant {@link ServletRewriteFlow} from the {@link SubflowTask} execution.
    */
   public static Flow perform(ServletRewrite<?, ?> rewrite, EvaluationContext context,
            SubflowTask subflowTask)
   {
      return perform(rewrite, context, rewrite.getFlow(), subflowTask);
   }

   /**
    * Perform the given {@link SubflowTask} using the given {@link ServletRewrite} event initialized with a new
    * {@link ServletRewriteFlow}. The original {@link ServletRewriteFlow} will be restored after task completion.
    * 
    * @param event the {@link ServletRewrite} event used in the given {@link SubflowTask}
    * @param subflow the {@link ServletRewriteFlow} to be us as the initial state for the given {@link SubflowTask}
    * 
    * @return the resultant {@link ServletRewriteFlow} from the {@link SubflowTask} execution.
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
    * The task to be performed in a sub-{@link ServletRewriteFlow}.
    */
   public abstract void performInSubflow(ServletRewrite<?, ?> event, EvaluationContext context);

}
