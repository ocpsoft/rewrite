package org.ocpsoft.rewrite.annotation.action;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.RequestAction;

@Named
@RequestScoped
@Join(path = "/rewritten-url", to = "/some-page.jsp")
public class InboundActionBean
{

   @RequestAction
   public void action()
   {
      throw new IllegalStateException(
               "This action should not be invoked because we will not " +
                        "send a request to [/rewritten-url] in the test.");
   }

}
