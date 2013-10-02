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
package org.ocpsoft.rewrite.annotation.issue135;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.RequestAction;

/**
 * A class that only has a single RequestAction annotation but no Rewrite conditions specifying a join or condition on
 * the class level.
 * 
 * @author Christian Kaltepoth
 */
@Named
@RequestScoped
public class NoClassLevelAnnotationBean
{

   @RequestAction
   public void action()
   {
      throw new IllegalStateException("Should NOT be invoked!");
   }

}
