/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.utils.reps;

/**
 *
 * @author Pedro
 */
public class EntActv{
     
     private String arti;
     private String oper;
     private String numr;
 
    public EntActv(String arti, String oper, String numr) {
        this.arti = arti;
        this.oper = oper;
        this.numr = numr;
     }

    public EntActv() {
    }

    public String getArti() {
        return arti;
    }

    public void setArti(String arti) {
        this.arti = arti;
    }

    public String getOper() {
        return oper;
    }

    public void setOper(String oper) {
        this.oper = oper;
    }

    public String getNumr() {
        return numr;
    }

    public void setNumr(String numr) {
        this.numr = numr;
    }
    
}
