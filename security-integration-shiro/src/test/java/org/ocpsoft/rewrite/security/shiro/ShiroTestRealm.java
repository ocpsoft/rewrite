package org.ocpsoft.rewrite.security.shiro;

import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.realm.SimpleAccountRealm;

public class ShiroTestRealm extends SimpleAccountRealm
{

   public ShiroTestRealm()
   {

      // an admin user
      SimpleAccount ck = new SimpleAccount("ck", "secret", getName());
      ck.addRole("admin");
      add(ck);

      // some other user
      SimpleAccount somebody = new SimpleAccount("somebody", "secret", getName());
      add(somebody);

   }

}
