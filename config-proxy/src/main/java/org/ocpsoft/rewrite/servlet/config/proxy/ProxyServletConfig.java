/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.ocpsoft.rewrite.servlet.config.proxy;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
class ProxyServletConfig implements ServletConfig
{
   private final ServletContext context;
   private final Map<String, String> params;

   public ProxyServletConfig(ServletContext context, Map<String, String> params)
   {
      this.context = context;
      this.params = params;
   }

   @Override
   public String getServletName()
   {
      return "Rewrite ProxyServlet";
   }

   @Override
   public ServletContext getServletContext()
   {
      return context;
   }

   @Override
   public String getInitParameter(String name)
   {
      return params.get(name);
   }

   @Override
   public Enumeration<String> getInitParameterNames()
   {
      return Collections.enumeration(params.keySet());
   }

}
