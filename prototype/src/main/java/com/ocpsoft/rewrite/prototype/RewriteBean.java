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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ocpsoft.rewrite.cdi.events.AfterRewrite;
import com.ocpsoft.rewrite.cdi.events.AfterRewriteLifecycle;
import com.ocpsoft.rewrite.cdi.events.BeforeRewrite;
import com.ocpsoft.rewrite.cdi.events.BeforeRewriteLifecycle;
import com.ocpsoft.rewrite.cdi.events.RewriteInbound;
import com.ocpsoft.rewrite.cdi.events.RewriteOutbound;
import com.ocpsoft.rewrite.event.RewriteEvent;
import com.ocpsoft.rewrite.servlet.HttpOutboundRewriteEvent;
import com.ocpsoft.rewrite.servlet.HttpRewriteEvent;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class RewriteBean
{
   public RewriteBean()
   {}

   public void rewriteInbound(
            @Observes @RewriteInbound final HttpRewriteEvent event) throws IOException
   {
      HttpServletRequest request = event.getRequest();
      System.out.println("INBOUND: " + request.getRequestURI());
   }

   public void rewriteOutbound(
            @Observes @RewriteOutbound final HttpOutboundRewriteEvent event)
   {
      System.out.println("OUTBOUND: " + event.getURL());
      event.setURL(event.getURL().replaceAll("miss", "be thinking of you having fun" +
               ""));
   }

   public void before(
            @Observes @BeforeRewriteLifecycle final RewriteEvent event)
   {
      System.out.println("Before Rewrite Lifecycle");
   }

   public void beforeRewrite(
            @Observes @BeforeRewrite final RewriteEvent event)
   {
      System.out.println("Before Rewrite");
   }

   public void afterRewrite(
            @Observes @AfterRewrite final HttpRewriteEvent event)
   {
      System.out.println("After Rewrite");

      HttpServletResponse response = event.getResponse();
      String url = response.encodeURL("I am going to miss you, Lincoln!");
      System.out.println(url);
   }

   public void after(
            @Observes @AfterRewriteLifecycle final HttpRewriteEvent event)
   {
      System.out.println("After Rewrite Lifecycle");
   }

}
