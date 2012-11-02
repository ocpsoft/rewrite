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

package com.ocpsoft.pretty.faces.util;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * @author Lincoln Baxter, III <lincoln@ocpsoft.com>
 */
public class FacesMessagesUtils
{
    protected static final String token = "com.ocpsoft.pretty.SAVED_FACES_MESSAGES";

    @SuppressWarnings("unchecked")
    public int saveMessages(final FacesContext facesContext, final Map<String, Object> destination)
    {
        int savedCount = 0;
        if (facesContext != null)
        {
            Set<FacesMessageWrapper> messages = new LinkedHashSet<FacesMessageWrapper>();
            for (Iterator<FacesMessage> iter = facesContext.getMessages(null); iter.hasNext();)
            {
                messages.add(new FacesMessageWrapper(iter.next()));
            }

            if (messages.size() > 0)
            {
                Set<FacesMessageWrapper> existingMessages = (LinkedHashSet<FacesMessageWrapper>) destination.get(token);
                if (existingMessages != null)
                {
                    existingMessages.addAll(messages);
                }
                else
                {
                    destination.put(token, messages);
                }
                savedCount = messages.size();
            }
        }
        return savedCount;
    }

    @SuppressWarnings("unchecked")
    public int restoreMessages(final FacesContext facesContext, final Map<String, Object> source)
    {
        int restoredCount = 0;
        if (facesContext != null)
        {
            // get save messages from the session
            Set<FacesMessageWrapper> messages = (LinkedHashSet<FacesMessageWrapper>) source.remove(token);

            // nothing to do
            if (messages == null)
            {
                return 0;
            }

            // build set of message currently in the FacesContext
            Set<FacesMessageWrapper> exitingMessages = new LinkedHashSet<FacesMessagesUtils.FacesMessageWrapper>();
            for (Iterator<FacesMessage> iter = facesContext.getMessages(null); iter.hasNext();)
            {
               exitingMessages.add(new FacesMessageWrapper(iter.next()));
            }

            // restore all messages not already in the FacesContext
            for (FacesMessageWrapper message : messages)
            {
               if (!exitingMessages.contains(message))
               {
                  facesContext.addMessage(null, message.getWrapped());
                  restoredCount++;
               }
            }

        }
        return restoredCount;
    }

    private static class FacesMessageWrapper implements Serializable {

      private static final long serialVersionUID = 1L;

      private final FacesMessage wrapped;

       public FacesMessageWrapper(FacesMessage wrapped)
       {
          this.wrapped = wrapped;
       }

       @Override
       public int hashCode()
       {
          final int prime = 31;
          int result = 1;
          result = prime * result + ((wrapped.getSeverity() == null) ? 0 : wrapped.getSeverity().hashCode());
          result = prime * result + ((wrapped.getSummary() == null) ? 0 : wrapped.getSummary().hashCode());
          result = prime * result + ((wrapped.getDetail() == null) ? 0 : wrapped.getDetail().hashCode());
          return result;
       }

       @Override
       public boolean equals(Object obj)
       {
          if (this == obj)
          {
             return true;
          }
          if (obj == null)
          {
             return false;
          }
          if (getClass() != obj.getClass())
          {
             return false;
          }
          FacesMessageWrapper other = (FacesMessageWrapper) obj;
          if (wrapped.getSeverity() == null)
          {
             if (other.wrapped.getSeverity() != null)
             {
                return false;
             }
          }
          else if (!wrapped.getSeverity().equals(other.wrapped.getSeverity()))
          {
             return false;
          }
          if (wrapped.getSummary() == null)
          {
             if (other.wrapped.getSummary() != null)
             {
                return false;
             }
          }
          else if (!wrapped.getSummary().equals(other.wrapped.getSummary()))
          {
             return false;
          }
          if (wrapped.getDetail() == null)
          {
             if (other.wrapped.getDetail() != null)
             {
                return false;
             }
          }
          else if (!wrapped.getDetail().equals(other.wrapped.getDetail()))
          {
             return false;
          }
          return true;
       }

      public FacesMessage getWrapped()
      {
         return wrapped;
      }

    }

}