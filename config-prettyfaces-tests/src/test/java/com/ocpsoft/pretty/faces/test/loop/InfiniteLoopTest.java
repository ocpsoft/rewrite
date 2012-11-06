package com.ocpsoft.pretty.faces.test.loop;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.jsfunit.jsfsession.JSFServerSession;
import org.jboss.jsfunit.jsfsession.JSFSession;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.test.PrettyFacesTestBase;

@RunWith(Arquillian.class)
public class InfiniteLoopTest
{
   @Deployment
   public static WebArchive createDeployment()
   {
      return PrettyFacesTestBase.createDeployment()
               .addAsWebResource("loop/loop.xhtml", "loop.xhtml")
               .addAsWebInfResource("loop/loop-pretty-config.xml", "pretty-config.xml");
   }

   @Test
   public void testRewriteTrailingSlashToLowerCase() throws IOException
   {
      JSFSession jsfSession = new JSFSession("/loop.jsf");

      PrettyContext context = PrettyContext.getCurrentInstance();
      assertEquals("/loop.jsf", context.getRequestURL().toURL());

      JSFServerSession server = jsfSession.getJSFServerSession();
      assertEquals("/loop.xhtml", server.getCurrentViewID());
   }
}
