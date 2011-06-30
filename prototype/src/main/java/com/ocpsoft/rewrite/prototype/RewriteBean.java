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

import javax.enterprise.event.Observes;
import javax.servlet.http.HttpServletResponse;

import com.ocpsoft.rewrite.cdi.events.AfterRewrite;
import com.ocpsoft.rewrite.cdi.events.AfterRewriteLifecycle;
import com.ocpsoft.rewrite.cdi.events.BeforeRewrite;
import com.ocpsoft.rewrite.cdi.events.BeforeRewriteLifecycle;
import com.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import com.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class RewriteBean
{
   public void rewriteInbound(
            @Observes final HttpInboundServletRewrite event) throws IOException
   {
      String requestURL = event.getRequestURL();

      System.out.println("INBOUND MAPPING: " + requestURL);

      if ("/page".equals(requestURL)) {
         event.forward("/faces/page.xhtml");

         HttpServletResponse response = event.getResponse();
         String url = response.encodeURL("I am going to miss you, Lincoln!");
         System.out.println(url);
      }
   }

   public void rewriteOutboundPage(
            @Observes final HttpOutboundServletRewrite event)
   {
      String outboundURL = event.getOutboundURL();

      if (outboundURL.equals(event.getContextPath() + "/faces/page.xhtml")) {
         System.out.println("OUTBOUND MAPPING: " + outboundURL);
         event.setOutboundURL(event.getContextPath() + "/page");
      }
   }

   /*
    * Examples
    */

   public void rewriteOutbound(
            @Observes final HttpOutboundServletRewrite event)
   {
      System.out.println("OUTBOUND: " + event.getOutboundURL());
      event.setOutboundURL(event.getOutboundURL().replaceAll("miss you", "be thinking of you having fun" +
               ""));
   }

   public void before(
            @Observes final BeforeRewriteLifecycle event)
   {
      System.out.println("Before Rewrite Lifecycle");
   }

   public void beforeRewrite(
            @Observes final BeforeRewrite event)
   {
      System.out.println("Before Rewrite");
   }

   public void afterRewrite(
            @Observes final AfterRewrite event)
   {
      System.out.println("After Rewrite");
   }

   public void after(
            @Observes final AfterRewriteLifecycle event)
   {
      System.out.println("After Rewrite Lifecycle");
   }

}
