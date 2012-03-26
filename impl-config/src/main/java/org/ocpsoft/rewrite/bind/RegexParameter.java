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
package org.ocpsoft.rewrite.bind;

import org.ocpsoft.rewrite.bind.parse.CapturingGroup;
import org.ocpsoft.rewrite.param.ParameterBuilder;

/**
 * An {@link String} specific implementation of {@link Bindable}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class RegexParameter extends ParameterBuilder<RegexParameter, String>
{
   private String pattern;
   private final CapturingGroup capture;

   /**
    * Create a new instance for the given {@link CapturingGroup}, using ".*" as the default pattern.
    */
   public RegexParameter(final CapturingGroup capture)
   {
      this.capture = capture;
   }

   public RegexParameter matches(final String pattern)
   {
      this.pattern = pattern;
      return this;
   }

   public String getPattern()
   {
      return pattern;
   }

   @Override
   public String getName()
   {
      return new String(capture.getCaptured());
   }

   public CapturingGroup getCapture()
   {
      return capture;
   }

   @Override
   public String toString()
   {
      return "RegexParameter [capture=" + capture + "]";
   }
}
