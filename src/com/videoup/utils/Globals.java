/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.utils;

import com.videoup.entities.VideoupConf;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Pedro
 */
public class Globals {
    
    private static VideoupConf dataCnf=null;
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("VideoupPU");
    private static EntityManager em = emf.createEntityManager();
    
    public static void reloadConfig(){
        Session sesion=getSession();
        if(dataCnf==null){
            dataCnf=(VideoupConf) sesion.get(VideoupConf.class, 1);
        }else{
            sesion.refresh(dataCnf);
        }
    }
    
    public static Object getConfig(String param){
        Object getted;
        Method mthd;
        try{
            mthd=dataCnf.getClass().getMethod("get"+param.substring(0,1).toUpperCase()+param.substring(1));
            getted=mthd.invoke(dataCnf);
        }catch(Exception ex){
            getted=null;
        }
        return getted;
    }
    
    public static int getIntConfig(String param){
        Object getted=getConfig(param);
        int val;
        try{
            val=Integer.parseInt(getted.toString());
        }catch(Exception ex){
            val=-1;
        }
        return val;
    }
    
    public static boolean getBooleanConfig(String param){
        Object getted=getConfig(param);
        boolean val;
        try{
            val=(Boolean)getted;
        }catch(Exception ex){
            val=false;
        }
        return val;
    }
    
    public static Session getSession(){
        Session ses=(Session)em.getDelegate();
        return ses;
    }
    
    public static Transaction getTransaction(Session sesion){
        Transaction trx = sesion.beginTransaction();
        return trx;
    }
    
    public static Date getToday(){
        Calendar cal=Calendar.getInstance();
        return cal.getTime();
    }
}
