package org.ocpsoft.rewrite.faces;

import java.util.ArrayList;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.faces.config.PhaseAction;
import org.ocpsoft.rewrite.faces.config.PhaseOperation;
import org.ocpsoft.rewrite.servlet.config.Lifecycle;
import org.ocpsoft.rewrite.servlet.event.BaseRewrite.Flow;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class RewritePhaseListener implements PhaseListener
{
   private static final long serialVersionUID = 6706075446314218089L;
   private static Logger log = Logger.getLogger(RewritePhaseListener.class);

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

      ArrayList<PhaseOperation<?>> operations = PhaseOperation.getSortedPhaseOperations(request);
      if (operations != null)
      {
         for (PhaseOperation<?> operation : operations) {
            if (operation.getBeforePhases().contains(event.getPhaseId())
                     || operation.getBeforePhases().contains(PhaseId.ANY_PHASE))
            {
               Lifecycle.proceed().perform(operation.getEvent(), operation.getContext());
               operation.performOperation(operation.getEvent(), operation.getContext());
               if (operation.getEvent().getFlow().is(Flow.ABORT_REQUEST))
               {
                  event.getFacesContext().responseComplete();
               }
               if (operation.getEvent().getFlow().is(Flow.HANDLED))
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

      ArrayList<PhaseOperation<?>> operations = PhaseOperation.getSortedPhaseOperations(request);
      if (operations != null)
      {
         for (PhaseOperation<?> operation : operations) {
            if (operation.getAfterPhases().contains(event.getPhaseId())
                     || operation.getAfterPhases().contains(PhaseId.ANY_PHASE))
            {
               Lifecycle.proceed().perform(operation.getEvent(), operation.getContext());
               operation.performOperation(operation.getEvent(), operation.getContext());
               if (operation.getEvent().getFlow().is(Flow.ABORT_REQUEST))
               {
                  event.getFacesContext().responseComplete();
               }
               if (operation.getEvent().getFlow().is(Flow.HANDLED))
               {
                  break;
               }
            }
         }
      }
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
