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

import java.util.Map;

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.Bindings;
import org.ocpsoft.rewrite.bind.Evaluation;
import org.ocpsoft.rewrite.bind.ParameterizedPatternImpl;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.PatternParameter;
import org.ocpsoft.rewrite.servlet.config.bind.Request;
import org.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * A simple {@link org.ocpsoft.rewrite.config.Condition} that inspects the value of
 * {@link org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite#getRequestPath()}
 * by evaluating equallity.
 * 
 * @author <a href="mailto:christian.beikov@gmail.com">Christian Beikov</a>
 */
public class SimplePath extends HttpCondition implements IPath
{
   private final String path;

   private SimplePath(final String path)
   {
      Assert.notNull(path, "Path must not be null.");
      this.path = path;
   }

   /**
    * Inspect the current request URL, comparing against the given path.
    * 
    */
   public static IPath matches(final String path)
   {
      return new SimplePath(path);
   }

   /**
    * Not supported
    * 
    * @throws UnsupportedOperationException
    */
   @Override
   public IPath withRequestBinding()
   {
      throw new UnsupportedOperationException("SimplePath does not support parameters!");
   }

   @Override
   public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      String url;
      int fromIndex = 0;

      if (event instanceof HttpOutboundServletRewrite)
      {
         url = ((HttpOutboundServletRewrite) event).getOutboundURL();
         if (url != null)
         {
            if (url.startsWith(event.getContextPath()))
            {
               fromIndex = event.getContextPath().length();
            }
         }
      }
      else
         url = event.getRequestPath();

      if(url == null)
      {
          return false;
      }
      
      /* 
       * The evaluation of the equallity can probably be done simpler, but this
       * was the most efficient way I could think of. I am open for suggestions 
       * :)
       */
      final int urlCompareLength = url.length() - fromIndex;
      
      return url.startsWith(path, fromIndex) && (urlCompareLength == path.length() || (urlCompareLength > path.length() && url.charAt(fromIndex  + path.length()) == '?'));
   }

   /**
    * Not supported
    * 
    * @throws UnsupportedOperationException
    */
   @Override
   public ParameterizedPatternImpl getPathExpression()
   {
      throw new UnsupportedOperationException("SimplePath does not support parameters!");
   }

   @Override
   public String toString()
   {
      return path;
   }

   /**
    * Not supported
    * 
    * @throws UnsupportedOperationException
    */
   @Override
   public PathParameter where(String param)
   {
      throw new UnsupportedOperationException("SimplePath does not support parameters!");
   }

   /**
    * Not supported
    * 
    * @throws UnsupportedOperationException
    */
   @Override
   public PathParameter where(String param, Binding binding)
   {
      throw new UnsupportedOperationException("SimplePath does not support parameters!");
   }
}