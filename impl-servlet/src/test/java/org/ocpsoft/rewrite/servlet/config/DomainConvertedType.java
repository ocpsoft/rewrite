package org.ocpsoft.rewrite.servlet.config;

public class DomainConvertedType
{
   private String domain;

   public DomainConvertedType(String domain)
   {
      this.domain = domain;
   }

   public String getDomain()
   {
      return domain;
   }

   public void setDomain(String domain)
   {
      this.domain = domain;
   }

   @Override
   public String toString()
   {
      return "DomainConvertedType [" + domain + "]";
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((domain == null) ? 0 : domain.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      DomainConvertedType other = (DomainConvertedType) obj;
      if (domain == null) {
         if (other.domain != null)
            return false;
      }
      else if (!domain.equals(other.domain))
         return false;
      return true;
   }

}
