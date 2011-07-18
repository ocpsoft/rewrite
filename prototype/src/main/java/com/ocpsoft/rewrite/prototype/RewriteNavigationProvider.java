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
package com.ocpsoft.rewrite.prototype;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.mvc.spi.NavigationProvider;

/**
 * This class will be provided by the MVC framework, and will use whatever kind of configuration we decide to go with.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class RewriteNavigationProvider implements NavigationProvider<Flow>
{
   @Override
   public boolean handles(final Object result)
   {
      return (result instanceof Flow);
   }

   @Override
   public boolean navigate(final HttpServletRequest req, final HttpServletResponse resp, final Flow result)
   {
      // Always redirect. Otherwise, we have form issues.
      resp.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
      resp.setHeader("Location",
               resp.encodeRedirectURL(req.getContextPath() + "/" + result.name().toLowerCase() + ".mvc"));
      try {
         resp.flushBuffer();
      }
      catch (IOException e) {
         throw new RuntimeException(e);
      }
      return true;
   }
}
