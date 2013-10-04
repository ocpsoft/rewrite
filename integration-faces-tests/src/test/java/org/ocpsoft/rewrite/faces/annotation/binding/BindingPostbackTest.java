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
public class BindingPostbackTest extends RewriteTestBase
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteTest.getDeployment()
               .addAsLibrary(RewriteFacesAnnotationsTest.getRewriteAnnotationArchive())
               .addAsLibrary(RewriteFacesAnnotationsTest.getRewriteFacesArchive())
               .addClass(BindingPostbackBean.class)
               .addAsWebResource("binding-postback.xhtml", "binding.xhtml");
   }

   @Test
   public void testActionPhases() throws Exception
   {

      HtmlPage firstPage = getWebClient("/binding/foo/").getPage();

      String firstPageContent = firstPage.getWebResponse().getContentAsString();
      assertContains(firstPageContent, "valueDefault = [foo]");
      assertContains(firstPageContent, "valueIgnorePostback = [foo]");

      // reload so we get a postback
      HtmlPage secondPage = firstPage.getHtmlElementById("form:reload").click();

      String secondPageContent = secondPage.getWebResponse().getContentAsString();
      assertContains(secondPageContent, "valueDefault = [foo]");
      assertContains(secondPageContent, "valueIgnorePostback = []");

   }

}