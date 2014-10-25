/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.entities;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Pedro
 */
@Entity
@Table(name = "videoup_soldbonos")
@XmlRootElement
public class VideoupSoldBonos extends VideoupBaseEntity implements Serializable{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idsb")
    private Integer idsb;
    @Column(name = "inicia")
    @Temporal(TemporalType.TIMESTAMP)
    private Date inicia;
    @Column(name = "hasta")
    @Temporal(TemporalType.TIMESTAMP)
    private Date hasta;
    @Column(name = "used")
    private int used;
    @Column(name = "bonos")
    private int bonos;
    @Column(name = "pagado")
    private float pagado;
    @Column(name = "hours")
    private int hours;
    @Basic(optional = false)
    @Column(name = "applymvs")
    private boolean applymvs;
    @Basic(optional = false)
    @Column(name = "applygms")
    private boolean applygms;
    @Transient
    private int using;
    
    @JoinColumn(name = "idcli", referencedColumnName = "idct")
    @ManyToOne(optional = false)
    private VideoupCustomers idcli;

    public VideoupSoldBonos() {
    }

    public VideoupSoldBonos(Date inicia, Date hasta, int used, int bonos, float pagado, VideoupCustomers idcli,
            int hours, boolean applymvs, boolean applygms) {
        this.inicia = inicia;
        this.hasta = hasta;
        this.used = used;
        this.bonos = bonos;
        this.pagado = pagado;
        this.idcli = idcli;
        this.hours=hours;
        this.applygms=applygms;
        this.applymvs=applymvs;
    }

    @Override
    public Integer getId() {
        return idsb;
    }
    
    public Date getInicia() {
        return inicia;
    }

    public void setInicia(Date inicia) {
        this.inicia = inicia;
    }

    public Date getHasta() {
        return hasta;
    }

    public void setHasta(Date hasta) {
        this.hasta = hasta;
    }

    public int getUsed() {
        return used;
    }

    public void setUsed(int used) {
        this.used = used;
    }

    public int getBonos() {
        return bonos;
    }

    public void setBonos(int bonos) {
        this.bonos = bonos;
    }

    public float getPagado() {
        return pagado;
    }

    public void setPagado(float pagado) {
        this.pagado = pagado;
    }

    public VideoupCustomers getIdcli() {
        return idcli;
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

    public void setIdcli(VideoupCustomers idcli) {
        this.idcli = idcli;
    }

    public int getUsing() {
        return using;
    }

    public void setUsing(int using) {
        this.using = using;
    }

    @Override
    public String toString() {
        return "Bono vendido "+idsb;
    }
    
    public String getExtDescrip(){
        NumberFormat frmCurr=NumberFormat.getCurrencyInstance();
        DateFormat dteFrmt=DateFormat.getDateInstance(DateFormat.LONG);
        String desc=bonos+" articulos X "+frmCurr.format(pagado);
        desc+=" ("+(applymvs?"peliculas":"")+(applygms?" juegos":"")+") vigencia hasta "+dteFrmt.format(hasta);
        desc+=" Usados: "+used;
        return  desc;
    }
}
