package org.ocpsoft.rewrite.security.shiro;

import javax.enterprise.context.RequestScoped;

import org.ocpsoft.rewrite.annotation.ForwardTo;
import org.ocpsoft.rewrite.annotation.PathPattern;

@RequestScoped
@PathPattern("/admin/something")
@ShiroRoleRequired("admin")
@ForwardTo("/faces/protected-page.xhtml")
public class AdminPageBean
{

}
