package org.ocpsoft.rewrite.prettyfaces.outbound;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

@Named("outbound")
@RequestScoped
public class ViewHardcodedParamsBean
{

   public String hCommandLink() {
      return "view-hardcoded-params.jsf?faces-redirect=true&param=value";
   }

   public String hCommandLinkExtra() {
      return "view-hardcoded-params.jsf?faces-redirect=true&param=value&extraParam=extraValue";
   }

   public String hCommandLinkNotMapped() {
      return "view-hardcoded-params.jsf?faces-redirect=true&param=value2";
   }
}
