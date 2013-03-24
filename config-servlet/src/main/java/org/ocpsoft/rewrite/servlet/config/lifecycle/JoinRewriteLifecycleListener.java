/*
 * Copyright 2013 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ocpsoft.rewrite.servlet.config.lifecycle;

import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.servlet.config.rule.Join;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.servlet.spi.RewriteLifecycleListener;

/**
 * Implementation class for handling {@link Join} configuration instances.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class JoinRewriteLifecycleListener implements RewriteLifecycleListener<HttpServletRewrite>
{
   private static final String JOIN_DISABLED_KEY = Join.class.getName() + "_DISABLED";
   private static final String JOIN_DISABLED_RESET_NEXT_KEY = Join.class.getName() + "_DISABLED_RESET_NEXT";

   @Override
   public boolean handles(Rewrite payload)
   {
      return payload instanceof HttpServletRewrite;
   }

   @Override
   public int priority()
   {
      return Integer.MAX_VALUE;
   }

   @Override
   public void beforeInboundLifecycle(HttpServletRewrite event)
   {}

   @Override
   public void beforeInboundRewrite(HttpServletRewrite event)
   {}

   @Override
   public void afterInboundRewrite(HttpServletRewrite event)
   {
      if (Boolean.TRUE.equals(event.getRewriteContext().get(JOIN_DISABLED_RESET_NEXT_KEY)))
      {
         event.getRewriteContext().put(JOIN_DISABLED_KEY, false);
         event.getRewriteContext().put(JOIN_DISABLED_RESET_NEXT_KEY, false);
      }
      else if (Boolean.TRUE.equals(event.getRewriteContext().get(JOIN_DISABLED_KEY)))
      {
         event.getRewriteContext().put(JOIN_DISABLED_RESET_NEXT_KEY, true);
      }
   }

   @Override
   public void afterInboundLifecycle(HttpServletRewrite event)
   {}

   @Override
   public void beforeOutboundRewrite(HttpServletRewrite event)
   {}

   @Override
   public void afterOutboundRewrite(HttpServletRewrite event)
   {}

}
