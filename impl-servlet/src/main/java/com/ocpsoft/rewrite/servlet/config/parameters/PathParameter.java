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

import java.util.ArrayList;
import java.util.List;

import com.ocpsoft.rewrite.servlet.config.Path;
import com.ocpsoft.rewrite.servlet.parse.CapturingGroup;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class PathParameter implements Parameter
{
   private final Path parent;
   private final CapturingGroup capture;

   private String pattern = "[^/]+";
   private final List<ParameterBinding> bindings = new ArrayList<ParameterBinding>();
   private final List<ParameterBinding> optionalBindings = new ArrayList<ParameterBinding>();

   public PathParameter(final Path path, final CapturingGroup capture)
   {
      this.parent = path;
      this.capture = capture;
   }

   public PathParameter matches(final String pattern)
   {
      this.pattern = pattern;
      return this;
   }

   public PathParameter bindsTo(final ParameterBinding binding)
   {
      this.bindings.add(binding);
      return this;
   }

   public PathParameter and(final String param)
   {
      return parent.and(param);
   }

   public PathParameter attemptBindTo(final ParameterBinding binding)
   {
      this.optionalBindings.add(binding);
      return this;
   }

   public CapturingGroup getCapture()
   {
      return capture;
   }

   @Override
   public String toString()
   {
      return "PathParameter [capture=" + capture + ", pattern=" + pattern + "]";
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
