package com.ocpsoft.rewrite.faces;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.servlet.ServletContext;

import com.ocpsoft.rewrite.config.Configuration;
import com.ocpsoft.rewrite.config.ConfigurationBuilder;
import com.ocpsoft.rewrite.config.Operation;
import com.ocpsoft.rewrite.context.EvaluationContext;
import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.faces.config.PhaseOperation;
import com.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import com.ocpsoft.rewrite.servlet.config.Path;
import com.ocpsoft.rewrite.servlet.config.SendStatus;

public class PhaseOperationTestConfigurationProvider extends HttpConfigurationProvider
{
   @Override
   public Configuration getConfiguration(ServletContext context)
   {
      return ConfigurationBuilder.begin()

               .defineRule()
               .when(Path.matches("/operation"))
               .perform(PhaseOperation.enqueue(new Operation() {
                  @Override
                  public void perform(Rewrite event, EvaluationContext context)
                  {
                     if (PhaseId.RESTORE_VIEW.equals(FacesContext.getCurrentInstance().getCurrentPhaseId()))
                        SendStatus.code(203).perform(event, context);
                     else
                        SendStatus.code(503).perform(event, context);
                  }
               }).after(PhaseId.RESTORE_VIEW));
   }

   @Override
   public int priority()
   {
      return 0;
   }
}
