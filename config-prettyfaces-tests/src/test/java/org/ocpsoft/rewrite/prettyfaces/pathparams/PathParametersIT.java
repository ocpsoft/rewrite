/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ocpsoft.rewrite.prettyfaces.pathparams;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.prettyfaces.PrettyFacesITBase;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteITBase;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class PathParametersIT extends RewriteITBase
{
   @Deployment(testable = false)
   public static WebArchive createDeployment()
   {
      return PrettyFacesITBase.getDeployment()
               .addAsWebResource("pathparams/index.xhtml", "index.xhtml")
               .addAsWebInfResource("pathparams/pretty-config.xml", "pretty-config.xml");
   }

   @Test
   public void testNamedPathParameterWithDefaultRegex() throws Exception
   {
      HttpAction action = get("/default/1234");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).contains("url=/default/1234");
      assertThat(action.getResponseContent()).contains("prettyRequest=true");
      assertThat(action.getResponseContent()).contains("paramDigits=1234");
   }

   @Test
   public void testNamedPathParameterWithCustomRegex() throws Exception
   {
      HttpAction action = get("/digits/1234");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).contains("url=/digits/1234");
      assertThat(action.getResponseContent()).contains("prettyRequest=true");
      assertThat(action.getResponseContent()).contains("paramDigits=1234");
   }

   @Test
   public void testNamedPathParameterWithCustomRegexFailingPattern() throws Exception
   {
      HttpAction action = get("/digits/1234s");
      assertThat(action.getStatusCode()).isEqualTo(404);
   }

}