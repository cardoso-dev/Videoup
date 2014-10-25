/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.hibernate.annotations.BatchSize;

/**
 *
 * @author Pedro
 */
@Entity
@Table(name = "videoup_rentas")
@XmlRootElement
public class VideoupRentas extends VideoupBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idrt")
    private Integer idrt;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "cst_fin")
    private Float cstFin;
    @Column(name = "otcst_fin")
    private Float otcstFin;
    @Column(name = "factura")
    private Boolean factura;
    @Column(name = "impuesto")
    private Float impuesto;
    @Column(name = "fromCredito")
    private Float fromCredito;
    @Column(name = "toAdeudo")
    private Float toAdeudo;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idrt")
    @BatchSize(size=77)
    private List<VideoupItemsrnt> videoupItemsrntList;
    @JoinColumn(name = "idcli", referencedColumnName = "idct")
    @ManyToOne(optional = false)
    private VideoupCustomers idcli;

    public VideoupRentas() {
    }

    public VideoupRentas(Integer idrt) {
        this.idrt = idrt;
    }

    @Override
    public Integer getId() {
        return idrt;
    }

    public Integer getIdrt() {
        return idrt;
    }

    public void setIdrt(Integer idrt) {
        this.idrt = idrt;
    }

    public Float getCstFin() {
        return cstFin;
    }

    public void setCstFin(Float cstFin) {
        this.cstFin = cstFin;
    }

    public Float getOtcstFin() {
        return otcstFin;
    }

    public void setOtcstFin(Float otcstFin) {
        this.otcstFin = otcstFin;
    }

    public Boolean getFactura() {
        return factura;
    }

    public Float getImpuesto() {
        return impuesto;
    }

    public Float getFromCredito() {
        if(fromCredito==null){ return 0f; }
        return fromCredito;
    }

    public void setFromCredito(Float fromCredito) {
        this.fromCredito = fromCredito;
    }

    public Float getToAdeudo() {
        if(toAdeudo==null){ return 0f; }
        return toAdeudo;
    }

    public void setToAdeudo(Float toAdeudo) {
        this.toAdeudo = toAdeudo;
    }

    public void setImpuesto(Float impuesto) {
        this.impuesto = impuesto;
    }

    public void setFactura(Boolean factura) {
        this.factura = factura;
    }

    @XmlTransient
    public List<VideoupItemsrnt> getVideoupItemsrntList() {
        return videoupItemsrntList;
    }
    
    public void addVideoupItemsrntList(VideoupItemsrnt itm){
        if(videoupItemsrntList==null){
            videoupItemsrntList=new ArrayList<VideoupItemsrnt>();
       }
        videoupItemsrntList.add(itm);
    }

    public void setVideoupItemsrntList(List<VideoupItemsrnt> videoupItemsrntList) {
        this.videoupItemsrntList = videoupItemsrntList;
    }

    public VideoupCustomers getIdcli() {
        return idcli;
    }

    public void setIdcli(VideoupCustomers idcli) {
        this.idcli = idcli;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idrt != null ? idrt.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VideoupRentas)) {
            return false;
        }
        VideoupRentas other = (VideoupRentas) object;
        if ((this.idrt == null && other.idrt != null) || (this.idrt != null && !this.idrt.equals(other.idrt))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getIdcli().getCodCst()+", "+getIdcli().getName()+" "+getIdcli().getApplldos();
    }
    
}
