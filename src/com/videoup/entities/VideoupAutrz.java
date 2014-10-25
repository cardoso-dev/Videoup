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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Pedro
 */
@Entity
@Table(name = "videoup_autrz")
@XmlRootElement
public class VideoupAutrz extends VideoupBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idtz")
    private Integer idtz;
    @Basic(optional = false)
    @Column(name = "pname")
    private String pname;
    @JoinColumn(name = "idct", referencedColumnName = "idct")
    @ManyToOne
    private VideoupCustomers idct;

    public VideoupAutrz() {
    }

    public VideoupAutrz(Integer idtz) {
        this.idtz = idtz;
    }

    public VideoupAutrz(String pname) {
        this.pname = pname;
    }

    @Override
    public Integer getId() {
        return idtz;
    }
    
    public Integer getIdtz() {
        return idtz;
    }

    public void setIdtz(Integer idtz) {
        this.idtz = idtz;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public VideoupCustomers getIdct() {
        return idct;
    }

    public void setIdct(VideoupCustomers idct) {
        this.idct = idct;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idtz != null ? idtz.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VideoupAutrz)) {
            return false;
        }
        VideoupAutrz other = (VideoupAutrz) object;
        if ((this.idtz == null && other.idtz != null) || (this.idtz != null && !this.idtz.equals(other.idtz))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.videoup.entities.VideoupAutrz[ idtz=" + idtz + " ]";
    }
    /**
     * @return  -1_str=invalid_message; 0_str=warning_message; null=totally valid
     */
    public String validate2Import(){
        String warn="";
        if(pname==null){
            return "-1_Nombre de representante sin especificar";
        }else if(pname.length()>255){
            warn="Nombre de representante demasiado largo, ha sido truncado a 255 caracteres ";
            pname=pname.substring(0,255);
        }
        if(warn.length()>0){
            return "0_"+warn;
        }
        return null;
    }
}
