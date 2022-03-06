package org.ocpsoft.rewrite.annotation.query;

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
      HttpAction action = get("/query?q=foo");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).contains("Log: [foo]");
      assertThat(action.getResponseContent()).contains("IsNull: [false]");
   }

   @Test
   public void shouldFindCorrectQueryParameterIfOthersExist() throws Exception
   {
      HttpAction action = get("/query?a=b&q=foo&c=d");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).contains("Log: [foo]");
      assertThat(action.getResponseContent()).contains("IsNull: [false]");
   }

   @Test
   public void shouldSupportMissingQueryParameter() throws Exception
   {
      HttpAction action = get("/query");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).contains("Log: []");
      assertThat(action.getResponseContent()).contains("IsNull: [true]");
   }

}
