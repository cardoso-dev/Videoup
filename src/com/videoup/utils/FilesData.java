/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.TreeMap;

/**
 *
 * @author Pedro
 */
public class FilesData {
    
    /** Get codebar info
     * @param type 1=movies & games; 2=socios
     * @return 
     */
    public static String getCodeBrType(int type){
        ArrayList<TreeMap<String,String>> data=getTagsInfo(type);
        String getted=null;
        if(data==null){ return getted; }
        for(int h=0;h<data.size();h++){
            TreeMap<String,String> c_item=data.get(h);
            if(c_item.get("teg").equals(""+type) && c_item.get("type").equals("2")){
                getted=c_item.get("bct").toLowerCase();
                getted=getted.replace(" ","");
            }
        }
        return getted;
    }
    
    /**
     * 
     * @param type 0=all, 1=movies & games 2=socios
     * @return 
     */
    public static ArrayList<TreeMap<String,String>> getTagsInfo(int type){
        String dirBase=System.getProperty("user.dir");
        String auxs="dy3bfdb";
        File ar = new File(dirBase+File.separator+"lib"+File.separator+auxs);
        boolean bandera=true;
        FileInputStream arSrc;
        ObjectInputStream dtSrc;
        int num2read;
        StringTokenizer tks;
        String tk0, tk1;
        int teg=0;
        TreeMap<String,String> b_item;
        ArrayList<TreeMap<String,String>> cont=new ArrayList<TreeMap<String,String>>();
        if(ar.isFile()){
            try{
                arSrc = new FileInputStream(dirBase+File.separator+"lib"+File.separator+auxs);
                dtSrc = new ObjectInputStream(arSrc);
                num2read=Integer.parseInt(dtSrc.readObject().toString());
                b_item=new TreeMap<String,String>();
                for(int g=0;g<num2read;g++){
                    auxs=((String)dtSrc.readObject());
                    tks=new StringTokenizer(auxs,"|");
                    tk0=tks.nextToken(); tk1=tks.nextToken();
                    b_item.put(tk0,tk1); b_item.put("teg",""+teg);
                    if(tk0.equals("ly")){
                        if(teg==type || type==0){ cont.add(b_item); }
                        b_item=new TreeMap<String,String>();
                    }
                    if(tk0.equals("c_items")){ teg++; }
                }
                dtSrc.close();
            }catch(java.io.IOException ioexp){ bandera=false; }
            catch(ClassNotFoundException cnfexp){ bandera=false; }
            catch(NullPointerException npexp){ bandera=false; }
            if(!bandera){ return null; }
            return cont;
        }else{ return null; }
    }
    
}
