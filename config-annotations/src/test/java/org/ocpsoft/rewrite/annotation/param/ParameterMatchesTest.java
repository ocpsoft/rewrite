package org.ocpsoft.rewrite.annotation.param;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.apache.http.client.methods.HttpGet;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Ignore;
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
      return RewriteTest.getDeployment()
               .addAsLibrary(RewriteAnnotationTest.getRewriteAnnotationArchive())
               .addAsLibrary(RewriteAnnotationTest.getRewriteCdiArchive())
               .addClass(ParameterMatchesBean.class)
               .addAsWebResource(new StringAsset(
                        "Value: [${parameterMatchesBean.value}]"),
                        "param.jsp");
   }

   @Test
   @Ignore
   public void testMatchesWithValidUrl() throws Exception
   {
      HttpAction<HttpGet> action = get("/param/abcd/");
      assertEquals(200, action.getStatusCode());
      assertTrue(action.getResponseContent().contains("Value: [abcd]"));
   }

   @Test
   @Ignore
   public void testMatchesWithInvalidUrl() throws Exception
   {
      HttpAction<HttpGet> action = get("/param/abcde/");
      assertEquals(404, action.getStatusCode());
   }

}
