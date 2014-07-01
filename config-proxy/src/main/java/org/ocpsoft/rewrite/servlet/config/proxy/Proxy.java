/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.ocpsoft.rewrite.servlet.config.proxy;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.exception.RewriteException;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.ParameterizedPatternBuilder;
import org.ocpsoft.rewrite.param.RegexParameterizedPatternBuilder;
import org.ocpsoft.rewrite.servlet.config.HttpOperation;
import org.ocpsoft.rewrite.servlet.config.Lifecycle;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public abstract class Proxy extends HttpOperation implements Parameterized
{
   private final ParameterizedPatternBuilder destination;

   private Proxy(String destination)
   {
      this.destination = new RegexParameterizedPatternBuilder(destination);
   }

   public static Proxy to(final String destination)
   {
      return new Proxy(destination) {
         @Override
         public String toString()
         {
            return "Proxy.to(\"" + destination + "\")";
         }
      };
   }

   @Override
   public void performHttp(HttpServletRewrite event, EvaluationContext context)
   {
      Map<String, String> params = new LinkedHashMap<String, String>();
      params.put("targetUri", destination.build(event, context));
      ProxyServlet proxyServlet = new ProxyServlet();
      ProxyServletConfig proxyConfig = new ProxyServletConfig(event.getServletContext(), params);
      try
      {
         proxyServlet.init(proxyConfig);
         proxyServlet.service(event.getRequest(), event.getResponse());
         Lifecycle.abort().perform(event, context);
      }
      catch (Exception e)
      {
         throw new RewriteException("Could not proxy event [" + event + "] to destination [" + destination + "]", e);
      }
   }

   @Override
   public Set<String> getRequiredParameterNames()
   {
      return destination.getRequiredParameterNames();
   }

   @Override
   public void setParameterStore(ParameterStore store)
   {
      destination.setParameterStore(store);
   }

}
