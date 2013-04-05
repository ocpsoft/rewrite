package org.ocpsoft.rewrite.faces;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.faces.config.PhaseBinding;
import org.ocpsoft.rewrite.faces.config.PhaseOperation;
import org.ocpsoft.rewrite.param.Validator;
import org.ocpsoft.rewrite.servlet.config.DispatchType;
import org.ocpsoft.rewrite.servlet.config.Forward;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.HttpOperation;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.Response;
import org.ocpsoft.rewrite.servlet.config.SendStatus;
import org.ocpsoft.rewrite.servlet.config.bind.RequestBinding;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

public class PhaseOperationTestConfigurationProvider extends HttpConfigurationProvider
{
   @Override
   public Configuration getConfiguration(ServletContext context)
   {
      Validator<String> validator = new Validator<String>() {
         @Override
         public boolean isValid(Rewrite event, EvaluationContext context, String value)
         {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            if (facesContext == null)
            {
               throw new IllegalStateException("FacesContext should be active.");
            }
            return "true".equals(value);
         }
      };
      return ConfigurationBuilder
               .begin()

               /*
                * Perform an operation after restore view.
                */
               .addRule()
               .when(Path.matches("/empty.xhtml").and(DispatchType.isRequest()))
               .perform(PhaseOperation.enqueue(new Operation() {

                  @Override
                  public void perform(Rewrite event, EvaluationContext context)
                  {
                     if (PhaseId.RESTORE_VIEW.equals(FacesContext.getCurrentInstance().getCurrentPhaseId()))
                        SendStatus.code(203).perform(event, context);
                     else
                        SendStatus.code(503).perform(event, context);
                  }
               }).after(PhaseId.RESTORE_VIEW))

               /*
                * Perform before Render Response
                */
               .addRule()
               .when(Path.matches("/render_response").and(DispatchType.isRequest()))
               .perform(Forward.to("/empty.xhtml").and(PhaseOperation.enqueue(new Operation() {
                  @Override
                  public void perform(Rewrite event, EvaluationContext context)
                  {
                     if (PhaseId.RENDER_RESPONSE.equals(FacesContext.getCurrentInstance().getCurrentPhaseId()))
                        SendStatus.code(204).perform(event, context);
                     else
                        SendStatus.code(504).perform(event, context);
                  }
               }).before(PhaseId.RENDER_RESPONSE)))

               /*
                * PhaseBinding deferral
                */
               .addRule()
               .when(Path.matches("/binding/{value}").and(DispatchType.isRequest()))
               .perform(Forward.to("/empty.xhtml")

                        .and(PhaseOperation.enqueue(new HttpOperation() {
                           @Override
                           public void performHttp(HttpServletRewrite event, EvaluationContext context)
                           {
                              String value = event.getRequest().getParameter("v");
                              if (value != null)
                                 SendStatus.code(505).perform(event, context);
                           }
                        }).before(PhaseId.RESTORE_VIEW))

                        .and(PhaseOperation.enqueue(new HttpOperation() {
                           @Override
                           public void performHttp(HttpServletRewrite event, EvaluationContext context)
                           {
                              HttpServletRequest request = event.getRequest();
                              String value = request.getParameter("v");
                              if (value != null)
                                 Response.addHeader("Value", value).and(SendStatus.code(205)).perform(event, context);
                              else
                                 SendStatus.code(505).perform(event, context);
                           }
                        }).before(PhaseId.RENDER_RESPONSE)))
               .where("value").bindsTo(PhaseBinding.to(RequestBinding.parameter("v")).after(PhaseId.RESTORE_VIEW))

               /*
                * Defer validation with binding.
                */
               .addRule()
               .when(Path.matches("/defer_validation/{value}").and(DispatchType.isRequest()))
               .perform(Forward.to("/empty.xhtml"))
               .where("value").bindsTo(PhaseBinding.to(RequestBinding.parameter("v")).validatedBy(validator).after(
                        PhaseId.RESTORE_VIEW))

               /*
                * Perform eager validation.
                */
               .addRule()
               .when(Path.matches("/eager_validation/{value}"))
               .perform(Forward.to("/empty.xhtml"))
               .where("value").validatedBy(validator);
   }

   @Override
   public int priority()
   {
      return 0;
   }
}
