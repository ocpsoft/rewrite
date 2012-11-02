package com.ocpsoft.pretty.faces;

import static junit.framework.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.util.ReflectionUtils;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.PrettyFilter;
import com.ocpsoft.pretty.faces.config.PrettyConfig;
import com.ocpsoft.pretty.faces.config.rewrite.RewriteRule;

public class PrettyFilterRewritingTest
{

   private MockServletContext servletContext;
   private PrettyFilter filter;
   private PrettyConfig prettyConfig;

   @Before
   public void prepare() throws Exception {

      servletContext = new MockServletContext();

      prettyConfig = new PrettyConfig();
      servletContext.setAttribute(PrettyContext.CONFIG_KEY, prettyConfig);
      
      filter = new PrettyFilter();

      /*
       * Hack: Set ServletContext via reflection because we don't want
       * the filter to run the initialization procedure
       */
      Field servletContextField = ReflectionUtils.findField(PrettyFilter.class, "servletContext");
      ReflectionUtils.makeAccessible(servletContextField);
      ReflectionUtils.setField(servletContextField, filter, servletContext);

   }
   
   @Test
   public void testSimpleRedirect() throws Exception {

      // set up the rewrite rule
      prettyConfig.setGlobalRewriteRules( Arrays.asList(
            createRule("/redirect", "/new-url")
      ));

      // call filter
      MockHttpServletRequest request = createRequest("/redirect");
      MockHttpServletResponse response = new MockHttpServletResponse();
      filter.doFilter(request, response, new MockFilterChain());
      
      // verify outcome
      assertEquals("/new-url", response.getHeader("Location"));

   }

   @Test
   public void testPathEncodingInLocationHeader() throws Exception {

      // set up the rewrite rule
      prettyConfig.setGlobalRewriteRules( Arrays.asList(
            createRule("/redirect", "/\u00fc")  // german umlaut
      ));

      // call filter
      MockHttpServletRequest request = createRequest("/redirect");
      MockHttpServletResponse response = new MockHttpServletResponse();
      filter.doFilter(request, response, new MockFilterChain());
      
      // verify outcome
      assertEquals("/%C3%BC", response.getHeader("Location"));

   }
   
   @Test
   public void testRewriteWithFullURL() throws Exception {

      // set up the rewrite rule
      prettyConfig.setGlobalRewriteRules( Arrays.asList(
            createRule("/redirect", "http://www.google.com/")
      ));

      // call filter
      MockHttpServletRequest request = createRequest("/redirect");
      MockHttpServletResponse response = new MockHttpServletResponse();
      filter.doFilter(request, response, new MockFilterChain());
      
      // verify outcome
      assertEquals("http://www.google.com/", response.getHeader("Location"));

   }

   @Test
   public void testRewriteWithQueryParameters() throws Exception {

      // set up the rewrite rule
      prettyConfig.setGlobalRewriteRules( Arrays.asList(
            createRule("/query/(\\w+)/(\\w+)", "http://www.google.com/search?hl=$1&q=$2")
      ));

      // call filter
      MockHttpServletRequest request = createRequest("/query/en/something");
      MockHttpServletResponse response = new MockHttpServletResponse();
      filter.doFilter(request, response, new MockFilterChain());
      
      // verify outcome
      assertEquals("http://www.google.com/search?hl=en&q=something", response.getHeader("Location"));

   }

   @Test
   public void testSpaceCharacterQueryParameter() throws Exception {

      // set up the rewrite rule
      prettyConfig.setGlobalRewriteRules( Arrays.asList(
            createRule("/rewrite", "http://127.0.0.1/search?query=some query&other=something else")
      ));

      // call filter
      MockHttpServletRequest request = createRequest("/rewrite");
      MockHttpServletResponse response = new MockHttpServletResponse();
      filter.doFilter(request, response, new MockFilterChain());

      // verify outcome
      assertEquals("http://127.0.0.1/search?query=some+query&other=something+else", response.getHeader("Location"));

   }
   
   private MockHttpServletRequest createRequest(String req) {
      MockHttpServletRequest request = new MockHttpServletRequest(servletContext, "GET", "/test"+req);
      request.setContextPath("/test");
      return request;
   }
   
   private static RewriteRule createRule(String match, String url) {
      RewriteRule rule = new RewriteRule();
      rule.setMatch(match);
      rule.setUrl(url);
      return rule;
   }
   
}
