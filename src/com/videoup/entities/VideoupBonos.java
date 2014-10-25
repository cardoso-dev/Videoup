/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.entities;

import java.io.Serializable;
import java.text.NumberFormat;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Pedro
 */
@Entity
@Table(name = "videoup_bonos")
@XmlRootElement
public class VideoupBonos extends VideoupBaseEntity implements Serializable{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idbn")
    private Integer idbn;
    @Column(name = "dias")
    private int dias;
    @Column(name = "articls")
    private int articls;
    @Column(name = "hours")
    private int hours;
    @Column(name = "costo")
    private float costo;
    @Basic(optional = false)
    @Column(name = "applymvs")
    private boolean applymvs;
    @Basic(optional = false)
    @Column(name = "applygms")
    private boolean applygms;

    public VideoupBonos() {
    }

    public VideoupBonos(int dias, int articls, int hours, float costo, boolean movs, boolean games){
        this.dias = dias;
        this.articls = articls;
        this.hours = hours;
        this.costo = costo;
        this.applygms=games;
        this.applymvs=movs;
    }

    @Override
    public Integer getId() {
        return idbn;
    }
    
    public Integer getIdbn() {
        return idbn;
    }

    public void setIdbn(Integer idbn) {
        this.idbn = idbn;
    }

    public int getDias(){
        return dias;
    }

    public void setDias(int dias) {
        this.dias = dias;
    }

    public int getArticls() {
        return articls;
    }

    public void setArticls(int articls) {
        this.articls = articls;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public boolean isApplymvs() {
        return applymvs;
    }

    public void setApplymvs(boolean applymvs) {
        this.applymvs = applymvs;
    }

    public boolean isApplygms() {
        return applygms;
    }

    public void setApplygms(boolean applygms) {
        this.applygms = applygms;
    }

    public float getCosto() {
        return costo;
    }

    public void setCosto(float costo) {
        this.costo = costo;
    }
    
    @Override
    public String toString() {
        NumberFormat frmCurr=NumberFormat.getCurrencyInstance();
        return "Bono: "+articls+" articulos X "+frmCurr.format(costo);
    }
    
    public String getExtDescrip(){
        NumberFormat frmCurr=NumberFormat.getCurrencyInstance();
        String desc="Bono: "+articls+" articulos X "+frmCurr.format(costo);
        desc+=" ("+(applymvs?"peliculas":"")+(applygms?" juegos":"")+") vigencia "+dias+" dias";
        return  desc;
    }
}
