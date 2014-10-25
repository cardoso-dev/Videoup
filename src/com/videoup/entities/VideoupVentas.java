/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
import javax.persistence.Temporal;

/**
 *
 * @author Pedro
 */
@Entity
@Table(name = "videoup_ventas")
public class VideoupVentas extends VideoupBaseEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idvn")
    private Integer idvn;
    @Column(name = "tp_env")
    private String tpEnv;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "cst_env")
    private float cstEnv;
    @Column(name = "status")
    private Integer status;
    @Column(name = "factura")
    private Boolean factura;@Column(name = "impuesto")
    private float impuesto;
    @Column(name = "cst_subtotal")
    private float subtotal;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Column(name = "onfecha")
    private Date onFecha;
    @JoinColumn(name = "idcli", referencedColumnName = "idct")
    @ManyToOne(optional = false)
    private VideoupCustomers idcli;
    @Column(name = "fromCredito")
    private Float fromCredito;
    @Column(name = "toAdeudo")
    private Float toAdeudo;
    
    @OneToMany(mappedBy = "idvn", cascade = { CascadeType.REMOVE } )
    private List<VideoupItemsvnt> videoupItemsvntList;

    public VideoupVentas() {
    }

    public VideoupVentas(Integer idvn) {
        this.idvn = idvn;
    }

    @Override
    public Integer getId() {
        return idvn;
    }

    public Integer getIdvn() {
        return idvn;
    }

    public float getSubtotal() {
        return subtotal;
    }

    public Date getOnFecha() {
        return onFecha;
    }

    public List<VideoupItemsvnt> getVideoupItemsvntList() {
        return videoupItemsvntList;
    }

    public void setVideoupItemsvntList(List<VideoupItemsvnt> videoupItemsvntList) {
        this.videoupItemsvntList = videoupItemsvntList;
    }

    public void addVideoupItemsvnt(VideoupItemsvnt item) {
        if(videoupItemsvntList==null){
            videoupItemsvntList=new ArrayList<VideoupItemsvnt>();
        }
        videoupItemsvntList.add(item);
    }

    public void setIdvn(Integer idvn) {
        this.idvn = idvn;
    }

    public String getTpEnv() {
        return tpEnv;
    }

    public void setTpEnv(String tpEnv) {
        this.tpEnv = tpEnv;
    }

    public float getCstEnv() {
        return cstEnv;
    }

    public void setCstEnv(float cstEnv) {
        this.cstEnv = cstEnv;
    }

    public Integer getStatus() {
        return status;
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

    public void setSubtotal(float subtotal) {
        this.subtotal = subtotal;
    }

    public void setOnFecha(Date onFecha) {
        this.onFecha = onFecha;
    }

    public String getNameStatus(){
        String[] names={"Apartado","Finalizado","Cancelado"};
        return names[status];
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getFactura() {
        return factura;
    }

    public void setFactura(Boolean factura) {
        this.factura = factura;
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
        hash += (idvn != null ? idvn.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VideoupVentas)) {
            return false;
        }
        VideoupVentas other = (VideoupVentas) object;
        if ((this.idvn == null && other.idvn != null) || (this.idvn != null && !this.idvn.equals(other.idvn))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Venta "+idvn+" "+getNameStatus();
    }

    public Float getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(float impuesto) {
        this.impuesto = impuesto;
    }
    
}
