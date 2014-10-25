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
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Pedro
 */
@Entity
@Table(name = "videoup_conf")
@XmlRootElement
public class VideoupConf extends VideoupBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idcc")
    private Integer idcc;
    @Column(name = "dni_def")
    private String dniDef;
    @Column(name = "dni_rgexp")
    private String dniRgexp;
    @Column(name = "n_vclub")
    private String nVclub;
    @Lob
    @Column(name = "logo_vclub")
    private byte[] logoVclub;
    @Column(name = "using_web")
    private Boolean usingWeb;
    @Basic(optional = false)
    @Lob
    @Column(name = "contrato")
    private String contrato;
    @Column(name = "pcode_rgexp")
    private String pcodeRgexp;
    @Basic(optional = false)
    @Column(name = "movtypebarcode")
    private String movtypebarcode;
    @Basic(optional = false)
    @Column(name = "gamtypebarcode")
    private String gamtypebarcode;
    @Basic(optional = false)
    @Column(name = "socvtypebarcode")
    private String socvtypebarcode;
    @Column(name = "maxoffrts")
    private Integer maxoffrts;
    @Column(name = "pagesize")
    private Integer pagesize;
    @Column(name = "chgEstreno")
    private Boolean changeFinEstreno;
    @Column(name = "fracmin_extra")
    private Integer fracMinExtra;
    
    public VideoupConf() {
    }

    public VideoupConf(Integer idcc) {
        this.idcc = idcc;
    }

    public VideoupConf(Integer idcc, String contrato, String movtypebarcode, String gamtypebarcode, String socvtypebarcode) {
        this.idcc = idcc;
        this.contrato = contrato;
        this.movtypebarcode = movtypebarcode;
        this.gamtypebarcode = gamtypebarcode;
        this.socvtypebarcode = socvtypebarcode;
    }

    @Override
    public Integer getId() {
        return idcc;
    }
    
    public Integer getIdcc() {
        return idcc;
    }

    public void setIdcc(Integer idcc) {
        this.idcc = idcc;
    }

    public String getDniDef() {
        return dniDef;
    }

    public void setDniDef(String dniDef) {
        this.dniDef = dniDef;
    }

    public Integer getFracMinExtra() {
        return fracMinExtra;
    }

    public void setFracMinExtra(Integer fracMinExtra) {
        this.fracMinExtra = fracMinExtra;
    }

    public String getDniRgexp() {
        return dniRgexp;
    }

    public String getnVclub() {
        return nVclub;
    }

    public Integer getMaxoffrts() {
        return maxoffrts;
    }

    public void setPagesize(Integer pagesize) {
        this.pagesize = pagesize;
    }

    public Integer getPagesize() {
        return pagesize;
    }

    public void setnVclub(String nVclub) {
        this.nVclub = nVclub;
    }

    public void setMaxoffrts(Integer maxoffrts) {
        this.maxoffrts = maxoffrts;
    }

    public void setDniRgexp(String dniRgexp) {
        this.dniRgexp = dniRgexp;
    }

    public String getNVclub() {
        return nVclub;
    }

    public void setNVclub(String nVclub) {
        this.nVclub = nVclub;
    }

    public byte[] getLogoVclub() {
        return logoVclub;
    }

    public Boolean getChangeFinEstreno() {
        if(changeFinEstreno==null){
            changeFinEstreno=false;
        }
        return changeFinEstreno;
    }

    public void setChangeFinEstreno(Boolean changeFinEstreno) {
        this.changeFinEstreno = changeFinEstreno;
    }

    public void setLogoVclub(byte[] logoVclub) {
        this.logoVclub = logoVclub;
    }

    public Boolean getUsingWeb() {
        return usingWeb;
    }

    public void setUsingWeb(Boolean usingWeb) {
        this.usingWeb = usingWeb;
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    public String getPcodeRgexp() {
        return pcodeRgexp;
    }

    public void setPcodeRgexp(String pcodeRgexp) {
        this.pcodeRgexp = pcodeRgexp;
    }

    public String getMovtypebarcode() {
        return movtypebarcode;
    }

    public void setMovtypebarcode(String movtypebarcode) {
        this.movtypebarcode = movtypebarcode;
    }

    public String getGamtypebarcode() {
        return gamtypebarcode;
    }

    public void setGamtypebarcode(String gamtypebarcode) {
        this.gamtypebarcode = gamtypebarcode;
    }

    public String getSocvtypebarcode() {
        return socvtypebarcode;
    }

    public void setSocvtypebarcode(String socvtypebarcode) {
        this.socvtypebarcode = socvtypebarcode;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idcc != null ? idcc.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VideoupConf)) {
            return false;
        }
        VideoupConf other = (VideoupConf) object;
        if ((this.idcc == null && other.idcc != null) || (this.idcc != null && !this.idcc.equals(other.idcc))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.videoup.entities.VideoupConf[ idcc=" + idcc + " ]";
    }
    
}
