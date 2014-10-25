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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Pedro
 */
@Entity
@Table(name = "videoup_formts")
@XmlRootElement
public class VideoupFormts extends VideoupBaseEntity implements Serializable {
    @OneToMany(mappedBy = "frmt")
    private List<VideoupBcodes> videoupBcodesList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idcf")
    private Integer idcf;
    @Basic(optional = false)
    @Column(name = "frmt")
    private String frmt;
    @Column(name = "dfrmt")
    private String dfrmt;

    public VideoupFormts() {
    }

    public VideoupFormts(Integer idcf) {
        this.idcf = idcf;
    }

    public VideoupFormts(Integer idcf, String frmt) {
        this.idcf = idcf;
        this.frmt = frmt;
    }

    public VideoupFormts(String frmt) {
        this.frmt = frmt;
    }

    @Override
    public Integer getId() {
        return idcf;
    }

    public Integer getIdcf() {
        return idcf;
    }

    public void setIdcf(Integer idcf) {
        this.idcf = idcf;
    }

    public String getFrmt() {
        return frmt;
    }

    public void setFrmt(String frmt) {
        this.frmt = frmt;
    }

    public String getDfrmt() {
        return dfrmt;
    }

    public void setDfrmt(String dfrmt) {
        this.dfrmt = dfrmt;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idcf != null ? idcf.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VideoupFormts)) {
            return false;
        }
        VideoupFormts other = (VideoupFormts) object;
        if ((this.idcf == null && other.idcf != null) || (this.idcf != null && !this.idcf.equals(other.idcf))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return frmt;
    }

    public List<VideoupBcodes> getVideoupBcodesList() {
        return videoupBcodesList;
    }

    public void setVideoupBcodesList(List<VideoupBcodes> videoupBcodesList) {
        this.videoupBcodesList = videoupBcodesList;
    }
    
    /**
     * @return  -1_str=invalid_message; 0_str=warning_message; null=totally valid
     */
    public String validate2Import(){
        String warn="";
        if(frmt==null){
            return "-1_Nombre de formato sin especificar";
        }else if(frmt.length()>154){
            warn+="0_Nombre de formato demasiado largo, ha sido truncado a 155 caracteres ";
            frmt=frmt.substring(0,154);
        }
        if(dfrmt!=null && dfrmt.length()>255){
            warn+="0_Descripcion de formato demasiado largo, ha sido truncado a 255 caracteres ";
            dfrmt=dfrmt.substring(0,255);
        }
        if(warn.length()>0){
            return "0_"+warn;
        }
        return null;
    }
}
