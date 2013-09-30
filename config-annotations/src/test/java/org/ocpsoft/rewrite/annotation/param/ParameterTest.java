package org.ocpsoft.rewrite.annotation.param;

import org.apache.http.client.methods.HttpGet;
import org.hamcrest.Matchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.annotation.RewriteAnnotationTest;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTest;
import org.ocpsoft.rewrite.test.RewriteTestBase;

@Ignore("This still fails randomly/sporadically.")
@RunWith(Arquillian.class)
public class ParameterTest extends RewriteTestBase
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteTest.getDeploymentWithCDI()
               .addAsLibrary(RewriteAnnotationTest.getRewriteAnnotationArchive())
               .addAsLibrary(RewriteAnnotationTest.getRewriteCdiArchive())
               .addClass(ParameterTestBean.class)
               .addAsWebResource(new StringAsset(
                        "Value: [${parameterTestBean.value}]"),
                        "param.jsp");
   }

   @Test
   public void testParameterBindingAnnotation() throws Exception
   {
      HttpAction<HttpGet> action = get("/param/christian/");
      Assert.assertEquals(200, action.getStatusCode());
      Assert.assertThat(action.getResponseContent(), Matchers.containsString("Value: [christian]"));
   }

}
