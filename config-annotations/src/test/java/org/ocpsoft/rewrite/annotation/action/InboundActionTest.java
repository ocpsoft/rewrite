package org.ocpsoft.rewrite.annotation.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.apache.http.client.methods.HttpGet;
import org.hamcrest.Matchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.annotation.RewriteAnnotationTest;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTest;
import org.ocpsoft.rewrite.test.RewriteTestBase;

@RunWith(Arquillian.class)
public class InboundActionTest extends RewriteTestBase
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteTest.getDeploymentWithCDI()
               .addAsLibrary(RewriteAnnotationTest.getRewriteAnnotationArchive())
               .addAsLibrary(RewriteAnnotationTest.getRewriteCdiArchive())
               .addClass(InboundActionBean.class)
               .addAsWebResource(new StringAsset(
                        "not important for this test"), "some-page.jsp")
               .addAsWebResource(new StringAsset(
                        "<%= response.encodeURL(\"/some-page.jsp\") %>"), "page-with-link.jsp");
   }

   @Test
   public void testRequestActionOnlyInvokedForInboundRewrites() throws Exception
   {
      HttpAction<HttpGet> action = get("/page-with-link.jsp");
      assertEquals(200, action.getStatusCode());
      assertThat(action.getResponseContent(), Matchers.containsString("/rewritten-url"));
   }

}
