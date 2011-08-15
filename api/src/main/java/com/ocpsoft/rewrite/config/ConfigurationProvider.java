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
package com.ocpsoft.rewrite.config;

import com.ocpsoft.rewrite.services.Specialized;
import com.ocpsoft.rewrite.services.Weighted;

/**
 * Provider configuration to the Rewrite runtime environment.
 * 
 * Additional configuration providers my be specified by providing a service activator file containing the name of your
 * implementations:
 * <p>
 * <code> /META-INF/services/com.ocpsoft.rewrite.config.ConfigurationProvider<br> 
 * 
 * --------------<br>
 * com.example.ConfigurationProviderImpl</code>
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ConfigurationProvider<T> extends Weighted, Specialized<Object>
{
   /**
    * Return the additional configuration.
    */
   public Configuration getConfiguration(T context);
}
