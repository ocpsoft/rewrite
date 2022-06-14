package org.ocpsoft.rewrite.prettyfaces.loop;

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
public class InfiniteLoopIT extends RewriteITBase
{
   @Deployment(testable=false)
   public static WebArchive createDeployment()
   {
      return PrettyFacesITBase.getDeployment()
               .addAsWebResource("loop/loop.xhtml", "loop.xhtml")
               .addAsWebInfResource("loop/loop-pretty-config.xml", "pretty-config.xml");
   }

   @Test
   public void testRewriteTrailingSlashToLowerCase() throws Exception
   {
      HttpAction action = get("/loop.jsf");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).contains("Loop avoided.");
   }
}
