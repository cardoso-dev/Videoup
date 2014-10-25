/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Pedro
 */
@Entity
@Table(name = "videoup_ctprrentas")
@XmlRootElement
public class VideoupCtprrentas extends VideoupBaseEntity implements Serializable {
    @OneToMany(mappedBy = "idcpr")
    private List<VideoupMovies> videoupMoviesList;
    @OneToMany(mappedBy = "idcpr")
    private List<VideoupGames> videoupGamesList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idcpr")
    private Integer idcpr;
    @Basic(optional = false)
    @Column(name = "namec")
    private String namec;
    @Basic(optional = false)
    @Column(name = "costou")
    private float costou;
    @Basic(optional = false)
    @Column(name = "unidad_nm")
    private String unidadNm;
    @Basic(optional = false)
    @Column(name = "unidad_mins")
    private int unidadMins;
    @Basic(optional = false)
    @Column(name = "uns_base")
    private int unsBase;
    @Basic(optional = false)
    @Column(name = "cstu_xtra")
    private float cstuXtra;
    @JoinTable(name = "videoup_rnts_ctprof", joinColumns = {
        @JoinColumn(name = "idcpr", referencedColumnName = "idcpr")}, inverseJoinColumns = {
        @JoinColumn(name = "idof", referencedColumnName = "idcof")})
    @ManyToMany
    private List<VideoupCtofrentas> videoupCtofrentasList;

    public VideoupCtprrentas() {
    }

    public VideoupCtprrentas(Integer idcpr) {
        this.idcpr = idcpr;
    }

    public VideoupCtprrentas(Integer idcpr, String namec, float costou, String unidadNm, int unidadMins, int unsBase, float cstuXtra) {
        this.idcpr = idcpr;
        this.namec = namec;
        this.costou = costou;
        this.unidadNm = unidadNm;
        this.unidadMins = unidadMins;
        this.unsBase = unsBase;
        this.cstuXtra = cstuXtra;
    }

    @Override
    public Integer getId() {
        return idcpr;
    }

    public Integer getIdcpr() {
        return idcpr;
    }

    public void setIdcpr(Integer idcpr) {
        this.idcpr = idcpr;
    }

    public String getNamec() {
        return namec;
    }

    public void setNamec(String namec) {
        this.namec = namec;
    }

    public float getCostou() {
        return costou;
    }

    public void setCostou(float costou) {
        this.costou = costou;
    }

    public String getUnidadNm() {
        return unidadNm;
    }

    public void setUnidadNm(String unidadNm) {
        this.unidadNm = unidadNm;
    }

    public int getUnidadMins() {
        return unidadMins;
    }

    public void setUnidadMins(int unidadMins) {
        this.unidadMins = unidadMins;
    }

    public int getUnsBase() {
        return unsBase;
    }

    public void setUnsBase(int unsBase) {
        this.unsBase = unsBase;
    }

    public float getCstuXtra() {
        return cstuXtra;
    }

    public void setCstuXtra(float cstuXtra) {
        this.cstuXtra = cstuXtra;
    }

    @XmlTransient
    public List<VideoupCtofrentas> getVideoupCtofrentasList() {
        return videoupCtofrentasList;
    }

    public void setVideoupCtofrentasList(List<VideoupCtofrentas> videoupCtofrentasList) {
        this.videoupCtofrentasList = videoupCtofrentasList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idcpr != null ? idcpr.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VideoupCtprrentas)) {
            return false;
        }
        VideoupCtprrentas other = (VideoupCtprrentas) object;
        if ((this.idcpr == null && other.idcpr != null) || (this.idcpr != null && !this.idcpr.equals(other.idcpr))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return namec;
    }

    @XmlTransient
    public List<VideoupMovies> getVideoupMoviesList() {
        return videoupMoviesList;
    }

    public void setVideoupMoviesList(List<VideoupMovies> videoupMoviesList) {
        this.videoupMoviesList = videoupMoviesList;
    }

    @XmlTransient
    public List<VideoupGames> getVideoupGamesList() {
        return videoupGamesList;
    }

    public void setVideoupGamesList(List<VideoupGames> videoupGamesList) {
        this.videoupGamesList = videoupGamesList;
    }

}
