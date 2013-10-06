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
package org.ocpsoft.rewrite.faces.annotation.action;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.RequestAction;
import org.ocpsoft.rewrite.faces.annotation.Deferred;
import org.ocpsoft.rewrite.faces.annotation.Phase;

@ManagedBean
@RequestScoped
@Join(path = "/action", to = "/faces/action.xhtml")
public class DeferredActionsBean
{

   private final List<String> log = new ArrayList<String>();

   @RequestAction
   @Deferred
   public void action1()
   {
      log.add("Action 1 = " + getCurrentPhase());
   }

   @RequestAction
   @Deferred(before = Phase.RENDER_RESPONSE)
   public void action2()
   {
      log.add("Action 2 = " + getCurrentPhase());
   }

   @RequestAction
   @Deferred(after = Phase.INVOKE_APPLICATION)
   public void action3()
   {
      log.add("Action 3 = " + getCurrentPhase());
   }

   private static String getCurrentPhase()
   {
      return FacesContext.getCurrentInstance().getCurrentPhaseId().toString();
   }

   public List<String> getLog()
   {
      return log;
   }

}