/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.utils;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTabbedPane;

/**
 *
 * @author Pedro
 */
public class Validator {
    
    private List<ValidateUnit> valUnits;
    private String error;
    private JTabbedPane jtb;
    
    public Validator(){
        valUnits=new ArrayList();
    }
    
    public void setJTabbedPane(JTabbedPane jtb){
        this.jtb=jtb;
    }
    
    /**
     * Validates a string content of a component (JTextField)
     * @param comp The component
     * @param name The name of the field
     * @param required If the value is required or not
     * @param minLength Minimum length for string types
     * @param maxLength Maximum length for string types
     */
    public void addStringField(Component comp, String name, boolean required, int minLength, int maxLength,int idxTab){
        ValidateUnit vuni=new ValidateUnit(comp,name,1,required,minLength,maxLength,0,0,null);
        if(jtb!=null && idxTab>-1){
            vuni.setJTabbedPane(jtb,idxTab);
        }
        valUnits.add(vuni);
    }
        
    /**
     * Validates a content of a component (JTextField)
     * @param comp The component
     * @param name The name of the field
     * @param required If the value is required or not
     * @param minVal Minimum value for double type or integer (when integer it will be used floor value)
     * @param maxVal Maximum value for double type or integer (when integer it will be used floor value)
     */
    public void addIntField(Component comp, String name, boolean required, double minVal, double maxVal,int idxTab){
        ValidateUnit vuni=new ValidateUnit(comp,name,2,required,0,0,minVal,maxVal,null);
        if(jtb!=null && idxTab>-1){
            vuni.setJTabbedPane(jtb,idxTab);
        }
        valUnits.add(vuni);
    }
        
    /**
     * Validates a content of a component (JTextField)
     * @param comp The component
     * @param name The name of the field
     * @param required If the value is required or not
     * @param minVal Minimum value for double type or integer (when integer it will be used floor value)
     * @param maxVal Maximum value for double type or integer (when integer it will be used floor value)
     */
    public void addDoubleField(Component comp, String name, boolean required, double minVal, double maxVal,int idxTab){
        ValidateUnit vuni=new ValidateUnit(comp,name,3,required,0,0,minVal,maxVal,null);
        if(jtb!=null && idxTab>-1){
            vuni.setJTabbedPane(jtb,idxTab);
        }
        valUnits.add(vuni);
    }
    
    public void addDateField(Component comp, String name, boolean required, int idxTab){
        ValidateUnit vuni=new ValidateUnit(comp,name,4,required,0,0,0,0,null);
        if(jtb!=null && idxTab>-1){
            vuni.setJTabbedPane(jtb,idxTab);
        }
        valUnits.add(vuni);
    }
    
    /**
     * Validates a content of a component (JTextField)
     * @param comp The component
     * @param name The name of the field
     * @param required If the value is required or not
     * @param regExp Regular expresion
     */
    public void addRegExpField(Component comp, String name, boolean required, String regExp,int idxTab){
        ValidateUnit vuni=new ValidateUnit(comp,name,5,required,0,0,0,0,regExp);
        if(jtb!=null && idxTab>-1){
            vuni.setJTabbedPane(jtb,idxTab);
        }
        valUnits.add(vuni);
    }
    
    public void setIgnoreField(Component comp, boolean ignor){
        for(ValidateUnit vu: valUnits){
            if(vu.ifCompSetIgnored(comp, ignor)){
                return;
            }
        }
    }
    
    public boolean validate(){
        boolean allValid=true;
        try{
            for(ValidateUnit vu: valUnits){
                if(!vu.isValid()){
                    error=vu.getErrorMss();
                    allValid=false;
                    break;
                }
            }
        }catch(Exception ex){ ex.printStackTrace(); }
        return allValid;
    }
    
    public boolean hasAtLeastOneValue(){
        for(ValidateUnit vu: valUnits){
            if(vu.hasValue()){
                return true;
            }
        }
        return false;
    }

    public String getError() {
        return error;
    }
    
}
