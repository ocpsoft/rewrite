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

package com.ocpsoft.pretty.faces.rewrite;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ocpsoft.pretty.faces.config.rewrite.RewriteRule;

/**
 * Perform a rewrite operation on a given URL, utilizing any necessary information from the given {@link RewriteRule}
 * configuration object from which the processor was invoked.
 * 
 * @author Lincoln Baxter, III <lincoln@ocpsoft.com>
 */
public interface Processor
{
   /**
    * Process an inbound URL Rewrite request. This takes place when the request first comes in to the server and passes
    * through the rewrite rule processor.
    */
   String processInbound(HttpServletRequest request, HttpServletResponse response, RewriteRule rule, String url);

   /**
    * Process an outbound URL Rewrite request. This takes place when a URL is passed in to
    * {@link HttpServletResponse#encodeRedirectURL(String)}, and since most frameworks ensure the call to
    * 'encodeRedirectUrl()' occurs automatically, can be assumed to occur whenever a URL would be rendered to HTML
    * output.
    */
   String processOutbound(HttpServletRequest request, HttpServletResponse response, RewriteRule rule, String url);
}
