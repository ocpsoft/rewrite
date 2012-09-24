package org.ocpsoft.rewrite.annotation.param;

import org.apache.http.client.methods.HttpGet;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.annotation.RewriteAnnotationTest;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTest;
import org.ocpsoft.rewrite.test.RewriteTestBase;

@RunWith(Arquillian.class)
public class ParameterMatchesTest extends RewriteTestBase
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteTest.getDeploymentWithCDI()
               .addAsLibrary(RewriteAnnotationTest.getRewriteAnnotationArchive())
               .addAsLibrary(RewriteAnnotationTest.getRewriteCdiArchive())
               .addClass(ParameterMatchesTestBean.class)
               .addAsWebResource(new StringAsset(
                        "Value: [${parameterMatchesTestBean.value}]"),
                        "param.jsp");
   }

   @Test
   public void testMatchesWithValidUrl() throws Exception
   {
      HttpAction<HttpGet> action = get("/param/abcd/");
      Assert.assertEquals(200, action.getStatusCode());
      Assert.assertEquals("Value: [abcd]", action.getResponseContent());
   }

   @Test
   public void testMatchesWithInvalidUrl() throws Exception
   {
      HttpAction<HttpGet> action = get("/param/abcde/");
      Assert.assertEquals(404, action.getStatusCode());
   }

}
