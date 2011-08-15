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
package com.ocpsoft.rewrite.bind;

import java.util.LinkedList;
import java.util.List;

/**
 * Base {@link Bindable} implementation.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class DefaultBindable<T extends Bindable<T>> implements Bindable<T>
{
   private final LinkedList<Binding> bindings = new LinkedList<Binding>();

   @Override
   @SuppressWarnings("unchecked")
   public T bindsTo(final Binding binding)
   {
      /*
       * Bindings must be added to the front of the list, since we want the 
       * ability to override the default binding if necessary.
       */
      this.bindings.addFirst(binding);
      return (T) this;
   }

   @Override
   public List<Binding> getBindings()
   {
      return bindings;
   }
}
