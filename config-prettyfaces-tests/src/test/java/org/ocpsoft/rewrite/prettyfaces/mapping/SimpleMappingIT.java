package org.ocpsoft.rewrite.prettyfaces.mapping;

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
public class SimpleMappingIT extends RewriteITBase
{

   @Deployment(testable = false)
   public static WebArchive createDeployment()
   {
      return PrettyFacesITBase.getDeployment()
               .addAsWebResource("mapping/page.xhtml", "page.xhtml")
               .addAsWebResource("mapping/page.xhtml", "unusual-view-id-(.xhtml")
               .addAsWebInfResource("mapping/pretty-config.xml", "pretty-config.xml");
   }

   @Test
   public void testSimpleMapping() throws Exception
   {
      HttpAction action = get("/simple-mapping");
      assertThat(action.getResponseContent()).contains("Mapped view was rendered!");
   }

   @Test
   public void testUnusualViewId() throws Exception
   {
      HttpAction action = get("/unusual-view-id");
      assertThat(action.getResponseContent()).contains("Mapped view was rendered!");
   }

}
