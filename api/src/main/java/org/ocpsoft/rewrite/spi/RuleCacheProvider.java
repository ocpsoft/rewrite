package org.ocpsoft.rewrite.spi;

import java.util.List;

import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface RuleCacheProvider
{
   List<Rule> get(Object key);

   void put(Object key, List<Rule> rules);

   Object createKey(Rewrite event, EvaluationContext context);
}
