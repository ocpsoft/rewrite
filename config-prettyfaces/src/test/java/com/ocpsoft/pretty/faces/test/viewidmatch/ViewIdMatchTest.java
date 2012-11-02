package com.ocpsoft.pretty.faces.test.viewidmatch;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.jsfunit.jsfsession.JSFClientSession;
import org.jboss.jsfunit.jsfsession.JSFServerSession;
import org.jboss.jsfunit.jsfsession.JSFSession;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ocpsoft.pretty.faces.test.PrettyFacesTestBase;

@RunWith(Arquillian.class)
public class ViewIdMatchTest extends PrettyFacesTestBase
{

   @Deployment
   public static WebArchive getDeployment()
   {
      return PrettyFacesTestBase.getDeployment()
               .addClass(ViewIdMatchBean.class)
               .addClass(ViewIdMatchTest.class)
               .addAsWebResource("viewidmatch/viewidmatch.xhtml", "path/viewidmatch.xhtml");
   }

   @Test
   public void testPatternMatchingViewId() throws Exception
   {

      // Access the page
      JSFSession jsfSession = new JSFSession("/path/christian");
      JSFClientSession client = jsfSession.getJSFClientSession();
      JSFServerSession server = jsfSession.getJSFServerSession();

      // Check injected value
      assertTrue("Wrong value injected", client.getPageAsText().contains("Injected value: [christian]"));
      assertEquals("christian", server.getManagedBeanValue("#{viewIdMatchBean.value}"));

   }

}
