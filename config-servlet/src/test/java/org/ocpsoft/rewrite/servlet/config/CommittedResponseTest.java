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

import static org.junit.Assert.assertThat;

import org.apache.http.client.methods.HttpGet;
import org.hamcrest.Matchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTest;

/**
 * @see http://ocpsoft.org/support/topic/rewrite-and-picketlink-illegalstateexception/
 * @see https://github.com/ocpsoft/rewrite/issues/151
 * 
 * @author Christian Kaltepoth
 */
@RunWith(Arquillian.class)
@Ignore("https://github.com/ocpsoft/rewrite/issues/151")
public class CommittedResponseTest extends RewriteTest
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteTest.getDeployment()
               .addClass(CommittedResponseProvider.class)
               .addAsServiceProvider(ConfigurationProvider.class, CommittedResponseProvider.class)
               .addAsWebResource(new StringAsset("[redirection worked]"), "redirected.txt")
               .addAsWebResource(new StringAsset("[incorrect content]"), "incorrect.txt");
   }

   @Test
   public void alreadyCommittedReponseIsNotForwardedAgain() throws Exception
   {

      // WHEN the request is sent
      HttpAction<HttpGet> action = get("/path");

      // THEN the 3rd part filter should send the redirect
      // AND the Join should _not_ forward again
      assertThat(action.getStatusCode(), Matchers.equalTo(200));
      assertThat(action.getResponseContent(), Matchers.equalTo("[redirection worked]"));

   }

}