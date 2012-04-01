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

import java.util.ArrayList;
import java.util.List;

import org.ocpsoft.rewrite.bind.parse.CapturingGroup;
import org.ocpsoft.rewrite.param.Constrainable;
import org.ocpsoft.rewrite.param.Constraint;
import org.ocpsoft.rewrite.param.Transform;
import org.ocpsoft.rewrite.param.Transformable;

/**
 * Resultant capture of a {@link ParameterizedPattern}, used to store matching data and {@link Binding} instances.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class RegexCapture extends DefaultBindable<RegexCapture> implements Constrainable<RegexCapture, String>,
Transformable<RegexCapture, String>
{
   private String pattern;
   private final CapturingGroup capture;
   private final int index;

   /**
    * Create a new instance for the given {@link CapturingGroup}, using ".*" as the default pattern.
    * 
    * @param index
    */
   public RegexCapture(final CapturingGroup capture, int index)
   {
      this.capture = capture;
      this.index = index;
   }

   private final List<Transform<String>> transforms = new ArrayList<Transform<String>>();
   private final List<Constraint<String>> constraints = new ArrayList<Constraint<String>>();

   @Override
   public RegexCapture constrainedBy(Constraint<String> constraint)
   {
      this.constraints.add(constraint);
      return this;
   }

   @Override
   public List<Constraint<String>> getConstraints()
   {
      return constraints;
   }

   @Override
   public RegexCapture transformedBy(Transform<String> transform)
   {
      this.transforms.add(transform);
      return this;
   }

   @Override
   public List<Transform<String>> getTransforms()
   {
      return transforms;
   }

   public void matches(final String pattern)
   {
      this.pattern = pattern;
   }

   public String getPattern()
   {
      return pattern;
   }

   public int getIndex()
   {
      return index;
   }

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
      return "RegexParameter [name=" + getName() + ", capture=" + capture + "]";
   }
}