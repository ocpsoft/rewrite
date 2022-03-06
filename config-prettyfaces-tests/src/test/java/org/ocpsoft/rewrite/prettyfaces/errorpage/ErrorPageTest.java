package org.ocpsoft.rewrite.prettyfaces.errorpage;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.category.IgnoreForWildfly;
import org.ocpsoft.rewrite.prettyfaces.PrettyFacesTestBase;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTestBase;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Completely fails on Wildfly with some kind of sendError() recursion. We need some more time to debug this. As this
 * currently completely kills the Travis build, we should disable it for now.
 * 
 * @author Christian Kaltepoth
 * 
 * @see https://github.com/ocpsoft/rewrite/issues/144
 */
@RunWith(Arquillian.class)
@Category(IgnoreForWildfly.class)
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
      HttpAction action = get("/validate/foobar");

      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).contains("Parameter is valid");
   }

   @Test
   public void directlyAccessed404PageRendersCorrectly() throws Exception
   {
      HttpAction action = get("/404.jsf");

      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).contains("Custom 404 page");
      assertThat(action.getResponseContent()).contains("#{1+1} = 2");
   }

   @Test
   // https://github.com/ocpsoft/rewrite/issues/96
   public void failedValidationShouldRenderCustom404FacesView() throws Exception
   {
      HttpAction action = get("/validate/invalid");

      assertThat(action.getStatusCode()).isEqualTo(404);
      assertThat(action.getResponseContent()).contains("Custom 404 page");
      assertThat(action.getResponseContent()).contains("#{1+1} = 2");
   }

}