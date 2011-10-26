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

import java.util.ArrayList;
import java.util.List;

import com.ocpsoft.rewrite.bind.DefaultBindable;

/**
 * An base implementation of {@link Parameter}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class ParameterBuilder<P extends ParameterBuilder<P, T>, T> extends DefaultBindable<Parameter<T>>
         implements Parameter<T>
{
   private final List<Transform<T>> transforms = new ArrayList<Transform<T>>();
   private final List<Constraint<T>> constraints = new ArrayList<Constraint<T>>();

   /**
    * Create a new instance for the given pattern.
    */
   public ParameterBuilder()
   {}

   @Override
   @SuppressWarnings("unchecked")
   public P constrainedBy(Constraint<T> constraint)
   {
      this.constraints.add(constraint);
      return (P) this;
   }

   @Override
   public List<Constraint<T>> getConstraints()
   {
      return constraints;
   }

   @Override
   @SuppressWarnings("unchecked")
   public P transformedBy(Transform<T> transform)
   {
      this.transforms.add(transform);
      return (P) this;
   }

   @Override
   public List<Transform<T>> getTransforms()
   {
      return transforms;
   }

   @Override
   public String toString()
   {
      return "ParameterBuilder [transforms=" + transforms + ", constraints=" + constraints + "]";
   }

}
