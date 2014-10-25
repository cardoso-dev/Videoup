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
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 *
 * @author Pedro
 */
@Entity
@Table(name = "videoup_docs")
public class VideoupDocs extends VideoupBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "iddc")
    private Integer iddc;
    @Column(name = "idct")
    private Integer idct;
    @Basic(optional = false)
    @Column(name = "docname")
    private String docname;
    @Basic(optional = false)
    @Lob
    @Column(name = "docimg")
    private byte[] docimg;

    public VideoupDocs() {
    }

    @Override
    public Integer getId() {
        return iddc;
    }

    public VideoupDocs(Integer iddc) {
        this.iddc = iddc;
    }

    public VideoupDocs(Integer iddc, String docname, byte[] docimg) {
        this.iddc = iddc;
        this.docname = docname;
        this.docimg = docimg;
    }

    public VideoupDocs(String docname, byte[] docimg) {
        this.docname = docname;
        this.docimg = docimg;
    }

    public Integer getIddc() {
        return iddc;
    }

    public void setIddc(Integer iddc) {
        this.iddc = iddc;
    }

    public Integer getIdct() {
        return idct;
    }

    public void setIdct(Integer idct) {
        this.idct = idct;
    }

    public String getDocname() {
        return docname;
    }

    public void setDocname(String docname) {
        this.docname = docname;
    }

    public byte[] getDocimg() {
        return docimg;
    }

    public void setDocimg(byte[] docimg) {
        this.docimg = docimg;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (iddc != null ? iddc.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VideoupDocs)) {
            return false;
        }
        VideoupDocs other = (VideoupDocs) object;
        if ((this.iddc == null && other.iddc != null) || (this.iddc != null && !this.iddc.equals(other.iddc))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.videoup.entities.VideoupDocs[ iddc=" + iddc + " ]";
    }
    
}
