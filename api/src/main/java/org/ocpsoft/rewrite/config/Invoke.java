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
package org.ocpsoft.rewrite.config;

import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.Retrieval;
import org.ocpsoft.rewrite.bind.Submission;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.spi.InvocationResultHandler;

/**
 * Builds {@link Operation} instances used to directly invoke {@link Binding} submission or retrieval on {@link Rewrite}
 * events.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class Invoke extends DefaultOperationBuilder
{
   private static final Logger log = Logger.getLogger(Invoke.class);
   private final Submission submission;
   private final Retrieval retrieval;

   private Invoke(final Submission submission, final Retrieval retrieval)
   {
      this.submission = submission;
      this.retrieval = retrieval;
   }

   @Override
   @SuppressWarnings("unchecked")
   public void perform(final Rewrite event, final EvaluationContext context)
   {
      Object result = null;
      if ((submission == null) && (retrieval != null))
      {
         result = retrieval.retrieve(event, context);
         log.debug("Invoked binding [" + retrieval + "] returned value [" + result + "]");
      }
      else if (retrieval != null)
      {
         // TODO convert/validate here?
         result = submission.submit(event, context, retrieval.retrieve(event, context));
         log.debug("Invoked binding [" + submission + "] returned value [" + result + "]");
      }
      else
      {
         log.warn("No binding specified for Invocation.");
      }

      if (result != null)
      {
         ServiceLoader<InvocationResultHandler> providers = ServiceLoader.load(InvocationResultHandler.class);
         if (!providers.iterator().hasNext())
         {
            log.debug("No instances of [" + InvocationResultHandler.class.getName()
                     + "] were registered to handing binding invocation result [" + result + "]");
         }

         for (InvocationResultHandler handler : providers) {
            handler.handle(event, context, result);
         }
      }
   }

   /**
    * Invoke the given {@link Retrieval} and process {@link InvocationResultHandler} instances on the result value (if
    * any.)
    */
   public static DefaultOperationBuilder binding(final Retrieval retrieval)
   {
      return new Invoke(null, retrieval){

         @Override
         public String toString()
         {
            return "Invoke.binding(" + retrieval + ")";
         }
         
      };
   }

   /**
    * 
    * Invoke {@link Submission#submit(Rewrite, EvaluationContext, Object)}, use the result of the given
    * {@link Retrieval#retrieve(Rewrite, EvaluationContext)} as the value for this submission. Process
    * {@link InvocationResultHandler} instances on the result value (if any.)
    */
   public static DefaultOperationBuilder binding(final Submission to, final Retrieval from)
   {
      return new Invoke(to, from) {

         @Override
         public String toString()
         {
            return "Invoke.binding(" + to + ", " + from + ")";
         }

      };
   }

   @Override
   public abstract String toString();
}
