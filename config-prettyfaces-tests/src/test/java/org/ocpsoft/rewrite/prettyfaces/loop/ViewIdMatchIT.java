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
public class ViewIdMatchIT extends RewriteITBase
{
   @Deployment(testable = false)
   public static WebArchive createDeployment()
   {
      return PrettyFacesITBase.getDeployment()
               .addClass(ViewIdMatchBean.class)
               .addAsWebResource("loop/viewidmatch.xhtml", "path/viewidmatch.xhtml");
   }

   @Test
   public void testPatternMatchingViewId() throws Exception
   {
      HttpAction action = get("/path/christian");
      assertThat(action.getResponseContent()).contains("Injected value: [christian]");
   }

}
