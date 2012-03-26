/*
 * Copyright 2011 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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
package org.ocpsoft.rewrite.event;

import org.ocpsoft.rewrite.context.Context;

/**
 * Event propagated to registered {@link RewriteLifecycleListener} and {@link org.ocpsoft.rewrite.spi.RewriteProvider} instances when the
 * rewrite lifecycle is executed.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface Rewrite
{
   /**
    * Get the {@link org.ocpsoft.rewrite.context.Context} object associated with the current {@link Rewrite} event. This {@link Context} is created
    * at the start of a {@link Rewrite} event, and is destroyed once the {@link Rewrite} event is complete.
    */
   public Context getRewriteContext();
}
