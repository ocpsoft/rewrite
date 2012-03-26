package org.ocpsoft.rewrite.showcase.view;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.jboss.forge.scaffold.metawidget.persistence.PaginationHelper;
import org.jboss.forge.scaffold.metawidget.persistence.PersistenceUtil;

import org.ocpsoft.rewrite.showcase.domain.DomainEntity;

@Named
@Stateful
@RequestScoped
public class DomainBean extends PersistenceUtil
{
   private static final long serialVersionUID = 1L;
   private List<DomainEntity> list = null;
   private DomainEntity domain = new DomainEntity();
   private long id = 0;
   private PaginationHelper<DomainEntity> pagination;

   public void load()
   {
      domain = findById(DomainEntity.class, id);
   }

   public String create()
   {
      create(domain);
      return "view?faces-redirect=true&id=" + domain.getId();
   }

   public String delete()
   {
      delete(domain);
      return "list?faces-redirect=true";
   }

   public String save()
   {
      save(domain);
      return "view?faces-redirect=true&id=" + domain.getId();
   }

   public long getId()
   {
      return id;
   }

   public void setId(final long id)
   {
      this.id = id;
      if (id > 0) {
         load();
      }
   }

   public DomainEntity getDomain()
   {
      return domain;
   }

   public void setDomain(final DomainEntity domain)
   {
      this.domain = domain;
   }

   public List<DomainEntity> getList()
   {
      if (list == null) {
         list = getPagination().createPageDataModel();
      }
      return list;
   }

   public void setList(final List<DomainEntity> list)
   {
      this.list = list;
   }

   public PaginationHelper<DomainEntity> getPagination()
   {
      if (pagination == null) {
         pagination = new PaginationHelper<DomainEntity>(10) {
            @Override
            public int getItemsCount()
            {
               return count(DomainEntity.class);
            }

            @Override
            public List<DomainEntity> createPageDataModel()
            {
               return new ArrayList<DomainEntity>(findAll(DomainEntity.class, getPageFirstItem(), getPageSize()));
            }
         };
      }
      return pagination;
   }
}
