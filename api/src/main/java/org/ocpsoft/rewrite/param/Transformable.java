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
package org.ocpsoft.rewrite.param;

import java.util.List;

/**
 * Defines a class which can be transformed by a {@link Transform}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @param <C> The type containing the value to be transformed.
 * @param <T> The type of the value to be transformed.
 */
public interface Transformable<C extends Transformable<C, T>, T>
{
   /**
    * Add a {@link Transform} to this {@link C}; it will executed in the order in which it was added.
    */
   public C transformedBy(Transform<T> transform);

   /**
    * Get the underlying {@link List} of all {@link Transform} objects currently registered to this {@link C}
    */
   List<Transform<T>> getTransforms();
}
