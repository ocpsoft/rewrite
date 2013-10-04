package org.ocpsoft.rewrite.prettyfaces.interaction;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.prettyfaces.PrettyFacesTestBase;
import org.ocpsoft.rewrite.test.RewriteTestBase;

import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
public class FacesNavigateInteractionTest extends RewriteTestBase
{
   @Deployment(testable = false)
   public static WebArchive createDeployment()
   {
      return PrettyFacesTestBase.getDeployment()
               .addClass(FacesNavigateInteractionBean.class)
               .addAsWebResource("interaction/faces-navigate-page.xhtml", "page.xhtml")
               .addAsWebInfResource("interaction/faces-navigate-pretty-config.xml", "pretty-config.xml");
   }

   @Test
   public void testNavigate() throws Exception
   {
      HtmlPage firstPage = getWebClient("/page").getPage();
      HtmlPage secondPage = firstPage.getHtmlElementById("navigate").click();
      assertEquals(secondPage.getUrl().getPath(), getContextPath() + "/page");
   }
}
