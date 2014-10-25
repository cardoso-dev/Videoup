/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.views.sys;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 *
 * @author Pedro
 */
public class FailCatcher implements Thread.UncaughtExceptionHandler{
    
    private Videoup mainw;
    
    public FailCatcher(Videoup mainw){
        this.mainw=mainw;
    }
   
    /** Atrapa las excepciones inesperadas y genera un archivo log con la descripcion del error */
    public void uncaughtException(Thread trd, Throwable exc) {
        java.util.ArrayList<String> dts=new java.util.ArrayList<String>();
        dts.add("** Message: "+exc.toString());
        dts.add("** ** Stack trace:");
        if(guardaArchivoLog(dts,(Exception)exc)){
            mainw.showDialogErr("Ocurrio un error inesperado* Se ha generado un archivo de informe");
        }else{
            mainw.showDialogErr("Ocurrio un error inesperado **No se pudo generar archivo de informe");
        }
    }
    
    private boolean guardaArchivoLog(ArrayList<String> datos, Exception srcexc){
        // el nombre del archivo se compone: LOG_[fecha]_[horaactual]      
        Calendar cal=Calendar.getInstance();
        String patron="yyyyMMddHHmmssSSS";
        SimpleDateFormat formato=new SimpleDateFormat(patron);
        String nomlog="logs/LOG-"+formato.format(cal.getTime());
        try{
            java.io.File ar = new java.io.File(nomlog);            
            java.io.FileWriter arsale = new java.io.FileWriter(ar);
            java.io.BufferedWriter bufw = new java.io.BufferedWriter(arsale);
            java.lang.StackTraceElement[] elemnspila;
            if(datos!=null){
                for(int j=0;j<datos.size();j++) bufw.write(datos.get(j)+"\n");
            }
            if(srcexc!=null){
                bufw.write(srcexc.getMessage()+"\n");
                elemnspila = srcexc.getStackTrace();
                for(int j=0;j<elemnspila.length;j++) bufw.write(elemnspila[j]+"\n");
            }
            bufw.close();                       
            arsale.close();   
            return true;
        }
        catch(Exception exc){return false;}
    }   
    
}
