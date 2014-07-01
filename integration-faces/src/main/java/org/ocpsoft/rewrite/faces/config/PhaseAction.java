package org.ocpsoft.rewrite.faces.config;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.ocpsoft.common.pattern.Weighted;
import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.bind.Retrieval;
import org.ocpsoft.rewrite.bind.Submission;
import org.ocpsoft.rewrite.config.Invoke;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.exception.RewriteException;
import org.ocpsoft.rewrite.servlet.event.BaseRewrite.ServletRewriteFlow;
import org.ocpsoft.rewrite.servlet.event.ServletRewrite;
import org.ocpsoft.rewrite.servlet.event.SubflowTask;
import org.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.spi.InvocationResultHandler;

/**
 * An {@link Operation} that invokes an action before or after a given JavaServer Faces {@link PhaseId}. Has a
 * {@link Weighted#priority()} of 0.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class PhaseAction extends PhaseOperation<PhaseAction>
{
   private static final Logger log = Logger.getLogger(Invoke.class);
   private final Submission submission;
   private final Retrieval retrieval;

   /**
    * Create an {@link Operation} that invokes the given {@link Retrieval} and processes {@link InvocationResultHandler}
    * instances on the result value (if any).
    * <p>
    * By default, this action is invoked after {@link PhaseId#RESTORE_VIEW}
    */
   public static PhaseAction retrieveFrom(final Retrieval retrieval)
   {
      return new PhaseAction(null, retrieval) {

         @Override
         public String toString()
         {
            return "PhaseAction.retrieveFrom(" + retrieval + ")";
         }

      }.after(PhaseId.RESTORE_VIEW);
   }

   /**
    * Create an {@link Operation} that invokes
    * {@link Submission#submit(org.ocpsoft.rewrite.event.Rewrite, EvaluationContext, Object)}, and uses the result of
    * the given {@link Retrieval#retrieve(org.ocpsoft.rewrite.event.Rewrite, EvaluationContext)} as the value for this
    * submission. Process {@link InvocationResultHandler} instances on the result value (if any).
    * <p>
    * By default, this action is invoked after {@link PhaseId#RESTORE_VIEW}
    */
   public static PhaseAction submitTo(final Submission to, final Retrieval from)
   {
      return new PhaseAction(to, from) {

         @Override
         public String toString()
         {
            return "PhaseAction.submitTo(" + to + ", " + from + ")";
         }

      }.after(PhaseId.RESTORE_VIEW);
   }

   @Override
   public int priority()
   {
      return 0;
   }

   private PhaseAction(final Submission submission, final Retrieval retrieval)
   {
      this.submission = submission;
      this.retrieval = retrieval;
   }

   @Override
   @SuppressWarnings("unchecked")
   public void performOperation(final HttpServletRewrite event, final EvaluationContext context)
   {
      SubflowTask.perform(event, context, ServletRewriteFlow.UN_HANDLED, new SubflowTask() {

         @Override
         public void performInSubflow(ServletRewrite<?, ?> event, EvaluationContext context)
         {
            Object result = null;
            if (retrieval != null)
            {
               result = retrieval.retrieve(event, context);
               log.debug("Invoked binding [" + submission + "] returned value [" + result + "]");
            }
            if (submission != null)
            {
               result = submission.submit(event, context, result);
               log.debug("Invoked binding [" + retrieval + "] returned value [" + result + "]");
            }
            if (retrieval == null && submission == null)
            {
               log.warn("No binding specified for Invocation.");
            }

            if (result instanceof Operation)
            {
               ((Operation) result).perform(event, context);
            }
            else if (result != null)
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

            if (result != null)
            {
               try {
                  if (event.getFlow().is(ServletRewriteFlow.ABORT_REQUEST))
                  {
                     FacesContext facesContext = FacesContext.getCurrentInstance();
                     if (event.getFlow().is(ServletRewriteFlow.FORWARD))
                     {
                        String dispatchResource = ((HttpInboundServletRewrite) event).getDispatchResource();
                        facesContext.getExternalContext().dispatch(dispatchResource);
                     }
                     facesContext.responseComplete();
                  }
                  else if (event.getFlow().is(ServletRewriteFlow.INCLUDE))
                  {
                     throw new IllegalStateException(
                              "Cannot issue INCLUDE directive within JSF lifecycle. Not supported.");
                  }
               }
               catch (Exception e) {
                  throw new RewriteException("", e);
               }
            }

         }
      });
   }

}
