package org.ocpsoft.rewrite.faces.annotation.binding;

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
public class DeferredBindingTest extends RewriteTestBase
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteTest.getDeployment()
               .addAsLibrary(RewriteFacesAnnotationsTest.getRewriteAnnotationArchive())
               .addAsLibrary(RewriteFacesAnnotationsTest.getRewriteFacesArchive())
               .addClass(DeferredBindingBean.class)
               .addAsWebResource("binding-phases.xhtml", "binding.xhtml");
   }

   @Test
   public void testActionPhases() throws Exception
   {

      HtmlPage firstPage = getWebClient("/binding/foo/").getPage();

      // reload so we get a postback that visits all the phases
      HtmlPage secondPage = firstPage.getHtmlElementById("form:reload").click();

      String secondPageContent = secondPage.getWebResponse().getContentAsString();
      assertContains(secondPageContent, "Wrote [foo] to [defaultPhase] during RESTORE_VIEW");
      assertContains(secondPageContent, "Wrote [foo] to [beforeRenderResponse] during RENDER_RESPONSE");
      assertContains(secondPageContent, "Wrote [foo] to [afterInvokeApplication] during INVOKE_APPLICATION");

   }

}