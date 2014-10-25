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
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Pedro
 */
@Entity
@Table(name = "videoup_itemsvnt")
public class VideoupItemsvnt extends VideoupBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idiv")
    private Integer idiv;
    @Basic(optional = false)
    @Lob
    @Column(name = "descr")
    private String descr;
    
    @JoinColumn(name = "idbc", referencedColumnName = "idbc")
    @ManyToOne
    private VideoupBcodes idbc;
    @JoinColumn(name = "idvn", referencedColumnName = "idvn")
    @ManyToOne
    private VideoupVentas idvn;

    public VideoupItemsvnt() {
    }

    @Override
    public Integer getId() {
        return idiv;
    }

    public VideoupItemsvnt(Integer idiv) {
        this.idiv = idiv;
    }

    public VideoupItemsvnt(Integer idiv, String descr) {
        this.idiv = idiv;
        this.descr = descr;
    }

    public Integer getIdiv() {
        return idiv;
    }

    public void setIdiv(Integer idiv) {
        this.idiv = idiv;
    }

    public String getDescr() {
        return descr;
    }

    public Boolean getIsmov() {
        return idbc.getIsmov();
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public VideoupBcodes getIdbc() {
        return idbc;
    }

    public void setIdbc(VideoupBcodes idbc) {
        this.idbc = idbc;
    }

    public VideoupVentas getIdvn() {
        return idvn;
    }

    public void setIdvn(VideoupVentas idvn) {
        this.idvn = idvn;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idiv != null ? idiv.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VideoupItemsvnt)) {
            return false;
        }
        VideoupItemsvnt other = (VideoupItemsvnt) object;
        if ((this.idiv == null && other.idiv != null) || (this.idiv != null && !this.idiv.equals(other.idiv))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.videoup.entities.VideoupItemsvnt[ idiv=" + idiv + " ]";
    }
    
}
