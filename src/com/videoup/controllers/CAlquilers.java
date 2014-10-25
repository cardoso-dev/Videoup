/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.controllers;

import com.videoup.entities.VideoupCustomers;
import com.videoup.entities.VideoupRentas;
import com.videoup.views.modalqrs.CambioTit;
import com.videoup.views.modalqrs.Devolucion;
import com.videoup.views.modalqrs.Ficha;
import com.videoup.views.modalqrs.Lista;
import com.videoup.views.sys.Videoup;
import com.videoup.views.utils.View;
import com.videoup.views.utils.ViewsSelector;
import java.util.ArrayList;

/**
 *
 * @author Pedro
 */
public class CAlquilers extends Controller{
    
    private Devolucion devl;
    private CambioTit cmbt;
    
    public CAlquilers(Videoup vm,ViewsSelector vsl, String tt, String ico){
        super(vm,vsl,tt,ico,1);
        if(vsl==null){
            reloadViews();
        }
        goToViewsSel();
    }
    
    private void reloadViews(){
        devl=new Devolucion(this,-3);
        cmbt=new CambioTit(this,-4);
        addAndShowView(devl,"Devolucion de articulos",false,true);
        addAndShowView(cmbt,"Cambio de articulos",false,true);
    }
	
    @Override
    protected View getListView(){
        if(mainView==null){ mainView=new Lista(this); }
        return mainView;
    }

    @Override
    public void goToNamedView(String name){
        if(name.equals("new")){
            loadNewFicha();
        }else if(name.equals("dev")){
            if(devl==null || !existsIdFicha(-3)){
                devl=new Devolucion(this,-3);
                addAndShowView(devl,"Devolucion de articulos",false,true);
            }
            goToView(-3, false);
        }else if(name.equals("chn")){
            if(cmbt==null || !existsIdFicha(-4)){
                cmbt=new CambioTit(this,-4);
                addAndShowView(cmbt,"Cambio de articulos",false,true);
            }
            goToView(-4, false);
        }else if(name.equals("lst")){
            goToView(-1, true);
        }
    }
    
    @Override
    public void loadNewFicha(){
        if(!existsIdFicha(0)){
            addAndShowView(new Ficha(this,0),"Nuevo alquiler");
        }else{ goToView(0, false); }
    }
    
    @Override
    public void loadFicha(int recId){
        Ficha ficha=new Ficha(this,recId);
        addAndShowView(ficha,ficha.getTitle());
    }
    
    public void sent2change(String code){
        if(cmbt==null || !existsIdFicha(-4)){
            cmbt=new CambioTit(this,-4);
            addAndShowView(cmbt,"Cambio de articulos",false,true);
        }
        cmbt.setItem2change(code);
        goToView(-4, false);
    }
 
    public void sent2finalize(ArrayList<String> codes, int hh){
        if(devl==null || !existsIdFicha(-3)){
            devl=new Devolucion(this,-3);
            addAndShowView(devl,"Devolucion de articulos",false,true);
        }
        devl.addItems2Resume(codes,hh);
        goToView(-3, false);
    }
    
    public void reloadFicha(int idr, VideoupRentas entRenta, String return2){
        if(idr>0){
            Ficha fich=(Ficha)super.getView(idr);
            if(fich==null){
                fich=new Ficha(this,entRenta,entRenta.getIdrt());
                addAndShowView(fich,fich.getTitle());
            }else{
                fich.loadEntity(entRenta);
            }
            if(return2!=null){ goToNamedView(return2); }
        }
    }
    
    public void startAlqr(VideoupCustomers soc){
        Ficha ficha;
        if(existsIdFicha(0)){
            ficha=((Ficha)this.getView(0));
            if(ficha.getSocioCode()==soc.getIdct()){
                goToView(0, false);
            }
        }else{
            ficha=new Ficha(this,0);
            ficha.setCustomr(soc);
            addAndShowView(ficha,"Nuevo alquiler");
        }
    }
    
}
