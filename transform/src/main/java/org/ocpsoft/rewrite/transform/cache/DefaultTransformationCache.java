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
package org.ocpsoft.rewrite.transform.cache;

import java.io.Serializable;
import java.util.HashMap;

public class DefaultTransformationCache implements TransformationCache
{

   private final HashMap<Serializable, CachedTransformation> cache = new HashMap<Serializable, CachedTransformation>();

   @Override
   public CachedTransformation get(Serializable key)
   {
      return cache.get(key);
   }

   @Override
   public void put(Serializable key, CachedTransformation entry)
   {
      cache.put(key, entry);
   }

}
