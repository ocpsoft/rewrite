package org.ocpsoft.rewrite.prettyfaces.dynaview;

import org.assertj.core.api.Assertions;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.prettyfaces.PrettyFacesITBase;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteITBase;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class ParameterizedDynaViewInboundIT extends RewriteITBase
{
   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return PrettyFacesITBase.getDeployment()
               .addClass(ParameterizedDynaViewPathParamBean.class)
               .addClass(ParameterizedDynaViewQueryParamBean.class)
               .addClass(ParameterizedDynaViewValidator.class)
               .addAsWebResource("dynaview/parameterized-index.xhtml", "index.xhtml")
               .addAsWebResource("dynaview/parameterized-correct.xhtml", "correct.xhtml");
   }

   @Test
   public void testPathParamInjectionHappensBeforeViewDetermination() throws Exception
   {
      HttpAction action = get("/pathparam/correct");

      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).contains("The parameter was correctly injected");
   }

   @Test
   public void testQueryParamInjectionHappensBeforeViewDetermination() throws Exception
   {
      HttpAction action = get("/queryparam?param=correct");

      assertThat(action.getStatusCode()).isEqualTo(200);
      Assertions.assertThat(action.getResponseContent())
               .contains("The parameter was correctly injected");
   }

   @Test
   public void testInvalidPathParamWithDynaView() throws Exception
   {
      HttpAction action = get("/pathparam/invalid");
      assertThat(action.getStatusCode()).isEqualTo(404);
   }

   @Test
   public void testInvalidQueryParamWithDynaView() throws Exception
   {
      HttpAction action = get("/queryparam?param=invalid");
      assertThat(action.getStatusCode()).isEqualTo(404);
   }

}
