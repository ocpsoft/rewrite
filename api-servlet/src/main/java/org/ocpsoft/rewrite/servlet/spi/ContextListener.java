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
package org.ocpsoft.rewrite.servlet.spi;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.ocpsoft.common.pattern.Weighted;

/**
 * Listens to {@link ServletContextEvent}.
 * 
 * Additional listeners may be specified by providing a service activator file containing the name of your
 * implementations:
 * <p>
 * <code> /META-INF/services/org.ocpsoft.rewrite.servlet.spi.ContextListener<br>
 * 
 * --------------<br>
 * com.example.ContextListenerImpl</code>
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface ContextListener extends Weighted
{
   /**
    * Respond to {@link ServletContext} initialized event.
    */
   void contextInitialized(ServletContextEvent event);

   /**
    * Respond to {@link ServletContext} destroyed event.
    */
   void contextDestroyed(ServletContextEvent event);
}
