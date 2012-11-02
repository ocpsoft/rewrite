/*
 * Copyright 2010 Lincoln Baxter, III
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

package com.ocpsoft.pretty.faces.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletRegistration;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class MockFacesServletRegistration implements ServletRegistration
{

   public String getName()
   {
      return "Faces Servlet";
   }

   public String getClassName()
   {
      return "javax.faces.webapp.FacesServlet";
   }

   public boolean setInitParameter(String name, String value)
   {
      return false;
   }

   public String getInitParameter(String name)
   {
      return null;
   }

   public Set<String> setInitParameters(Map<String, String> initParameters)
   {
      return null;
   }

   public Map<String, String> getInitParameters()
   {
      return new HashMap<String, String>();
   }

   public Set<String> addMapping(String... urlPatterns)
   {
      return new HashSet<String>();
   }

   public Collection<String> getMappings()
   {
      ArrayList<String> results = new ArrayList<String>();
      results.add("/faces/*");
      return results;
   }

   public String getRunAsRole()
   {
      return null;
   }

}
