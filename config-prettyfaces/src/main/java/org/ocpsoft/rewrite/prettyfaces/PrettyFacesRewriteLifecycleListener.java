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
package org.ocpsoft.rewrite.prettyfaces;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.rewrite.servlet.http.HttpRewriteLifecycleListener;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.servlet.spi.RewriteLifecycleListener;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.config.PrettyConfig;
import com.ocpsoft.pretty.faces.config.reload.PrettyConfigReloader;

/**
 * Propagates events from {@link RewriteLifecycleListener} to CDI Event bus.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class PrettyFacesRewriteLifecycleListener extends HttpRewriteLifecycleListener
{
   PrettyConfigReloader reloader = new PrettyConfigReloader();

   @Override
   public int priority()
   {
      return -100;
   }

   @Override
   public void beforeInboundLifecycle(final HttpServletRewrite event)
   {
      HttpServletRequest request = event.getRequest();
      ServletContext servletContext = event.getServletContext();

      // we may need to reload the configuration if in development mode
      reloader.reloadIfNecessary(servletContext);

      // we need the PrettyConfig later, so save it as a request attribute
      if (request.getAttribute(PrettyContext.CONFIG_KEY) == null)
      {
         PrettyConfig config = (PrettyConfig) servletContext.getAttribute(PrettyContext.CONFIG_KEY);
         request.setAttribute(PrettyContext.CONFIG_KEY, config);
      }

      // build the PrettyContext and attach it to the request
      if (request.getAttribute(UrlMappingRuleAdaptor.REWRITE_MAPPING_ID_KEY) == null) {
         PrettyContext context = PrettyContext.newDetachedInstance(request);
         PrettyContext.setCurrentContext(event.getRequest(), context);
      }

   }

   @Override
   public void beforeInboundRewrite(final HttpServletRewrite event)
   {}

   @Override
   public void afterInboundRewrite(final HttpServletRewrite event)
   {}

   @Override
   public void afterInboundLifecycle(final HttpServletRewrite event)
   {
      PrettyContext.setCurrentContext(event.getRequest(), null);
   }

   @Override
   public void beforeOutboundRewrite(final HttpServletRewrite event)
   {}

   @Override
   public void afterOutboundRewrite(final HttpServletRewrite event)
   {}
}
