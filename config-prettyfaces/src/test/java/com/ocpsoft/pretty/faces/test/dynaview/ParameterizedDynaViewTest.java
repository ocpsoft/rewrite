package com.ocpsoft.pretty.faces.test.dynaview;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.jsfunit.jsfsession.JSFClientSession;
import org.jboss.jsfunit.jsfsession.JSFSession;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ocpsoft.pretty.faces.test.PrettyFacesTestBase;

@RunWith(Arquillian.class)
public class ParameterizedDynaViewTest extends PrettyFacesTestBase
{

   @Deployment
   public static WebArchive getDeployment()
   {
      return PrettyFacesTestBase.getDeployment()
            .addClass(PathParamDynaViewBean.class)
            .addClass(QueryParamDynaViewBean.class)
            .addClass(DynaViewParameterValidator.class)
            .addAsWebResource("dynaview/index.xhtml", "index.xhtml")
            .addAsWebResource("dynaview/correct.xhtml", "correct.xhtml");
   }

   @Test
   public void testPathParamInjectionHappensBeforeViewDetermination() throws Exception
   {
      
      // First visit the start page
      JSFSession jsfSession = new JSFSession("/index.jsf");

      // click on the link talking the user to /pathparam/correct
      JSFClientSession client = jsfSession.getJSFClientSession();
      client.click("pathParamLink");

      // the dynaview code should send the user to the correct page.
      assertTrue(client.getPageAsText().contains("The parameter was correctly injected"));
      
   }
   
   @Test
   public void testQueryParamInjectionHappensBeforeViewDetermination() throws Exception
   {
      
      // First visit the start page
      JSFSession jsfSession = new JSFSession("/index.jsf");
      
      // click on the link talking the user to /queryparam?param=correct
      JSFClientSession client = jsfSession.getJSFClientSession();
      client.click("queryParamLink");
      
      // the dynaview code should send the user to the correct page.
      assertTrue(client.getPageAsText().contains("The parameter was correctly injected"));
      
   }
   
   @Test
   public void testInvalidPathParamWithDynaView() throws Exception
   {
      
      // First visit the start page
      JSFSession jsfSession = new JSFSession("/index.jsf");
      jsfSession.getWebClient().setThrowExceptionOnFailingStatusCode(false);
      
      // click on the link talking the user to /pathparam/invalid
      JSFClientSession client = jsfSession.getJSFClientSession();
      client.click("invalidPathParamLink");
      
      // the dynaview code should send the user to the correct page.
      assertEquals(404, client.getContentPage().getWebResponse().getStatusCode());
      
   }
   
   @Test
   public void testInvalidQueryParamWithDynaView() throws Exception
   {
      
      // First visit the start page
      JSFSession jsfSession = new JSFSession("/index.jsf");
      jsfSession.getWebClient().setThrowExceptionOnFailingStatusCode(false);
      
      // click on the link talking the user to /queryparam?param=invalid
      JSFClientSession client = jsfSession.getJSFClientSession();
      client.click("invalidQueryParamLink");
      
      // the dynaview code should send the user to the correct page.
      assertEquals(404, client.getContentPage().getWebResponse().getStatusCode());
      
   }

}
