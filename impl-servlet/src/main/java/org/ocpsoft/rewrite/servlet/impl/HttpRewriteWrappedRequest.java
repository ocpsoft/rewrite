/*
 * Copyright 2011 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ocpsoft.rewrite.servlet.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.ocpsoft.rewrite.servlet.spi.RequestParameterProvider;

/**
 * @author Lincoln Baxter, III <lincoln@ocpsoft.com>
 */
public class HttpRewriteWrappedRequest extends HttpServletRequestWrapper implements RequestParameterProvider
{
   private final Map<String, String[]> modifiableParameters;
   private HttpRewriteWrappedRequest outer = null;

   /**
    * Create a new request wrapper that will merge additional parameters into the request object without prematurely
    * reading parameters from the original request.
    */
   public HttpRewriteWrappedRequest(final HttpServletRequest request, final Map<String, String[]> additionalParams)
   {
      super(request);
      modifiableParameters = new TreeMap<String, String[]>();
      modifiableParameters.putAll(additionalParams);

      /*
       * The previous wrapped request needs to be updated when this object's values are modified.
       */
      HttpRewriteWrappedRequest inner = getFromRequest(request);
      if (inner != null)
         inner.setOuter(this);

      setInRequest(this, request);
   }

   private void setOuter(HttpRewriteWrappedRequest outer)
   {
      this.outer = outer;
   }

   public Map<String, String[]> getModifiableParameters()
   {
      return modifiableParameters;
   }

   public static HttpRewriteWrappedRequest getFromRequest(ServletRequest request)
   {
      HttpRewriteWrappedRequest wrapper = (HttpRewriteWrappedRequest) request
               .getAttribute(HttpRewriteWrappedRequest.class.getName());
      return wrapper;
   }

   private static void setInRequest(final HttpRewriteWrappedRequest wrapped, final ServletRequest request)
   {
      request.setAttribute(HttpRewriteWrappedRequest.class.getName(), wrapped);
   }

   /*
    * HttpServletRequest overrides
    */

   @Override
   public String getParameter(final String name)
   {
      String[] strings = getParameterMap().get(name);
      if (strings != null)
      {
         return strings[0];
      }
      return super.getParameter(name);
   }

   @Override
   public String getAuthType()
   {
      // TODO Auto-generated method stub
      return super.getAuthType();
   }

   @Override
   public Cookie[] getCookies()
   {
      // TODO Auto-generated method stub
      return super.getCookies();
   }

   @Override
   public long getDateHeader(String name)
   {
      // TODO Auto-generated method stub
      return super.getDateHeader(name);
   }

   @Override
   public String getHeader(String name)
   {
      // TODO Auto-generated method stub
      return super.getHeader(name);
   }

   @Override
   public Enumeration<String> getHeaders(String name)
   {
      // TODO Auto-generated method stub
      return super.getHeaders(name);
   }

   @Override
   public Enumeration<String> getHeaderNames()
   {
      // TODO Auto-generated method stub
      return super.getHeaderNames();
   }

   @Override
   public int getIntHeader(String name)
   {
      // TODO Auto-generated method stub
      return super.getIntHeader(name);
   }

   @Override
   public String getMethod()
   {
      // TODO Auto-generated method stub
      return super.getMethod();
   }

   @Override
   public String getPathInfo()
   {
      // TODO Auto-generated method stub
      return super.getPathInfo();
   }

   @Override
   public String getPathTranslated()
   {
      // TODO Auto-generated method stub
      return super.getPathTranslated();
   }

   @Override
   public String getContextPath()
   {
      // TODO Auto-generated method stub
      return super.getContextPath();
   }

   @Override
   public String getQueryString()
   {
      // TODO Auto-generated method stub
      return super.getQueryString();
   }

   @Override
   public String getRemoteUser()
   {
      // TODO Auto-generated method stub
      return super.getRemoteUser();
   }

   @Override
   public boolean isUserInRole(String role)
   {
      // TODO Auto-generated method stub
      return super.isUserInRole(role);
   }

   @Override
   public Principal getUserPrincipal()
   {
      // TODO Auto-generated method stub
      return super.getUserPrincipal();
   }

   @Override
   public String getRequestedSessionId()
   {
      // TODO Auto-generated method stub
      return super.getRequestedSessionId();
   }

   @Override
   public String getRequestURI()
   {
      // TODO Auto-generated method stub
      return super.getRequestURI();
   }

   @Override
   public StringBuffer getRequestURL()
   {
      // TODO Auto-generated method stub
      return super.getRequestURL();
   }

   @Override
   public String getServletPath()
   {
      // TODO Auto-generated method stub
      return super.getServletPath();
   }

   @Override
   public HttpSession getSession(boolean create)
   {
      // TODO Auto-generated method stub
      return super.getSession(create);
   }

   @Override
   public HttpSession getSession()
   {
      // TODO Auto-generated method stub
      return super.getSession();
   }

   @Override
   public boolean isRequestedSessionIdValid()
   {
      // TODO Auto-generated method stub
      return super.isRequestedSessionIdValid();
   }

   @Override
   public boolean isRequestedSessionIdFromCookie()
   {
      // TODO Auto-generated method stub
      return super.isRequestedSessionIdFromCookie();
   }

   @Override
   public boolean isRequestedSessionIdFromURL()
   {
      // TODO Auto-generated method stub
      return super.isRequestedSessionIdFromURL();
   }

   @Override
   public boolean isRequestedSessionIdFromUrl()
   {
      // TODO Auto-generated method stub
      return super.isRequestedSessionIdFromUrl();
   }

   @Override
   public boolean authenticate(HttpServletResponse response) throws IOException, ServletException
   {
      // TODO Auto-generated method stub
      return super.authenticate(response);
   }

   @Override
   public void login(String username, String password) throws ServletException
   {
      // TODO Auto-generated method stub
      super.login(username, password);
   }

   @Override
   public void logout() throws ServletException
   {
      // TODO Auto-generated method stub
      super.logout();
   }

   @Override
   public Collection<Part> getParts() throws IOException, ServletException
   {
      // TODO Auto-generated method stub
      return super.getParts();
   }

   @Override
   public Part getPart(String name) throws IOException, ServletException
   {
      // TODO Auto-generated method stub
      return super.getPart(name);
   }

   @Override
   public ServletRequest getRequest()
   {
      // TODO Auto-generated method stub
      return super.getRequest();
   }

   @Override
   public void setRequest(ServletRequest request)
   {
      // TODO Auto-generated method stub
      super.setRequest(request);
   }

   @Override
   public Object getAttribute(String name)
   {
      // TODO Auto-generated method stub
      return super.getAttribute(name);
   }

   @Override
   public Enumeration<String> getAttributeNames()
   {
      // TODO Auto-generated method stub
      return super.getAttributeNames();
   }

   @Override
   public String getCharacterEncoding()
   {
      // TODO Auto-generated method stub
      return super.getCharacterEncoding();
   }

   @Override
   public void setCharacterEncoding(String enc) throws UnsupportedEncodingException
   {
      // TODO Auto-generated method stub
      super.setCharacterEncoding(enc);
   }

   @Override
   public int getContentLength()
   {
      // TODO Auto-generated method stub
      return super.getContentLength();
   }

   @Override
   public String getContentType()
   {
      // TODO Auto-generated method stub
      return super.getContentType();
   }

   @Override
   public ServletInputStream getInputStream() throws IOException
   {
      // TODO Auto-generated method stub
      return super.getInputStream();
   }

   @Override
   public String getProtocol()
   {
      // TODO Auto-generated method stub
      return super.getProtocol();
   }

   @Override
   public String getScheme()
   {
      // TODO Auto-generated method stub
      return super.getScheme();
   }

   @Override
   public String getServerName()
   {
      // TODO Auto-generated method stub
      return super.getServerName();
   }

   @Override
   public int getServerPort()
   {
      // TODO Auto-generated method stub
      return super.getServerPort();
   }

   @Override
   public BufferedReader getReader() throws IOException
   {
      // TODO Auto-generated method stub
      return super.getReader();
   }

   @Override
   public String getRemoteAddr()
   {
      // TODO Auto-generated method stub
      return super.getRemoteAddr();
   }

   @Override
   public String getRemoteHost()
   {
      // TODO Auto-generated method stub
      return super.getRemoteHost();
   }

   @Override
   public void setAttribute(String name, Object o)
   {
      // TODO Auto-generated method stub
      super.setAttribute(name, o);
   }

   @Override
   public void removeAttribute(String name)
   {
      // TODO Auto-generated method stub
      super.removeAttribute(name);
   }

   @Override
   public Locale getLocale()
   {
      // TODO Auto-generated method stub
      return super.getLocale();
   }

   @Override
   public Enumeration<Locale> getLocales()
   {
      // TODO Auto-generated method stub
      return super.getLocales();
   }

   @Override
   public boolean isSecure()
   {
      // TODO Auto-generated method stub
      return super.isSecure();
   }

   @Override
   public RequestDispatcher getRequestDispatcher(String path)
   {
      // TODO Auto-generated method stub
      return super.getRequestDispatcher(path);
   }

   @Override
   @SuppressWarnings("deprecation")
   public String getRealPath(String path)
   {
      // TODO Auto-generated method stub
      return super.getRealPath(path);
   }

   @Override
   public int getRemotePort()
   {
      // TODO Auto-generated method stub
      return super.getRemotePort();
   }

   @Override
   public String getLocalName()
   {
      // TODO Auto-generated method stub
      return super.getLocalName();
   }

   @Override
   public String getLocalAddr()
   {
      // TODO Auto-generated method stub
      return super.getLocalAddr();
   }

   @Override
   public int getLocalPort()
   {
      // TODO Auto-generated method stub
      return super.getLocalPort();
   }

   @Override
   public ServletContext getServletContext()
   {
      // TODO Auto-generated method stub
      return super.getServletContext();
   }

   @Override
   public boolean isAsyncStarted()
   {
      // TODO Auto-generated method stub
      return super.isAsyncStarted();
   }

   @Override
   public boolean isAsyncSupported()
   {
      // TODO Auto-generated method stub
      return super.isAsyncSupported();
   }

   @Override
   public AsyncContext getAsyncContext()
   {
      // TODO Auto-generated method stub
      return super.getAsyncContext();
   }

   @Override
   public boolean isWrapperFor(ServletRequest wrapped)
   {
      // TODO Auto-generated method stub
      return super.isWrapperFor(wrapped);
   }

   @Override
   @SuppressWarnings("rawtypes")
   public boolean isWrapperFor(Class wrappedType)
   {
      // TODO Auto-generated method stub
      return super.isWrapperFor(wrappedType);
   }

   @Override
   public DispatcherType getDispatcherType()
   {
      // TODO Auto-generated method stub
      return super.getDispatcherType();
   }

   @Override
   public Map<String, String[]> getParameterMap()
   {
      Map<String, String[]> allParameters = new TreeMap<String, String[]>();
      allParameters.putAll(super.getParameterMap());

      allParameters.putAll(modifiableParameters);

      if (outer != null)
         allParameters.putAll(outer.getModifiableParameters());

      return Collections.unmodifiableMap(allParameters);
   }

   @Override
   public Enumeration<String> getParameterNames()
   {
      return Collections.enumeration(getParameterMap().keySet());
   }

   @Override
   public String[] getParameterValues(final String name)
   {
      return getParameterMap().get(name);
   }

   @Override
   public Map<String, String[]> getParameters(ServletRequest request, ServletResponse response)
   {
      return modifiableParameters;
   }

   @Override
   public String toString()
   {
      return super.getRequestURL().toString();
   }
}
