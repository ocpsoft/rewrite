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
package org.ocpsoft.rewrite.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.servlet.event.ServletRewrite;
import org.ocpsoft.rewrite.spi.RewriteProvider;

/**
 * Abstract implementation of {@link org.ocpsoft.rewrite.spi.RewriteProvider} which only handles {@link org.ocpsoft.rewrite.servlet.event.ServletRewrite} events.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public abstract class ServletRewriteProvider<T extends ServletRewrite<? extends ServletRequest, ? extends ServletResponse>>
         implements RewriteProvider<ServletContext, Rewrite>
{
   @Override
   public boolean handles(final Rewrite payload)
   {
      return payload instanceof ServletRewrite;
   }
}
