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
package com.ocpsoft.rewrite.param;

import java.util.List;

import com.ocpsoft.rewrite.bind.Bindable;

/**
 * An {@link String} specific {@link Bindable}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface Parameter<T> extends Bindable<Parameter<T>>
{
   /**
    * Get the name of this {@link Parameter}
    */
   public String getName();

   /**
    * Add a constraint to which this {@link Parameter} must match.
    */
   public Parameter<T> constrainedBy(Constraint<T> pattern);

   /**
    * Get the underlying {@link List} of all {@link Constraint} objects currently registered to this {@link Parameter}
    */
   List<Constraint<T>> getConstraints();

   /**
    * Add a {@link Transform} to this {@link Parameter}; it will executed in the order in which it was added.
    */
   public Parameter<T> transformedBy(Transform<T> transform);

   /**
    * Get the underlying {@link List} of all {@link Transform} objects currently registered to this {@link Parameter}
    */
   List<Transform<T>> getTransforms();
}
