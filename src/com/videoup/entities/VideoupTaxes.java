/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.entities;

import java.io.Serializable;
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
@Table(name = "videoup_taxes")
@XmlRootElement
public class VideoupTaxes extends VideoupBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idtx")
    private Integer idtx;
    @Column(name = "namet")
    private String namet;
    @Basic(optional = false)
    @Column(name = "porcent")
    private int porcent;
    @Basic(optional = false)
    @Column(name = "ap_rent")
    private boolean apRent;
    @Basic(optional = false)
    @Column(name = "ap_vent")
    private boolean apVent;
    @Basic(optional = false)
    @Column(name = "factur_onl")
    private boolean facturOnl;

    public VideoupTaxes() {
    }

    public VideoupTaxes(Integer idtx) {
        this.idtx = idtx;
    }

    public VideoupTaxes(Integer idtx, int porcent, boolean apRent, boolean apVent, boolean facturOnl) {
        this.idtx = idtx;
        this.porcent = porcent;
        this.apRent = apRent;
        this.apVent = apVent;
        this.facturOnl = facturOnl;
    }

    @Override
    public Integer getId() {
        return idtx;
    }

    public Integer getIdtx() {
        return idtx;
    }

    public void setIdtx(Integer idtx) {
        this.idtx = idtx;
    }

    public String getNamet() {
        return namet;
    }

    public void setNamet(String namet) {
        this.namet = namet;
    }

    public int getPorcent() {
        return porcent;
    }

    public void setPorcent(int porcent) {
        this.porcent = porcent;
    }

    public boolean getApRent() {
        return apRent;
    }

    public void setApRent(boolean apRent) {
        this.apRent = apRent;
    }

    public boolean getApVent() {
        return apVent;
    }

    public void setApVent(boolean apVent) {
        this.apVent = apVent;
    }

    public boolean getFacturOnl() {
        return facturOnl;
    }

    public void setFacturOnl(boolean facturOnl) {
        this.facturOnl = facturOnl;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idtx != null ? idtx.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VideoupTaxes)) {
            return false;
        }
        VideoupTaxes other = (VideoupTaxes) object;
        if ((this.idtx == null && other.idtx != null) || (this.idtx != null && !this.idtx.equals(other.idtx))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.videoup.entities.VideoupTaxes[ idtx=" + idtx + " ]";
    }
    
}
