package org.ocpsoft.rewrite.faces;

import org.ocpsoft.logging.Logger;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import org.ocpsoft.rewrite.spi.InvocationResultHandler;

/**
 * (Priority: 100) Implementation of {@link InvocationResultHandler} which handles JavaServer Faces action result and
 * navigation strings. Together with {@link RewritePhaseListener}, integrates {@link org.ocpsoft.rewrite.faces.config.PhaseAction} into Faces navigation.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class NavigatingInvocationResultHandler implements InvocationResultHandler
{
   public static final String QUEUED_NAVIGATION = NavigatingInvocationResultHandler.class.getName()
            + "_QUEUED_NAVIGATION";

   private static Logger log = Logger.getLogger(NavigatingInvocationResultHandler.class);

   @Override
   public int priority()
   {
      return 100;
   }

   @Override
   public boolean handles(final Object payload)
   {
      return payload instanceof String;
   }

   @Override
   public void handle(final Rewrite event, final EvaluationContext context, final Object result)
   {
      if (event instanceof HttpInboundServletRewrite)
      {
         if (result instanceof String)
         {
            log.info("Storing Invocation result [" + result + "] as deferred navigation string.");
            ((HttpInboundServletRewrite) event).getRequest().setAttribute(QUEUED_NAVIGATION, result);
         }
      }
   }

}
