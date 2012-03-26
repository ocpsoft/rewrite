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

import javax.faces.event.PhaseId;

import org.ocpsoft.logging.Logger;

import com.ocpsoft.rewrite.bind.Binding;
import com.ocpsoft.rewrite.bind.El.ElProperty;
import com.ocpsoft.rewrite.context.EvaluationContext;
import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * Wraps & holds a param binding till before or after a given JavaServer Faces {@link PhaseId}
 * 
 * @author <a href="mailto:fabmars@gmail.com">Fabien Marsaud</a>
 */
public class PhaseInjection extends PhaseOperation<PhaseInjection> implements Binding
{
   private static final Logger log = Logger.getLogger(PhaseInjection.class);

   private final Binding binding;

   private PhaseInjection(Binding binding)
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

   @Override
   public int priority()
   {
      return -5;
   }

   @Override
   public void performOperation(HttpServletRewrite event, EvaluationContext context)
   {
      // TODO implement
   }

   /**
    * Wraps the Binding into a new PhaseInjection, so as to have it performed duriung the JSF lifecycle By default, the
    * original binding will be performed after {@link PhaseId#RESTORE_VIEW}
    */
   public static PhaseInjection withhold(final Binding binding)
   {
      return (PhaseInjection) new PhaseInjection(binding).after(PhaseId.RESTORE_VIEW);
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
}
