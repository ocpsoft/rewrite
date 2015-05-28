package org.ocpsoft.rewrite.annotation.forward;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.apache.http.client.methods.HttpGet;
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
public class SimpleForwardTest extends RewriteTestBase
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteTest.getDeploymentWithCDI()
               .addAsLibrary(RewriteAnnotationTest.getRewriteAnnotationArchive())
               .addClass(SimpleForwardBean.class)
               .addAsWebResource(new StringAsset("<h1>Simple Forward</h1>"), "simple-forward.html");
   }

   @Test
   public void testSimpleForward() throws Exception
   {
      HttpAction<HttpGet> action = get("/forward");
      assertEquals(200, action.getStatusCode());
      assertTrue(action.getResponseContent().contains("Simple Forward"));
   }

}
