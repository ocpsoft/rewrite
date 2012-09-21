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

import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.Evaluation;
import org.ocpsoft.rewrite.bind.ParameterizedPatternImpl;
import org.ocpsoft.rewrite.bind.parse.CaptureType;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.PatternParameter;
import org.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * A simple {@link org.ocpsoft.rewrite.config.Operation} that performs forwards via
 * {@link org.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite#forward(String)}
 * 
 * @author <a href="mailto:christian.beikov@gmail.com">Christian Beikov</a>
 */
public class SimpleForward extends HttpOperation implements IForward
{
   private final String location;

   private SimpleForward(final String location)
   {
      Assert.notNull(location, "Location must not be null.");
      this.location = location;
   }

   /**
    * Forward the current request to the given location within the servlet container. This does not change the browser
    * {@link URL}, all processing is handled within the current {@link HttpServletRequest}.
    */
   public static SimpleForward to(final String location)
   {
      return new SimpleForward(location);
   }

   @Override
   public void performHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      if (event instanceof HttpInboundServletRewrite)
      {
         ((HttpInboundServletRewrite) event).forward(location);
      }
   }

   /**
    * Not supported
    * 
    * @throws UnsupportedOperationException
    */
   @Override
   public ForwardParameter where(final String param)
   {
	      throw new UnsupportedOperationException("SimpleForward does not support parameters!");
   }

   /**
    * Not supported
    * 
    * @throws UnsupportedOperationException
    */
   @Override
   public ForwardParameter where(final String param, final Binding binding)
   {
	      throw new UnsupportedOperationException("SimpleForward does not support parameters!");
   }

   @Override
   public String toString()
   {
      return location;
   }

   /**
    * Not supported
    * 
    * @throws UnsupportedOperationException
    */
   @Override
   public ParameterizedPatternImpl getTargetExpression()
   {
	      throw new UnsupportedOperationException("SimpleForward does not support parameters!");
   }
}
