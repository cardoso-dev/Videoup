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
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Pedro
 */
@Entity
@Table(name = "videoup_ctofrentas")
public class VideoupCtofrentas extends VideoupBaseEntity implements Serializable {
    @ManyToMany(mappedBy = "videoupCtofrentasList")
    private List<VideoupItemsrnt> videoupItemsrntList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idcof")
    private Integer idcof;
    @Basic(optional = false)
    @Column(name = "imovie")
    private boolean imovie;
    @Basic(optional = false)
    @Column(name = "ingame")
    private boolean ingame;
    @Basic(optional = false)
    @Column(name = "tpo")
    private int tpo;
    @Column(name = "aux_n")
    private Integer auxN;
    @Column(name = "aux_m")
    private Integer auxM;
    @Column(name = "aux_d1")
    @Temporal(TemporalType.DATE)
    private Date auxD1;
    @Column(name = "aux_d2")
    @Temporal(TemporalType.DATE)
    private Date auxD2;
    @Basic(optional = false)
    @Column(name = "tpsv")
    private int tpsv;
    @Column(name = "pr_desc")
    private Integer prDesc;
    @Basic(optional = false)
    @Column(name = "cst_spc")
    private float cstSpc;
    @Basic(optional = false)
    @Column(name = "namer")
    private String namer;
    @Column(name = "apl_lunes")
    private Boolean aplLunes;
    @Column(name = "apl_martes")
    private Boolean aplMartes;
    @Column(name = "apl_miercoles")
    private Boolean aplMiercoles;
    @Column(name = "apl_jueves")
    private Boolean aplJueves;
    @Column(name = "apl_viernes")
    private Boolean aplViernes;
    @Column(name = "apl_sabado")
    private Boolean aplSabado;
    @Column(name = "apl_domingo")
    private Boolean aplDomingo;
    @Column(name = "bypuntos")
    private Integer bypuntos;
    @Column(name = "aplydias")
    private Integer aplydias;
    @Column(name = "priority")
    private Integer priority;
    @JoinTable(name = "videoup_rnts_ctprof", joinColumns = {
        @JoinColumn(name = "idof", referencedColumnName = "idcof")}, inverseJoinColumns = {
        @JoinColumn(name = "idcpr", referencedColumnName = "idcpr")})
    @ManyToMany
    private List<VideoupCtprrentas> videoupCtprrentasList;
    @JoinTable(name = "videoup_frmts_ctprof", joinColumns = {
        @JoinColumn(name = "idof", referencedColumnName = "idcof")}, inverseJoinColumns = {
        @JoinColumn(name = "idfr", referencedColumnName = "idcf")})
    @ManyToMany
    private List<VideoupFormts> VideoupFormtsList;
    @JoinTable(name = "videoup_catgs_ctprof", joinColumns = {
        @JoinColumn(name = "idof", referencedColumnName = "idcof")}, inverseJoinColumns = {
        @JoinColumn(name = "idcg", referencedColumnName = "idcg")})
    @ManyToMany
    private List<VideoupCatgs> VideoupCatgsList;

    public VideoupCtofrentas() {
    }

    @Override
    public Integer getId() {
        return idcof;
    }

    public VideoupCtofrentas(Integer idcof) {
        this.idcof = idcof;
    }

    public VideoupCtofrentas(Integer idcof, boolean imovie, boolean ingame, int tpo, int tpsv, float cstSpc, String namer) {
        this.idcof = idcof;
        this.imovie = imovie;
        this.ingame = ingame;
        this.tpo = tpo;
        this.tpsv = tpsv;
        this.cstSpc = cstSpc;
        this.namer = namer;
    }

    public Integer getIdcof() {
        return idcof;
    }

    public void setIdcof(Integer idcof) {
        this.idcof = idcof;
    }

    public boolean getImovie() {
        return imovie;
    }

    public void setImovie(boolean imovie) {
        this.imovie = imovie;
    }

    public boolean getIngame() {
        return ingame;
    }

    public void setIngame(boolean ingame) {
        this.ingame = ingame;
    }

    public int getTpo() {
        return tpo;
    }

    public void setTpo(int tpo) {
        this.tpo = tpo;
    }

    public void setAplydias(Integer aplydias) {
        this.aplydias = aplydias;
    }

    public Integer getAplydias() {
        return aplydias;
    }

    public Integer getAuxN() {
        return auxN;
    }

    public void setAuxN(Integer auxN) {
        this.auxN = auxN;
    }

    public Integer getAuxM() {
        return auxM;
    }

    public void setAuxM(Integer auxM) {
        this.auxM = auxM;
    }

    public Date getAuxD1() {
        return auxD1;
    }

    public void setAuxD1(Date auxD1) {
        this.auxD1 = auxD1;
    }

    public Date getAuxD2() {
        return auxD2;
    }

    public void setAuxD2(Date auxD2) {
        this.auxD2 = auxD2;
    }

    public int getTpsv() {
        return tpsv;
    }

    public void setTpsv(int tpsv) {
        this.tpsv = tpsv;
    }

    public Integer getPrDesc() {
        return prDesc;
    }

    public void setPrDesc(Integer prDesc) {
        this.prDesc = prDesc;
    }

    public List<VideoupFormts> getVideoupFormtsList() {
        return VideoupFormtsList;
    }

    public void setVideoupFormtsList(List<VideoupFormts> VideoupFormtsList) {
        this.VideoupFormtsList = VideoupFormtsList;
    }
    
    public void addVideoupFormts(VideoupFormts ctpr) {
        if(VideoupFormtsList==null){
            VideoupFormtsList=new ArrayList<VideoupFormts>();
        }
        VideoupFormtsList.add(ctpr);
    }

    public void clearVideoupFormtsList() {
        if(VideoupFormtsList!=null){
            VideoupFormtsList.clear();
        }
    }

    public float getCstSpc() {
        return cstSpc;
    }

    public void setCstSpc(float cstSpc) {
        this.cstSpc = cstSpc;
    }

    public String getNamer() {
        return namer;
    }

    public void setNamer(String namer) {
        this.namer = namer;
    }

    public Boolean getAplLunes() {
        return aplLunes;
    }

    public void setAplLunes(Boolean aplLunes) {
        this.aplLunes = aplLunes;
    }

    public Boolean getAplMartes() {
        return aplMartes;
    }

    public void setAplMartes(Boolean aplMartes) {
        this.aplMartes = aplMartes;
    }

    public Boolean getAplMiercoles() {
        return aplMiercoles;
    }

    public void setAplMiercoles(Boolean aplMiercoles) {
        this.aplMiercoles = aplMiercoles;
    }

    public Boolean getAplJueves() {
        return aplJueves;
    }

    public void setAplJueves(Boolean aplJueves) {
        this.aplJueves = aplJueves;
    }

    public Boolean getAplViernes() {
        return aplViernes;
    }

    public void setAplViernes(Boolean aplViernes) {
        this.aplViernes = aplViernes;
    }

    public Boolean getAplSabado() {
        return aplSabado;
    }

    public void setAplSabado(Boolean aplSabado) {
        this.aplSabado = aplSabado;
    }

    public Boolean getAplDomingo() {
        return aplDomingo;
    }

    public void setAplDomingo(Boolean aplDomingo) {
        this.aplDomingo = aplDomingo;
    }

    public Integer getBypuntos() {
        return bypuntos;
    }

    public void setBypuntos(Integer bypuntos) {
        this.bypuntos = bypuntos;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
    
    public List<VideoupCtprrentas> getVideoupCtprrentasList() {
        return videoupCtprrentasList;
    }

    public void setVideoupCtprrentasList(List<VideoupCtprrentas> videoupCtprrentasList) {
        this.videoupCtprrentasList = videoupCtprrentasList;
    }

    public void addVideoupCtprrentas(VideoupCtprrentas ctpr) {
        if(videoupCtprrentasList==null){
            videoupCtprrentasList=new ArrayList<VideoupCtprrentas>();
        }
        videoupCtprrentasList.add(ctpr);
    }

    public void clearVideoupCtprrentasList() {
        if(videoupCtprrentasList!=null){
            videoupCtprrentasList.clear();
        }
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idcof != null ? idcof.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VideoupCtofrentas)) {
            return false;
        }
        VideoupCtofrentas other = (VideoupCtofrentas) object;
        if ((this.idcof == null && other.idcof != null) || (this.idcof != null && !this.idcof.equals(other.idcof))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return namer;
    }

    public List<VideoupItemsrnt> getVideoupItemsrntList() {
        return videoupItemsrntList;
    }

    public void setVideoupItemsrntList(List<VideoupItemsrnt> videoupItemsrntList) {
        this.videoupItemsrntList = videoupItemsrntList;
    }

    public List<VideoupCatgs> getVideoupCatgsList() {
        return VideoupCatgsList;
    }

    public void setVideoupCatgsList(List<VideoupCatgs> VideoupCatgsList) {
        this.VideoupCatgsList = VideoupCatgsList;
    }
    
    public void addVideoupCatgs(VideoupCatgs ctpr) {
        if(VideoupCatgsList==null){
            VideoupCatgsList=new ArrayList<VideoupCatgs>();
        }
        VideoupCatgsList.add(ctpr);
    }

    public void clearVideoupCatgsList() {
        if(VideoupCatgsList!=null){
            VideoupCatgsList.clear();
        }
    }
    
}
