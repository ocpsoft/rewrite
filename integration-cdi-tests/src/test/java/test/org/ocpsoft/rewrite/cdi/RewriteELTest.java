/*
 * Copyright 2011 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Copyright 2011 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package test.org.ocpsoft.rewrite.cdi;

import java.io.File;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

public class RewriteELTest
{
   public static JavaArchive getRewriteAnnotationArchive()
   {
      return ShrinkWrap.create(JavaArchive.class, "rewrite-annotations.jar")
               .addAsResource(new File("../annotations-api/target/classes/org"))
               .addAsResource(new File("../annotations-impl/target/classes/org"))
               .addAsResource(new File("../annotations-impl/target/classes/META-INF"));
   }

   public static JavaArchive getRewriteFacesArchive()
   {
      return ShrinkWrap.create(JavaArchive.class, "rewrite-faces.jar")
               .addAsResource(new File("../integration-faces/target/classes/org"))
               .addAsResource(new File("../integration-faces/target/classes/META-INF"));
   }

   public static JavaArchive getRewriteCDIArchive()
   {
      return ShrinkWrap.create(JavaArchive.class, "rewrite-integration-cdi.jar")
               .addAsResource(new File("../integration-cdi/target/classes/org"))
               .addAsResource(new File("../integration-cdi/target/classes/META-INF"));
   }

}
