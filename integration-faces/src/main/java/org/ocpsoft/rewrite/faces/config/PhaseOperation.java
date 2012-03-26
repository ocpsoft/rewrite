package org.ocpsoft.rewrite.faces.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.faces.event.PhaseId;
import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.common.pattern.Weighted;
import org.ocpsoft.common.pattern.WeightedComparator;

import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.config.OperationBuilder;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.config.HttpOperation;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * This class encapsulates another operation that should be performed during the JSF lifecycle
 * 
 * @author <a href="mailto:fabmars@gmail.com">Fabien Marsaud</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class PhaseOperation<T extends PhaseOperation<T>> extends HttpOperation implements Weighted
{
   private static final String REQUEST_KEY = PhaseOperation.class.getName() + "_DEFERRED";
   private HttpServletRewrite event;
   private EvaluationContext context;

   private final Set<PhaseId> beforePhases = new HashSet<PhaseId>();
   private final Set<PhaseId> afterPhases = new HashSet<PhaseId>();

   public abstract void performOperation(final HttpServletRewrite event, final EvaluationContext context);

   public Set<PhaseId> getBeforePhases()
   {
      return beforePhases;
   }

   public Set<PhaseId> getAfterPhases()
   {
      return afterPhases;
   }

   public OperationBuilder before(final PhaseId... phases)
   {
      if (phases != null)
         this.beforePhases.addAll(Arrays.asList(phases));
      return this;
   }

   public OperationBuilder after(final PhaseId... phases)
   {
      if (phases != null)
         this.afterPhases.addAll(Arrays.asList(phases));
      return this;
   }

   /**
    * Invoked during the rewrite process, just add "this" to a queue of deferred bindings in the request
    */
   @Override
   public final void performHttp(HttpServletRewrite event, EvaluationContext context)
   {
      this.event = event;
      this.context = context;
      getSortedPhaseOperations(event.getRequest()).add(this);
   }

   @SuppressWarnings("unchecked")
   public static ArrayList<PhaseOperation<?>> getSortedPhaseOperations(HttpServletRequest request)
   {
      ArrayList<PhaseOperation<?>> operations = (ArrayList<PhaseOperation<?>>) request.getAttribute(REQUEST_KEY);
      if (operations == null)
      {
         operations = new ArrayList<PhaseOperation<?>>();
         request.setAttribute(REQUEST_KEY, operations);
      }

      Collections.sort(operations, new WeightedComparator());

      return operations;
   }

   public HttpServletRewrite getEvent()
   {
      return event;
   }

   public EvaluationContext getContext()
   {
      return context;
   }

   public static PhaseOperation<?> enqueue(final Operation operation)
   {
      return enqueue(operation, 0);
   }

   /**
    * Enqueue an {@link org.ocpsoft.rewrite.config.Operation} to be performed before or after one or many JavaServer Faces life-cycle phases, specified via invoking {@link #before(PhaseId...)} or {@link #after(PhaseId...)}.
    */
   @SuppressWarnings("rawtypes")
   public static PhaseOperation<?> enqueue(final Operation operation, final int priority)
   {
      return new PhaseOperation() {

         @Override
         public int priority()
         {
            return priority;
         }

         @Override
         public void performOperation(HttpServletRewrite event, EvaluationContext context)
         {
            operation.perform(event, context);
         }
      };
   }
}
