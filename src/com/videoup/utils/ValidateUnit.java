/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.utils;

import java.awt.Color;
import java.awt.Component;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import org.jdesktop.swingx.JXDatePicker;

/**
 *
 * @author Pedro
 */
public class ValidateUnit {
    
    private Component comp;
    private String name;
    private int type; // 1=string, 2=int, 4=date, 3=double, 5=regexp
    private boolean required;
    private int minLength; // for type string
    private int maxLength; // for type string
    private double minVal; // for type int (floor) or type double
    private double maxVal; // for type int (floor) or type double
    private String regExp; // regexp for type regexp
    private String errMss;
    private int indexTab; // optional index if the comp is in a tab (-1 if not)
    private JTabbedPane jtb;
    private boolean ignore;
    
    public ValidateUnit(Component comp, String name, int type, boolean required, int minLength, 
            int maxLength, double minVal, double maxVal, String regExp){
        this.comp = comp;
        this.name = name;
        this.type = type;
        this.required = required;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.minVal = minVal;
        this.maxVal = maxVal;
        this.regExp=regExp;
        this.indexTab=-1;
        this.jtb=null;
    }
    
    public void setJTabbedPane(JTabbedPane jtb, int index){
        this.jtb=jtb;
        indexTab=index;
    }
    
    public boolean isValid(){
        String svl;
        Integer ivl;
        Double dvl;
        Pattern ptn;
        Matcher mtch;
        Date dte;
        if(ignore){
            return true;
        }
        if(type==1){
            svl=getStringVal();
            if(required && svl.length()==0){
                errMss=name+" no puede ser omitido";
                markErrorInComp(true);
                return false;
            }
            if(svl.length()>0 && (svl.length()<minLength || svl.length()>maxLength) ){
                errMss=name+" debe tener una longitud entre "+minLength+" y "+maxLength;
                markErrorInComp(true);
                return false;
            }
            markErrorInComp(false);
            return true;
        }else if(type==2){
            ivl=getIntVal();
            if(ivl==null && errMss!=null){
                errMss+=" en "+name;
                markErrorInComp(true);
                return false;
            }else if(ivl==null && required){
                errMss="Debe especificar el valor de "+name;
                markErrorInComp(true);
                return false;
            }
            if(ivl!=null && (ivl<Math.floor(minVal) || ivl>Math.floor(maxVal))){
                errMss=name+" debe tener una valor entre "+minVal+" y "+maxVal;
                markErrorInComp(true);
                return false;
            }
            markErrorInComp(false);
            return true;
        }else if(type==3){
            dvl=getDoubleVal();
            if(dvl==null && errMss!=null){
                errMss+=" en "+name;
                markErrorInComp(true);
                return false;
            }else if(dvl==null && required){
                errMss="Debe especificar el valor de "+name;
                markErrorInComp(true);
                return false;
            }
            if(dvl!=null && (dvl<minVal || dvl>maxVal)){
                errMss=name+" debe tener una valor entre "+minVal+" y "+maxVal;
                markErrorInComp(true);
                return false;
            }
            markErrorInComp(false);
            return true;
        }else if(type==4){
            dte=getDateVal();
            if(required && dte==null){
                errMss="Debe especificar la fecha de "+name;
                markErrorInComp(true);
                return false;
            }
            markErrorInComp(false);
            return true;
        }else if(type==5){
            svl=getStringVal();
            ptn=Pattern.compile(regExp);
            mtch=ptn.matcher(svl);
            if(required && svl.length()==0){
                errMss=name+" no puede ser omitido";
                markErrorInComp(true);
                return false;
            }
            if(svl.length()>0 && !mtch.matches()){
                errMss=name+" tiene un formato invalido";
                markErrorInComp(true);
                return false;
            }
            markErrorInComp(false);
            return true;
        }else{
            errMss="Not recognized "+type+" type for "+name;
            markErrorInComp(true);
            errMss="Type unknown";
        }
        return false;
    }
    
    public boolean hasValue(){
        if(type==1 && getStringVal()!=null && getStringVal().length()>0){
            return true;
        }else if(type==2 && getIntVal()!=null){
            return true;
        }else if(type==3 && getDoubleVal()!=null){
            return true;
        }else if(type==4 && getDateVal()!=null){
            return true;
        }else if(type==5 && getStringVal()!=null && getStringVal().length()>0){
            return true;
        }
        return false;
    }
    
    private void markErrorInComp(boolean err){
        comp.setBackground(err?new Color(255,107,97):Color.white);
        if(comp instanceof JXDatePicker){
            ((JXDatePicker)comp).getEditor().setBackground(err?new Color(255,107,97):Color.white);
        }
        if(err && jtb!=null && indexTab>-1){
            jtb.setSelectedIndex(indexTab);
        }
    }
    
    private String getStringVal(){
        if(comp instanceof JTextField){
            return ((JTextField)comp).getText().trim();
        }else if(comp instanceof JTextArea){
            return ((JTextArea)comp).getText().trim();
        }
        return null;
    }
    
    private Integer getIntVal(){
        int val;
        String tx=getStringVal();
        if(tx.length()==0 && !required){
            errMss=null;
            return null;
        }
        if(comp instanceof JTextField){
            try{
                val=Integer.parseInt(tx);
            }catch(NumberFormatException nfe){
                errMss="Numero invalido, debe escribir un numero entero";
                return null;
            }
            return val;
        }
        errMss=null;
        return null;
    }
    
    private Double getDoubleVal(){
        double val;
        String tx=getStringVal();
        if(tx.length()==0 && !required){
            errMss=null;
            return null;
        }
        if(comp instanceof JTextField){
            try{
                val=Double.parseDouble(tx);
            }catch(NumberFormatException nfe){
                errMss="Numero invalido";
                return null;
            }
            return val;
        }
        errMss=null;
        return null;
    }
    
    private Date getDateVal(){
        if(comp instanceof JXDatePicker){
            return ((JXDatePicker)comp).getDate();
        }
        return null;
    }
    
    public boolean ifCompSetIgnored(Component cmp, boolean ignr){
        if(comp.equals(cmp)){
            ignore=ignr;
            return true;
        }
        return false;
    }
    
    public String getErrorMss(){
        return errMss;
    }
}
