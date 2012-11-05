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

import org.ocpsoft.common.pattern.Weighted;
import org.ocpsoft.rewrite.util.Instances;

/**
 * Responsible for performing object instance lookups. See {@link Instances#lookup(Class)} for convenient access to this
 * API.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface InstanceProvider extends Weighted
{
   /**
    * Get an instance of the given {@link Class} type. Return null if no instance could be retrieved.
    */
   Object getInstance(Class<?> type);
}
