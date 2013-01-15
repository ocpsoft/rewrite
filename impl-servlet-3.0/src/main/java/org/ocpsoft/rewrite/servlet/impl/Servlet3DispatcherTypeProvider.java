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
package org.ocpsoft.rewrite.servlet.impl;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;

import org.ocpsoft.rewrite.servlet.DispatcherType;
import org.ocpsoft.rewrite.servlet.spi.DispatcherTypeProvider;

/**
 * Implementation of {@link DispatcherTypeProvider} for Servlet 3.0 which supports
 * <code>HttpServletRequest.getDispatcherType()</code>
 * 
 * @author Christian Kaltepoth
 */
public class Servlet3DispatcherTypeProvider implements DispatcherTypeProvider
{

   @Override
   public int priority()
   {
      return 10;
   }

   @Override
   public DispatcherType getDispatcherType(ServletRequest request, ServletContext context)
   {
      if (context.getMajorVersion() >= 3) {
         return DispatcherType.valueOf(request.getDispatcherType().name());
      }
      return null;
   }

}
