package org.ocpsoft.rewrite.test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;

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
   protected static File[] resolveDependencies(final String coords)
   {
      return Maven.resolver()
               .loadPomFromFile("pom.xml")
               .resolve(coords)
               .withTransitivity()
               .asFile();
   }

   /**
    * Resolve an {@link Archive} from Maven coordinates.
    */
   protected static File resolveDependency(final String coords)
   {
      return resolveDependencies(coords)[0];
   }

   /**
    * Request a resource from the deployed test-application. The {@link HttpServletRequest#getContextPath()} will be
    * automatically prepended to the given path.
    * <p>
    * E.g: A path of '/example' will be sent as '/rewrite-test/example'
    * 
    * @throws Exception
    */
   protected HttpAction<HttpGet> get(final String path) throws Exception
   {
      DefaultHttpClient client = new DefaultHttpClient();
      return get(client, path);
   }

   /**
    * Request a resource from the deployed test-application. The {@link HttpServletRequest#getContextPath()} will be
    * automatically prepended to the given path.
    * <p>
    * E.g: A path of '/example' will be sent as '/rewrite-test/example'
    * 
    * @throws Exception
    */
   protected HttpAction<HttpGet> get(HttpClient client, String path) throws Exception
   {
      return get(client, path, new Header[0]);
   }

   /**
    * Request a resource from the deployed test-application. The {@link HttpServletRequest#getContextPath()} will be
    * automatically prepended to the given path.
    * <p>
    * E.g: A path of '/example' will be sent as '/rewrite-test/example'
    * 
    * @throws Exception
    */
   protected HttpAction<HttpGet> get(HttpClient client, String path, Header... headers) throws Exception
   {
      HttpGet request = new HttpGet(getBaseURL() + getContextPath() + path);
      if (headers != null && headers.length > 0) {
         request.setHeaders(headers);
      }
      HttpContext context = new BasicHttpContext();
      HttpResponse response = client.execute(request, context);

      return new HttpAction<HttpGet>(client, context, request, response, getBaseURL(), getContextPath());
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

   @ArquillianResource
   URL baseUrl;

   protected String getBaseURL()
   {
      return baseUrl.getProtocol() + "://" + baseUrl.getHost()
               + (baseUrl.getPort() == -1 ? "" : ":" + baseUrl.getPort());
   }

   protected String getContextPath()
   {
      String contextPath = baseUrl.getPath();
      if (!"/".equals(contextPath))
         contextPath = contextPath.replaceAll("^(.*)/$", "$1").replaceAll("ROOT$", "");
      return contextPath;
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

   /**
    * Verifies that the given text contains the given string.
    */
   protected static void assertContains(String text, String s)
   {
      if (text == null || s == null || !text.contains(s)) {
         Assert.fail("Could not find [" + s + "] in text: " + text);
      }
   }

}
