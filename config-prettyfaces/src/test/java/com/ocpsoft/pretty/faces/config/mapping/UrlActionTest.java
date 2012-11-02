/*
 * Copyright 2010 Lincoln Baxter, III
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
package com.ocpsoft.pretty.faces.config.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;

/**
 * @author lb3
 */
public class UrlActionTest
{

   @Test
   public void testUrlActionSetsDefaultPhaseIdAndEmptyAction()
   {
      UrlAction urlAction = new UrlAction();
      assertNull(urlAction.getAction());
      assertEquals(PhaseId.RESTORE_VIEW, urlAction.getPhaseId());
   }

   @Test
   public void testUrlActionStringSetsActionMethod()
   {
      String action = "#{this.is.my.action}";
      UrlAction urlAction = new UrlAction(action);
      assertEquals(action, urlAction.getAction().getELExpression());
   }

   @Test
   public void testUrlActionStringPhaseId()
   {
      String action = "#{this.is.my.action}";
      UrlAction urlAction = new UrlAction(action, PhaseId.APPLY_REQUEST_VALUES);
      assertEquals(action, urlAction.getAction().getELExpression());
      assertEquals(PhaseId.APPLY_REQUEST_VALUES, urlAction.getPhaseId());
   }

}
