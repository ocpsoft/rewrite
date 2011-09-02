package com.ocpsoft.rewrite.showcase.view;
import java.util.List;
import java.util.ArrayList;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import com.ocpsoft.rewrite.showcase.domain.Domain;
import org.metawidget.forge.persistence.PaginationHelper;
import org.metawidget.forge.persistence.PersistenceUtil;
import org.jboss.seam.transaction.Transactional;
@Transactional @Named @Stateful @RequestScoped public class DomainBean extends PersistenceUtil {
  private static final long serialVersionUID=1L;
  private List<Domain> list=null;
  private Domain domain=new Domain();
  private long id=0;
  private PaginationHelper<Domain> pagination;
  public void load(){
    domain=findById(Domain.class,id);
  }
  public String create(){
    create(domain);
    return "view?faces-redirect=true&id=" + domain.getId();
  }
  public String delete(){
    delete(domain);
    return "list?faces-redirect=true";
  }
  public String save(){
    save(domain);
    return "view?faces-redirect=true&id=" + domain.getId();
  }
  public long getId(){
    return id;
  }
  public void setId(  long id){
    this.id=id;
    if (id > 0) {
      load();
    }
  }
  public Domain getDomain(){
    return domain;
  }
  public void setDomain(  Domain domain){
    this.domain=domain;
  }
  public List<Domain> getList(){
    if (list == null) {
      list=getPagination().createPageDataModel();
    }
    return list;
  }
  public void setList(  List<Domain> list){
    this.list=list;
  }
  public PaginationHelper<Domain> getPagination(){
    if (pagination == null) {
      pagination=new PaginationHelper<Domain>(10){
        @Override public int getItemsCount(){
          return count(Domain.class);
        }
        @Override public List<Domain> createPageDataModel(){
          return new ArrayList<Domain>(findAll(Domain.class,getPageFirstItem(),getPageSize()));
        }
      }
;
    }
    return pagination;
  }
}
