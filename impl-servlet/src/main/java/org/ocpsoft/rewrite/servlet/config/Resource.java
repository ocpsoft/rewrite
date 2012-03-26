/**
 * This file is part of OCPsoft SocialPM: Agile Project Management Tools (SocialPM)
 *
 * Copyright (c)2011 Lincoln Baxter, III <lincoln@ocpsoft.com> (OCPsoft)
 * Copyright (c)2011 OCPsoft.com (http://ocpsoft.com)
 * 
 * If you are developing and distributing open source applications under
 * the GNU General Public License (GPL), then you are free to re-distribute SocialPM
 * under the terms of the GPL, as follows:
 *
 * SocialPM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SocialPM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SocialPM.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * For individuals or entities who wish to use SocialPM privately, or
 * internally, the following terms do not apply:
 * 
 * For OEMs, ISVs, and VARs who wish to distribute SocialPM with their
 * products, or host their product online, OCPsoft provides flexible
 * OEM commercial licenses.
 * 
 * Optionally, Customers may choose a Commercial License. For additional
 * details, contact an OCPsoft representative (sales@ocpsoft.com)
 */
package org.ocpsoft.rewrite.servlet.config;

import java.net.MalformedURLException;

import org.ocpsoft.logging.Logger;

import org.ocpsoft.rewrite.bind.Evaluation;
import org.ocpsoft.rewrite.bind.ParameterizedPattern;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.param.Parameter;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * A {@link org.ocpsoft.rewrite.config.Condition} responsible for determining existence of resources within the web root of the servlet container.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Resource extends HttpCondition
{
   private static final Logger log = Logger.getLogger(Resource.class);

   private final ParameterizedPattern resource;

   private Resource(final String resource)
   {
      this.resource = new ParameterizedPattern(resource);

      for (Parameter<String> parameter : this.resource.getParameters().values()) {
         parameter.bindsTo(Evaluation.property(parameter.getName()));
      }
   }

   @Override
   public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      if (resource != null)
      {
         String file = resource.build(event, context);
         try {
            return event.getRequest().getServletContext().getResource(file) != null;
         }
         catch (MalformedURLException e) {
            log.debug("Invalid file format [{}]", file);
         }
      }
      return false;
   }

   /**
    * Create a new {@link org.ocpsoft.rewrite.config.Condition} that returns true if the given resource exists relative to the web root of the
    * current application.
    */
   public static Resource exists(final String resource)
   {
      return new Resource(resource);
   }

}
