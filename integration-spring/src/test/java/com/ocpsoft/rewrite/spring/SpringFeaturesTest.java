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
package com.ocpsoft.rewrite.spring;

import junit.framework.Assert;

import org.apache.http.client.methods.HttpGet;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.web.context.WebApplicationContext;

import com.ocpsoft.rewrite.test.HttpAction;
import com.ocpsoft.rewrite.test.RewriteTestBase;

/**
 * <p>
 * This tests the basic features of the Spring integration module:
 * </p>
 * 
 * <ul>
 * <li>SpringExpressionLanguageProvider: Binding parameters to Spring beans</li>
 * <li>SpringServiceEnricher: {@link SpringExpressionLanguageProvider} requires {@link WebApplicationContext}</li>
 * <li>SpringServiceLocator: Automatic discovery of {@link SpringFeaturesConfigProvider}</li>
 * </ul>
 * 
 * @author Christian Kaltepoth
 */
@RunWith(Arquillian.class)
public class SpringFeaturesTest extends RewriteTestBase {

    @Deployment(testable = false)
    public static WebArchive getDeployment() {
        return ShrinkWrap
                .create(WebArchive.class, "rewrite-test.war")
                .addAsLibraries(getRewriteArchive())
                .setWebXML("jetty-web-spring.xml")
                .addAsResource("jetty-log4j.xml", ArchivePaths.create("/log4j.xml"))
                .addAsWebInfResource("applicationContext.xml")
                .addPackages(true, SpringRoot.class.getPackage());
    }

    @Test
    public void testSpringFeatures() {
        HttpAction<HttpGet> action = get("/name-christian");
        Assert.assertEquals(200, action.getResponse().getStatusLine().getStatusCode());
        Assert.assertEquals("/hello/CHRISTIAN", action.getCurrentContextRelativeURL());
    }

}
