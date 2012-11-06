package org.ocpsoft.rewrite.prettyfaces;

import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

public final class PFUtil
{
   private static final String REWRITE_OCCURRED_KEY = InboundRewriteRuleAdaptor.class.getName() + "_REWRITE_OCCURRED";

   public static void setRewriteOccurred(Rewrite event)
   {
      ((HttpServletRewrite) event).getRequest().setAttribute(REWRITE_OCCURRED_KEY, true);
   }

   public static boolean isRewritingEnabled(Rewrite event)
   {
      HttpServletRequest request = ((HttpServletRewrite) event).getRequest();
      Object rewriteOccurred = request.getAttribute(REWRITE_OCCURRED_KEY);
      return rewriteOccurred == null && isMappingEnabled(event);
   }

   public static boolean isMappingEnabled(Rewrite event)
   {
      HttpServletRequest request = ((HttpServletRewrite) event).getRequest();
      Object mappingForwardOccurred = request.getAttribute(UrlMappingRuleAdaptor.REWRITE_MAPPING_ID_KEY);
      return mappingForwardOccurred == null;
   }
}
