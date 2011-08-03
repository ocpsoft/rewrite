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

import java.util.HashMap;
import java.util.Map;

import com.ocpsoft.rewrite.RewriteContext;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class MockRewriteContext implements RewriteContext
{

   private final Map<Object, Object> map = new HashMap<Object, Object>();

   @Override
   public Object get(final Object key)
   {
      return map.get(key);
   }

   @Override
   public void put(final Object key, final Object value)
   {
      map.put(key, value);
   }

   @Override
   public void containsKey(final Object key)
   {
      map.containsKey(key);
   }

}
