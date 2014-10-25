/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.entities;

import java.io.Serializable;
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

/**
 *
 * @author Pedro
 */
@Entity
@Table(name = "videoup_histcredito")
public class VideoupHistcredito  extends VideoupBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idhc")
    private Integer idhc;
    @Basic(optional = false)
    @Column(name = "monto")
    private float monto;
    @Basic(optional = false)
    @Column(name = "asig_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date asigOn;
    @JoinColumn(name = "idct", referencedColumnName = "idct")
    @ManyToOne
    private VideoupCustomers idct;
    
    public VideoupHistcredito(){}

    public VideoupHistcredito(Integer idhc) {
        this.idhc = idhc;
    }

    public VideoupHistcredito(Integer idhc, float monto, Date asigOn, VideoupCustomers idct) {
        this.idhc = idhc;
        this.monto = monto;
        this.asigOn = asigOn;
        this.idct = idct;
    }

    public VideoupHistcredito(float monto, Date asigOn, VideoupCustomers idct) {
        this.monto = monto;
        this.asigOn = asigOn;
        this.idct = idct;
    }

    @Override
    public Integer getId() {
        return idhc;
    }
    
    public float getMonto() {
        return monto;
    }

    public void setMonto(float monto) {
        this.monto = monto;
    }

    public Date getAsigOn() {
        return asigOn;
    }

    public void setAsigOn(Date asigOn) {
        this.asigOn = asigOn;
    }

    public VideoupCustomers getIdct() {
        return idct;
    }

    public void setIdct(VideoupCustomers idct) {
        this.idct = idct;
    }
    
}
