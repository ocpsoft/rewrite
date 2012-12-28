package org.ocpsoft.rewrite.faces;

import javax.faces.event.PhaseId;
import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.faces.config.PhaseOperation;
import org.ocpsoft.rewrite.servlet.config.DispatchType;
import org.ocpsoft.rewrite.servlet.config.Forward;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.Redirect;
import org.ocpsoft.rewrite.servlet.config.Response;
import org.ocpsoft.rewrite.servlet.config.SendStatus;

public class DeferredOperationTestConfigurationProvider extends HttpConfigurationProvider
{
   @Override
   public Configuration getConfiguration(ServletContext context)
   {
      return ConfigurationBuilder
               .begin()

               /*
                * Perform an operation after restore view.
                */
               .addRule()
               .when(Path.matches("/redirect").and(DispatchType.isRequest()))
               .perform(PhaseOperation.enqueue(Redirect.temporary(context.getContextPath() + "/redirect_result"))
                        .after(PhaseId.RESTORE_VIEW).and(Forward.to("/empty.xhtml")
                        ))

               .addRule()
               .when(Path.matches("/redirect_result"))
               .perform(SendStatus.code(201))

               /*
                * Perform before Render Response
                */
               .addRule()
               .when(Path.matches("/forward").and(DispatchType.isRequest()))
               .perform(PhaseOperation.enqueue(Forward.to("/forward_result")).before(PhaseId.RENDER_RESPONSE)
                        .and(Forward.to("/empty.xhtml")))

               .addRule()
               .when(Path.matches("/forward_result").and(DispatchType.isForward()))
               .perform(Response.addHeader("Forward-Occurred", "True").and(SendStatus.code(202)));
   }

   @Override
   public int priority()
   {
      return 0;
   }
}
