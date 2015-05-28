package org.ocpsoft.rewrite.annotation.validate;

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
      HttpAction<HttpGet> action = get("/validate/abcd/");
      assertEquals(200, action.getStatusCode());
      assertTrue(action.getResponseContent().contains("Value: [abcd]"));
   }

   @Test
   public void testValidationWithInvalidValue() throws Exception
   {
      HttpAction<HttpGet> action = get("/validate/abc/");
      assertEquals(404, action.getStatusCode());
   }

}
