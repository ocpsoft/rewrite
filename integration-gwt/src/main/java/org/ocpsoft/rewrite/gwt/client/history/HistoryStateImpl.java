package org.ocpsoft.rewrite.gwt.client.history;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.impl.HistoryImpl;

/**
 * History implementation based on pushState
 */
public class HistoryStateImpl extends HistoryImpl
{
   private static List<ContextPathProvider> providers = Arrays.asList(new CookieContextPathProvider(),
            new RequestContextPathProvider());

   static String contextPath = null;

   private static List<ContextPathListener> listeners = new ArrayList<ContextPathListener>();

   @Override
   public boolean init()
   {
      for (ContextPathProvider provider : providers) {
         provider.getContextPath(this);
         if (contextPath != null)
         {
            break;
         }
      }

      return initNative();
   }

   static void setContextPath(String contextPath)
   {
      if (contextPath != null && !contextPath.endsWith("/"))
      {
         contextPath += "/";
      }
      
      HistoryStateImpl.contextPath = contextPath;
      
      System.out.println("Set contextPath: " + contextPath);
      
      for (ContextPathListener listner : listeners) {
         listner.onContextPathSet(contextPath);
      }
   }

   public static ListenerRegistration addContextPathListener(final ContextPathListener listener)
   {
      listeners.add(listener);
      return new ListenerRegistration() {
         @Override
         public void removeListener()
         {
            listeners.remove(listener);
         }
      };
   }

   public static String getContextPath()
   {
      return contextPath;
   }

   public static boolean isInitialized()
   {
      return contextPath != null;
   }

   public native boolean initNative() /*-{
		var token = '';
		var historyImpl = this;

		var path = $wnd.location.pathname;
		if (path.length > 0) {
			token = historyImpl.@com.google.gwt.user.client.impl.HistoryImpl::decodeFragment(Ljava/lang/String;)(path);
			token = historyImpl.@org.ocpsoft.rewrite.gwt.client.history.HistoryStateImpl::cleanToken(Ljava/lang/String;)(token);
		}

		@com.google.gwt.user.client.impl.HistoryImpl::setToken(Ljava/lang/String;)(token);

		var oldHandler = $wnd.history.onpopstate;

		$wnd.onpopstate = $entry(function() {
			var token = '';
			var path = $wnd.location.pathname;
			if (path.length > 0) {
				token = historyImpl.@com.google.gwt.user.client.impl.HistoryImpl::decodeFragment(Ljava/lang/String;)(path);
				token = historyImpl.@org.ocpsoft.rewrite.gwt.client.history.HistoryStateImpl::cleanToken(Ljava/lang/String;)(token);
			}

			historyImpl.@com.google.gwt.user.client.impl.HistoryImpl::newItemOnEvent(Ljava/lang/String;)(token);

			if (oldHandler) {
				oldHandler();
			}
		});

		return true;
   }-*/;

   protected String cleanToken(String historyToken)
   {
      String contextPath = HistoryStateImpl.getContextPath();
      if (!contextPath.equals(historyToken) && historyToken.startsWith(contextPath))
         historyToken = historyToken.substring(contextPath.length());

      System.out.println("History.pop() = [" + historyToken + "]");
      return historyToken;
   }

   @Override
   protected void nativeUpdate(String historyToken)
   {
      String contextPath = HistoryStateImpl.getContextPath();
      if (!contextPath.equals(historyToken))
         historyToken = contextPath + historyToken;

      System.out.println("History.push(" + historyToken + ")");
      update(historyToken);
   }

   protected native void update(String historyToken) /*-{
		var encodedToken = this.@com.google.gwt.user.client.impl.HistoryImpl::encodeFragment(Ljava/lang/String;)(historyToken);
		$wnd.history.pushState(encodedToken, $wnd.document.title, encodedToken);
   }-*/;

}