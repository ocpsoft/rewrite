/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.ocpsoft.rewrite;

import java.util.ArrayList;
import java.util.List;

import org.ocpsoft.logging.Logger;
import org.ocpsoft.logging.Logger.Level;
import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.config.RuleMetadata;
import org.ocpsoft.rewrite.context.Context;
import org.ocpsoft.rewrite.context.ContextBase;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public abstract class AbstractRewrite implements Rewrite
{
   private static final Logger log = Logger.getLogger(AbstractRewrite.class);

   private Context context = new ContextBase() {};

   @Override
   public Context getRewriteContext()
   {
      return context;
   }

   @Override
   @SuppressWarnings("unchecked")
   public List<Rule> getEvaluatedRules()
   {
      List<Rule> evaluated = (List<Rule>) this.getRewriteContext().get(
               AbstractRewrite.class.getName() + "_EVALUATED_RULES");
      if (evaluated == null)
      {
         evaluated = new ArrayList<Rule>();
         this.getRewriteContext().put(AbstractRewrite.class.getName() + "_EVALUATED_RULES", evaluated);
      }
      return evaluated;
   }

   /**
    * Print a log of all {@link Rule} instances evaluated during the given {@link Rewrite} event. This method is a no-op
    * if the selected logging {@link Level} is disabled.
    */
   public static void logEvaluatedRules(Rewrite event, Level level)
   {
      switch (level)
      {
      case INFO:
         if (log.isInfoEnabled())
            log.info(buildLogOutput(event).toString());
         break;
      case WARN:
         if (log.isWarnEnabled())
            log.warn(buildLogOutput(event).toString());
         break;
      case ERROR:
         if (log.isErrorEnabled())
            log.error(buildLogOutput(event).toString());
         break;
      case DEBUG:
         if (log.isDebugEnabled())
            log.debug(buildLogOutput(event).toString());
         break;
      case TRACE:
         if (log.isTraceEnabled())
            log.trace(buildLogOutput(event).toString());
         break;

      default:
         break;
      }
   }

   private static StringBuilder buildLogOutput(Rewrite event)
   {
      StringBuilder builder = new StringBuilder();
      builder.append("Rewrite rule evaluation for event [" + event + "]\n");

      int i = 0;
      for (Rule rule : event.getEvaluatedRules()) {
         builder.append("\tRule " + i + ": " + rule);
         if (rule instanceof Context)
         {
            builder.append(" defined at "
                     + ((Context) rule).get(RuleMetadata.PROVIDER_LOCATION) + "\n");
         }
         i++;
      }
      return builder;
   }
}
