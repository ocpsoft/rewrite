package org.ocpsoft.rewrite.faces.config;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.common.pattern.Weighted;
import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.logging.Logger;

import org.ocpsoft.rewrite.bind.Retrieval;
import org.ocpsoft.rewrite.bind.Submission;
import org.ocpsoft.rewrite.config.Invoke;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.exception.RewriteException;
import org.ocpsoft.rewrite.servlet.event.BaseRewrite.Flow;
import org.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.spi.InvocationResultHandler;

/**
 * Invoke an action before or after a given JavaServer Faces {@link PhaseId}. Has a {@link Weighted#priority()} of 0.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class PhaseAction extends PhaseOperation<PhaseAction>
{
   private static final String QUEUED_ACTIONS = PhaseAction.class + "_QUEUED";
   private static final Logger log = Logger.getLogger(Invoke.class);
   private final Submission submission;
   private final Retrieval retrieval;

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

   public static void removeQueuedPhaseActions(final HttpServletRequest request)
   {
      request.removeAttribute(QUEUED_ACTIONS);
   }

   @Override
   @SuppressWarnings("unchecked")
   public void performOperation(final HttpServletRewrite event, final EvaluationContext context)
   {
      Object result = null;
      if ((submission == null) && (retrieval != null))
      {
         result = retrieval.retrieve(event, context);
         log.debug("Invoked binding [" + retrieval + "] returned value [" + result + "]");
      }
      else if (retrieval != null)
      {
         Object converted = submission.convert(event, context, retrieval.retrieve(event, context));
         result = submission.submit(event, context, converted);
         log.debug("Invoked binding [" + submission + "] returned value [" + result + "]");
      }
      else
      {
         log.warn("No binding specified for Invocation.");
      }

      Flow savedState = event.getFlow();
      event.setFlow(Flow.UN_HANDLED);

      try
      {
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
               if (event.getFlow().is(Flow.ABORT_REQUEST))
               {
                  FacesContext facesContext = FacesContext.getCurrentInstance();
                  if (event.getFlow().is(Flow.FORWARD))
                  {
                     /*
                      * We don't want to carry queued phase actions through more than one lifecycle.
                      */
                     PhaseAction.removeQueuedPhaseActions(event.getRequest());
                     String dispatchResource = ((HttpInboundServletRewrite) event).getDispatchResource();
                     facesContext.getExternalContext().dispatch(dispatchResource);
                  }
                  facesContext.responseComplete();
               }
               else if (event.getFlow().is(Flow.INCLUDE))
               {
                  throw new IllegalStateException("Cannot issue INCLUDE directive within JSF lifecycle. Not supported.");
               }
            }
            catch (Exception e) {
               throw new RewriteException(e);
            }
         }
      }
      finally
      {
         event.setFlow(savedState);
      }
   }

   /**
    * Invoke the given {@link Retrieval} and process {@link InvocationResultHandler} instances on the result value (if
    * any.)
    * <p>
    * By default, this action is invoked after {@link PhaseId#RESTORE_VIEW}
    */
   public static PhaseAction retrieveFrom(final Retrieval retrieval)
   {
      return (PhaseAction) new PhaseAction(null, retrieval).after(PhaseId.RESTORE_VIEW);
   }

   /**
    * Invoke {@link Submission#submit(org.ocpsoft.rewrite.event.Rewrite, EvaluationContext, Object)}, use the result of the given
    * {@link Retrieval#retrieve(org.ocpsoft.rewrite.event.Rewrite, EvaluationContext)} as the value for this submission. Process
    * {@link InvocationResultHandler} instances on the result value (if any.)
    * <p>
    * By default, this action is invoked after {@link PhaseId#RESTORE_VIEW}
    */
   public static PhaseAction submitTo(final Submission to, final Retrieval from)
   {
      return (PhaseAction) new PhaseAction(to, from).after(PhaseId.RESTORE_VIEW);
   }

}
