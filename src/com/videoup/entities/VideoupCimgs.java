/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 *
 * @author Pedro
 */
@Entity
@Table(name = "videoup_cimgs")
public class VideoupCimgs extends VideoupBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "idrl")
    private Integer idrl;
    @Basic(optional = false)
    @Lob
    @Column(name = "foto")
    private byte[] foto;

    public VideoupCimgs() {
    }

    @Override
    public Integer getId() {
        return idrl;
    }

    public VideoupCimgs(Integer idrl) {
        this.idrl = idrl;
    }

    public VideoupCimgs(Integer idrl, byte[] foto) {
        this.idrl = idrl;
        this.foto = foto;
    }

    public Integer getIdrl() {
        return idrl;
    }

    public void setIdrl(Integer idrl) {
        this.idrl = idrl;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idrl != null ? idrl.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VideoupCimgs)) {
            return false;
        }
        VideoupCimgs other = (VideoupCimgs) object;
        if ((this.idrl == null && other.idrl != null) || (this.idrl != null && !this.idrl.equals(other.idrl))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.videoup.entities.VideoupCimgs[ idrl=" + idrl + " ]";
    }
    
}
