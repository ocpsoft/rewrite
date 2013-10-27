package org.ocpsoft.rewrite.security.shiro;

import static org.junit.Assert.assertEquals;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTest;
import org.ocpsoft.rewrite.test.RewriteTestBase;

@RunWith(Arquillian.class)
public class ShiroUsersTest extends RewriteTestBase
{

   @Deployment(testable = false)
   public static WebArchive createDeployment()
   {
      // web.xml has to be removed first because we bundle a different one
      WebArchive baseDeployment = RewriteTest.getDeployment();
      baseDeployment.delete("/WEB-INF/web.xml");

      return baseDeployment
               .addClasses(AdminPageBean.class, LoginServlet.class, LogoutServlet.class, ShiroTestRealm.class)
               .addAsLibraries(resolveDependencies("org.apache.shiro:shiro-web"))
               .addAsLibraries(resolveDependencies("org.ocpsoft.rewrite:rewrite-security-integration-shiro"))
               // Glassfish needs SLF4J to be in the deployment
               .addAsLibraries(resolveDependencies("org.slf4j:slf4j-simple:jar:1.7.5"))
               .setWebXML("shiro-web.xml")
               .addAsWebInfResource("shiro.ini")
               .addAsWebResource("protected-page.xhtml");
   }

   @Test
   public void testShiroAsAnonymousUser() throws Exception
   {
      HttpAction<HttpGet> action = get("/admin/something");
      Assert.assertEquals(404, action.getStatusCode());
      action.getResponseContent(); // consume response
   }

   @Test
   public void testShiroAsAuthorizedUser() throws Exception
   {
      HttpClient client = new DefaultHttpClient();

      // before login
      HttpAction<HttpGet> beforeLogin = get(client, "/admin/something");
      Assert.assertEquals(404, beforeLogin.getStatusCode());
      beforeLogin.getResponseContent(); // consume response

      // login as admin
      HttpAction<HttpGet> login = get(client, "/login?user=ck");
      Assert.assertEquals(200, login.getStatusCode());
      Assert.assertFalse(login.getResponseContent().contains("404"));

      // page is available
      HttpAction<HttpGet> afterLogin = get(client, "/admin/something");
      Assert.assertEquals(200, afterLogin.getStatusCode());
      Assert.assertTrue(afterLogin.getResponseContent().contains("Protected admin page"));

      // logout as admin
      HttpAction<HttpGet> logout = get(client, "/logout");
      assertEquals(200, logout.getStatusCode());
      Assert.assertFalse(logout.getResponseContent().contains("404"));

      // after logout
      HttpAction<HttpGet> afterLogout = get(client, "/admin/something");
      assertEquals(404, afterLogout.getStatusCode());
      afterLogout.getResponseContent(); // consume response

   }

   @Test
   public void testShiroAsOtherUser() throws Exception
   {
      HttpClient client = new DefaultHttpClient();

      // before login
      HttpAction<HttpGet> beforeLogin = get(client, "/admin/something");
      Assert.assertEquals(404, beforeLogin.getStatusCode());
      beforeLogin.getResponseContent(); // consume response

      // login as someone else
      HttpAction<HttpGet> login = get(client, "/login?user=somebody");
      assertEquals(200, login.getStatusCode());
      Assert.assertFalse(login.getResponseContent().contains("404"));

      // wrong role
      HttpAction<HttpGet> afterLogin = get(client, "/admin/something");
      Assert.assertEquals(404, afterLogin.getStatusCode());
      afterLogin.getResponseContent(); // consume response

      // logout as someone else
      HttpAction<HttpGet> logout = get(client, "/logout");
      assertEquals(200, logout.getStatusCode());
      Assert.assertFalse(logout.getResponseContent().contains("404"));

      // after logout
      HttpAction<HttpGet> afterLogout = get(client, "/admin/something");
      assertEquals(404, afterLogout.getStatusCode());
      afterLogout.getResponseContent(); // consume response

   }

}
