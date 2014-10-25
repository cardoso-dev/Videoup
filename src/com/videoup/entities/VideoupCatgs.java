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
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Pedro
 */
@Entity
@Table(name = "videoup_catgs")
@XmlRootElement
public class VideoupCatgs extends VideoupBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idcg")
    private Integer idcg;
    @Basic(optional = false)
    @Column(name = "catg")
    private String catg;
    @Column(name = "dcatg")
    private String dcatg;
    @Lob
    @Column(name = "cond_tx")
    private String condTx;
    @OneToMany(mappedBy = "catg")
    private List<VideoupGames> videoupGamesList;
    @OneToMany(mappedBy = "catg")
    private List<VideoupMovies> videoupMoviesList;

    public VideoupCatgs() {
    }

    public VideoupCatgs(Integer idcg) {
        this.idcg = idcg;
    }

    public VideoupCatgs(Integer idcg, String catg) {
        this.idcg = idcg;
        this.catg = catg;
    }

    public VideoupCatgs(String catg) {
        this.catg = catg;
    }

    @Override
    public Integer getId() {
        return idcg;
    }
    
    public Integer getIdcg() {
        return idcg;
    }

    public void setIdcg(Integer idcg) {
        this.idcg = idcg;
    }

    public String getCatg() {
        return catg;
    }

    public void setCatg(String catg) {
        this.catg = catg;
    }

    public String getDcatg() {
        return dcatg;
    }

    public void setDcatg(String dcatg) {
        this.dcatg = dcatg;
    }

    public String getCondTx() {
        return condTx;
    }

    public void setCondTx(String condTx) {
        this.condTx = condTx;
    }

    @XmlTransient
    public List<VideoupGames> getVideoupGamesList() {
        return videoupGamesList;
    }

    public void setVideoupGamesList(List<VideoupGames> videoupGamesList) {
        this.videoupGamesList = videoupGamesList;
    }

    @XmlTransient
    public List<VideoupMovies> getVideoupMoviesList() {
        return videoupMoviesList;
    }

    public void setVideoupMoviesList(List<VideoupMovies> videoupMoviesList) {
        this.videoupMoviesList = videoupMoviesList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idcg != null ? idcg.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof VideoupCatgs)) {
            return false;
        }
        VideoupCatgs other = (VideoupCatgs) object;
        if ((this.idcg == null && other.idcg != null) || (this.idcg != null && !this.idcg.equals(other.idcg))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return catg;
    }
        
    /**
     * @return  -1_str=invalid_message; 0_str=warning_message; null=totally valid
     */
    public String validate2Import(){
        String warn="";
        if(catg==null){
            return "-1_Nombre de categoria sin especificar";
        }else if(catg.length()>155){
            warn+="0_Nombre de formato demasiado largo, ha sido truncado a 155 caracteres ";
            catg=catg.substring(0,155);
        }
        if(dcatg!=null && dcatg.length()>255){
            warn+="0_Descripcion de categoria demasiado largo, ha sido truncado a 255 caracteres ";
            dcatg=dcatg.substring(0,255);
        }
        if(warn.length()>0){
            return "0_"+warn;
        }
        return null;
    }
}
