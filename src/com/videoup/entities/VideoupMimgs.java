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
@Table(name = "videoup_mimgs")
public class VideoupMimgs extends VideoupBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "idrl")
    private Integer idrl;
    @Basic(optional = false)
    @Lob
    @Column(name = "img")
    private byte[] img;

    public VideoupMimgs() {
    }

    @Override
    public Integer getId() {
        return idrl;
    }

    public VideoupMimgs(Integer idrl) {
        this.idrl = idrl;
    }

    public VideoupMimgs(Integer idrl, byte[] img) {
        this.idrl = idrl;
        this.img = img;
    }

    public Integer getIdrl() {
        return idrl;
    }

    public void setIdrl(Integer idrl) {
        this.idrl = idrl;
    }

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
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
        if (!(object instanceof VideoupMimgs)) {
            return false;
        }
        VideoupMimgs other = (VideoupMimgs) object;
        if ((this.idrl == null && other.idrl != null) || (this.idrl != null && !this.idrl.equals(other.idrl))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.videoup.entities.VideoupMimgs[ idrl=" + idrl + " ]";
    }
    
    @Override
    public VideoupMimgs clone(){
        VideoupMimgs newIns=new VideoupMimgs();
        newIns.setImg(img);
        return newIns;
    }
    
}
