package org.ocpsoft.rewrite.faces;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Flow;
import org.ocpsoft.rewrite.exception.RewriteException;
import org.ocpsoft.rewrite.faces.config.PhaseAction;
import org.ocpsoft.rewrite.faces.config.PhaseOperation;
import org.ocpsoft.rewrite.faces.config.PhaseOperation.DeferredOperation;
import org.ocpsoft.rewrite.servlet.RewriteLifecycleContext;
import org.ocpsoft.rewrite.servlet.event.BaseRewrite.ServletRewriteFlow;
import org.ocpsoft.rewrite.servlet.event.ServletRewrite;
import org.ocpsoft.rewrite.servlet.event.SubflowTask;
import org.ocpsoft.rewrite.servlet.http.HttpRewriteLifecycleContext;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.servlet.spi.RewriteResultHandler;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class RewritePhaseListener implements PhaseListener
{
   private static final long serialVersionUID = 6706075446314218089L;
   private static Logger log = Logger.getLogger(RewritePhaseListener.class);

   public RewritePhaseListener()
   {
      log.info(RewritePhaseListener.class.getSimpleName() + " starting up.");
   }

   @Override
   public PhaseId getPhaseId()
   {
      return PhaseId.ANY_PHASE;
   }

   @Override
   public void beforePhase(final PhaseEvent event)
   {
      if (!PhaseId.RESTORE_VIEW.equals(event.getPhaseId()))
      {
         handleBeforePhaseOperations(event);
      }

      if (PhaseId.RENDER_RESPONSE.equals(event.getPhaseId()))
         handleNavigation(event);
   }

   @Override
   public void afterPhase(final PhaseEvent event)
   {
      if (!PhaseId.RENDER_RESPONSE.equals(event.getPhaseId()))
      {
         handleAfterPhaseOperations(event);
         handleNavigation(event);
      }
   }

   private void handleBeforePhaseOperations(final PhaseEvent event)
   {
      FacesContext facesContext = event.getFacesContext();
      HttpServletRequest request = ((HttpServletRequest) facesContext.getExternalContext().getRequest());

      ArrayList<DeferredOperation> operations = PhaseOperation.getSortedPhaseOperations(request);
      if (operations != null)
      {
         for (final DeferredOperation deferredOperation : operations) {
            PhaseOperation<?> operation = deferredOperation.getOperation();
            if (operation.getBeforePhases().contains(event.getPhaseId())
                     || operation.getBeforePhases().contains(PhaseId.ANY_PHASE))
            {
               Flow flow = handlePhaseOperation(deferredOperation);

               if (flow.is(ServletRewriteFlow.ABORT_REQUEST))
               {
                  event.getFacesContext().responseComplete();
               }
               if (flow.is(ServletRewriteFlow.HANDLED))
               {
                  break;
               }
            }
         }
      }
   }

   private void handleAfterPhaseOperations(final PhaseEvent event)
   {
      FacesContext facesContext = event.getFacesContext();
      HttpServletRequest request = ((HttpServletRequest) facesContext.getExternalContext().getRequest());

      ArrayList<DeferredOperation> operations = PhaseOperation.getSortedPhaseOperations(request);
      if (operations != null)
      {
         for (final DeferredOperation deferredOperation : operations) {
            PhaseOperation<?> operation = deferredOperation.getOperation();
            if (operation.getAfterPhases().contains(event.getPhaseId())
                     || operation.getAfterPhases().contains(PhaseId.ANY_PHASE))
            {
               Flow flow = handlePhaseOperation(deferredOperation);

               if (flow.is(ServletRewriteFlow.ABORT_REQUEST))
               {
                  event.getFacesContext().responseComplete();
               }
               if (flow.is(ServletRewriteFlow.HANDLED))
               {
                  break;
               }
            }
         }
      }
   }

   private Flow handlePhaseOperation(final DeferredOperation operation)
   {
      Flow flow = SubflowTask.perform(operation.getEvent(), operation.getContext(), ServletRewriteFlow.UN_HANDLED,
               new SubflowTask() {

                  @Override
                  public void performInSubflow(ServletRewrite<?, ?> rewriteEvent, EvaluationContext context)
                  {
                     try {
                        operation.getOperation().performOperation((HttpServletRewrite) rewriteEvent, context);

                        List<RewriteResultHandler> resultHandlers = ((HttpRewriteLifecycleContext) ((HttpServletRewrite) rewriteEvent)
                                 .getRequest().getAttribute(RewriteLifecycleContext.LIFECYCLE_CONTEXT_KEY))
                                 .getResultHandlers();

                        int handlerCount = resultHandlers.size();
                        for (int i = 0; i < handlerCount; i++)
                        {
                           RewriteResultHandler handler = resultHandlers.get(i);
                           if (handler.handles(operation.getEvent()))
                              handler.handleResult(operation.getEvent());
                        }
                     }
                     catch (Exception e) {
                        throw new RewriteException("Failed to handle PhaseOperation [" + operation + "]", e);
                     }
                  }

               });
      return flow;
   }

   public void handleNavigation(final PhaseEvent event)
   {
      FacesContext facesContext = event.getFacesContext();
      HttpServletRequest request = ((HttpServletRequest) facesContext.getExternalContext().getRequest());
      String navigationCase = (String) request.getAttribute(NavigatingInvocationResultHandler.QUEUED_NAVIGATION);
      if (navigationCase != null)
      {
         request.setAttribute(NavigatingInvocationResultHandler.QUEUED_NAVIGATION, null);
         NavigationHandler navigationHandler = facesContext.getApplication().getNavigationHandler();
         log.debug("Passing queued " + PhaseAction.class.getName() + " result [" + navigationCase
                  + "] to NavigationHandler.handleNavigation()");
         navigationHandler.handleNavigation(facesContext, "", navigationCase);
      }
   }

}
