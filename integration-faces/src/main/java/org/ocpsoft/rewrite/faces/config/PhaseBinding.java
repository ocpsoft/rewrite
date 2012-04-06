package org.ocpsoft.rewrite.faces.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.faces.event.PhaseId;

import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.Submission;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.servlet.config.HttpOperation;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * Wraps & holds a param binding till before or after a given JavaServer Faces {@link PhaseId}
 * 
 * @author <a href="mailto:fabmars@gmail.com">Fabien Marsaud</a>
 */
public class PhaseBinding extends HttpOperation implements Binding
{

   private final Set<PhaseId> beforePhases = new HashSet<PhaseId>();
   private final Set<PhaseId> afterPhases = new HashSet<PhaseId>();

   private final Submission deferred;
   private Object deferredValue;

   private PhaseBinding(Submission binding)
   {
      this.deferred = binding;
   }

   public static PhaseBinding to(Submission submission)
   {
      return new PhaseBinding(submission);
   }

   @Override
   public void performHttp(HttpServletRewrite event, EvaluationContext context)
   {
      deferred.submit(event, context, deferredValue);
   }

   @Override
   public Object retrieve(Rewrite event, EvaluationContext context)
   {
      throw new IllegalStateException("PhaseInjection does not support retrieval.");
   }

   @Override
   public Object submit(Rewrite event, EvaluationContext context, Object value)
   {
      /**
       * Must occur before queued {@link PhaseAction} instances during the same phase.
       */
      PhaseOperation.enqueue(this, -5)
      .before(beforePhases.toArray(new PhaseId[] {}))
      .after(afterPhases.toArray(new PhaseId[] {}))
      .perform(event, context);
      deferredValue = value;
      return null;
   }

   @Override
   public boolean validates(Rewrite event, EvaluationContext context, Object value)
   {
      return deferred.validates(event, context, value);
   }

   @Override
   public Object convert(Rewrite event, EvaluationContext context, Object value)
   {
      deferredValue = deferred.convert(event, context, value);
      return deferredValue;
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

   public PhaseBinding before(final PhaseId... phases)
   {
      if (phases != null)
         this.beforePhases.addAll(Arrays.asList(phases));
      return this;
   }

   public PhaseBinding after(final PhaseId... phases)
   {
      if (phases != null)
         this.afterPhases.addAll(Arrays.asList(phases));
      return this;
   }
}
