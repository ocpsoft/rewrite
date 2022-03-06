package org.ocpsoft.rewrite.prettyfaces.loop;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.prettyfaces.PrettyFacesTestBase;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTestBase;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class RewriteLoopTest extends RewriteTestBase
{

   @Deployment(testable = false)
   public static WebArchive createDeployment()
   {
      return PrettyFacesTestBase.getDeployment()
               .addAsWebResource("loop/rewrite-loop.xhtml", "rewrite-loop.xhtml")
               .addAsWebInfResource("loop/rewrite-loop-config.xml", "pretty-config.xml");
   }

   @Test
   public void testRewriteRuleSubstiuteMatchingRulePattern() throws Exception
   {
      HttpAction action = get("/rewrite-loop.html");

      // the page should render fine without any rewrite loop
      assertThat(action.getCurrentURL()).endsWith("/rewrite-loop.html");
      assertThat(action.getResponseContent()).contains("Rewrite loop test page rendered");
   }

}
