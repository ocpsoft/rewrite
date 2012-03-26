package org.ocpsoft.rewrite.gwt.client.history;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;

public class RequestContextPathProvider implements ContextPathProvider
{

   @Override
   public void getContextPath(HistoryStateImpl historyState)
   {
      new RequestBuilder(RequestBuilder.HEAD, GWT.getHostPageBaseURL()).setCallback(new RequestCallback() {
         
         @Override
         public void onResponseReceived(Request request, Response response)
         {
            HistoryStateImpl.setContextPath(response.getHeader("org.ocpsoft.rewrite.gwt.history.contextPath"));
         }
         
         @Override
         public void onError(Request request, Throwable exception)
         {}
      });  
   }
}
