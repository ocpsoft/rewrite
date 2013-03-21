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
package org.ocpsoft.rewrite.showcase.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.bind.Evaluation;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.RegexParameterizedPatternBuilder;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.HttpOperation;
import org.ocpsoft.rewrite.servlet.config.Method;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.Response;
import org.ocpsoft.rewrite.servlet.config.SendStatus;
import org.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class RestRewriteConfiguration extends HttpConfigurationProvider
{

   private final class PostOperation extends HttpOperation implements Parameterized
   {
      @Override
      public void performHttp(final HttpServletRewrite event, final EvaluationContext context)
      {
         try {
            Product product = XMLUtil.streamToObject(Product.class, event.getRequest().getInputStream());
            product = products.add(product);

            /**
             * Just for fun, set a response header containing the URL to the newly created Product.
             */
            String location = new RegexParameterizedPatternBuilder(event.getContextPath()
                     + "/store/product/{pid}").build(Arrays.<Object> asList(product.getId()));
            Response.addHeader("Location", location).perform(event, context);

            event.getResponse().setContentType("text/html");
            ((HttpInboundServletRewrite) event).sendStatusCode(200);
         }
         catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      @Override
      public Set<String> getRequiredParameterNames()
      {
         return null;
      }

      @Override
      public void setParameterStore(ParameterStore store)
      {

      }
   }

   @Inject
   private ProductRegistry products;

   @Inject
   private ProductConverter productConverter;

   @Inject
   private ProductValidator productValidator;

   @Override
   public Configuration getConfiguration(final ServletContext context)
   {
      return ConfigurationBuilder
               .begin()

               .addRule()

               /**
                * Define the inbound conditions and conversion mechanisms to be used when handling inbound requests.
                */
               .when(Method.isGet()
                        .and(Path.matches("/store/product/{pid}")))
               .perform(new HttpOperation() {
                  @Override
                  public void performHttp(final HttpServletRewrite event, final EvaluationContext context)
                  {
                     /**
                      * Extract the stored {pid} from our Path and load the Product. This is an example of how we can
                      * use a converter to directly bind and store the object we want into a binding. {@link Evaluation}
                      * is an low-level construct, and binds array values that must be dereferenced. If using other
                      * bindings such as {@link El}, the value will be bound directly to the type of the referenced
                      * property type, and this array downcast is not necessary.
                      */
                     ParameterStore store = (ParameterStore) context.get(ParameterStore.class);
                     Product product = (Product) Evaluation.property("pid").retrieveConverted(event, context,
                              store.get("pid"));

                     /**
                      * Marshal the Product into XML using JAXB. This has been extracted into a utility class.
                      */
                     try {
                        XMLUtil.streamFromObject(Product.class, product, event.getResponse()
                                 .getOutputStream());
                     }
                     catch (IOException e) {
                        throw new RuntimeException(e);
                     }

                     /**
                      * Set the content type and status code of the response, this again could be extracted into a REST
                      * utility class.
                      */
                     event.getResponse().setContentType("application/xml");
                     ((HttpInboundServletRewrite) event).sendStatusCode(200);
                  }
               }).where("pid").matches("\\d+")
               .constrainedBy(new IntegerConstraint())
               .convertedBy(productConverter)
               .validatedBy(productValidator)

               .addRule()
               .when(Path.matches("/store/products").and(Method.isGet()))
               .perform(new HttpOperation() {
                  @Override
                  public void performHttp(final HttpServletRewrite event, final EvaluationContext context)
                  {
                     try {
                        XMLUtil.streamFromObject(ProductRegistry.class, products, event.getResponse().getOutputStream());
                        event.getResponse().setContentType("application/xml");
                        ((HttpInboundServletRewrite) event).sendStatusCode(200);
                     }
                     catch (Exception e) {
                        throw new RuntimeException(e);
                     }
                  }
               })

               .addRule()
               .when(Path.matches("/store/products").and(Method.isPost()))
               .perform(new PostOperation())

               .addRule().when(Path.matches("/")).perform(new HttpOperation() {

                  @Override
                  public void performHttp(final HttpServletRewrite event, final EvaluationContext context)
                  {
                     try {
                        PrintWriter writer = event.getResponse().getWriter();
                        writer.write("<html>"
                                 +
                                 "<body>"
                                 +
                                 "<h1>Rewrite Rest Demo</h1>"
                                 +
                                 "Sorry for the boring page, there are no HTML pages in this demo! Try some of the following operations:"
                                 + ""
                                 +
                                 "<ul>"
                                 +
                                 "<li>GET <a href=\""
                                 + event.getContextPath()
                                 + "/store/product/0\">/store/product/0</a></li>"
                                 +
                                 "<li>GET <a href=\""
                                 + event.getContextPath()
                                 + "/store/products\">/store/products</a></li>"
                                 +
                                 "<li>POST "
                                 + event.getContextPath()
                                 + "/store/products - This requires a rest client, or you can use `curl`<br/>"
                                 +
                                 "curl --data \"&lt;product&gt;&lt;name&gt;James&lt;/name&gt;&lt;description&gt;yay&lt;/description&gt;&lt;price&gt;12.9&lt;/price&gt;&lt;/product&gt;\" http://localhost:8080/rewrite-showcase-rest/store/products</li>"
                                 +
                                 "</ul>" +
                                 "</body></html>");
                        SendStatus.code(200).perform(event, context);
                     }
                     catch (IOException e) {
                        throw new RuntimeException();
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
