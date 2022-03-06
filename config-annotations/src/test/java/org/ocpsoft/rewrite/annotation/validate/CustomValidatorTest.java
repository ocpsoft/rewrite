package org.ocpsoft.rewrite.annotation.validate;

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

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class CustomValidatorTest extends RewriteTestBase
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteTest.getDeploymentWithCDI()
               .addAsLibrary(RewriteAnnotationTest.getRewriteAnnotationArchive())
               .addAsLibrary(RewriteAnnotationTest.getRewriteCdiArchive())
               .addClasses(CustomValidatorBean.class, EvenLengthValidator.class)
               .addAsWebResource(new StringAsset(
                        "Value: [${customValidatorBean.value}]"),
                        "validate.jsp");
   }

   @Test
   public void testValidationWithValidValue() throws Exception
   {
      HttpAction action = get("/validate/abcd/");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).contains("Value: [abcd]");
   }

   @Test
   public void testValidationWithInvalidValue() throws Exception
   {
      HttpAction action = get("/validate/abc/");
      assertThat(action.getStatusCode()).isEqualTo(404);
   }

}
