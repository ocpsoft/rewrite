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
package org.ocpsoft.rewrite.spring.resolver;

import org.springframework.stereotype.Component;

/**
 * @author Christian Kaltepoth
 */
@Component
public class SpringBeanNameResolverBean
{

   private String name;

   private String uppercase;

   public void action()
   {
      System.out.println("----------------------------------");
      System.out.println("----------------------------------");
      System.out.println("----------------------------------");
      System.out.println("----------------------------------");
      uppercase = name != null ? name.toUpperCase() : null;
      System.out.println(name + "----> "+uppercase);
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public String getUppercase()
   {
      return uppercase;
   }

   public void setUppercase(String uppercase)
   {
      this.uppercase = uppercase;
   }

}
