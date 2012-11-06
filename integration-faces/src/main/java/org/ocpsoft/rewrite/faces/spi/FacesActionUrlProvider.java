/*
 * Copyright 2012 Lincoln Baxter, III
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
package org.ocpsoft.rewrite.faces.spi;

import javax.faces.context.FacesContext;

import org.ocpsoft.common.pattern.Weighted;

/**
 * SPI for integrating with Faces Action URL generation.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface FacesActionUrlProvider extends Weighted
{
   /**
    * Get the Action URL for the given {@link FacesContext} and view ID.
    */
   String getActionURL(FacesContext context, String viewId);
}
