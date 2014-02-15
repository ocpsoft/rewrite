/*
 * Copyright 2013 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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

import java.io.IOException;

import javax.servlet.ServletException;

import org.ocpsoft.common.pattern.Specialized;
import org.ocpsoft.common.pattern.Weighted;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * Handles responses to take after a {@link Rewrite} event has been processed. Response should be determined based on
 * the resultant state of the event.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface RewriteResultHandler extends Weighted, Specialized<Rewrite>
{
   /**
    * Perform any actions necessary to respond to state of the system after the given {@link Rewrite} event has been
    * processed.
    */
   public void handleResult(Rewrite event) throws ServletException, IOException;
}
