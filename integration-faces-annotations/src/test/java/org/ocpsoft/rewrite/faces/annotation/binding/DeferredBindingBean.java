package org.ocpsoft.rewrite.faces.annotation.binding;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.ParameterBinding;
import org.ocpsoft.rewrite.faces.annotation.Deferred;
import org.ocpsoft.rewrite.faces.annotation.Phase;

@ManagedBean
@RequestScoped
@Join(path = "/binding/{value}/", to = "/faces/binding.xhtml")
public class DeferredBindingBean
{

   private final List<String> log = new ArrayList<String>();

   @ParameterBinding("value")
   @Deferred
   private String defaultPhase;

   @ParameterBinding("value")
   @Deferred(after = Phase.INVOKE_APPLICATION)
   private String afterInvokeApplication;

   @ParameterBinding("value")
   @Deferred(before = Phase.RENDER_RESPONSE)
   private String beforeRenderResponse;

   public String getDefaultPhase()
   {
      return defaultPhase;
   }

   public void setDefaultPhase(String defaultPhase)
   {
      this.defaultPhase = defaultPhase;
      log.add("Wrote [" + defaultPhase + "] to [defaultPhase] during [" + getCurrentPhase() + "]");
   }

   public String getAfterInvokeApplication()
   {
      return afterInvokeApplication;
   }

   public void setAfterInvokeApplication(String afterInvokeApplication)
   {
      this.afterInvokeApplication = afterInvokeApplication;
      log.add("Wrote [" + afterInvokeApplication + "] to [afterInvokeApplication] during [" + getCurrentPhase() + "]");
   }

   public String getBeforeRenderResponse()
   {
      return beforeRenderResponse;
   }

   public void setBeforeRenderResponse(String beforeRenderResponse)
   {
      this.beforeRenderResponse = beforeRenderResponse;
      log.add("Wrote [" + beforeRenderResponse + "] to [beforeRenderResponse] during [" + getCurrentPhase() + "]");
   }

   public List<String> getLog()
   {
      return log;
   }

   private static String getCurrentPhase()
   {
      String s = FacesContext.getCurrentInstance().getCurrentPhaseId().toString();
      if (s != null && s.length() > 5) {
         // remove the trailing ordinal value of phase
         return s.substring(0, s.length() - 2);
      }
      return s;
   }

}
