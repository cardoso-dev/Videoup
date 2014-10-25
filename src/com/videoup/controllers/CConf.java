/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.controllers;

import com.videoup.entities.VideoupConf;
import com.videoup.utils.GeneralDAO;
import com.videoup.views.modconf.AvWeb;
import com.videoup.views.modconf.BarCode;
import com.videoup.views.modconf.CTaxes;
import com.videoup.views.modconf.DataBase;
import com.videoup.views.modconf.JLang;
import com.videoup.views.modconf.SConf;
import com.videoup.views.sys.Videoup;
import com.videoup.views.utils.View;
import com.videoup.views.utils.ViewsSelector;

/**
 *
 * @author Pedro
 */
public class CConf extends Controller{
    
    private VideoupConf ent;
    private SConf sconf;
    private DataBase dbase;
    private BarCode bcode;
    private JLang jlang;
    private CTaxes ctaxes;
    private AvWeb avweb;
    
    public CConf(Videoup vm,ViewsSelector vsl, String tt, String ico){
        super(vm,vsl,tt,ico,3);
        ent=null;
        crrView=-2;
        if(vsl==null){
            reloadViews();
            goToView(-2, false);
        }
        removeListView();
        vsel.removeObtnNew();
    }
    
    private void reloadViews(){
        sconf=new SConf(this,1);
        dbase=new DataBase(this,2);
        bcode=new BarCode(this,3);
        jlang=new JLang(this,4);
        ctaxes=new CTaxes(this,5);
        avweb=new AvWeb(this,6);
        addAndShowView(sconf,"Configuracion sistema",false,true);
        addAndShowView(dbase,"Conexion a base de datos",false,true);
        addAndShowView(bcode,"Codigos de barras",false,true);
        addAndShowView(jlang,"Idiomas en productos",false,true);
        addAndShowView(ctaxes,"Configuracion de impuestos",false,true);
        addAndShowView(avweb,"Avisos de la web",false,true);
    }
    
    @Override
    public void goToNamedView(String name){
        if(name.equals("sys")){
            sconf=new SConf(this,1);
            addAndShowView(sconf,"Configuracion sistema",true,true);
            goToView(1, false);
        }else if(name.equals("dbc")){
            dbase=new DataBase(this,2);
            addAndShowView(dbase,"Conexion a base de datos",true,true);
            goToView(2, false);
        }else if(name.equals("cdb")){
            bcode=new BarCode(this,3);
            addAndShowView(bcode,"Codigos de barras",true,true);
            goToView(3, false);
        }else if(name.equals("idm")){
            jlang=new JLang(this,4);
            addAndShowView(jlang,"Idiomas en productos",true,true);
            goToView(4, true);
        }else if(name.equals("txs")){
            ctaxes=new CTaxes(this,5);
            addAndShowView(ctaxes,"Configuracion de impuestos",true,true);
            goToView(5, true);
        }else if(name.equals("web")){
            avweb=new AvWeb(this,6);
            addAndShowView(avweb,"Avisos de la web",true,true);
            goToView(6, true);
        }
    }
    
    public VideoupConf getConfEnt(){
        GeneralDAO gDao=new GeneralDAO();
        if(ent==null){
            ent=(VideoupConf) gDao.getEntity("from VideoupConf where idcc=1");
        }
        return ent;
    }
    
    @Override
    public void closeView(View vw){
        super.closeView(vw);
        if(vw==sconf){
            sconf=null;
        }else if(vw==dbase){
            dbase=null;
        }else if(vw==bcode){
            bcode=null;
        }else if(vw==jlang){
            jlang=null;
        }else if(vw==ctaxes){
            ctaxes=null;
        }else if(vw==avweb){
            avweb=null;
        }
    }
    
}
