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
package com.ocpsoft.rewrite.faces.config;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.PhaseId;
import javax.servlet.http.HttpServletRequest;

import com.ocpsoft.logging.Logger;
import com.ocpsoft.rewrite.bind.Binding;
import com.ocpsoft.rewrite.bind.El.ElProperty;
import com.ocpsoft.rewrite.context.EvaluationContext;
import com.ocpsoft.rewrite.event.Rewrite;

/**
 * Wraps & holds a param binding till before or after a given JavaServer Faces {@link PhaseId}
 * 
 * @author <a href="mailto:fabmars@gmail.com">Fabien Marsaud</a>
 */
public class PhaseBinding implements Binding
{
   private static final String DEFERRED_OPERATIONS = PhaseBinding.class + "_QUEUED";
   private static final Logger log = Logger.getLogger(PhaseBinding.class);
   
   private final Binding binding;
   private PhaseId beforePhase; //it's wrong to perform the binding before PhaseId.RESTORE_VIEW
   private PhaseId afterPhase; //it's useless to perform the binding after PhaseId.RENDER_RESPONSE


   private PhaseBinding(Binding binding)
   {
      if (binding == null)
      {
         throw new NullPointerException("binding");
      }
      else if (!(binding instanceof ElProperty))
      {
         log.warn("binding ought to be an ElProperty; instead it is a [" + binding.getClass().getName() + "].");
      }
      this.binding = binding;
   }
   
   /**
    * Wraps the Binding into a new PhaseInjection, so as to have it performed duriung the JSF lifecycle 
    * By default, the original binding will be performed after {@link PhaseId#RESTORE_VIEW}
    */
   public static PhaseBinding withhold(final Binding binding)
   {
      return (PhaseBinding) new PhaseBinding(binding).tillAfter(PhaseId.RESTORE_VIEW);
   }

   public PhaseId getBeforePhase()
   {
      return beforePhase;
   }

   public PhaseId getAfterPhase()
   {
      return afterPhase;
   }

   public Binding tillBefore(final PhaseId phase)
   {
      this.beforePhase = phase;
      return this;
   }

   public Binding tillAfter(final PhaseId phase)
   {
      this.afterPhase = phase;
      return this;
   }

   @Override
   public Object retrieve(Rewrite event, EvaluationContext context)
   {
      return null;
   }

   @Override
   public Object convert(Rewrite event, EvaluationContext context, Object value)
   {
      return binding.convert(event, context, value);
   }

   @Override
   public boolean validates(Rewrite event, EvaluationContext context, Object value)
   {
      return binding.validates(event, context, value);
   }

   @Override
   public Object submit(Rewrite event, EvaluationContext context, Object value)
   {
      return binding.submit(event, context, value);
   }

   @Override
   public boolean supportsRetrieval()
   {
      return false;
   }

   @Override
   public boolean supportsSubmission()
   {
      return true;
   }
   
   @SuppressWarnings("unchecked")
   public static List<DeferredOperation> getDeferredOperations(final HttpServletRequest request)
   {
      List<DeferredOperation> operations = (List<DeferredOperation>) request.getAttribute(DEFERRED_OPERATIONS);
      if (operations == null)
      {
         operations = new ArrayList<DeferredOperation>();
         request.setAttribute(DEFERRED_OPERATIONS, operations);
      }
      return operations;
   }

   public static void removeDefferredOperations(final HttpServletRequest request)
   {
      request.removeAttribute(DEFERRED_OPERATIONS);
   }
}
