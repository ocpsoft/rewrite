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
package org.ocpsoft.rewrite.servlet.config;

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.ParameterizedPatternImpl;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * Responsible for substituting inbound/outbound URLs with a replacement. For {@link org.ocpsoft.rewrite.event.InboundRewrite} events, this
 * {@link Operation} calls {@link HttpInboundServletRewrite#forward(String)}, and for {@link org.ocpsoft.rewrite.event.OutboundRewrite} events,
 * this method calls {@link HttpOutboundServletRewrite#setOutboundURL(String)}
 * 
 * @author <a href="mailto:christian.beikov@gmail.com">Christian Beikov</a>
 */
public class SimpleSubstitute extends HttpOperation implements ISubstitute
{
   private final String location;

   private SimpleSubstitute(final String location)
   {
      Assert.notNull(location, "Location must not be null.");
      this.location = location;
   }

   /**
    * Substitute the current URL with the given location.
    */
   public static SimpleSubstitute with(final String location)
   {
      return new SimpleSubstitute(location);
   }

   @Override
   public void performHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      if (event instanceof HttpInboundServletRewrite)
      {
         ((HttpInboundServletRewrite) event).forward(location);
      }
      else if (event instanceof HttpOutboundServletRewrite)
      {
         String target = location;
         if (((HttpOutboundServletRewrite) event).getOutboundURL().startsWith(event.getContextPath())
                  && target.startsWith("/")
                  && !target.startsWith(event.getContextPath()))
         {
            target = event.getContextPath() + target;
         }
         ((HttpOutboundServletRewrite) event).setOutboundURL(target);
      }
   }

   /**
    * Not supported
    * 
    * @throws UnsupportedOperationException
    */
   @Override
   public SubstituteParameter where(final String param)
   {
	      throw new UnsupportedOperationException("SimpleSubstitute does not support parameters!");
   }

   /**
    * Not supported
    * 
    * @throws UnsupportedOperationException
    */
   @Override
   public SubstituteParameter where(final String param, final Binding binding)
   {
	      throw new UnsupportedOperationException("SimpleSubstitute does not support parameters!");
   }

   /**
    * Not supported
    * 
    * @throws UnsupportedOperationException
    */
   @Override
   public ParameterizedPatternImpl getTargetExpression()
   {
	      throw new UnsupportedOperationException("SimpleSubstitute does not support parameters!");
   }
}
