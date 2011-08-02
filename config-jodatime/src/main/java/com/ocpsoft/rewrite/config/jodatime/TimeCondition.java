package com.ocpsoft.rewrite.config.jodatime;

import org.joda.time.DateTime;

/**
 * A condition used to evaluate a given {@link DateTime} instant.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface TimeCondition
{
   public boolean matches(DateTime time);
}