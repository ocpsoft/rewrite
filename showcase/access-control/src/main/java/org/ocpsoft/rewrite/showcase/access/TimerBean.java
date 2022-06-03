package org.ocpsoft.rewrite.showcase.access;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.units.Second;

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
      LocalDateTime time = LocalDateTime.now();
      int secondsRemain = 60 - time.getSecond();
      LocalDateTime deniedTime = time.plusSeconds(secondsRemain);
      PrettyTime prettyTime = new PrettyTime();
      prettyTime.setUnits(new Second());
      return prettyTime.format(deniedTime);
   }

   public String getSecondsUntilDenied()
   {
      LocalDateTime time = LocalDateTime.now();
      int secondsRemain = 30 - time.getSecond();
      LocalDateTime grantedTime = time.plusSeconds(secondsRemain);
      PrettyTime prettyTime = new PrettyTime();
      prettyTime.setUnits(new Second());
      return prettyTime.format(grantedTime);
   }
}
