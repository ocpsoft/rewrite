package org.ocpsoft.rewrite.annotation;

import java.io.File;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

public class RewriteAnnotationTest
{
   public static JavaArchive getRewriteAnnotationArchive()
   {
      return ShrinkWrap.create(JavaArchive.class, "rewrite-annotations.jar")
               .addAsResource(new File("../annotations-api/target/classes/org"))
               .addAsResource(new File("../annotations-impl/target/classes/org"))
               .addAsResource(new File("../annotations-impl/target/classes/META-INF"));
   }

   public static JavaArchive getRewriteCdiArchive()
   {
      return ShrinkWrap.create(JavaArchive.class, "rewrite-cdi.jar")
               .addAsResource(new File("../integration-cdi/target/classes/org"))
               .addAsResource(new File("../integration-cdi/target/classes/META-INF"));
   }

}
