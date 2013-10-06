package org.ocpsoft.rewrite.faces.annotation.binding;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.faces.annotation.Deferred;
import org.ocpsoft.rewrite.faces.annotation.Phase;

@ManagedBean
@RequestScoped
@Join(path = "/binding/{value}/", to = "/faces/binding.xhtml")
public class DeferredBindingBean
{

   private final List<String> log = new ArrayList<String>();

   @Parameter("value")
   @Deferred
   private String defaultPhase;

   @Parameter("value")
   @Deferred(after = Phase.INVOKE_APPLICATION)
   private String afterInvokeApplication;

   @Parameter("value")
   @Deferred(before = Phase.RENDER_RESPONSE)
   private String beforeRenderResponse;

   public String getDefaultPhase()
   {
      return defaultPhase;
   }

   public void setDefaultPhase(String defaultPhase)
   {
      this.defaultPhase = defaultPhase;
      log.add("Wrote [" + defaultPhase + "] to [defaultPhase] during " + getCurrentPhase());
   }

   public String getAfterInvokeApplication()
   {
      return afterInvokeApplication;
   }

   public void setAfterInvokeApplication(String afterInvokeApplication)
   {
      this.afterInvokeApplication = afterInvokeApplication;
      log.add("Wrote [" + afterInvokeApplication + "] to [afterInvokeApplication] during " + getCurrentPhase());
   }

   public String getBeforeRenderResponse()
   {
      return beforeRenderResponse;
   }

   public void setBeforeRenderResponse(String beforeRenderResponse)
   {
      this.beforeRenderResponse = beforeRenderResponse;
      log.add("Wrote [" + beforeRenderResponse + "] to [beforeRenderResponse] during " + getCurrentPhase());
   }

   public List<String> getLog()
   {
      return log;
   }

   private static String getCurrentPhase()
   {
      return FacesContext.getCurrentInstance().getCurrentPhaseId().toString();
   }

}
