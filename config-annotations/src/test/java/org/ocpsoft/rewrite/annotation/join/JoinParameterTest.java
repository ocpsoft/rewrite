package org.ocpsoft.rewrite.annotation.join;

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
public class JoinParameterTest extends RewriteTestBase
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteTest
               .getDeployment()
               .addAsLibrary(RewriteAnnotationTest.getRewriteAnnotationArchive())
               .addClass(JoinParameterBean.class)
               .addAsWebResource(new StringAsset("Value: [<%= request.getParameter(\"value\") %>]"),
                        "join-parameter.jsp");
   }

   @Test
   public void testJoinWithParameter() throws Exception
   {

      HttpAction action = get("/join/test/");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).contains("Value: [test]");
   }

}
