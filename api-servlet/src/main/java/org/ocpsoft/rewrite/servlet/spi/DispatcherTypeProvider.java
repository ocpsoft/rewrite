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
import javax.servlet.ServletRequest;

import org.ocpsoft.common.pattern.Weighted;
import org.ocpsoft.rewrite.servlet.DispatcherType;

/**
 * SPI for obtaining the {@link DispatcherType} of a request.
 * 
 * @author Christian Kaltepoth
 */
public interface DispatcherTypeProvider extends Weighted
{

   /**
    * Returns the {@link DispatcherType} for the given request. MEthod must return <code>null</code> if the provider is
    * unable to reason about the dispatch type.
    */
   DispatcherType getDispatcherType(ServletRequest request, ServletContext context);

}
