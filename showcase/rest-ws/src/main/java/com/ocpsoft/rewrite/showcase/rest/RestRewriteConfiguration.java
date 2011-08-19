package com.ocpsoft.rewrite.showcase.rest;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.ocpsoft.rewrite.bind.Binding;
import com.ocpsoft.rewrite.bind.Evaluation;
import com.ocpsoft.rewrite.bind.ParameterizedPattern;
import com.ocpsoft.rewrite.config.Configuration;
import com.ocpsoft.rewrite.config.ConfigurationBuilder;
import com.ocpsoft.rewrite.context.EvaluationContext;
import com.ocpsoft.rewrite.convert.IntegerConverter;
import com.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import com.ocpsoft.rewrite.servlet.config.HttpOperation;
import com.ocpsoft.rewrite.servlet.config.Method;
import com.ocpsoft.rewrite.servlet.config.Path;
import com.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class RestRewriteConfiguration extends HttpConfigurationProvider
{
   @Inject
   private ProductRegistry products;

   @Override
   public Configuration getConfiguration(final ServletContext context)
   {
      return ConfigurationBuilder.begin()

               .defineRule()
               .when(Path.matches("/store/product/{pid}").and(Method.isGet()))
               .perform(new HttpOperation() {
                  @Override
                  public void performHttp(final HttpServletRewrite event, final EvaluationContext context)
                  {
                     /*
                      * Extract the stored {pid} from our Path and load the Product.
                      */
                     Binding property = Evaluation.property("pid").convertedBy(IntegerConverter.class);
                     Product product = products.getById((Integer) property.convert(event, context,
                              property.retrieve(event, context)));

                     /*
                      * Marshal the Product into XML using JAXB
                      */
                     try {
                        Marshaller marshaller = JAXBContext.newInstance(Product.class)
                                 .createMarshaller();
                        marshaller.setProperty("jaxb.formatted.output", true);
                        marshaller.marshal(product, event.getResponse().getOutputStream());

                        event.getResponse().setContentType("application/xml");
                        ((HttpInboundServletRewrite) event).sendStatusCode(200);
                     }
                     catch (Exception e) {
                        throw new RuntimeException(e);
                     }
                  }
               })

               .defineRule()
               .when(Path.matches("/store/products").and(Method.isPost()))
               .perform(new HttpOperation() {
                  @Override
                  public void performHttp(final HttpServletRewrite event, final EvaluationContext context)
                  {
                     /*
                      * Unmarshal the Request Body into XML using JAXB
                      */
                     try {
                        Unmarshaller unmarshaller = JAXBContext.newInstance(Product.class).createUnmarshaller();
                        Product product = (Product) unmarshaller.unmarshal(event.getRequest()
                                 .getInputStream());

                        product = products.add(product);

                        Map<String, List<Object>> values = new LinkedHashMap<String, List<Object>>();
                        values.put("pid", (List) Arrays.asList(product.getId()));
                        ((HttpInboundServletRewrite) event).redirectPermanent(event.getContextPath()
                                 + new ParameterizedPattern("/store/product/{pid}").build(values));
                     }
                     catch (Exception e) {
                        throw new RuntimeException(e);
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
