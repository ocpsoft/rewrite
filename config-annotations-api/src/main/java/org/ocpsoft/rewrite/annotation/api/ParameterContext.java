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
package org.ocpsoft.rewrite.annotation.api;

import org.ocpsoft.rewrite.bind.BindingBuilder;

/**
 * Context for scanning a single field of a class
 *
 * @author Christian Kaltepoth
 */
@SuppressWarnings("rawtypes")
public interface ParameterContext extends ClassContext
{
   /**
    * Sets the {@link BindingBuilder} for the current parameter. Should be called by annotation handlers after creating
    * a binding for the parameter.
    */
   public void setBindingBuilder(BindingBuilder bindingBuilder);

   /**
    * Returns the {@link BindingBuilder} for the current parameter. May return <code>null</code> if no binding has yet
    * been created for the parameter.
    */
   public BindingBuilder getBindingBuilder();

   /**
    * Return the parent {@link ClassContext} to which this context is a descendant.
    */
   ClassContext getClassContext();

   /**
    * Return the parent {@link MethodContext} to which this context is a descendant.
    */
   MethodContext getMethodContext();
}
