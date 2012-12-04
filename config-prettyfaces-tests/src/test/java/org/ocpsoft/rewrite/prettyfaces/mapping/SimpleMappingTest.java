package org.ocpsoft.rewrite.prettyfaces.mapping;

import static org.junit.Assert.assertTrue;

import org.apache.http.client.methods.HttpGet;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.prettyfaces.PrettyFacesTestBase;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTestBase;

@RunWith(Arquillian.class)
public class SimpleMappingTest extends RewriteTestBase
{

   @Deployment(testable = false)
   public static WebArchive createDeployment()
   {
      return PrettyFacesTestBase.getDeployment()
               .addAsWebResource("mapping/page.xhtml", "page.xhtml")
               .addAsWebResource("mapping/page.xhtml", "unusual-view-id-(.xhtml")
               .addAsWebInfResource("mapping/pretty-config.xml", "pretty-config.xml");
   }

   @Test
   public void testSimpleMapping() throws Exception
   {
      HttpAction<HttpGet> action = get("/simple-mapping");
      assertTrue(action.getResponseContent().contains("Mapped view was rendered!"));
   }

   @Test
   public void testUnusualViewId() throws Exception
   {
      HttpAction<HttpGet> action = get("/unusual-view-id");
      assertTrue(action.getResponseContent().contains("Mapped view was rendered!"));
   }

}
