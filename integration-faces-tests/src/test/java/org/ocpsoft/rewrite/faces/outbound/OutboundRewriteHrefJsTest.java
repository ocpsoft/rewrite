package org.ocpsoft.rewrite.faces.outbound;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.category.IgnoreForGlassfish4;
import org.ocpsoft.rewrite.faces.annotation.RewriteFacesAnnotationsTest;
import org.ocpsoft.rewrite.test.RewriteTest;
import org.ocpsoft.rewrite.test.RewriteTestBase;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * This test doesn't work on Glassfish 4.0 at all. For some reason Glassfish _always_ appends the JSESSIONID, even if
 * the href just contains JavaScript. This leads to links like this:
 * 
 * <pre>
 * href = &quot;javascript:void(0);jsessionid=fde6b122b14c03b5ad7dcf4c51b5&quot;
 * </pre>
 * 
 * This can be reproduced even in a simple sample application without Rewrite.
 */
@RunWith(Arquillian.class)
@Category(IgnoreForGlassfish4.class)
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
