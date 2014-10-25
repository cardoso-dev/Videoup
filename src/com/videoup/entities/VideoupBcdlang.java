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
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Pedro
 */
@Entity
@Table(name = "videoup_bcdlang")
public class VideoupBcdlang extends VideoupBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idbl")
    private Integer idbl;
    @Basic(optional = false)
    @Column(name = "as_lang")
    private boolean asLang;
    @JoinColumn(name = "idbc", referencedColumnName = "idbc")
    @ManyToOne(optional = false)
    private VideoupBcodes idbc;
    @JoinColumn(name = "idit", referencedColumnName = "idit")
    @ManyToOne(optional = false)
    private VideoupLangs idit;

    public VideoupBcdlang() {
    }

    @Override
    public Integer getId() {
        return idbl;
    }

    public VideoupBcdlang(Integer idbl) {
        this.idbl = idbl;
    }

    public VideoupBcdlang(VideoupBcodes idbc, VideoupLangs idit, boolean asLang) {
        this.idbc = idbc;
        this.idit=idit;
        this.asLang = asLang;
    }

    public Integer getIdbl() {
        return idbl;
    }

    public void setIdbl(Integer idbl) {
        this.idbl = idbl;
    }

    public boolean getAsLang() {
        return asLang;
    }

    public void setAsLang(boolean asLang) {
        this.asLang = asLang;
    }

    public VideoupBcodes getIdbc() {
        return idbc;
    }

    public void setIdbc(VideoupBcodes idbc) {
        this.idbc = idbc;
    }

    public VideoupLangs getIdit() {
        return idit;
    }

    public void setIdit(VideoupLangs idit) {
        this.idit = idit;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idbl != null ? idbl.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VideoupBcdlang)) {
            return false;
        }
        VideoupBcdlang other = (VideoupBcdlang) object;
        if ((this.idbl == null && other.idbl != null) || (this.idbl != null && !this.idbl.equals(other.idbl))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.videoup.entities.VideoupBcdlang[ idbl=" + idbl + " ]";
    }
    
}
