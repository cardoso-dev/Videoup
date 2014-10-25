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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Pedro
 */
@Entity
@Table(name = "videoup_cstmrnotes")
@XmlRootElement
public class VideoupCstmrnotes extends VideoupBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idnt")
    private Integer idnt;
    @Basic(optional = false)
    @Lob
    @Column(name = "note")
    private String note;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @JoinColumn(name = "idct", referencedColumnName = "idct")
    @ManyToOne
    private VideoupCustomers idct;
    @Column(name = "ntype")
    private Integer ntype;

    public VideoupCstmrnotes() {
    }

    public VideoupCstmrnotes(Integer idnt) {
        this.idnt = idnt;
    }

    public VideoupCstmrnotes(String note, Date fecha) {
        this.note = note;
        this.fecha=fecha;
    }

    @Override
    public Integer getId() {
        return idnt;
    }

    public Integer getIdnt() {
        return idnt;
    }

    public void setIdnt(Integer idnt) {
        this.idnt = idnt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public VideoupCustomers getIdct() {
        return idct;
    }

    public Integer getNtype() {
        return ntype;
    }

    public void setNtype(Integer ntype) {
        this.ntype = ntype;
    }

    public void setIdct(VideoupCustomers idct) {
        this.idct = idct;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idnt != null ? idnt.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VideoupCstmrnotes)) {
            return false;
        }
        VideoupCstmrnotes other = (VideoupCstmrnotes) object;
        if ((this.idnt == null && other.idnt != null) || (this.idnt != null && !this.idnt.equals(other.idnt))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.videoup.entities.VideoupCstmrnotes[ idnt=" + idnt + " ]";
    }
    
}
