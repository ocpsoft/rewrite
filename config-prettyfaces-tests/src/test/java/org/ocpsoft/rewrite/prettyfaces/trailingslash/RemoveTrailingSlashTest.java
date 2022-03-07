package org.ocpsoft.rewrite.prettyfaces.trailingslash;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.prettyfaces.PrettyFacesTestBase;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTestBase;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class RemoveTrailingSlashTest extends RewriteTestBase
{
   @Deployment(testable = false)
   public static WebArchive createDeployment()
   {
      return PrettyFacesTestBase.getDeployment()
               .addAsWebResource(new StringAsset("some content"), "foobar.txt")
               .addAsWebInfResource("trailingslash/trailingslash-pretty-config.xml", "pretty-config.xml");
   }

   @Test
   // https://github.com/ocpsoft/rewrite/issues/95
   public void requestingFileDirectly() throws Exception
   {
      HttpAction action = get("/foobar.txt");

      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).contains("some content");
   }

   @Test
   // https://github.com/ocpsoft/rewrite/issues/95
   public void requestWithTrailing() throws Exception
   {
      HttpAction action = get("/foobar.txt/");

      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).contains("some content");
   }

}