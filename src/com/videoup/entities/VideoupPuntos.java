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
@Table(name = "videoup_puntos")
public class VideoupPuntos extends VideoupBaseEntity implements Serializable, Comparable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idpn")
    private Integer idpn;
    @Basic(optional = false)
    @Column(name = "puntos")
    private int puntos;
    @Basic(optional = false)
    @Column(name = "asig_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date asigOn;
    @Basic(optional = false)
    @Column(name = "v_hasta")
    @Temporal(TemporalType.TIMESTAMP)
    private Date vHasta;
    @Column(name = "used_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date usedOn;
    @JoinColumn(name = "idct", referencedColumnName = "idct")
    @ManyToOne
    private VideoupCustomers idct;
    @Column(name = "used_pnts")
    private Integer UsedPuntos;

    public VideoupPuntos() {
    }

    public VideoupPuntos(Integer idpn) {
        this.idpn = idpn;
    }

    public VideoupPuntos(Integer idpn,int puntos, Date asigOn, Date vHasta) {
        this.idpn=idpn;
        this.puntos = puntos;
        this.asigOn = asigOn;
        this.vHasta = vHasta;
    }

    public VideoupPuntos(int puntos, Date asigOn, Date vHasta, Date usedOn, VideoupCustomers idct){
        this.puntos = puntos;
        this.asigOn = asigOn;
        this.vHasta = vHasta;
        this.usedOn = usedOn;
        this.idct = idct;
    }

    @Override
    public Integer getId() {
        return idpn;
    }

    public Integer getIdpn() {
        return idpn;
    }

    public void setIdpn(Integer idpn) {
        this.idpn = idpn;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public Date getAsigOn() {
        return asigOn;
    }

    public void setAsigOn(Date asigOn) {
        this.asigOn = asigOn;
    }

    public Date getVHasta() {
        return vHasta;
    }

    public void setVHasta(Date vHasta) {
        this.vHasta = vHasta;
    }

    public Date getUsedOn() {
        return usedOn;
    }

    public void setUsedOn(Date usedOn) {
        this.usedOn = usedOn;
    }

    public VideoupCustomers getIdct() {
        return idct;
    }

    public void setIdct(VideoupCustomers idct) {
        this.idct = idct;
    }

    public Integer getUsedPuntos() {
        if(UsedPuntos==null){ UsedPuntos=0; }
        return UsedPuntos;
    }

    public void setUsedPuntos(int UsedPuntos) {
        this.UsedPuntos = UsedPuntos;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idpn != null ? idpn.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VideoupPuntos)) {
            return false;
        }
        VideoupPuntos other = (VideoupPuntos) object;
        if ((this.idpn == null && other.idpn != null) || (this.idpn != null && !this.idpn.equals(other.idpn))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.videoup.entities.VideoupPuntos[ idpn=" + idpn + " ]";
    }

    @Override
    public int compareTo(Object o) {
        VideoupPuntos toCompare=((VideoupPuntos)o);
        if(toCompare.getAsigOn().before(asigOn)){
            return 1;
        }else if(toCompare.getAsigOn().after(asigOn)){
            return -1;
        }
        return 0;
    }
   
}
