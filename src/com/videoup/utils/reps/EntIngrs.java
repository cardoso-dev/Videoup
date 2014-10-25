/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.utils.reps;

/**
 *
 * @author Pedro
 */
public class EntIngrs {
    
    private String fecha;
    private String oper;
    private String ingre;

    public EntIngrs() {
    }

    public EntIngrs(String fecha, String operacion, String ingreso) {
        this.fecha = fecha;
        this.oper = operacion;
        this.ingre = ingreso;
    }

    public String getFecha() {
        return fecha;
    }

    public String getOper() {
        return oper;
    }

    public String getIngre() {
        return ingre;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setOper(String operacion) {
        this.oper = operacion;
    }

    public void setIngre(String ingreso) {
        this.ingre = ingreso;
    }
    
    
}
