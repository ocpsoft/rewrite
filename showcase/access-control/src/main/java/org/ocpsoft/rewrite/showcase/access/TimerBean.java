package org.ocpsoft.rewrite.showcase.access;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.joda.time.DateTime;
import org.ocpsoft.pretty.time.PrettyTime;
import org.ocpsoft.pretty.time.units.Second;

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
