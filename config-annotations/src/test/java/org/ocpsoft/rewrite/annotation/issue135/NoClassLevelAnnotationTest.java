package org.ocpsoft.rewrite.annotation.issue135;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.apache.http.client.methods.HttpGet;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.annotation.RewriteAnnotationTest;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTest;
import org.ocpsoft.rewrite.test.RewriteTestBase;

@RunWith(Arquillian.class)
public class NoClassLevelAnnotationTest extends RewriteTestBase
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteTest.getDeploymentWithCDI()
               .addAsLibrary(RewriteAnnotationTest.getRewriteAnnotationArchive())
               .addAsLibrary(RewriteAnnotationTest.getRewriteCdiArchive())
               .addClass(NoClassLevelAnnotationBean.class)
               .addAsWebResource(new StringAsset("some static file content"), "file.txt");
   }

   @Test
   public void requestPhysicalFile() throws Exception
   {
      // request a physical file deployed with the WAR
      HttpAction<HttpGet> action = get("/file.txt");

      // the file should be served without NoClassLevelAnnotationBean#action() beeing invoked
      assertEquals(200, action.getStatusCode());
      assertTrue(action.getResponseContent().contains("some static file content"));
   }

}
