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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Pedro
 */
@Entity
@Table(name = "videoup_itemsrnt")
public class VideoupItemsrnt extends VideoupBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idir")
    private Integer idir;
    @Basic(optional = false)
    @Lob
    @Column(name = "descr")
    private String descr;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "cst_ut")
    private Float cstUt;
    @Column(name = "cst_utx")
    private Float cstUtx;
    @Basic(optional = false)
    @Column(name = "b_time")
    private int bTime;
    @Basic(optional = false)
    @Column(name = "u_time")
    private int uTime;
    @Column(name = "cst_fin")
    private Float cstFin;
    @Basic(optional = false)
    @Column(name = "cst_xt")
    private Float cstXt;
    @Column(name = "cst_apli")
    private Float cstApli;
    @Basic(optional = false)
    @Column(name = "nm_ctrprrent")
    private String nmCtrprrent;
    @Column(name = "ofrt_off")
    private Integer ofrtOff;
    @Column(name = "ofrt_cfin")
    private Float ofrtCfin;
    @Column(name = "s_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date sTime;
    @Column(name = "i_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date iTime;
    @Column(name = "f_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fTime;
    @Column(name = "status")
    private Integer status;
    @Column(name = "onchange")
    private Integer onchange;
    @JoinTable(name = "videoup_itrntofrt", joinColumns = {
        @JoinColumn(name = "idir", referencedColumnName = "idir")}, inverseJoinColumns = {
        @JoinColumn(name = "idofr", referencedColumnName = "idcof")})
    @ManyToMany
    private List<VideoupCtofrentas> videoupCtofrentasList;
    @JoinColumn(name = "idbc", referencedColumnName = "idbc")
    @ManyToOne(optional = false)
    private VideoupBcodes idbc;
    @JoinColumn(name = "idrt", referencedColumnName = "idrt")
    @ManyToOne(optional = false)
    private VideoupRentas idrt;
    @Column(name = "ismov")
    private Boolean ismov;

    public VideoupItemsrnt() {
    }

    @Override
    public Integer getId() {
        return idir;
    }

    public VideoupItemsrnt(Integer idir) {
        this.idir = idir;
    }

    public VideoupItemsrnt(Integer idir, String descr, Float cstUt, int bTime, int uTime, Float cstXt, String nmCtrprrent) {
        this.idir = idir;
        this.descr = descr;
        this.cstUt = cstUt;
        this.bTime = bTime;
        this.uTime = uTime;
        this.cstXt = cstXt;
        this.nmCtrprrent = nmCtrprrent;
    }

    public Boolean getIsmov() {
        return ismov;
    }

    public Integer getIdir() {
        return idir;
    }

    public void setCstUtx(Float cstUtx) {
        this.cstUtx = cstUtx;
    }

    public Float getCstUtx() {
        return cstUtx;
    }

    public void setCstApli(Float cstApli) {
        this.cstApli = cstApli;
    }

    public Float getCstApli() {
        return cstApli;
    }

    public void setIdir(Integer idir) {
        this.idir = idir;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public Float getCstUt() {
        return cstUt;
    }

    public void setCstUt(Float cstUt) {
        this.cstUt = cstUt;
    }

    public int getBTime() {
        return bTime;
    }

    public void setBTime(int bTime) {
        this.bTime = bTime;
    }

    public int getUTime() {
        return uTime;
    }

    public void setUTime(int uTime) {
        this.uTime = uTime;
    }

    public Float getCstFin() {
        return cstFin;
    }

    public void setCstFin(Float cstFin) {
        this.cstFin = cstFin;
    }

    public Float getCstXt() {
        return cstXt;
    }

    public void setCstXt(Float cstXt) {
        this.cstXt = cstXt;
    }

    public String getNmCtrprrent() {
        return nmCtrprrent;
    }

    public void setNmCtrprrent(String nmCtrprrent) {
        this.nmCtrprrent = nmCtrprrent;
    }

    public Integer getOfrtOff() {
        return ofrtOff;
    }

    public void setOfrtOff(Integer ofrtOff) {
        this.ofrtOff = ofrtOff;
    }

    public Float getOfrtCfin() {
        return ofrtCfin;
    }

    public void setOfrtCfin(Float ofrtCfin) {
        this.ofrtCfin = ofrtCfin;
    }

    public Date getSTime() {
        return sTime;
    }

    public void setSTime(Date sTime) {
        this.sTime = sTime;
    }

    public Date getITime() {
        return iTime;
    }

    public void setITime(Date iTime) {
        this.iTime = iTime;
    }

    public Date getFTime() {
        return fTime;
    }

    public void setIsmov(Boolean ismov) {
        this.ismov = ismov;
    }

    public void setFTime(Date fTime) {
        this.fTime = fTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getOnchange() {
        return onchange;
    }

    public void setOnchange(Integer onchange) {
        this.onchange = onchange;
    }

    public List<VideoupCtofrentas> getVideoupCtofrentasList() {
        return videoupCtofrentasList;
    }

    public VideoupCtofrentas getVideoupCtofrentas(){
        if(videoupCtofrentasList!=null && !videoupCtofrentasList.isEmpty()){
            return videoupCtofrentasList.get(0);
        }
        return null;
    }
    
    public void addVideoupCtofrentas(VideoupCtofrentas ofr){
        if(videoupCtofrentasList==null){
            videoupCtofrentasList=new ArrayList<VideoupCtofrentas>();
        }
        videoupCtofrentasList.add(ofr);
    }

    public void setVideoupCtofrentasList(List<VideoupCtofrentas> videoupCtofrentasList) {
        this.videoupCtofrentasList = videoupCtofrentasList;
    }

    public void clearVideoupCtofrentasList() {
        if(videoupCtofrentasList!=null){
            videoupCtofrentasList.clear();
        }
    }

    public VideoupBcodes getIdbc() {
        return idbc;
    }

    public void setIdbc(VideoupBcodes idbc) {
        this.idbc = idbc;
    }

    public VideoupRentas getIdrt() {
        return idrt;
    }

    public void setIdrt(VideoupRentas idrt) {
        this.idrt = idrt;
    }

    public boolean hasOfrts(){
        if(videoupCtofrentasList==null){
            return false;
        }
        return !videoupCtofrentasList.isEmpty();
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idir != null ? idir.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VideoupItemsrnt)) {
            return false;
        }
        VideoupItemsrnt other = (VideoupItemsrnt) object;
        if ((this.idir == null && other.idir != null) || (this.idir != null && !this.idir.equals(other.idir))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.videoup.entities.VideoupItemsrnt[ idir=" + idir + " ]";
    }
    
}
