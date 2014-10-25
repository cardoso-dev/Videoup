/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.controllers;

import com.videoup.views.modreps.RActivity;
import com.videoup.views.modreps.RCardSocios;
import com.videoup.views.modreps.RIncomings;
import com.videoup.views.modreps.RTagProds;
import com.videoup.views.sys.Videoup;
import com.videoup.views.utils.View;
import com.videoup.views.utils.ViewsSelector;

/**
 *
 * @author Pedro
 */
public class CReps extends Controller{
    
    private RIncomings incomes;
    private RActivity activity;
    private RCardSocios socios;
    private RTagProds movies;
    private RTagProds games;
    
    public CReps(Videoup vm,ViewsSelector vsl, String tt, String ico){
        super(vm,vsl,tt,ico,11);
        crrView=-2;
        if(vsl==null){
            reloadViews();
            goToView(-2, false);
        }
        removeListView();
        vsel.removeObtnNew();
    }
    
    private void reloadViews(){
        incomes=new RIncomings(this,1);
        addAndShowView(incomes,"Reporte de ingresos",false,true);
        socios=new RCardSocios(this,2);
        addAndShowView(socios,"Credenciales de socios",true,true);
        movies=new RTagProds(this,3,"Peliculas",true);
        addAndShowView(movies,"Etiquetas de peliculas",true,true);
        games=new RTagProds(this,4,"Videojuegos",false);
        addAndShowView(games,"Etiquetas de videojuegos",true,true);
        activity=new RActivity(this,5);
        addAndShowView(activity,"Reporte de actividad",true,true);
    }
    
    @Override
    public void goToNamedView(String name){
        if(name.equals("ings")){
            if(incomes==null){
                incomes=new RIncomings(this,1);
            }
            addAndShowView(incomes,"Reporte de ingresos",true,true);
            goToView(1, false);
        }else if(name.equals("socs")){
            socios=new RCardSocios(this,2);
            addAndShowView(socios,"Credenciales de socios",true,true);
            goToView(2, false);
        }else if(name.equals("movs")){
            movies=new RTagProds(this,3,"Peliculas",true);
            addAndShowView(movies,"Etiquetas de peliculas",true,true);
            goToView(3, false);
        }else if(name.equals("games")){
            games=new RTagProds(this,4,"Videojuegos",false);
            addAndShowView(games,"Etiquetas de videojuegos",true,true);
            goToView(4, false);
        }else if(name.equals("activ")){
            if(activity==null){
                activity=new RActivity(this,5);
            }
            addAndShowView(activity,"Reporte de actividad",true,true);
            goToView(1, false);
        }
    }
    
    @Override
    public void closeView(View vw){
        super.closeView(vw);
        if(vw==incomes){
            incomes=null;
        }else if(vw==activity){
            activity=null;
        }
    }
    
    public void generateSociosCred(String codes){
        goToNamedView("socs");
        socios.builFromCodes(codes);
    }
    
    public void generateMoviesTag(String codes){
        goToNamedView("movs");
        movies.builFromCodes(codes);
    }
    
    public void generateGamesTag(String codes){
        goToNamedView("games");
        games.builFromCodes(codes);
    }
}
