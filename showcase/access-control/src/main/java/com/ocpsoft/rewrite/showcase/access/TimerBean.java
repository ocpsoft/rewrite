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
package com.ocpsoft.rewrite.showcase.access;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.joda.time.DateTime;

import com.ocpsoft.pretty.time.PrettyTime;
import com.ocpsoft.pretty.time.units.Second;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@Named
@SessionScoped
public class TimerBean implements Serializable
{
   private static final long serialVersionUID = 4702776314232622624L;

   public String getSecondsUntilGranted()
   {
      DateTime time = new DateTime();
      int secondsRemain = 60 - time.getSecondOfMinute();
      DateTime deniedTime = time.plusSeconds(secondsRemain);
      PrettyTime prettyTime = new PrettyTime();
      prettyTime.setUnits(new Second(prettyTime.getLocale()));
      return prettyTime.format(deniedTime.toDate());
   }

   public String getSecondsUntilDenied()
   {
      DateTime time = new DateTime();
      int secondsRemain = 30 - time.getSecondOfMinute();
      DateTime grantedTime = time.plusSeconds(secondsRemain);
      PrettyTime prettyTime = new PrettyTime();
      prettyTime.setUnits(new Second(prettyTime.getLocale()));
      return prettyTime.format(grantedTime.toDate());
   }
}
