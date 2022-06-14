package org.ocpsoft.rewrite.annotation.config;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.annotation.RewriteAnnotationTest;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteIT;
import org.ocpsoft.rewrite.test.RewriteITBase;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class AnnotationEnabledConfigIT extends RewriteITBase
{
   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteIT.getDeploymentWithCDI()
               .addAsLibrary(RewriteAnnotationTest.getRewriteAnnotationArchive())
               .addClasses(AnnotationEnabledConfig.class);
   }

   @Test
   public void testConfigEnabledByAnnotations() throws Exception
   {
      HttpAction action = get("/config-enabled-by-annotation");
      assertThat(action.getStatusCode()).isEqualTo(299);
   }

}
