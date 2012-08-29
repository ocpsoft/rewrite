package org.ocpsoft.rewrite.annotation;

import java.io.File;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

public class RewriteAnnotationTest
{
   public static JavaArchive getRewriteAnnotationArchive()
   {
      return ShrinkWrap.create(JavaArchive.class, "rewrite-annotations.jar")
               .addAsResource(new File("../config-annotations-api/target/classes/org"))
               .addAsResource(new File("../config-annotations-impl/target/classes/org"))
               .addAsResource(new File("../config-annotations-impl/target/classes/META-INF"));
   }

}
