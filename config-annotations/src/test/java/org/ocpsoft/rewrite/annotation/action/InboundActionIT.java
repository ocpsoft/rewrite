package org.ocpsoft.rewrite.annotation.action;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.annotation.RewriteAnnotationTest;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteIT;
import org.ocpsoft.rewrite.test.RewriteITBase;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class InboundActionIT extends RewriteITBase
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteIT.getDeploymentWithCDI()
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
      HttpAction action = get("/page-with-link.jsp");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).contains("/rewritten-url");
   }

}
