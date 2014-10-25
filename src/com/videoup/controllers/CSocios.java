/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.controllers;

import com.videoup.entities.VideoupCustomers;
import com.videoup.views.modsocios.Ficha;
import com.videoup.views.modsocios.Lista;
import com.videoup.views.sys.Videoup;
import com.videoup.views.utils.View;
import com.videoup.views.utils.ViewsSelector;

/**
 *
 * @author Pedro
 */
public class CSocios extends Controller{
    
    public CSocios(Videoup vm,ViewsSelector vsl, String tt, String ico){
        super(vm,vsl,tt,ico,9);
    }
	
    @Override
    protected View getListView(){
        if(mainView==null){ mainView=new Lista(this); }
        return mainView;
    }

    @Override
    public void loadNewFicha(){       
        if(!existsIdFicha(0)){
            addAndShowView(new Ficha(this,0),"Nuevo registro de socio");
        }else{ goToView(0, false); }
    }
    
    @Override
    public void loadFicha(int recId){
        Ficha ficha=new Ficha(this,recId);
        addAndShowView(ficha,ficha.getTitle());
    }
    
    @Override
    public void loadFilteredList(String filter){
        if(mainView==null){ mainView=getListView(); }
        ((com.videoup.views.utils.ViewLista)mainView).setAndApplyFilter(filter);
    }
    
}
