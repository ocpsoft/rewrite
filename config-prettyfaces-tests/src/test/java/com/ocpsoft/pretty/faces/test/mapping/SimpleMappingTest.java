package com.ocpsoft.pretty.faces.test.mapping;

import static org.junit.Assert.assertTrue;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.jsfunit.jsfsession.JSFClientSession;
import org.jboss.jsfunit.jsfsession.JSFSession;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ocpsoft.pretty.faces.test.PrettyFacesTestBase;

@RunWith(Arquillian.class)
public class SimpleMappingTest
{

   @Deployment
   public static WebArchive createDeployment()
   {
      return PrettyFacesTestBase.createDeployment()
            .addAsWebResource("mapping/page.xhtml", "page.xhtml")
            .addAsWebResource("mapping/page.xhtml", "unusual-view-id-(.xhtml")
            .addAsWebInfResource("mapping/pretty-config.xml", "pretty-config.xml");
   }

   @Test
   public void testSimpleMapping() throws Exception
   {
      
      JSFSession jsfSession = new JSFSession("/simple-mapping");
      JSFClientSession client = jsfSession.getJSFClientSession();

      assertTrue(client.getPageAsText().contains("Mapped view was rendered!"));
      
   }

   @Test
   public void testUnusualViewId() throws Exception
   {
      
      JSFSession jsfSession = new JSFSession("/unusual-view-id");
      JSFClientSession client = jsfSession.getJSFClientSession();
      
      assertTrue(client.getPageAsText().contains("Mapped view was rendered!"));
      
   }
   
}
