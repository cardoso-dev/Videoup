/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.controllers;

import com.videoup.entities.VideoupCustomers;
import com.videoup.views.modvnts.Ficha;
import com.videoup.views.modvnts.Lista;
import com.videoup.views.sys.Videoup;
import com.videoup.views.utils.View;
import com.videoup.views.utils.ViewsSelector;

/**
 *
 * @author Pedro
 */
public class CVentas extends Controller{
    
    public CVentas(Videoup vm,ViewsSelector vsl, String tt, String ico){
        super(vm,vsl,tt,ico,10);
        goToViewsSel();
    }
    
    @Override
    protected View getListView(){
        if(mainView==null){ mainView=new Lista(this); }
        return mainView;
    }

    @Override
    public void loadNewFicha(){       
        if(!existsIdFicha(0)){
            addAndShowView(new Ficha(this,0),"Nueva venta");
        }else{ goToView(0, false); }
    }
    
    @Override
    public void loadFicha(int recId){
        Ficha ficha=new Ficha(this,recId);
        addAndShowView(ficha,ficha.getTitle());
    }
    
    public void startVenta(VideoupCustomers soc){
        Ficha ficha;
        if(existsIdFicha(0)){
            ficha=((Ficha)this.getView(0));
            if(ficha.getSocioCode()==soc.getIdct()){
                goToView(0, false);
            }
        }else{
            ficha=new Ficha(this,0);
            ficha.setCustomr(soc);
            addAndShowView(ficha,"Nueva venta");
        }
    }
    
}
