package org.ocpsoft.rewrite.faces.outbound;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.faces.annotation.RewriteFacesAnnotationsTest;
import org.ocpsoft.rewrite.test.RewriteTest;
import org.ocpsoft.rewrite.test.RewriteTestBase;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

@RunWith(Arquillian.class)
public class OutboundRewriteHrefJsTest extends RewriteTestBase
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteTest.getDeployment()
               .addAsLibrary(RewriteFacesAnnotationsTest.getRewriteAnnotationArchive())
               .addAsLibrary(RewriteFacesAnnotationsTest.getRewriteFacesArchive())
               .addClass(OutboundRewriteHrefJsTestBean.class)
               .addAsWebResource("outbound-js-void.xhtml", "outbound.xhtml");
   }

   @Test
   public void testOutboundHrefUnmodified() throws Exception
   {
      HtmlPage firstPage = getWebClient("/outbound").getPage();
      String firstPageContent = firstPage.getWebResponse().getContentAsString();
      assertContains(firstPageContent, "href=\"javascript:void(0)\"");
   }

}
