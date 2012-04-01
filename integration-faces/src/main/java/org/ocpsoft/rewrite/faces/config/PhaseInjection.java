package org.ocpsoft.rewrite.faces.config;

import javax.faces.event.PhaseId;

import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.El;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * Wraps & holds a param binding till before or after a given JavaServer Faces {@link PhaseId}
 * 
 * @author <a href="mailto:fabmars@gmail.com">Fabien Marsaud</a>
 */
public class PhaseInjection extends PhaseOperation<PhaseInjection> implements Binding
{
   private static final Logger log = Logger.getLogger(PhaseInjection.class);

   private final Binding binding;

   private PhaseInjection(Binding binding)
   {
      if (binding == null)
      {
         throw new NullPointerException("binding");
      }
      else if (!(binding instanceof El.ElProperty))
      {
         log.warn("binding ought to be an ElProperty; instead it is a [" + binding.getClass().getName() + "].");
      }
      this.binding = binding;
   }

   @Override
   public int priority()
   {
      return -5;
   }

   @Override
   public void performOperation(HttpServletRewrite event, EvaluationContext context)
   {
      // TODO retrieve value from evalcontext and submit
   }

   /**
    * Wraps the Binding into a new PhaseInjection, so as to have it performed duriung the JSF lifecycle By default, the
    * original binding will be performed after {@link PhaseId#RESTORE_VIEW}
    */
   public static PhaseInjection withhold(final Binding binding)
   {
      return (PhaseInjection) new PhaseInjection(binding).after(PhaseId.RESTORE_VIEW);
   }

   @Override
   public Object retrieve(Rewrite event, EvaluationContext context)
   {
      throw new IllegalStateException(
               "Attempted to retrieve value from PhaseInjection, which does not support retrieval.");
   }

   @Override
   public Object convert(Rewrite event, EvaluationContext context, Object value)
   {
      return binding.convert(event, context, value);
   }

   @Override
   public boolean validates(Rewrite event, EvaluationContext context, Object value)
   {
      return binding.validates(event, context, value);
   }

   @Override
   public Object submit(Rewrite event, EvaluationContext context, Object value)
   {
      return binding.submit(event, context, value);
   }

   @Override
   public boolean supportsRetrieval()
   {
      return false;
   }

   @Override
   public boolean supportsSubmission()
   {
      return true;
   }
}
