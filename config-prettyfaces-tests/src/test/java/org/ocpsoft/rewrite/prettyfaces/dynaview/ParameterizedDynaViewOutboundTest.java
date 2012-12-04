package org.ocpsoft.rewrite.prettyfaces.dynaview;

import static org.junit.Assert.assertTrue;

import org.apache.http.client.methods.HttpGet;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.prettyfaces.PrettyFacesTestBase;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTestBase;

@RunWith(Arquillian.class)
public class ParameterizedDynaViewOutboundTest extends RewriteTestBase
{

   @Deployment(testable = false)
   public static WebArchive createDeployment()
   {
      return PrettyFacesTestBase.getDeployment()
               .addClass(ParameterizedDynaViewPathParamBean.class)
               .addClass(ParameterizedDynaViewQueryParamBean.class)
               .addClass(ParameterizedDynaViewValidator.class)
               .addAsWebResource("dynaview/parameterized-index.xhtml", "index.xhtml")
               .addAsWebResource("dynaview/parameterized-correct.xhtml", "correct.xhtml");
   }

   @Test
   public void testPathParamInjectionHappensBeforeViewDetermination() throws Exception
   {
      HttpAction<HttpGet> action = get("/index.jsf");

      Assert.assertEquals(200, action.getStatusCode());
      assertTrue(action.getResponseContent().contains("/pathparam/correct"));
      assertTrue(action.getResponseContent().contains("/pathparam/invalid"));
      assertTrue(action.getResponseContent().contains("/queryparam?param=correct"));
      assertTrue(action.getResponseContent().contains("/queryparam?param=invalid"));

   }

}
