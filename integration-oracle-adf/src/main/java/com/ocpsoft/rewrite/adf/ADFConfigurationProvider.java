package com.ocpsoft.rewrite.adf;
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


import javax.servlet.ServletContext;

import com.ocpsoft.rewrite.config.Configuration;
import com.ocpsoft.rewrite.config.ConfigurationBuilder;
import com.ocpsoft.rewrite.context.EvaluationContext;
import com.ocpsoft.rewrite.servlet.config.HttpCondition;
import com.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import com.ocpsoft.rewrite.servlet.config.HttpOperation;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ADFConfigurationProvider extends HttpConfigurationProvider
{
   @Override
   public int priority()
   {
      return -100;
   }

   /**
    * <b>Inbound:</b><br>
    * -----------<br>
    * If ... , then ....
    * <p>
    * <b>Outbound:</b><br>
    * -----------<br>
    * If ... , then ....
    */
   @Override
   public Configuration getConfiguration(final ServletContext context)
   {
      Configuration config = ConfigurationBuilder.begin()

               .defineRule()
               .when(new HttpCondition() {
                  
                  @Override
                  public boolean evaluateHttp(HttpServletRewrite event, EvaluationContext context)
                  {

                     return false;
                  }
               }
               
               // .and(/* extra conditions go here */ null) 
               )
               
               .perform(new HttpOperation() {
                  
                  @Override
                  public void performHttp(HttpServletRewrite event, EvaluationContext context)
                  {
                     
                  }
               }.and(/* more operations go here */ null))

               /*
                * And you can have more than one rule
                */
               .defineRule() // ...

      ;

      /**
       * TODO convert this into a Rewrite configuration.
       * 
       * public String processInbound(final HttpServletRequest request, final HttpServletResponse response, final
       * RewriteRule rewrite, final String url) {
       * 
       * // we have a URL with query params
       * 
       * if (request.getParameterValues("_adf.ctrl-state") != null && url.contains("_adf.ctrl-state")) { // we have
       * access to _adf.ctrl-state & query string includes _adf.ctrl-state, so we can begin rewriting the inbound String
       * newUrl = url.replaceAll("\\?(.*)", ""); // remove query string String params = "";
       * 
       * // add other required ADF params back, in order if (request.getParameterValues("_afrWindowId") != null) {
       * params = "?_afrWindowId=" + request.getParameterValues("_afrWindowId")[0]; }
       * 
       * if (request.getParameterValues("_afrLoop") != null) { params = params + (params.length() == 0 ? "?_afrLoop=" :
       * "&_afrLoop=") + request.getParameterValues("_afrLoop")[0]; }
       * 
       * if (request.getParameterValues("_afrWindowMode") != null) { params = params + (params.length() == 0 ?
       * "?_afrWindowMode=" : "&_afrWindowMode=") + request.getParameterValues("_afrWindowMode")[0]; }
       * 
       * newUrl = newUrl + params + (params.length() == 0 ? "?_adf.ctrl-state=" : "&_adf.ctrl-state=") +
       * request.getParameterValues("_adf.ctrl-state")[0]; // add _adf.ctrl-state back
       * 
       * return newUrl; }
       * 
       * return url; }
       */

      return config;
   }
}
