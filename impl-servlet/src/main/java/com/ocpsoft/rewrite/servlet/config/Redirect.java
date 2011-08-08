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
package com.ocpsoft.rewrite.servlet.config;

import com.ocpsoft.rewrite.EvaluationContext;
import com.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class Redirect extends HttpOperation
{
   private final String location;
   private final RedirectType type;

   public Redirect(final String location, final RedirectType type)
   {
      this.location = location;
      this.type = type;
   }

   @Override
   public void performHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      if (event instanceof HttpInboundServletRewrite)
      {
         switch (type)
         {
         case PERMANENT:
            ((HttpInboundServletRewrite) event).redirectPermanent(location);
            break;
         case TEMPORARY:
            ((HttpInboundServletRewrite) event).redirectTemporary(location);
            break;
         default:
            break;
         }
      }
      else
      {
         throw new IllegalStateException("Redirect operations may only be performed on inbound rewrite events.");
      }
   }

   public static Redirect permanent(final String location)
   {
      return new Redirect(location, RedirectType.PERMANENT);
   }

   public static Redirect temporary(final String location)
   {
      return new Redirect(location, RedirectType.TEMPORARY);
   }

   private enum RedirectType
   {
      PERMANENT, TEMPORARY
   }

}
