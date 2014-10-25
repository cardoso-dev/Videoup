/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.controllers;

import com.videoup.views.sys.Videoup;
import com.videoup.views.utils.View;
import com.videoup.views.utils.ViewsSelector;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.ImageIcon;

/**
 *
 * @author Pedro
 */
public class Controller {
    
    protected int crrView;
    protected TreeMap<Integer,View> views;
    protected String titulo;
    protected ImageIcon icomimg;
    protected View mainView;
    protected ViewsSelector vsel;
    private Videoup mainwin;
    protected int idCtrl;
    
    public Controller(Videoup mainwin, ViewsSelector vsl, String tt, String ico, int idc){
        idCtrl=idc;
        crrView=-2;
        views=new TreeMap<Integer,View>();
        titulo=tt;
        this.mainwin= mainwin;
        icomimg=new javax.swing.ImageIcon(getClass().getResource(ico));
        mainView=null;
        if(vsl==null){
            vsel=new ViewsSelector(this,titulo,icomimg);
            addViewNameToList("Listado de "+this.getTitulo(), -1, false);
        }else{
            vsel=vsl;
        }
        views.put(crrView,vsel);
    }
    
    public String getTitulo(){ return titulo; }
    
    public ImageIcon getIcon(){ return icomimg; }

    public int getIdCtrl() {
        return idCtrl;
    }
    
    protected void removeListView(){
        vsel.changeClosable(idCtrl,-1,true);
        vsel.removeView(idCtrl,-1);
    }
    
    public View getCurrentView(){
        return views.get(crrView);
    }
    
    public void addAndShowView(View nvw,String tit){
        addAndShowView(nvw,tit,true,true);
    }
    
    public void addAndShowView(View nvw,String tit,boolean clsbl, boolean lead2it){
        views.put(nvw.getId(),nvw);
        addViewNameToList(tit,nvw.getId(),clsbl);
        crrView=nvw.getId();
        if(lead2it){ mainwin.replaceView(nvw,false); }
    }
    
    private void addViewNameToList(String tit, int id, boolean closable){
        if(mainwin.usingMenu()==0){
            vsel.addOption(tit,id,closable);
        }else if(mainwin.usingMenu()==1){
            mainwin.addVNameGlobalList(this, tit, id, true);
        }
    }
    
    public void changeViewName(View cvw,String newTit){
        changeViewName(cvw,newTit,true);
    }
    
    public void changeViewName(View cvw,String newTit,boolean lead2it){
        Set<Integer> keys=views.keySet();
        Iterator<Integer> itk=keys.iterator();
        int ckey=-1;
        while(itk.hasNext()){
            ckey=itk.next();
            if(views.get(ckey).equals(cvw)){ 
                vsel.removeView(idCtrl,ckey);
                views.remove(ckey);
                break;
            }
        }
        addAndShowView(cvw,newTit,true,lead2it);
    }
    
    public void goToView(int id, boolean create){
        View tView;
        if(existsIdFicha(id)){
            crrView=id;
            mainwin.replaceView(views.get(id),false);
        }else if(create){
            if(id==-1){
                tView=getListView();
                views.put(-1, tView);
                addViewNameToList("Listado de "+this.getTitulo(), -1, false);
                mainwin.replaceView(tView,false);
            }else if(id==0){
                loadNewFicha();
            }
        }
    }
    
    public void goToNamedView(String name){
        if(name.equals("new")){
            loadNewFicha();
        }else if(name.equals("lst")){
            goToView(-1, true);
        }else{
            System.out.println("Load: "+name+". Implement method on: "+this);
        }
    }
    
    public View getView(int id){
        if(existsIdFicha(id)){
            return views.get(id);
        }
        return null;
    }
    
    protected View getListView(){ return null; }
    
    public void loadFicha(int recId){
        System.out.println("Load Ficha en "+recId+" > programme");
    }
    
    public void loadNewFicha(){
        System.out.println("Checking for a loaded record... program me");
    }
    
    protected boolean existsIdFicha(int id){
        return views.containsKey(id);
    }
    
    public void closeView(View vw){
        int key=-2;        
        mainwin.removeView(vw);
        Set<Integer> keys=views.keySet();
        Iterator<Integer> itk=keys.iterator();
        int ckey;
        while(itk.hasNext()){
            ckey=itk.next();
            if(views.get(ckey).equals(vw)){ 
                if(ckey==crrView){ crrView=-2; }
                key=ckey; break; 
            }
        }
        if(vw.equals(mainView)){ mainView=null; }
        if(key!=-2){
            if(vsel.isCloseView(idCtrl,key)){
                views.remove(key);
                vsel.removeView(idCtrl,key);
            }
        }
        goToViewsSel();
    }
    
    public void loadFilteredList(String filter){
    }
    
    public boolean try2CloseView(int k,boolean closable){
        View vw=views.get(k);
        boolean closed=true;
        if(vw!=null && closable){
            vw.closeMe();
        }else{ goToViewsSel(); }
        return closed;
    }

    public void goToViewsSel(){
        crrView=-2;
        mainwin.replaceView(vsel,true);
    }
    
    public boolean confirm(String tit, String msg){
        return mainwin.confirm(tit, msg);
    }
    
    public boolean hasChanges(){
        Set<Integer> keys=views.keySet();
        Iterator<Integer> itk=keys.iterator();
        int ckey;
        boolean flag=false;
        while(itk.hasNext()){
            ckey=itk.next();
            if(views.get(ckey).hasChanges()){ 
                flag=true; break; 
            }
        }
        return flag;
    }
    
    public void showDialogErr(String err){ mainwin.showDialogErr(err);  }
    
    public void showDialogInf(String tit, String inf){ mainwin.showDialogInf(tit,inf); }
    
    public String askForString(String mssg, String title){
        String input=mainwin.askForString(mssg, title);
        return (input!=null?input.trim():null);
    }
    
    public Videoup getMainWindow(){
        return mainwin;
    }
}
