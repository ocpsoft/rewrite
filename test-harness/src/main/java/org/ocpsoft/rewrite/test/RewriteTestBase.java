package org.ocpsoft.rewrite.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Base utility class for Rewrite Tests.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class RewriteTestBase
{
   /**
    * Resolve an {@link Archive} from Maven coordinates.
    */
   protected static Collection<GenericArchive> resolveDependencies(final String coords)
   {
      return DependencyResolvers.use(MavenDependencyResolver.class)
               .artifacts(coords)
               .loadMetadataFromPom("pom.xml")
               .resolveAs(GenericArchive.class);
   }

   /**
    * Resolve an {@link Archive} from Maven coordinates.
    */
   protected static GenericArchive resolveDependency(final String coords)
   {
      return new ArrayList<GenericArchive>(DependencyResolvers.use(MavenDependencyResolver.class)
               .artifacts(coords)
               .loadMetadataFromPom("pom.xml")
               .resolveAs(GenericArchive.class)).get(0);
   }
   /**
    * Request a resource from the deployed test-application. The {@link HttpServletRequest#getContextPath()} will be
    * automatically prepended to the given path.
    * <p>
    * E.g: A path of '/example' will be sent as '/rewrite-test/example'
    */
   protected HttpAction<HttpGet> get(final String path)
   {
      DefaultHttpClient client = new DefaultHttpClient();
      return get(client, path);
   }

   /**
    * Request a resource from the deployed test-application. The {@link HttpServletRequest#getContextPath()} will be
    * automatically prepended to the given path.
    * <p>
    * E.g: A path of '/example' will be sent as '/rewrite-test/example'
    */
   protected HttpAction<HttpGet> get(HttpClient client, String path)
   {
      try
      {
         HttpGet request = new HttpGet(getBaseURL() + getContextPath() + path);
         HttpContext context = new BasicHttpContext();
         HttpResponse response = client.execute(request, context);

         return new HttpAction<HttpGet>(client, context, request, response, getBaseURL(), getContextPath());
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   /**
    * Request a resource from the deployed test-application. The {@link HttpServletRequest#getContextPath()} will be
    * automatically prepended to the given path.
    * <p>
    * E.g: A path of '/example' will be sent as '/rewrite-test/example'
    */
   protected HttpAction<HttpHead> head(final String path)
   {
      DefaultHttpClient client = new DefaultHttpClient();
      try
      {
         HttpHead request = new HttpHead(getBaseURL() + getContextPath() + path);
         HttpContext context = new BasicHttpContext();
         HttpResponse response = client.execute(request, context);

         return new HttpAction<HttpHead>(client, context, request, response, getBaseURL(), getContextPath());
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   protected String getBaseURL()
   {
      String baseUrl = "http://localhost:8080";
      if (baseUrl.endsWith("/"))
      {
         baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
      }
      return baseUrl;
   }

   protected String getContextPath()
   {
      return "/rewrite-test";
   }

   protected HtmlAction getWebClient(String path) throws FailingHttpStatusCodeException, IOException
   {
      try {
         WebClient client = new WebClient();
         return new HtmlAction(client, (HtmlPage) client.getPage(getBaseURL() + getContextPath() + path));
      }
      catch (MalformedURLException e) {
         throw new RuntimeException(e);
      }
   }
}
