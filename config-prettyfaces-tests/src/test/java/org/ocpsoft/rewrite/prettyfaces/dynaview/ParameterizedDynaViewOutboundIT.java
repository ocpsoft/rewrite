package org.ocpsoft.rewrite.prettyfaces.dynaview;

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
public class ParameterizedDynaViewOutboundIT extends RewriteITBase
{

   @Deployment(testable = false)
   public static WebArchive createDeployment()
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
      HttpAction action = get("/index.jsf");

      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).contains("/pathparam/correct");
      assertThat(action.getResponseContent()).contains("/pathparam/invalid");
      assertThat(action.getResponseContent()).contains("/queryparam?param=correct");
      assertThat(action.getResponseContent()).contains("/queryparam?param=invalid");

   }

}
