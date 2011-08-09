/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.ocpsoft.rewrite.faces;

import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ConversationExceptionHandler extends ExceptionHandlerWrapper
{

   private final ExceptionHandler wrapped;

   public ConversationExceptionHandler(ExceptionHandler wrapped)
   {
      this.wrapped = wrapped;
   }

   @Override
   public ExceptionHandler getWrapped()
   {
      return wrapped;
   }

   @Override
   public void handle() throws FacesException
   {
      for (Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator(); i.hasNext();)
      {
         ExceptionQueuedEvent event = i.next();
         ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();
         Throwable t = context.getException();
         if (t instanceof IllegalStateException && t.getMessage().matches("Context is already active"))
         {
            i.remove();
         }
      }
      getWrapped().handle();
   }

}
