package org.ocpsoft.rewrite.prettyfaces.loop;

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
public class ViewIdMatchTest extends RewriteTestBase
{
   @Deployment(testable = false)
   public static WebArchive createDeployment()
   {
      return PrettyFacesTestBase.getDeployment()
               .addClass(ViewIdMatchBean.class)
               .addAsWebResource("loop/viewidmatch.xhtml", "path/viewidmatch.xhtml");
   }

   @Test
   public void testPatternMatchingViewId() throws Exception
   {
      HttpAction<HttpGet> action = get("/path/christian");
      Assert.assertTrue(action.getResponseContent().contains("Injected value: [christian]"));
   }

}
