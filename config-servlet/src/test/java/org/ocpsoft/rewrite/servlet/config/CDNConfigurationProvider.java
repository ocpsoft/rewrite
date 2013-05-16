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

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.config.rule.CDN;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public class CDNConfigurationProvider extends HttpConfigurationProvider
{
   @Override
   public int priority()
   {
      return 0;
   }

   /**
    * <b>Outbound:</b><br>
    * -----------<br>
    * If URL matches path convert the URL to the form specified by resource.
    */
   @Override
   public Configuration getConfiguration(final ServletContext context)
   {
      Configuration config = ConfigurationBuilder
               .begin()

               /*
                * Set up our rule (This does the work.)
                */
               .addRule(CDN.relocate("{p}jquery{s}.js")
                        .to("http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"))
               .where("p").matches(".*")
               .where("s").matches(".*")

               .addRule(CDN.relocate("{p}foo-{version}.js")
                        .to("http://mycdn.com/foo-{version}.js"))
               .where("p").matches(".*")
               .where("version").matches(".*")

               // URL without a schema
               .addRule(CDN.relocate("{*}/angular.min.js")
                        .to("//ajax.googleapis.com/ajax/libs/angularjs/1.0.6/angular.min.js"))

               /*
                * Now send a response to our test case containing the relocated resource.
                */
               .addRule().when(Direction.isInbound().and(Path.matches("/relocate")))
               .perform(new HttpOperation() {

                  @Override
                  public void performHttp(final HttpServletRewrite event, final EvaluationContext context)
                  {
                     try {
                        HttpServletResponse response = event.getResponse();
                        response.getWriter().write(response.encodeURL("/jquery.min.js"));
                        response.getWriter().write(response.encodeURL("/something/foo-1.2.3.js"));
                        response.getWriter().write("[" + response.encodeURL("/somewwhere/angular.min.js") + "]");
                        SendStatus.code(200).perform(event, context);
                     }
                     catch (IOException e) {
                        throw new RuntimeException(e);
                     }
                  }
               });

      return config;
   }
}
