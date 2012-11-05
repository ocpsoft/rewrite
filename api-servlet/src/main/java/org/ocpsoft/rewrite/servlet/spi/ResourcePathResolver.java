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

import org.ocpsoft.common.pattern.Weighted;

/**
 * SPI which is responsible to resolve the server-side target resource assigned to a given {@link Class}.
 * 
 * @author Christian Kaltepoth
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ResourcePathResolver extends Weighted
{
   /**
    * Resolve the server side target resource. This should return the path to the resource as it would be requested from
    * the client. E.g.: "/pages/index.xhtml".
    */
   String resolveFrom(Class<?> clazz);
}
