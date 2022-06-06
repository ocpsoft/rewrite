package org.ocpsoft.rewrite.annotation.param;

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
public class ParameterMatchesIT extends RewriteITBase
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteIT.getDeploymentWithCDI()
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
      HttpAction action = get("/param/abcd/");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).isEqualTo("Value: [abcd]");
   }

   @Test
   public void testMatchesWithInvalidUrl() throws Exception
   {
      HttpAction action = get("/param/abcde/");
      assertThat(action.getStatusCode()).isEqualTo(404);
   }

}
