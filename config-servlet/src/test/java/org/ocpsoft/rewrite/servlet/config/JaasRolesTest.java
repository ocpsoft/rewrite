package org.ocpsoft.rewrite.servlet.config;

import static org.junit.Assert.assertEquals;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.common.services.ServiceProvider;
import org.ocpsoft.rewrite.test.RewriteTest;

@RunWith(Arquillian.class)
public class JaasRolesTest extends RewriteTest
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteTest.getDeployment()
               .addPackages(true, ConfigRoot.class.getPackage())
               .addAsServiceProvider(ServiceProvider.class, JaasRolesTestProvider.class);
   }

   @Test
   public void testJAASAnonymousUser() throws Exception
   {
      assertEquals(404, get("/admin/something").getStatusCode());
   }

}
