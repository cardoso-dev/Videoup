/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.utils;

import java.util.ArrayList;

/**
 *
 * @author Pedro
 */
public class DBUtils {
    
    private static String error;
    
    public static boolean deleteBCodes(String idRls,boolean isMov){
        GeneralDAO genDAO=new GeneralDAO();
        long rnts, vnts;
        String idbcList;
        String rel=(isMov?"idcm":"idca");
        String toDel1="delete from videoup_bcdlang WHERE;";
        String toDel1a="delete from videoup_bcdgam WHERE;";
        String toDel1b="delete from videoup_bcodes WHERE;";
        String qry="select distinct vb.idbc from videoup_bcodes vb, ";
        qry+=(isMov?"videoup_bcdmov vr, videoup_movies vp":"videoup_bcdgam vr, videoup_games vp");
        qry+=" where vb.idbc=vr.idbc and vr."+rel+"=vp."+rel+" and ("+idRls.replaceAll("IDN","vp."+rel)+")";
        idbcList=loadIdListUseSQL(qry,0,false);
        if(idbcList==null){ error=genDAO.getError(); return false; }
        if(idbcList.length()>0){
            idbcList=idbcList.replaceAll("(\\d+)","idbc=$1");
            // revisar que no hara relaciones con items vnt o rnt
            rnts=genDAO.countExistEntitiesUseSQL("select count(*) from videoup_itemsvnt where "+idbcList);
            vnts=genDAO.countExistEntitiesUseSQL("select count(*) from videoup_itemsrnt where "+idbcList);
            if(vnts<0 || rnts<0){ error=genDAO.getError(); return false; }
            if(vnts>0 || rnts>0){ error="Existe relacion con registros de alquiler o ventas"; return false; }
            // borrarlos
            idbcList=idbcList.replaceAll("(\\d+)","idbc=$1");
            toDel1=toDel1.replaceAll("WHERE"," where "+idbcList);
            toDel1a=toDel1a.replaceAll("WHERE"," where "+idbcList);
            toDel1b=toDel1b.replaceAll("WHERE"," where "+idbcList);
            if(genDAO.executeSQL(toDel1)<0){
                error=genDAO.getError(); return false;
            } if(genDAO.executeSQL(toDel1a)<0){
                error=genDAO.getError(); return false;
            } if(genDAO.executeSQL(toDel1b)<0){
                error=genDAO.getError(); return false;
            }
        }
        return true;
    }
    
    public static String loadIdListUseSQL(String fquery, int pag, boolean limit){
        GeneralDAO genDAO=new GeneralDAO();
        ArrayList lista=(ArrayList)genDAO.getIdsListUseSQL(fquery,pag,limit);
        String serie=null;
        if(lista==null){
            error=genDAO.getError();
        }else{
            serie="";
            for(Object str: lista){
                serie+=(serie.length()>0?" or ":"")+str;
            }
        }
        return serie;
    }
    
    public static String getError(){
        return error;
    }
    
}
