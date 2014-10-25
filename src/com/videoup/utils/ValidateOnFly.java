/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.utils;

import javax.swing.JTextField;

/**
 *
 * @author Pedro
 */
public class ValidateOnFly {
    
    private static String error;
    private static int lastInt;
    private static float lastFloat;
    
    public static boolean validateInteger(JTextField jtf, boolean gtZero){
        try{
            lastInt=Integer.parseInt(jtf.getText());
        }catch(NumberFormatException nfe){
            error="Debe indicar un numero entero";
            lastInt=-1;
            return false;
        }
        if(gtZero && lastInt<=0){
            error="Debe indicar un numero entero mayor a cero";
            return false;
        }
        return true;
    }
    
    public static int getLastInt(){
        return lastInt;
    }
    
    public static boolean validateFloat(JTextField jtf, boolean gtZero){
        try{
            lastFloat=Float.parseFloat(jtf.getText());
        }catch(NumberFormatException nfe){
            error="Debe indicar un numero real";
            lastFloat=-1;
            return false;
        }
        if(gtZero && lastFloat<=0){
            error="Debe indicar un numero real mayor a cero";
            return false;
        }
        return true;
    }
    
    public static float getLastFloat(){
        return lastFloat;
    }
    
    public static String getError(){
        return error;
    }
}
