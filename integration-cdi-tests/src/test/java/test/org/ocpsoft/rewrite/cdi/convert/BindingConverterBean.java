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
package test.org.ocpsoft.rewrite.cdi.convert;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Convert;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.faces.annotation.Deferred;

@Named
@RequestScoped
@Join(path = "/convert/{value}/", to = "/faces/convert.xhtml")
public class BindingConverterBean
{

   @Convert(id = "AdvancedStringConverter")
   @Deferred
   @Parameter("value")
   private AdvancedString byId;

   @Convert
   @Deferred
   @Parameter("value")
   private AdvancedString byType;

   public AdvancedString getById()
   {
      return byId;
   }

   public void setById(AdvancedString byId)
   {
      this.byId = byId;
   }

   public AdvancedString getByType()
   {
      return byType;
   }

   public void setByType(AdvancedString byType)
   {
      this.byType = byType;
   }

}
