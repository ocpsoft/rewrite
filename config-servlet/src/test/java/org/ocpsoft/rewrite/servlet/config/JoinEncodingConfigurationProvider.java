/*
 * Copyright 2012 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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
import java.nio.charset.Charset;

import javax.servlet.ServletContext;

import org.ocpsoft.common.util.Streams;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.config.rule.Join;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * Configuration for {@link JoinEncodingConfigurationTest}.
 * 
 * @author Christian Kaltepoth
 */
public class JoinEncodingConfigurationProvider extends HttpConfigurationProvider
{

   @Override
   public Configuration getConfiguration(final ServletContext context)
   {
      return ConfigurationBuilder.begin()

               /*
                * a simple join
                */
               .addRule(Join.path("/encoding/{param}").to("/encoding.html").withInboundCorrection())

               /*
                * request to this URL will response with details about the requested (forwarded) URL and the query parameter
                */
               .addRule()
               .when(Direction.isInbound().and(Path.matches("/encoding.html")))
               .perform(new HttpOperation() {
                  @Override
                  public void performHttp(HttpServletRewrite event, EvaluationContext context)
                  {
                     StringBuilder result = new StringBuilder();
                     result.append("HttpServletRequest.getRequestPath() = ");
                     result.append(event.getInboundAddress().getPath());
                     result.append("\n");

                     for (Object key : event.getRequest().getParameterMap().keySet()) {
                        result.append("getParameter('");
                        result.append(key);
                        result.append("') = ");
                        result.append(event.getRequest().getParameter(key.toString()));
                        result.append("\n");
                     }

                     try {

                        // I'm sure there is a nicer way to do this. :)
                        event.getResponse().getOutputStream().write(
                                 result.toString().getBytes(Charset.forName("UTF-8")));
                        SendStatus.code(200).perform(event, context);
                        Lifecycle.handled().perform(event, context);

                     }
                     catch (IOException e) {
                        throw new IllegalStateException(e);
                     }

                  }
               })

               /*
                * Clients can post an URL to this address and will receive the result processed by HttpServletResponse.encodeURL()
                */
               .addRule()
               .when(Direction.isInbound().and(Path.matches("/outbound")))
               .perform(new HttpOperation() {
                  @Override
                  public void performHttp(HttpServletRewrite event, EvaluationContext context)
                  {

                     try {

                        String url = Streams.toString(event.getRequest().getInputStream());
                        String rewritten = event.getResponse().encodeURL(url);

                        // I'm sure there is a nicer way to do this. :)
                        event.getResponse().getOutputStream().write(rewritten.getBytes(Charset.forName("UTF-8")));
                        SendStatus.code(200).perform(event, context);
                        Lifecycle.handled().perform(event, context);

                     }
                     catch (IOException e) {
                        throw new IllegalStateException(e);
                     }

                  }
               });

   }

   @Override
   public int priority()
   {
      return 0;
   }

}
