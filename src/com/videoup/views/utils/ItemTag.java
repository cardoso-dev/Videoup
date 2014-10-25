/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.views.utils;

import com.videoup.entities.VideoupTagitems;
import java.awt.Dimension;
import java.awt.Point;

/**
 *
 * @author Pedro
 */
public class ItemTag {
    
    private VideoupTagitems ent;

    public int getType() { return ent.getItemtype(); }
    public String getTitle() { return ent.getTitle(); }
    public String getText() { return ent.getTexto(); }
    public String getBcType() { return ent.getBcodetype(); }
    public String getBcValue() { return ent.getBcodevalue(); }
    public byte[] getImage() { return ent.getImg(); }
    public Dimension getDim() { return new Dimension(ent.getDimwidth(),ent.getDimheight()); }
    public Point getLocation() { return new Point(ent.getLocX(),ent.getLocY()); }

    public void setType(int type) { ent.setItemtype(type); }
    public void setTitle(String title) { ent.setTitle(title); }
    public void setText(String text) { ent.setTexto(text); } 
    public void setBcType(String bcType) { ent.setBcodetype(bcType); }
    public void setBcValue(String bcValue) { ent.setBcodevalue(bcValue); }
    public void setImage(byte[] img) { ent.setImg(img); }
    public void setDim(Dimension dim){ ent.setDimwidth(dim.width); ent.setDimheight(dim.height); }
    public void setLocation(Point location){ ent.setLocX(location.x); ent.setLocY(location.y); }
    
    private String error;
    
    public ItemTag(){
        ent=new VideoupTagitems();
    }
    
    public ItemTag(VideoupTagitems ent){
        this.ent=ent;
    }
    
    public ItemTag(int tp, String tt){
        this(); ent.setItemtype(tp); ent.setTitle(tt);
    }
    
    public ItemTag(String tt, Dimension dm, Point lc){
        this();
        ent.setItemtype(1); ent.setTitle(tt);
        ent.setDimwidth(dm.width); ent.setDimheight(dm.height);
        ent.setLocX(lc.x); ent.setLocY(lc.y);
    }
    
    public ItemTag(String tt, String tx, Dimension dm, Point lc){
        this();
        ent.setItemtype(1); ent.setTitle(tt); ent.setTexto(tx);
        ent.setDimwidth(dm.width); ent.setDimheight(dm.height);
        ent.setLocX(lc.x); ent.setLocY(lc.y);
    }
    
    public ItemTag(String tt, String bct, String bcv, Dimension dm, Point lc){
        this(); 
        ent.setItemtype(2); ent.setTitle(tt); 
        ent.setBcodetype(bct); ent.setBcodevalue(bcv);
        ent.setDimwidth(dm.width); ent.setDimheight(dm.height);
        ent.setLocX(lc.x); ent.setLocY(lc.y);
    }
    
    public ItemTag(String tt, byte[] img, Dimension dm, Point lc){
        this(); 
        ent.setItemtype(3); ent.setTitle(tt); 
        ent.setImg(img);
        ent.setDimwidth(dm.width); ent.setDimheight(dm.height);
        ent.setLocX(lc.x); ent.setLocY(lc.y);
    }
    
    public ItemTag(ItemTag itm){
        ent.setItemtype(itm.getType());
        ent.setTitle(itm.getTitle());
        ent.setTexto(itm.getText());
        ent.setBcodetype(itm.getBcType());
        ent.setBcodevalue(itm.getBcValue());
        ent.setImg(itm.getImage());
        setDim(itm.getDim());
        setLocation(itm.getLocation());
    }
    
    public VideoupTagitems getEntity(){
        return ent;
    }
     
    public void cloneVideoupTagitems(VideoupTagitems bEnt){
        ent.setItemtype(bEnt.getItemtype());
        ent.setTitle(bEnt.getTitle());
        ent.setTexto(bEnt.getTexto());
        ent.setBcodetype(bEnt.getBcodetype());
        ent.setBcodevalue(bEnt.getBcodevalue());
        ent.setImg(bEnt.getImg());
        ent.setDimwidth(bEnt.getDimwidth());
        ent.setDimheight(bEnt.getDimheight());
        ent.setLocX(bEnt.getLocX());
        ent.setLocY(bEnt.getLocY());
    }
    
    public void setItemAs(ItemTag itm){
        //title=itm.getTitle();
        ent.setItemtype(itm.getType());
        ent.setTexto(itm.getText());
        ent.setBcodetype(itm.getBcType());
        ent.setBcodevalue(itm.getBcValue());
        ent.setImg(itm.getImage());
        setDim(itm.getDim());
        setLocation(itm.getLocation());
    }

    public boolean validateInside(){
        boolean flag=true; error=null;
        if(ent.getItemtype()<1 || ent.getItemtype()>21){ error="Tipo de item invalido"; flag=false; }
        if(ent.getItemtype()==1 && (ent.getTexto()==null || ent.getTexto().trim().equals(""))){
            error="Texto contenido vacio"; flag=false;
        }
        else if(ent.getItemtype()==3 && getImage()==null){ error="Imagen vacia"; flag=false; }
        if(getDim().height<1 || getDim().width<1){ error="Altura o ancho invalido"; flag=false; }
        if(getLocation().x<1 || getLocation().x<1){ error="Posicion invalida"; flag=false; }
        if(getTitle().trim().equals("")){ error="Nombre de item invalido"; flag=false; }
        return flag;
    }
    
    public String getError(){ return error; }
    
}
