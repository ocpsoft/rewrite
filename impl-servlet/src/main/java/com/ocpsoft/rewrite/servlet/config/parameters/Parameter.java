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
package com.ocpsoft.rewrite.servlet.config.parameters;

import com.ocpsoft.rewrite.servlet.config.parameters.binding.Evaluation;
import com.ocpsoft.rewrite.servlet.parse.CapturingGroup;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Parameter extends DefaultBindable<Parameter, ParameterBinding> implements
         Bindable<Parameter, ParameterBinding>
{
   private final CapturingGroup capture;

   private String pattern = "[^/]+";

   public Parameter(final CapturingGroup capture)
   {
      this.capture = capture;

      // Set up default binding to evaluation context.
      this.bindsTo(Evaluation.property(getName()));
   }

   /*
    * Builders
    */
   public Parameter matches(final String pattern)
   {
      this.pattern = pattern;
      return this;
   }

   /*
    * Accessors
    */

   public CapturingGroup getCapture()
   {
      return capture;
   }

   @Override
   public String toString()
   {
      return "Parameter [capture=" + capture + ", pattern=" + pattern + "]";
   }

   public String getName()
   {
      return new String(capture.getCaptured());
   }

   public String getPattern()
   {
      return pattern;
   }
}
