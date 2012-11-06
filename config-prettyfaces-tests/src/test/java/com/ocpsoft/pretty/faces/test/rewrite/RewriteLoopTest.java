package com.ocpsoft.pretty.faces.test.rewrite;

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
public class RewriteLoopTest
{

   @Deployment
   public static WebArchive createDeployment()
   {
      return PrettyFacesTestBase.createDeployment()
            .addAsWebResource("rewrite/rewrite-loop.xhtml", "rewrite-loop.xhtml")
            .addAsWebInfResource("rewrite/rewrite-loop-config.xml", "pretty-config.xml");
   }

   @Test
   public void testRewriteRuleSubstiuteMatchingRulePattern() throws Exception
   {

      // Access the URL that is rewritten to the correct JSF page
      JSFSession jsfSession = new JSFSession("/rewrite-loop.html");
      JSFClientSession client = jsfSession.getJSFClientSession();

      // the page should render fine without any rewrite loop
      assertTrue(client.getContentPage().getUrl().toString().endsWith("/rewrite-loop.html"));
      assertTrue(client.getPageAsText().contains("Rewrite loop test page rendered"));

   }

}
