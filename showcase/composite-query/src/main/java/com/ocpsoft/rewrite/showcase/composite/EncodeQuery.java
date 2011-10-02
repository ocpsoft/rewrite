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
package com.ocpsoft.rewrite.showcase.composite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ocpsoft.rewrite.config.Condition;
import com.ocpsoft.rewrite.config.Operation;
import com.ocpsoft.rewrite.config.True;
import com.ocpsoft.rewrite.context.EvaluationContext;
import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.servlet.config.DispatchType;
import com.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import com.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import com.ocpsoft.rewrite.servlet.util.QueryStringBuilder;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class EncodeQuery implements Operation
{
   private static final String STATE = EncodeQuery.class.getName();
   private String name;
   private ChecksumStrategy checksumStrategy = new HashCodeChecksumStrategy();
   private EncodingStrategy encodingStrategy = new Base64EncodingStrategy();
   private Condition condition = new True();
   private final List<String> params = new ArrayList<String>();
   private final List<String> excludedParams = new ArrayList<String>();

   public EncodeQuery()
   {}

   public EncodeQuery(final String[] params)
   {
      if ((params != null) && (params.length > 0))
         this.params.addAll(Arrays.asList(params));
   }

   public static EncodeQuery params(final String... params)
   {
      return new EncodeQuery(params);
   }

   public EncodeQuery excluding(final String... params)
   {
      if ((params != null) && (params.length > 0))
         this.excludedParams.addAll(Arrays.asList(params));
      return this;
   }

   public EncodeQuery when(final Condition condition)
   {
      this.condition = condition;
      return this;
   }

   public EncodeQuery withEncodingStrategy(final EncodingStrategy strategy)
   {
      this.encodingStrategy = strategy;
      return this;
   }

   public EncodeQuery withChecksumStrategy(final ChecksumStrategy strategy)
   {
      this.checksumStrategy = strategy;
      return this;
   }

   public EncodeQuery to(final String param)
   {
      this.name = param;
      return this;
   }

   @Override
   public void perform(final Rewrite event, final EvaluationContext context)
   {
      if ((event instanceof HttpInboundServletRewrite) && !isProcessed(event))
      {
         HttpInboundServletRewrite in = (HttpInboundServletRewrite) event;

         QueryStringBuilder query = QueryStringBuilder.begin();
         query.addParameters(in.getRequestQueryString());

         String value = query.decode().getParameter(name);
         if (value != null)
         {
            String decoded = encodingStrategy.decode(value);
            String newUrl = in.getRequestPath() + "?" + decoded;
            setProcessed(event);
            in.forward(newUrl);
         }

         // TODO enable inbound correction
         else if (!query.isEmpty() && DispatchType.isRequest().evaluate(event, context))
         {
            String encoded = encodingStrategy.encode(in.getRequestQueryString());
            setProcessed(event);
            in.redirectTemporary(in.getContextPath() + in.getRequestPath()
                     + "?" + name + "=" + encoded);
         }
      }

      else if (event instanceof HttpOutboundServletRewrite)
      {
         HttpOutboundServletRewrite out = (HttpOutboundServletRewrite) event;

         if (out.getURL().startsWith(out.getContextPath()) || out.getURL().startsWith("/"))
         {
            QueryStringBuilder query = QueryStringBuilder.begin();
            query.addParameters(out.getRequestQueryString());

            if (!query.isEmpty())
            {
               String encoded = encodingStrategy.encode(out.getRequestQueryString());
               setProcessed(event);

               String outboundURL = out.getOutboundURL();
               if (outboundURL.contains("?"))
               {
                  outboundURL = outboundURL.split("\\?")[0];
               }

               out.setOutboundURL(outboundURL + out.getRequestQueryStringSeparator() + name + "=" + encoded);
            }
         }

      }

   }

   public boolean isProcessed(final Rewrite event)
   {
      return ((HttpServletRewrite) event).getRequest().getAttribute(STATE) != null;
   }

   public void setProcessed(final Rewrite event)
   {
      ((HttpServletRewrite) event).getRequest().setAttribute(STATE, EncodeQuery.class.getName());
   }

}
