package org.ocpsoft.rewrite.gwt.client.history;


/**
 * Provide a method of retrieving the root context path from which this application is hosted.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ContextPathProvider
{
   void getContextPath(HistoryStateImpl historyState);
}
