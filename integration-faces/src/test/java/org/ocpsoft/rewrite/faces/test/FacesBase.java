package org.ocpsoft.rewrite.faces.test;

import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.ocpsoft.rewrite.test.RewriteIT;

public class FacesBase
{
   public static WebArchive getDeployment()
   {
      return RewriteIT
               .getDeploymentNoWebXml()
               .setWebXML("faces-web.xml")
               .addAsWebInfResource("faces-config.xml", "faces-config.xml")
               // Necessary as of Faces 4.0 because CDI and Faces have been separated now.
               .addAsWebInfResource(new StringAsset("<beans/>"), "beans.xml");
   }
}
