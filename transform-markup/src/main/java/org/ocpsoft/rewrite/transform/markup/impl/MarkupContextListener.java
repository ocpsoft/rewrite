package org.ocpsoft.rewrite.transform.markup.impl;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.jruby.embed.ScriptingContainer;
import org.ocpsoft.rewrite.servlet.spi.ContextListener;

public class MarkupContextListener implements ContextListener
{

   @Override
   public int priority()
   {
      return 0;
   }

   @Override
   public void contextInitialized(ServletContextEvent event)
   {}

   @Override
   @SuppressWarnings("unchecked")
   public void contextDestroyed(ServletContextEvent event)
   {
      ServletContext context = event.getServletContext();
      Map<Class<?>, ScriptingContainer> storage = (Map<Class<?>, ScriptingContainer>) context
               .getAttribute(JRubyTransformer.CONTAINER_STORE_KEY);
      if (storage != null)
      {
         for (ScriptingContainer scriptingContainer : storage.values()) {
            if (scriptingContainer != null)
               scriptingContainer.terminate();
         }
      }
   }

}
