package org.ocpsoft.rewrite.el.cdi.faces.binding;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.el.cdi.faces.RewriteELTest;
import org.ocpsoft.rewrite.test.RewriteTest;
import org.ocpsoft.rewrite.test.RewriteTestBase;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

@RunWith(Arquillian.class)
public class DeferredBindingTest extends RewriteTestBase
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteTest.getDeploymentWithCDI()
               .addAsLibrary(RewriteELTest.getRewriteAnnotationArchive())
               .addAsLibrary(RewriteELTest.getRewriteFacesArchive())
               .addAsLibrary(RewriteELTest.getRewriteCDIArchive())
               .addClass(DeferredBindingBean.class)
               .addAsWebResource("binding-phases.xhtml", "binding.xhtml");
   }

   @Test
   public void testActionPhases() throws Exception
   {

      HtmlPage firstPage = getWebClient("/binding/foo/").getPage();

      // reload so we get a postback that visits all the phases
      HtmlPage secondPage = firstPage.getElementById("form:reload").click();

      String secondPageContent = secondPage.getWebResponse().getContentAsString();
      assertContains(secondPageContent, "Wrote [foo] to [defaultPhase] during [RESTORE_VIEW]");
      assertContains(secondPageContent, "Wrote [foo] to [beforeRenderResponse] during [RENDER_RESPONSE]");
      assertContains(secondPageContent, "Wrote [foo] to [afterInvokeApplication] during [INVOKE_APPLICATION]");

   }

}