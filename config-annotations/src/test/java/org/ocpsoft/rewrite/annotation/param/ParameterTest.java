package org.ocpsoft.rewrite.annotation.param;

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

import static org.assertj.core.api.Assertions.assertThat;

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
      HttpAction action = get("/param/christian/");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).contains("Value: [christian]");
   }

}
