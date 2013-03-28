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
package org.ocpsoft.rewrite.servlet.config.response;

import java.io.OutputStream;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * A wrapper for the {@link HttpServletResponse} content {@link OutputStream}. This should be used in favor of
 * {@link ResponseContentInterceptor} whenever possible to avoid buffering the entire response in memory.
 * 
 * <p>
 * <b>NOTICE:</b> Stream wrapping must be configured before any content or headers have been written to the
 * {@link ServletResponse}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ResponseStreamWrapper
{
   /**
    * Wrap the {@link OutputStream} for the current {@link HttpServletRewrite}.
    */
   OutputStream wrap(HttpServletRewrite rewrite, OutputStream outputStream);

   /**
    * Perform necessary final operations on the {@link OutputStream} for the current {@link HttpServletRewrite}.
    */
   void finish(HttpServletRewrite rewrite);
}
