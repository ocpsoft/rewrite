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


/**
 * Defines a class which can be transformed by a {@link Transform}
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @param <IMPLTYPE> The type containing the value to be transformed.
 * @param <VALUETYPE> The type of the value to be transformed.
 */
public interface Transformable<IMPLTYPE extends Transformable<IMPLTYPE, VALUETYPE>, VALUETYPE>
{
   /**
    * Add a {@link Transform} to this {@link IMPLTYPE}; it will executed in the order in which it was added.
    */
   public IMPLTYPE transformedBy(Transform<VALUETYPE> transform);
}
