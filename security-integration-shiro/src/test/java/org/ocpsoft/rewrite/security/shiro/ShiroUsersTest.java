package org.ocpsoft.rewrite.security.shiro;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTest;
import org.ocpsoft.rewrite.test.RewriteTestBase;

import static org.assertj.core.api.Assertions.assertThat;

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
      HttpAction action = get("/admin/something");
      assertThat(action.getStatusCode()).isEqualTo(404);
      action.getResponseContent(); // consume response
   }

   @Test
   public void testShiroAsAuthorizedUser() throws Exception
   {

      // before login
      HttpAction beforeLogin = get(client, "/admin/something");
      assertThat(beforeLogin.getStatusCode()).isEqualTo(404);
      beforeLogin.getResponseContent(); // consume response

      // login as admin
      HttpAction login = get(client, "/login?user=ck");
      assertThat(login.getStatusCode()).isEqualTo(200);
      assertThat(login.getResponseContent()).doesNotContain("404");

      // page is available
      HttpAction afterLogin = get(client, "/admin/something");
      assertThat(afterLogin.getStatusCode()).isEqualTo(200);
      assertThat(afterLogin.getResponseContent()).contains("Protected admin page");

      // logout as admin
      HttpAction logout = get(client, "/logout");
      assertThat(logout.getStatusCode()).isEqualTo(200);
      assertThat(logout.getResponseContent()).doesNotContain("404");

      // after logout
      HttpAction afterLogout = get(client, "/admin/something");
      assertThat(afterLogout.getStatusCode()).isEqualTo(404);
      afterLogout.getResponseContent(); // consume response

   }

   @Test
   public void testShiroAsOtherUser() throws Exception
   {

      // before login
      HttpAction beforeLogin = get(client, "/admin/something");
      assertThat(beforeLogin.getStatusCode()).isEqualTo(404);
      beforeLogin.getResponseContent(); // consume response

      // login as someone else
      HttpAction login = get(client, "/login?user=somebody");
      assertThat(login.getStatusCode()).isEqualTo(200);
      assertThat(login.getResponseContent()).doesNotContain("404");

      // wrong role
      HttpAction afterLogin = get(client, "/admin/something");
      assertThat(afterLogin.getStatusCode()).isEqualTo(404);
      afterLogin.getResponseContent(); // consume response

      // logout as someone else
      HttpAction logout = get(client, "/logout");
      assertThat(logout.getStatusCode()).isEqualTo(200);
      assertThat(logout.getResponseContent()).doesNotContain("404");

      // after logout
      HttpAction afterLogout = get(client, "/admin/something");
      assertThat(afterLogout.getStatusCode()).isEqualTo(404);
      afterLogout.getResponseContent(); // consume response

   }

}
