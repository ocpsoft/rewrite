package org.ocpsoft.rewrite.faces;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.RequestAction;
import org.ocpsoft.rewrite.faces.annotation.Deferred;
import org.ocpsoft.rewrite.faces.annotation.Phase;
import org.ocpsoft.rewrite.faces.navigate.Navigate;


@ManagedBean
@RequestScoped
@Join(path = "/navigate", to = "/faces/navigate-before-restoreview.xhtml")
public class NavigateBeforeRestoreViewBean
{
   @RequestAction
   @Deferred(before=Phase.RESTORE_VIEW)
   public Navigate action()
   {
      return Navigate.to(NavigateBeforeRestoreViewBean.class);
   }
}
