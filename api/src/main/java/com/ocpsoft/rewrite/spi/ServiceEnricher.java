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
package com.ocpsoft.rewrite.spi;

import com.ocpsoft.common.services.ServiceLoader;

/**
 * Provides enriching for services loaded with {@link ServiceLoader}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ServiceEnricher
{
   /**
    * Produce an enriched service of the given type. If no enriching took place, this method must return null.
    */
   <T> T produce(Class<T> type);

   /**
    * Enrich an instantiated instance of the given service type. If no enriching took place, this method must return the
    * original service without modification.
    */
   <T> T enrich(T service);
}
