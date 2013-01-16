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

import java.util.List;

import javax.servlet.ServletContext;

import org.ocpsoft.common.pattern.Weighted;
import org.ocpsoft.rewrite.servlet.ServletRegistration;

/**
 * SPI for obtaining all Servlets registered in the web application. This API is typically used by asking all registered
 * providers in an order determined by their priority. The result of the first implementation that doesn't return
 * <code>null</code> is expected to be the correct one.
 * 
 * @author Christian Kaltepoth
 */
public interface ServletRegistrationProvider extends Weighted
{

   /**
    * Returns a list of all Servlets registered in the application. Implementations MUST return <code>null</code> if
    * they are unable to obtain this list. Otherwise they have to return a complete list of registrations.
    */
   List<ServletRegistration> getServletRegistrations(ServletContext servletContext);

}
