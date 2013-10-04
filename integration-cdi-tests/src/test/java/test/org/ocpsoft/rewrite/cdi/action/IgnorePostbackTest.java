package test.org.ocpsoft.rewrite.cdi.action;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.test.RewriteTest;
import org.ocpsoft.rewrite.test.RewriteTestBase;

import test.org.ocpsoft.rewrite.cdi.RewriteELTest;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

@RunWith(Arquillian.class)
public class IgnorePostbackTest extends RewriteTestBase
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      WebArchive deployment = RewriteTest.getDeploymentWithFacesAndCDI()
               .addAsLibrary(RewriteELTest.getRewriteAnnotationArchive())
               .addAsLibrary(RewriteELTest.getRewriteFacesArchive())
               .addAsLibrary(RewriteELTest.getRewriteCDIArchive())
               .addClass(IgnorePostbackBean.class)
               .addAsWebResource("action-postback.xhtml", "action.xhtml");
      
      return deployment;
   }

   @Test
   public void testActionPhases() throws Exception
   {

      HtmlPage firstPage = getWebClient("/action").getPage();

      // reload so we get a postback that visits all the phases
      HtmlPage secondPage = firstPage.getHtmlElementById("form:reload").click();

      String secondPageContent = secondPage.getWebResponse().getContentAsString();
      assertContains(secondPageContent, "Default = [true]");
      assertContains(secondPageContent, "Ignore Postback = [false]");

   }

}
