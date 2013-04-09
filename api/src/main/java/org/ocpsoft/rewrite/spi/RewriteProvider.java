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
package org.ocpsoft.rewrite.spi;

import org.ocpsoft.common.pattern.Specialized;
import org.ocpsoft.common.pattern.Weighted;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * SPI for {@link Rewrite} event handling.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface RewriteProvider<C, T extends Rewrite> extends Weighted, Specialized<Rewrite>
{
   /**
    * Initialize this {@link RewriteProvider}
    */
   void init(C context);

   /**
    * Handle the given {@link Rewrite} event.
    */
   void rewrite(T event);

   /**
    * Shut down this {@link RewriteProvider}
    */
   void shutdown(C context);
}
