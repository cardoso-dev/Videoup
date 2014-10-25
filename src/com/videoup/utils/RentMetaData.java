/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.utils;

import com.videoup.entities.VideoupCatgs;
import com.videoup.entities.VideoupCtofrentas;
import com.videoup.entities.VideoupCtprrentas;
import com.videoup.entities.VideoupCustomers;
import com.videoup.entities.VideoupFormts;
import com.videoup.entities.VideoupItemsrnt;
import com.videoup.entities.VideoupRentas;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Pedro
 */
public class RentMetaData{
    
    private VideoupRentas vrt;
    private List<VideoupItemsrnt> items;
    
    public RentMetaData(VideoupRentas vrt){
        this.vrt=vrt;
        items=vrt.getVideoupItemsrntList();
    }

    public VideoupRentas getVrt() {
        return vrt;
    }

    public List<VideoupItemsrnt> getItems(boolean mv, boolean gm, List<VideoupCtprrentas> ctgs, 
            List<VideoupFormts> formts, List<VideoupCatgs> catgs, boolean samePrice, VideoupCtofrentas ofrt){
        List<VideoupItemsrnt> applies=new ArrayList<VideoupItemsrnt>();
        float price=-1;
        if(items!=null && !items.isEmpty()){
            for(VideoupItemsrnt itm: items){
                if(price==-1){ price=itm.getCstUt(); }
                if( (itm.getIsmov()&&mv) || (!itm.getIsmov()&&gm) ){
                    if( itemMatchAtLeastOne(itm,ctgs,formts,catgs) && !itm.hasOfrts() 
                                && price==itm.getCstUt() && itm.getStatus()==3 && applyDatenAndDays(ofrt) ){
                        applies.add(itm);
                    }
                }
            }
        }
        return applies;
    }
    
    public List<VideoupItemsrnt> getItems(Date dt1, Date dt2, boolean mv, boolean gm, List<VideoupCtprrentas> ctgs,
            List<VideoupFormts> formts, List<VideoupCatgs> catgs, VideoupCtofrentas ofrt){
        List<VideoupItemsrnt> applies=new ArrayList<VideoupItemsrnt>();
        if(items!=null && !items.isEmpty()){
            for(VideoupItemsrnt itm: items){
                if( (itm.getIsmov()&&mv) || (!itm.getIsmov()&&gm) ){
                    if( itemMatchAtLeastOne(itm,ctgs,formts,catgs) && applyDatenAndDays(ofrt) &&
                                GenProccess.isDateBetween(itm.getITime(), dt1, dt2)
                                && !itm.hasOfrts() && itm.getStatus()==3 ){
                        applies.add(itm);
                    }
                }
            }
        }
        return applies;
    }
    
    public List<VideoupItemsrnt> getItems(boolean[] days, boolean mv, boolean gm, List<VideoupCtprrentas> ctgs,
            List<VideoupFormts> formts, List<VideoupCatgs> catgs, VideoupCtofrentas ofrt){
        List<VideoupItemsrnt> applies=new ArrayList<VideoupItemsrnt>();
        Calendar cal=Calendar.getInstance();
        int dayOfWeek;
        if(items!=null && !items.isEmpty()){
            for(VideoupItemsrnt itm: items){
                if( (itm.getIsmov()&&mv) || (!itm.getIsmov()&&gm) ){
                    if( itemMatchAtLeastOne(itm,ctgs,formts,catgs) && !itm.hasOfrts() && 
                            itm.getStatus()==3 && applyDatenAndDays(ofrt) ){
                        cal.setTime(itm.getITime());
                        dayOfWeek=cal.get(Calendar.DAY_OF_WEEK);
                        if( (dayOfWeek==Calendar.SUNDAY && days[0])||(dayOfWeek==Calendar.MONDAY && days[1])
                                    ||(dayOfWeek==Calendar.TUESDAY && days[2])||(dayOfWeek==Calendar.WEDNESDAY && days[3])
                                    ||(dayOfWeek==Calendar.THURSDAY && days[4])||(dayOfWeek==Calendar.FRIDAY && days[5])
                                    ||(dayOfWeek==Calendar.SATURDAY && days[6]) ){
                            applies.add(itm);
                        }
                    }
                }
            }
        }
        return applies;
    }
    
    public List<VideoupItemsrnt> getItems(int puntos, boolean mv, boolean gm, List<VideoupCtprrentas> ctgs,
            List<VideoupFormts> formts, List<VideoupCatgs> catgs, VideoupCtofrentas ofrt){
        List<VideoupItemsrnt> applies=new ArrayList<VideoupItemsrnt>();
        VideoupCustomers cli=vrt.getIdcli();
        int pntsCli=cli.getAlivePuntos();
        if(items!=null && !items.isEmpty()){
            for(VideoupItemsrnt itm: items){
                if( (itm.getIsmov()&&mv) || (!itm.getIsmov()&&gm) ){
                    if( itemMatchAtLeastOne(itm,ctgs,formts,catgs) && pntsCli>=puntos && !itm.hasOfrts() && 
                            itm.getStatus()==3 && applyDatenAndDays(ofrt) ){
                        applies.add(itm);
                    }
                }
            }
        }
        return applies;
    }
    
    private boolean itemMatchAtLeastOne(VideoupItemsrnt itm, List<VideoupCtprrentas> ctgs, 
            List<VideoupFormts> formts, List<VideoupCatgs> catgs){
        boolean match=false;
        VideoupCatgs itmCatg;
        for(VideoupCtprrentas ctg: ctgs){
            if( ctg.getNamec().equals(itm.getNmCtrprrent()) ){
                match=true; break;
            }
        }
        if(!match){
            for(VideoupFormts frmt: formts){
                if( itm.getIdbc().getFrmt().getIdcf()==frmt.getIdcf() ){
                    match=true; break;
                }
            }
        }
        if(!match){
            if(itm.getIdbc().getIsmov()){
                itmCatg=itm.getIdbc().getVideoupMovie().getCatg();
            }else{
                itmCatg=itm.getIdbc().getVideoupGame().getCatg();
            }
            if(itmCatg!=null){
                for(VideoupCatgs catg: catgs){
                    if( itmCatg.getIdcg()==catg.getIdcg() ){
                        match=true; break;
                    }
                }
            }
        }
        return match;
    }
    
    private boolean applyDatenAndDays(VideoupCtofrentas ofrt){
        boolean apply=false;
        Calendar cal=Calendar.getInstance();
        int day=cal.get(Calendar.DAY_OF_WEEK); 
        Date today=cal.getTime();
        if( (day==Calendar.SUNDAY && ofrt.getAplDomingo()) || (day==Calendar.MONDAY && ofrt.getAplLunes())
                || (day==Calendar.TUESDAY && ofrt.getAplMartes()) || (day==Calendar.WEDNESDAY && ofrt.getAplMiercoles())
                || (day==Calendar.THURSDAY && ofrt.getAplJueves()) || (day==Calendar.FRIDAY && ofrt.getAplViernes())
                || (day==Calendar.SATURDAY && ofrt.getAplSabado()) ){
            apply=true;
        }
        if(ofrt.getAuxD1()!=null && ofrt.getAuxD2()!=null && apply){
            apply=(today.getTime()>=ofrt.getAuxD1().getTime() && today.getTime()<=ofrt.getAuxD2().getTime());
        }
        return apply;
    }
}

