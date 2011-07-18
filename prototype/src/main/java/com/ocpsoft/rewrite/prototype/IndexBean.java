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

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.inject.Named;

import com.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import com.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;

@Named
@RequestScoped
public class IndexBean
{
   private String name;
   private String selected;

   // @RequestMapping(url="/index/{param}")
   public void get(@Observes final HttpInboundServletRewrite event
            // ,@RequestParam String param
            ) throws IOException
   {
      // The code in these two methods will be replaced with framework annotations
      // on the class or action method itself
      String requestURL = event.getRequestURL();

      if ("/index".equals(requestURL)) {
         event.forward("/index.mvc");
      }
      if ("/result".equals(requestURL)) {
         event.forward("/result.mvc");
      }
   }

   public void rewriteOutbound(@Observes final HttpOutboundServletRewrite event) throws IOException
   {
      String outboundURL = event.getOutboundURL();
      if (outboundURL.endsWith(".mvc"))
      {
         event.setOutboundURL(outboundURL.substring(0, outboundURL.lastIndexOf(".mvc")));
      }
   }

   // This is our action method
   public Object handleSubmit(final Object i)
   {
      System.out.println("IndexBean.handleSubmit(" + i + ")");

      return Flow.RESULT;
   }

   public String getName()
   {
      return name;
   }

   public void setName(final String name)
   {
      System.out.println("Your name is " + name + "]");
      this.name = name;
   }

   public String getSelected()
   {
      return selected;
   }

   public void setSelected(final String selected)
   {
      System.out.println("You selected [" + selected + "]");
      this.selected = selected;
   }

}
