package org.ocpsoft.rewrite.prettyfaces.outbound;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.prettyfaces.PrettyFacesTestBase;
import org.ocpsoft.rewrite.test.RewriteTestBase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static org.hamcrest.Matchers.endsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class ViewHardcodedParamsOutboundTest extends RewriteTestBase
{

   @Deployment(testable = false)
   public static WebArchive createDeployment()
   {
      return PrettyFacesTestBase.getDeployment()
               .addClass(ViewHardcodedParamsBean.class)
               .addAsWebResource("outbound/view-hardcoded-params.xhtml", "index.xhtml")
               .addAsWebResource("outbound/view-hardcoded-params.xhtml", "view-hardcoded-params.xhtml")
               .addAsWebInfResource("outbound/view-hardcoded-params-pretty-config.xml", "pretty-config.xml");
   }

   @Drone
   WebDriver browser;

   @Test
   public void testHLink() throws Exception
   {
      browser.get(getBaseURL() + getContextPath() + "/index");
      String hlink = browser.findElement(By.id("hLink")).getAttribute("href");
      assertTrue(hlink.endsWith("/view-hardcoded-params"));
   }

   @Test
   public void testHLinkExtraParams() throws Exception
   {
      browser.get(getBaseURL() + getContextPath() + "/index");
      String hlink = browser.findElement(By.id("hLink-extra")).getAttribute("href");
      assertTrue(hlink.endsWith("/view-hardcoded-params?extraParam=extraValue"));
   }

   @Test
   public void testPLink() throws Exception
   {
      browser.get(getBaseURL() + getContextPath() + "/index");
      String url = browser.findElement(By.id("prettyLink")).getAttribute("href");
      assertTrue(url.endsWith("/view-hardcoded-params"));
   }

   @Test
   public void testPLinkExtraParams() throws Exception
   {
      browser.get(getBaseURL() + getContextPath() + "/index");
      String url = browser.findElement(By.id("prettyLink-extra")).getAttribute("href");
      assertTrue(url.endsWith("/view-hardcoded-params?extraParam=extraValue"));
   }

   @Test
   public void testHCommandLink() throws Exception
   {
      HtmlPage firstPage = getWebClient("/index").getPage();
      HtmlPage secondPage = firstPage.getElementById("hCommandLink").click();
      assertThat(secondPage.getUrl().toString(), endsWith("/view-hardcoded-params"));
   }

   @Test
   public void testHCommandLinkExtraParams() throws Exception
   {
      HtmlPage firstPage = getWebClient("/index").getPage();
      HtmlPage secondPage = firstPage.getElementById("hCommandLink-extra").click();
      assertThat(secondPage.getUrl().toString(), endsWith("/view-hardcoded-params?extraParam=extraValue"));
   }

   @Test
   public void testHCommandLinkInvalid() throws Exception
   {
      HtmlPage firstPage = getWebClient("/index").getPage();
      HtmlPage secondPage = firstPage.getElementById("hCommandLink-notMapped").click();
      assertThat(secondPage.getUrl().toString(), endsWith("/view-hardcoded-params.jsf?param=value2"));
   }
}
