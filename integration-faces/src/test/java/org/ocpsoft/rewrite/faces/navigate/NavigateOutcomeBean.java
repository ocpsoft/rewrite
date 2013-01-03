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
package org.ocpsoft.rewrite.faces.navigate;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean
@RequestScoped
public class NavigateOutcomeBean
{

   private String query;

   public Navigate redirectSimpleString()
   {
      return Navigate.to("/navigate.xhtml")
               .with("q", "foo");
   }

   public Navigate redirectWithSpace()
   {
      return Navigate.to("/navigate.xhtml")
               .with("q", "foo bar");
   }

   public Navigate redirectWithAmpersand()
   {
      return Navigate.to("/navigate.xhtml")
               .with("q", "foo&bar");
   }

   public Navigate redirectWithEquals()
   {
      return Navigate.to("/navigate.xhtml")
               .with("q", "foo=bar");
   }

   public Navigate redirectWithChinese()
   {
      return Navigate.to("/navigate.xhtml")
               .with("q", "\u6f22\u5b57");
   }

   public Navigate navigateNoParams()
   {
      return Navigate.to("/navigate.xhtml")
               .withoutRedirect();
   }

   public String getQuery()
   {
      return query;
   }

   public void setQuery(String query)
   {
      this.query = query;
   }

}