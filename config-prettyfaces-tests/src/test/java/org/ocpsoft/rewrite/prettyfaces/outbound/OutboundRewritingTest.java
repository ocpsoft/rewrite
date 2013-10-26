package org.ocpsoft.rewrite.prettyfaces.outbound;

import static org.junit.Assert.assertThat;

import java.net.URL;

import org.hamcrest.Matchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.category.IgnoreForWildfly;
import org.ocpsoft.rewrite.prettyfaces.PrettyFacesTestBase;
import org.ocpsoft.rewrite.test.RewriteTestBase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Ignored for Wildfly Beta1 because it ships with Mojarra 2.2.3 which ALWAYS appends 'jftfdi' and 'jffi' parameters.
 * This seems to break our test asserts.
 * 
 * @see https://java.net/jira/browse/JAVASERVERFACES-3054
 */
@RunWith(Arquillian.class)
@Category(IgnoreForWildfly.class)
public class OutboundRewritingTest extends RewriteTestBase
{

   @Deployment(testable = false)
   public static WebArchive createDeployment()
   {
      return PrettyFacesTestBase.getDeployment()
               .addAsWebResource("outbound/outbound.xhtml", "outbound.xhtml")
               .addAsWebResource(EmptyAsset.INSTANCE, "no-param.xhtml")
               .addAsWebResource(EmptyAsset.INSTANCE, "with-param.xhtml")
               .addAsWebInfResource("outbound/outbound-pretty-config.xml", "pretty-config.xml");
   }

   @Drone
   private WebDriver browser;

   @ArquillianResource
   private URL url;

   /**
    * Simple link for a mapping without any parameters.
    */
   @Test
   public void outboundWithoutPathParam() throws Exception
   {
      browser.get(url + "/outbound.jsf");
      String href = browser.findElement(By.id("no-param")).getAttribute("href");
      assertThat(href, Matchers.endsWith("/no-param"));
   }

   /**
    * The link contains a parameter that doesn't correspond to a path parameter. The URL should be rewritten and the
    * query parameter should be added to the rewritten URL.
    */
   @Test
   public void rewriteAdditionalQueryParam() throws Exception
   {
      browser.get(url + "/outbound.jsf");
      String href = browser.findElement(By.id("no-param-plus-query")).getAttribute("href");
      assertThat(href, Matchers.endsWith("/no-param?foo=bar"));
   }

   /**
    * The link contains a parameter that corresponds to a path parameter in the mapping. The URL should be correctly
    * rewritten.
    */
   @Test
   public void rewriteWithPathParam() throws Exception
   {
      browser.get(url + "/outbound.jsf");
      String href = browser.findElement(By.id("with-param")).getAttribute("href");
      assertThat(href, Matchers.endsWith("/with-param/foobar"));
   }

   /**
    * The link contains a parameter that corresponds to a path parameter in the mapping. There is also an additional
    * query parameter, that doesn't correspond to any path parameter. This query parameter should be added to the
    * rewritten URL.
    */
   @Test
   public void rewriteWithPathParamAndQueryParam() throws Exception
   {
      browser.get(url + "/outbound.jsf");
      String href = browser.findElement(By.id("with-param-plus-query")).getAttribute("href");
      assertThat(href, Matchers.endsWith("/with-param/foobar?foo=bar"));
   }

}