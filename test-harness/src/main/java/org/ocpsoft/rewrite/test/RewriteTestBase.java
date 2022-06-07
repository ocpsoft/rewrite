package org.ocpsoft.rewrite.test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import jakarta.servlet.http.HttpServletRequest;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import static org.assertj.core.api.Fail.fail;

/**
 * Base utility class for Rewrite Tests.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class RewriteTestBase
{
   protected final OkHttpClient client;

   protected RewriteTestBase() {
      HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(System.out::println);
      loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

      client = new OkHttpClient.Builder()
              .addInterceptor(loggingInterceptor)
              .build();
   }

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
   protected HttpAction get(final String path) throws Exception
   {
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
   protected HttpAction get(OkHttpClient client, String path) throws Exception
   {
      return get(client, path, Headers.of());
   }

   /**
    * Request a resource from the deployed test-application. The {@link HttpServletRequest#getContextPath()} will be
    * automatically prepended to the given path.
    * <p>
    * E.g: A path of '/example' will be sent as '/rewrite-test/example'
    * 
    * @throws Exception
    */
   protected HttpAction get(OkHttpClient client, String path, Headers headers) throws Exception
   {
      Request request = new Request.Builder().get()
              .url(getBaseURL() + getContextPath() + path)
              .headers(headers)
              .build();
      Response response = client.newCall(request).execute();

      return new HttpAction(client, request, response, getBaseURL(), getContextPath());
   }

   /**
    * Request a resource from the deployed test-application. The {@link HttpServletRequest#getContextPath()} will be
    * automatically prepended to the given path.
    * <p>
    * E.g: A path of '/example' will be sent as '/rewrite-test/example'
    */
   protected HttpAction head(final String path)
   {
      try
      {
         Request request = new Request.Builder()
                 .head()
                 .url(getBaseURL() + getContextPath() + path)
                 .build();
         Response response = client.newCall(request).execute();

         return new HttpAction(client, request, response, getBaseURL(), getContextPath());
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
         fail("Could not find [" + s + "] in text: " + text);
      }
   }

}
