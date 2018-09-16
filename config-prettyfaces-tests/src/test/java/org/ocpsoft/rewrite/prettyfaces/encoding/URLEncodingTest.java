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
package org.ocpsoft.rewrite.prettyfaces.encoding;

import org.apache.http.client.methods.HttpGet;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.prettyfaces.PrettyFacesTestBase;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTestBase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

@RunWith(Arquillian.class)
public class URLEncodingTest extends RewriteTestBase
{
   @Deployment(testable = false)
   public static WebArchive createDeployment()
   {
      return PrettyFacesTestBase.getDeployment()
               .addClass(EncodingBean.class)
               .addAsWebResource("encoding/encoding.xhtml", "encoding.xhtml")
               .addAsWebInfResource("encoding/encoding-pretty-config.xml", "pretty-config.xml");
   }

   /**
    * Test a rewrite rule using the 'substitute' attribute to modify the URL.
    * 
    * @see http://code.google.com/p/prettyfaces/issues/detail?id=76
    */
   @Test
   public void testRewriteEncodingSubstitute() throws Exception
   {
      String target = "/virtual/rewrite/substitute";
      String expected = "/virtu%C3%A1ln%C3%AD";

      HttpAction<HttpGet> action = get(target);

      String responseContent = action.getResponseContent();
      Assert.assertTrue(responseContent.contains(action.getContextPath() + expected));
   }

   /**
    * Test a rewrite rule using the 'url' attribute to create a completely new URL.
    * 
    * @see http://code.google.com/p/prettyfaces/issues/detail?id=76
    */
   @Test
   public void testRewriteEncodingUrl() throws Exception
   {
      String target = "/virtual/rewrite/url";
      String expected = "/virtu%C3%A1ln%C3%AD";

      HttpAction<HttpGet> action = get(target);

      Assert.assertTrue(action.getCurrentURL() + " should end with " + expected,
              action.getCurrentURL().endsWith(expected));
      Assert.assertTrue(action.getResponseContent().contains(expected));
   }

   @Test
   public void testPrettyFacesFormActionURLEncodesProperly() throws Exception
   {
      String expected = "/custom/form";

      HttpAction<HttpGet> action = get(expected);

      Assert.assertTrue(action.getCurrentURL().endsWith(expected));
      Assert.assertTrue(action.getResponseContent().contains(expected));
   }

   @Test
   // http://code.google.com/p/prettyfaces/issues/detail?id=64
   public void testPrettyFacesFormActionURLEncodesProperlyWithCustomRegexAndMultiplePathSegments() throws Exception
   {
      String expected = "/foo/bar/baz/car/";

      HttpAction<HttpGet> action = get(expected);

      Assert.assertTrue(action.getCurrentURL().endsWith(expected));
      Assert.assertTrue(action.getResponseContent().contains(expected));

      Assert.assertTrue(action.getResponseContent().contains("beanPathText=foo/bar/baz/car"));
   }

   @Test
   public void testNonMappedRequestRendersRewrittenURL() throws Exception
   {
      HttpAction<HttpGet> action = get("/encoding.jsf");

      Assert.assertTrue(action.getCurrentURL().endsWith("/encoding.jsf"));
      Assert.assertTrue(action.getResponseContent().contains("/custom/form"));
   }

   @Drone
   WebDriver browser;

   @Test
   public void testURLDecoding() throws Exception
   {
      browser.get(getBaseURL() + getContextPath() + "/encoding/Vračar?dis=Fooo Bar");
      Assert.assertTrue(browser.getPageSource().contains("/encoding/Vra%C4%8Dar?dis=Fooo+Bar"));
      Assert.assertTrue(browser.getPageSource().contains("beanPathText=Vračar"));
      Assert.assertTrue(browser.getPageSource().contains("beanQueryText=Fooo Bar"));
   }

   @Test
   public void testURLDecodingWithPoundSignEncoded() throws Exception
   {
      browser.get(getBaseURL() + getContextPath() + "/encoding/V%23r?dis=gt%23%232206");
      String pageSource = browser.getPageSource();
      Assert.assertTrue(pageSource.contains("/encoding/V%23r?dis=gt%23%232206"));
      Assert.assertTrue(pageSource.contains("beanPathText=V#r"));
      Assert.assertTrue(pageSource.contains("beanQueryText=gt##2206"));
   }

   @Test
   public void testURLDecodingWithPoundSign() throws Exception
   {
      browser.get(getBaseURL() + getContextPath() + "/encoding/V%23r?dis=gt##2206");
      String pageSource = browser.getPageSource();
      Assert.assertTrue(pageSource.contains("/encoding/V%23r?dis=gt"));
      Assert.assertTrue(pageSource.contains("beanPathText=V#r"));
      Assert.assertTrue(pageSource.contains("beanQueryText=gt"));
   }

   @Test
   public void testQueryDecoding() throws Exception
   {
      HttpAction<HttpGet> action = get("/encoding/Vračar?dis=Fooo%20Bar");

      Assert.assertTrue(action.getCurrentURL().endsWith("/encoding/Vračar?dis=Fooo%20Bar"));
      String responseContent = action.getResponseContent();
      Assert.assertTrue(responseContent.contains("/encoding/Vra%C4%8Dar?dis=Fooo+Bar"));
      Assert.assertTrue(responseContent.contains("beanQueryText=Fooo Bar"));
   }

   @Test
   public void testEncodedPathDecoding() throws Exception
   {
      HttpAction<HttpGet> action = get("/encoding/Vračar?dis=Fooo%20Bar");

      Assert.assertTrue(action.getCurrentURL().endsWith("/encoding/Vračar?dis=Fooo%20Bar"));
      Assert.assertTrue(action.getResponseContent().contains("/encoding/Vra%C4%8Dar?dis=Fooo+Bar"));
      Assert.assertTrue(action.getResponseContent().contains("beanPathText=Vračar"));
   }

   @Test
   public void testQueryWithGermanUmlaut() throws Exception
   {
      HttpAction<HttpGet> action = get("/encoding/Vračar?dis=%C3%BC");
      Assert.assertTrue(action.getCurrentURL().endsWith("/encoding/Vračar?dis=%C3%BC"));
      Assert.assertTrue(action.getResponseContent().contains(getContextPath() + "/encoding/Vra%C4%8Dar?dis=%C3%BC"));
      Assert.assertTrue(action.getResponseContent().contains("beanPathText=Vračar"));
      Assert.assertTrue(action.getResponseContent().contains("beanQueryText=\u00fc"));
   }

   @Test
   public void testUrlMappingPatternDecoding() throws Exception
   {
      browser.get(getBaseURL() + getContextPath() + "/hard encoding/Vračar");
      Assert.assertNotNull(browser.findElement(By.id("form")));
   }

   @Test
   public void testEncodedURLMatchesNonEncodedPattern() throws Exception
   {
      browser.get(getBaseURL() + getContextPath() + "/URL%20ENCODED");
      Assert.assertNotNull(browser.findElement(By.id("form")));
   }

   @Test
   public void testNoDecodeOnSubmitDoesNotCrash() throws Exception
   {
      browser.get(getBaseURL() + getContextPath() + "/decodequery");

      Assert.assertTrue(browser.getPageSource().contains("viewId=/encoding.xhtml"));
      browser.findElement(By.id("input1")).sendKeys("%");
      browser.findElement(By.id("submit")).click();
      Assert.assertTrue(browser.getPageSource().contains("viewId=/encoding.xhtml"));
   }

   @Test
   public void testBracesAndBracketsInURL() throws Exception
   {
      browser.get(getBaseURL() + getContextPath() + "/basic/[]{}");
      Assert.assertNotNull(browser.findElement(By.id("form")));
   }

   @Test
   public void testBracesAndBracketsInURLEncoded() throws Exception
   {
      browser.get(getBaseURL() + getContextPath() + "/basic/%5B%5D%7B%7D");
      Assert.assertNotNull(browser.findElement(By.id("form")));
   }
}
