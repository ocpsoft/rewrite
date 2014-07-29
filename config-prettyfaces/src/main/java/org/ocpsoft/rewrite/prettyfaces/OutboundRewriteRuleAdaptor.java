/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.ocpsoft.rewrite.prettyfaces;

import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.urlbuilder.Address;
import org.ocpsoft.urlbuilder.AddressBuilder;

import com.ocpsoft.pretty.faces.config.rewrite.RewriteRule;
import com.ocpsoft.pretty.faces.rewrite.RewriteEngine;
import com.ocpsoft.pretty.faces.url.URL;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public class OutboundRewriteRuleAdaptor implements Rule
{
   private final RewriteRule rule;

   public OutboundRewriteRuleAdaptor(final RewriteRule rule)
   {
      this.rule = rule;
   }

   /**
    * If the given URL is prefixed with this request's context-path, return the URI without the context path. Otherwise
    * return the URI unchanged.
    */
   private String stripContextPath(final String contextPath, String uri)
   {
      if (!contextPath.equals("/") && uri.startsWith(contextPath))
      {
         uri = uri.substring(contextPath.length());
      }
      return uri;
   }

   @Override
   public String getId()
   {
      return toString();
   }

   @Override
   public boolean evaluate(final Rewrite event, final EvaluationContext context)
   {
      if ((event instanceof HttpOutboundServletRewrite) && rule.isOutbound())
      {
         Address outboundResource = ((HttpOutboundServletRewrite) event).getAddress();
         String outboundUrl = outboundResource.toString();

         String contextPath = ((HttpServletRewrite) event).getContextPath();
         if (!contextPath.equals("/") && outboundUrl.startsWith(contextPath))
            outboundUrl = outboundUrl.substring(contextPath.length());

         if (rule.matches(URL.build(outboundUrl).decode().toURL()))
            return true;
      }
      return false;
   }

   @Override
   public void perform(final Rewrite event, final EvaluationContext context)
   {
      RewriteEngine engine = new RewriteEngine();
      HttpOutboundServletRewrite outbound = (HttpOutboundServletRewrite) event;
      String url = outbound.getOutboundAddress().toString();
      String strippedUrl = stripContextPath(outbound.getContextPath(), url);

      String result = "";
      if (!strippedUrl.equals(url))
      {
         result = outbound.getContextPath();
      }
      strippedUrl = engine.processOutbound(((HttpServletRewrite) event).getRequest(),
               ((HttpServletRewrite) event).getResponse(), rule, strippedUrl);
      result += strippedUrl;

      outbound.setOutboundAddress(AddressBuilder.create(result));
   }

}
