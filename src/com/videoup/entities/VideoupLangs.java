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
@Table(name = "videoup_langs")
@XmlRootElement
public class VideoupLangs extends VideoupBaseEntity implements Serializable {
    @Basic(optional = false)
    @Lob
    @Column(name = "img")
    private byte[] img;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idit")
    private Integer idit;
    @Column(name = "name")
    private String name;
    @OneToMany(mappedBy = "idit")
    private List<VideoupBcdlang> videoupBcdlangList;

    public VideoupLangs() {
    }

    public VideoupLangs(Integer idit) {
        this.idit = idit;
    }

    public VideoupLangs(String nombre, byte[] img) {
        this.name = nombre;
        this.img = img;
    }

    @Override
    public Integer getId() {
        return idit;
    }

    public Integer getIdit() {
        return idit;
    }

    public void setIdit(Integer idit) {
        this.idit = idit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlTransient
    public List<VideoupBcdlang> getVideoupBcdlangList() {
        return videoupBcdlangList;
    }

    public void setVideoupBcdlangList(List<VideoupBcdlang> videoupBcdlangList) {
        this.videoupBcdlangList = videoupBcdlangList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idit != null ? idit.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VideoupLangs)) {
            return false;
        }
        VideoupLangs other = (VideoupLangs) object;
        if ((this.idit == null && other.idit != null) || (this.idit != null && !this.idit.equals(other.idit))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.videoup.entities.VideoupLangs[ idit=" + idit + " ]";
    }

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }
    
}
