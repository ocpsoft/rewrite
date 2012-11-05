/*
 * Copyright 2012 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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

import java.util.List;

import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * SPI for implementing {@link Rule} caching. For use in optimized {@link RewriteProvider} instances.
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface RuleCacheProvider
{
   /**
    * Get {@link Rule} instances matching the given cache key.
    */
   List<Rule> get(Object key);

   /**
    * Store a list of {@link Rule} instances matching the given cache key.
    */
   void put(Object key, List<Rule> rules);

   /**
    * Create a unique key for the given {@link Rewrite} event and {@link EvaluationContext} states.
    */
   Object createKey(Rewrite event, EvaluationContext context);
}
