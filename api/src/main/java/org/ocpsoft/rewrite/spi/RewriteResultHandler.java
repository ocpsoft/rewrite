package org.ocpsoft.rewrite.spi;

import org.ocpsoft.common.pattern.Specialized;
import org.ocpsoft.common.pattern.Weighted;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * Handles responses to take after a {@link Rewrite} event has been processed. Response should be determined based on
 * the resultant state of the event.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface RewriteResultHandler extends Weighted, Specialized<Rewrite>
{
   /**
    * Perform any actions necessary to respond to state of the system after the given {@link Rewrite} event has been
    * processed.
    */
   public void handleResult(Rewrite event);
}
