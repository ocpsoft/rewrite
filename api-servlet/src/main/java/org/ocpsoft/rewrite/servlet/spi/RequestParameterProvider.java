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

import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.common.pattern.Weighted;

/**
 * Supplies additional request parameters to the {@link HttpServletRequest} object.
 * 
 * Additional parameter providers may be specified by providing a service activator file containing the name of your
 * implementations:
 * <p>
 * <code> /META-INF/services/org.ocpsoft.rewrite.servlet.spi.RequestParameterProvider<br>
 * 
 * --------------<br>
 * com.example.ParameterProviderImpl</code>
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface RequestParameterProvider extends Weighted
{
   /**
    * Return a map of parameters to be added to the current request as if they had been passed with the HTTP request
    * from the browser. This occurs during the wrapping stage of the lifecycle.
    * 
    * @see {@link RewriteLifecycleListener}
    */
   Map<String, String[]> getAdditionalParameters(ServletRequest request, ServletResponse response);
}
