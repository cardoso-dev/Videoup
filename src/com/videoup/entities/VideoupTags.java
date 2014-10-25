/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Pedro
 */
@Entity
@Table(name = "videoup_tags")
public class VideoupTags extends VideoupBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idtg")
    private Integer idtg;
    @Column(name = "nametag")
    private String nametag;
    @Column(name = "dimwidth")
    private Integer width;
    @Column(name = "dimheight")
    private Integer height;
    
    @OneToMany(mappedBy = "idtg")
    private List<VideoupTagitems> videoupTagitemsList;

    public VideoupTags() {
    }

    @Override
    public Integer getId() {
        return idtg;
    }

    public VideoupTags(Integer idtg) {
        this.idtg = idtg;
    }

    public Integer getIdtg() {
        return idtg;
    }

    public void setIdtg(Integer idtg) {
        this.idtg = idtg;
    }

    public String getNametag() {
        return nametag;
    }

    public void setNametag(String nametag) {
        this.nametag = nametag;
    }

    public List<VideoupTagitems> getVideoupTagitemsList() {
        return videoupTagitemsList;
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public void setVideoupTagitemsList(List<VideoupTagitems> videoupTagitemsList) {
        this.videoupTagitemsList = videoupTagitemsList;
    }

    public void addVideoupTagitems(VideoupTagitems item) {
        if(videoupTagitemsList==null){
            videoupTagitemsList=new ArrayList<VideoupTagitems>();
        }
        videoupTagitemsList.add(item);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idtg != null ? idtg.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VideoupTags)) {
            return false;
        }
        VideoupTags other = (VideoupTags) object;
        if ((this.idtg == null && other.idtg != null) || (this.idtg != null && !this.idtg.equals(other.idtg))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.videoup.entities.VideoupTags[ idtg=" + idtg + " ]";
    }
    
}
