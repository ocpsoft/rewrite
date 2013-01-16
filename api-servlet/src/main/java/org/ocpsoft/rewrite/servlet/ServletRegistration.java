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
package org.ocpsoft.rewrite.servlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Similar to <code>javax.servlet.ServletRegistration</code>. But as the original is only available in Servlet 3.0 and
 * new we use or own.
 * 
 * @author Christian Kaltepoth
 */
public class ServletRegistration
{

   private String className;

   private final List<String> mappings = new ArrayList<String>();

   public String getClassName()
   {
      return className;
   }

   public void setClassName(String className)
   {
      this.className = className;
   }

   public List<String> getMappings()
   {
      return mappings;
   }

   public void addMapping(String mapping)
   {
      this.mappings.add(mapping);
   }

   public void addMappings(Collection<String> mappings)
   {
      this.mappings.addAll(mappings);
   }

}
