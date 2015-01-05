package org.ocpsoft.rewrite.annotation.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.apache.http.client.methods.HttpGet;
import org.hamcrest.Matchers;
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
public class QueryParameterTest extends RewriteTestBase
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteTest.getDeploymentWithCDI()
               .addAsLibrary(RewriteAnnotationTest.getRewriteAnnotationArchive())
               .addAsLibrary(RewriteAnnotationTest.getRewriteCdiArchive())
               .addClass(QueryParameterBean.class)
               .addAsWebResource(new StringAsset(
                        "Log: [${queryParameterBean.value}]\n"
                        + "IsNull: [${queryParameterBean.value == null}]"),
                        "query.jsp");
   }

   @Test
   public void testQueryParameter() throws Exception
   {
      HttpAction<HttpGet> action = get("/query?q=foo");
      assertEquals(200, action.getStatusCode());
      assertThat(action.getResponseContent(), Matchers.containsString("Log: [foo]"));
      assertThat(action.getResponseContent(), Matchers.containsString("IsNull: [false]"));
   }

   @Test
   public void shouldFindCorrectQueryParameterIfOthersExist() throws Exception
   {
      HttpAction<HttpGet> action = get("/query?a=b&q=foo&c=d");
      assertEquals(200, action.getStatusCode());
      assertThat(action.getResponseContent(), Matchers.containsString("Log: [foo]"));
      assertThat(action.getResponseContent(), Matchers.containsString("IsNull: [false]"));
   }

   @Test
   public void shouldSupportMissingQueryParameter() throws Exception
   {
      HttpAction<HttpGet> action = get("/query");
      assertEquals(200, action.getStatusCode());
      assertThat(action.getResponseContent(), Matchers.containsString("Log: []"));
      assertThat(action.getResponseContent(), Matchers.containsString("IsNull: [true]"));
   }

}
