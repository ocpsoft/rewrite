package org.ocpsoft.rewrite.faces.test;

import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.ocpsoft.rewrite.test.RewriteTest;

public class FacesBase
{
   public static WebArchive getDeployment()
   {
      return RewriteTest
               .getDeploymentNoWebXml()
               .setWebXML("faces-web.xml")
               .addAsWebInfResource("faces-config.xml", "faces-config.xml");
   }

}
