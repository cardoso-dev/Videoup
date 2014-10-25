/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.utils;

import com.videoup.entities.VideoupBaseEntity;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Pedro
 */
public class GeneralDAO{
    
    private Session ses;
    private Transaction trx;
    private String error;
    private int lastTotalPages;
    private int lastTotalrecs;
    private int pageSize;
    
    public GeneralDAO(){
        ses=Globals.getSession();
        lastTotalPages=0;
        lastTotalrecs=0;
        pageSize=0;
    }
    
    public boolean saveEntity(VideoupBaseEntity entity,boolean forceAsNew){
        boolean saveok=true;
        try{
            trx=Globals.getTransaction(ses);
            if(entity.getId()==null || entity.getId()==0 || forceAsNew){
                ses.save(entity);
            }else{
                ses.update(entity);    
            }
            trx.commit();
        }catch(Exception he){
            trx.rollback();
            error="ERROR: "+he.getMessage();
            he.printStackTrace();
            saveok=false;
        }
        return saveok;
    }
    
    public boolean deleteEntity(VideoupBaseEntity entity){
        boolean deleted=true;
        try{
            trx=Globals.getTransaction(ses);
            ses.delete(entity);
            trx.commit();
        }catch(Exception he){
            trx.rollback();
            error="ERROR: "+he.getMessage();
            //he.printStackTrace();
            deleted=false;
        }
        return deleted;
    }
    
    public boolean deleteEntities(List<VideoupBaseEntity> entities){
        boolean deleted=true;
        try{
            trx=Globals.getTransaction(ses);
            for(VideoupBaseEntity entity: entities){
                ses.delete(entity);
            }
            trx.commit();
        }catch(Exception he){
            trx.rollback();
            error="ERROR: "+he.getMessage();
            //he.printStackTrace();
            deleted=false;
        }
        return deleted;
    }
    
    public boolean saveEntities(List<VideoupBaseEntity> entities){
        boolean deleted=true;
        try{
            trx=Globals.getTransaction(ses);
            for(VideoupBaseEntity entity: entities){
                ses.save(entity);
            }
            trx.commit();
        }catch(Exception he){
            trx.rollback();
            error="ERROR: "+he.getMessage();
            //he.printStackTrace();
            deleted=false;
        }
        return deleted;
    }
        
    public VideoupBaseEntity getEntity(String qry){
        Query query;
        VideoupBaseEntity ent;
        try{
            query=ses.createQuery(qry);
            ent=(VideoupBaseEntity) query.uniqueResult();
        }catch(Exception ex){
            error=ex.getMessage();
            //ex.printStackTrace();
            ent=null;
        }
        return ent;
    }
    
    public List getListEntities(String fquery, String countQry, int pag, boolean limit){
        List lista;
        Query query;
        Query count;
        Long cRecs;
        int pgZise=Globals.getIntConfig("pagesize");
        try{
            query=ses.createQuery(fquery);
            if(limit){
                query.setMaxResults(pgZise);
                query.setFirstResult(pag*pgZise);
            }
            lista=query.list();
            if(countQry!=null){
                count=ses.createQuery(countQry);
                cRecs=(Long)count.uniqueResult();
                lastTotalPages=(int)Math.ceil((double)cRecs/(double)pgZise);
                lastTotalrecs=cRecs.intValue();
                pageSize=pgZise;
            }else{
                lastTotalPages=0;
                lastTotalrecs=0;
                pageSize=0;
            }
        }catch(Exception ex){
            error=ex.getMessage();
            //ex.printStackTrace();
            lista=null;
        }
        return lista;
    }
    
    public List getListEntitiesUseSQL(String fquery, String countQry, int pag, boolean limit){
        List lista;
        Query query;
        Query count;
        BigInteger cRecs;
        int pgZise=Globals.getIntConfig("pagesize");
        try{
            query=ses.createSQLQuery(fquery);
            if(limit){
                query.setMaxResults(pgZise);
                query.setFirstResult(pag*pgZise);
            }
            lista=query.list();
            if(countQry!=null){
                count=ses.createSQLQuery(countQry);
                cRecs=(BigInteger)count.uniqueResult();
                lastTotalPages=(int)Math.ceil(cRecs.doubleValue()/(double)pgZise);
                lastTotalrecs=cRecs.intValue();
                pageSize=pgZise;
            }else{
                lastTotalPages=0;
                lastTotalrecs=0;
                pageSize=0;
            }
        }catch(Exception ex){
            error=ex.getMessage();
            //ex.printStackTrace();
            lista=null;
        }
        return lista;
    }
    
    public List getListEntities(String fquery, int pag, int limit){
        List lista;
        Query query;
        try{
            query=ses.createQuery(fquery);
            query.setMaxResults(limit);
            query.setFirstResult(pag*limit);
            lista=query.list();
        }catch(Exception ex){
            error=ex.getMessage();
            //ex.printStackTrace();
            lista=null;
        }
        return lista;
    }
    
    public long countExistEntities(String countQry){
        Long totals;
        Query count;
        try{
            count=ses.createQuery(countQry);
            totals=(Long)count.uniqueResult();
        }catch(Exception ex){
            error=ex.getMessage();
            //ex.printStackTrace();
            totals=-1l;
        }
        return totals;
    }
    
    public int countExistEntitiesUseSQL(String countQry){
        BigInteger  totals;
        Query count;
        try{
            count=ses.createSQLQuery(countQry);
            totals=(BigInteger)count.uniqueResult();
            if(totals==null){
                totals=new BigInteger("0");
            }
        }catch(Exception ex){
            error=ex.getMessage();
            //ex.printStackTrace();
            totals=new BigInteger("-1");
        }
        return totals.intValue();
    }
    
    public List getIdsListUseSQL(String qry, int page, boolean limit){
        List lista;
        Query query;
        int pgZise=Globals.getIntConfig("pagesize");
        try{
            query=ses.createSQLQuery(qry);
            if(limit){
                query.setMaxResults(pgZise);
                query.setFirstResult(page*pgZise);
            }
            lista=(ArrayList<String>)query.list();
        }catch(Exception ex){
            error=ex.getMessage();
            //ex.printStackTrace();
            lista=null;
        }
        return lista;
    }
    
    public int executeSQL(String qry){
        Query query;
        int res;
        try{
            query=ses.createSQLQuery(qry);
            res=query.executeUpdate();
            ses.clear();
        }catch(Exception ex){
            error=ex.getMessage();
            //ex.printStackTrace();
            res=-1;
        }
        return res;
    }
    
    public String getError(){
        return error;
    }

    public int getLastTotalPages() {
        return lastTotalPages;
    }

    public int getLastTotalrecs() {
        return lastTotalrecs;
    }

    public int getPageSize() {
        return pageSize;
    }
    
}
