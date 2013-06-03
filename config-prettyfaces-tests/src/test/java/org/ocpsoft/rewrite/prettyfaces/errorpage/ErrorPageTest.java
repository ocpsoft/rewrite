package org.ocpsoft.rewrite.prettyfaces.errorpage;

import static org.junit.Assert.assertThat;

import org.apache.http.client.methods.HttpGet;
import org.hamcrest.Matchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.prettyfaces.PrettyFacesTestBase;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTestBase;

@RunWith(Arquillian.class)
public class ErrorPageTest extends RewriteTestBase
{
   @Deployment(testable = false)
   public static WebArchive createDeployment()
   {
      return PrettyFacesTestBase.getDeployment()
               .addClass(ErrorPageValidator.class)
               .addAsWebResource("errorpage/errorpage-view.xhtml", "view.xhtml")
               .addAsWebResource("errorpage/errorpage-404.xhtml", "404.xhtml")
               .addAsWebInfResource("errorpage/errorpage-pretty-config.xml", "pretty-config.xml")
               .setWebXML("errorpage/errorpage-web.xml");
   }

   @Test
   public void successfulValidationShouldRenderView() throws Exception
   {
      HttpAction<HttpGet> action = get("/validate/foobar");

      assertThat(action.getStatusCode(), Matchers.is(200));
      assertThat(action.getResponseContent(), Matchers.containsString("Parameter is valid"));
   }

   @Test
   public void directlyAccessed404PageRendersCorrectly() throws Exception
   {
      HttpAction<HttpGet> action = get("/404.jsf");

      assertThat(action.getStatusCode(), Matchers.is(200));
      assertThat(action.getResponseContent(), Matchers.containsString("Custom 404 page"));
      assertThat(action.getResponseContent(), Matchers.containsString("#{1+1} = 2"));
   }

   // https://github.com/ocpsoft/rewrite/issues/96
   @Test
   @Ignore
   public void failedValidationShouldRenderCustom404FacesView() throws Exception
   {
      HttpAction<HttpGet> action = get("/validate/invalid");

      assertThat(action.getStatusCode(), Matchers.is(404));
      assertThat(action.getResponseContent(), Matchers.containsString("Custom 404 page"));
      assertThat(action.getResponseContent(), Matchers.containsString("#{1+1} = 2"));
   }

}