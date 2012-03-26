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
package org.ocpsoft.rewrite.servlet.http.event;

import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ocpsoft.rewrite.servlet.event.InboundServletRewrite;

/**
 * Rewrite event for an {@link HttpServletRequest}, {@link HttpServletResponse} lifecycle.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface HttpInboundServletRewrite extends InboundServletRewrite<HttpServletRequest, HttpServletResponse>,
         HttpServletRewrite
{
   /**
    * If the {@link HttpServletResponse} is not already committed, issue a Temporary Redirect (302) and call
    * {@link #abort()}. This location must be include {@link #getContextPath()} if attempting to redirect within the
    * current {@link ServletContext}
    * <p>
    * This method commits the response, after which no more information can be written and the response cannot be
    * modified.
    */
   public void redirectTemporary(final String location);

   /**
    * If the {@link HttpServletResponse} is not already committed, issue a Permanent Redirect (301) and call
    * {@link #abort()}. This location must be include {@link #getContextPath()} if attempting to redirect within the
    * current {@link ServletContext}
    * <p>
    * This method commits the response, after which no more information can be written and the response cannot be
    * modified.
    */
   public void redirectPermanent(final String location);

   /**
    * If the {@link HttpServletResponse} is not already committed, send an HTTP status code, flush the
    * {@link OutputStream} buffer, and and call {@link #abort()}.
    * <p>
    * This method commits the response, after which no more information can be written and the response cannot be
    * modified.
    */
   public void sendStatusCode(int code);

   /**
    * If the {@link HttpServletResponse} is not already committed, send an HTTP status code, flush the
    * {@link OutputStream} buffer, and and call {@link #abort()}. Provide the given message to the browser as
    * [text/html].
    * <p>
    * This method commits the response, after which no more information can be written and the response cannot be
    * modified.
    */
   public void sendStatusCode(int code, String message);

   /**
    * If the {@link HttpServletResponse} is not already committed, send an HTTP status code and and call
    * {@link #abort()}.
    * <p>
    * This method commits the response, after which no more information can be written and the response cannot be
    * modified.
    */
   public void sendErrorCode(int code);

   /**
    * If the {@link HttpServletResponse} is not already committed, send an HTTP status code and and call
    * {@link #abort()}. Provide the given message to the browser.
    * <p>
    * This method commits the response, after which no more information can be written and the response cannot be
    * modified.
    */
   public void sendErrorCode(int code, String message);
}
