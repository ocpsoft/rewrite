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
import org.ocpsoft.rewrite.config.Configuration;

/**
 * Provides {@link Configuration} caching strategies for the Rewrite runtime environment.
 * 
 * Additional cache providers may be specified by providing a service activator file containing the name of your
 * implementations:
 * <p>
 * <code> /META-INF/services/org.ocpsoft.rewrite.config.ConfigurationCacheProvider<br>
 * 
 * --------------<br>
 * com.example.ConfigurationCacheProviderImpl</code>
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ConfigurationCacheProvider<T> extends Weighted, Specialized<Object>
{
   /**
    * Return the cached {@link Configuration}, or null of the cache is empty or unprimed.
    */
   public Configuration getConfiguration(T context);

   /**
    * Store the given {@link Configuration} into the cache for later retrieval.
    */
   public void setConfiguration(T context, Configuration configuration);
}
