package org.ocpsoft.rewrite.security.shiro;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.ocpsoft.rewrite.annotation.Rule;

/**
 * Specifies that the element annotated with {@link Rule} must be constrained to users that are a member of the given
 * role or roles.
 * 
 * @author Christian Kaltepoth
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ShiroRoleRequired
{
   /**
    * The role or roles of which the user must be a member.
    */
   String[] value();
}
