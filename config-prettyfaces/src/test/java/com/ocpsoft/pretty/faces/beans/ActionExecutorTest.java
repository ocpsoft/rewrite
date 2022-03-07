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
package com.ocpsoft.pretty.faces.beans;

import org.junit.Test;

import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;
import com.ocpsoft.pretty.faces.config.mapping.UrlAction;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author lb3
 */
public class ActionExecutorTest
{
   ActionExecutor executor = new ActionExecutor();

   @Test
   public void testShouldExecuteOnPostback()
   {
      UrlAction action = new UrlAction("action", PhaseId.ANY_PHASE);
      action.setOnPostback(true);
      assertThat(executor.shouldExecute(action, javax.faces.event.PhaseId.RESTORE_VIEW, true)).isTrue();
   }

   @Test
   public void testShouldExecuteOnNonPostback()
   {
      UrlAction action = new UrlAction("action", PhaseId.ANY_PHASE);
      action.setOnPostback(true);
      assertThat(executor.shouldExecute(action, javax.faces.event.PhaseId.RESTORE_VIEW, false)).isTrue();
   }

   @Test
   public void testShouldExecuteOnPhaseNonPostback()
   {
      UrlAction action = new UrlAction("action", PhaseId.RESTORE_VIEW);
      action.setOnPostback(true);
      assertThat(executor.shouldExecute(action, javax.faces.event.PhaseId.RESTORE_VIEW, false)).isTrue();
   }

   @Test
   public void testShouldNotExecuteOnWrongPhaseNonPostback()
   {
      UrlAction action = new UrlAction("action", PhaseId.RESTORE_VIEW);
      action.setOnPostback(true);
      assertThat(executor.shouldExecute(action, javax.faces.event.PhaseId.APPLY_REQUEST_VALUES, false)).isFalse();
   }

   @Test
   public void testShouldExecuteOnPhasePostback()
   {
      UrlAction action = new UrlAction("action", PhaseId.RESTORE_VIEW);
      action.setOnPostback(true);
      assertThat(executor.shouldExecute(action, javax.faces.event.PhaseId.RESTORE_VIEW, true)).isTrue();
   }

   @Test
   public void testShouldNotExecuteOnWrongPhasePostback()
   {
      UrlAction action = new UrlAction("action", PhaseId.RESTORE_VIEW);
      action.setOnPostback(true);
      assertThat(executor.shouldExecute(action, javax.faces.event.PhaseId.APPLY_REQUEST_VALUES, true)).isFalse();
   }

   @Test
   public void testShouldNotExecuteOnPostbackFalse()
   {
      UrlAction action = new UrlAction("action", PhaseId.ANY_PHASE);
      action.setOnPostback(false);
      assertThat(executor.shouldExecute(action, javax.faces.event.PhaseId.RESTORE_VIEW, true)).isFalse();
   }

   @Test
   public void testShouldExecuteOnPostbackFalseWhenNotPostback()
   {
      UrlAction action = new UrlAction("action", PhaseId.ANY_PHASE);
      action.setOnPostback(false);
      assertThat(executor.shouldExecute(action, javax.faces.event.PhaseId.RESTORE_VIEW, false)).isTrue();
   }

}
