package org.ocpsoft.rewrite.gwt.client.history;

import com.google.gwt.user.client.Cookies;

/**
 * Standard {@link ContextPathProvider} for retrieving the context path via a cookie set when the server is accessed.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class CookieContextPathProvider implements ContextPathProvider
{
   public void getContextPath(HistoryStateImpl historyState)
   {
      String contextPath = Cookies.getCookie("org.ocpsoft.rewrite.gwt.history.contextPath");
      if (contextPath != null)
         HistoryStateImpl.setContextPath(contextPath);
   }

}
