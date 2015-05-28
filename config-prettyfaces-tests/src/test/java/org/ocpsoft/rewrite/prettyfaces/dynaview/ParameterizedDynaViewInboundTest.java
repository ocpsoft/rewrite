package org.ocpsoft.rewrite.prettyfaces.dynaview;

import org.apache.http.client.methods.HttpGet;
import org.assertj.core.api.Assertions;
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
public class ParameterizedDynaViewInboundTest extends RewriteTestBase
{
   @Deployment(testable = false)
   public static WebArchive getDeployment()
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
      HttpAction<HttpGet> action = get("/pathparam/correct");

      Assert.assertEquals(200, action.getStatusCode());
      Assert.assertTrue(action.getResponseContent().contains("The parameter was correctly injected"));
   }

   @Test
   public void testQueryParamInjectionHappensBeforeViewDetermination() throws Exception
   {
      HttpAction<HttpGet> action = get("/queryparam?param=correct");

      Assert.assertEquals(200, action.getStatusCode());
      Assertions.assertThat(action.getResponseContent())
               .contains("The parameter was correctly injected");
   }

   @Test
   public void testInvalidPathParamWithDynaView() throws Exception
   {
      HttpAction<HttpGet> action = get("/pathparam/invalid");
      Assert.assertEquals(404, action.getStatusCode());
   }

   @Test
   public void testInvalidQueryParamWithDynaView() throws Exception
   {
      HttpAction<HttpGet> action = get("/queryparam?param=invalid");
      Assert.assertEquals(404, action.getStatusCode());
   }

}
