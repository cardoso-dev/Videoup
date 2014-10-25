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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Pedro
 */
@Entity
@Table(name = "videoup_tagitems")
public class VideoupTagitems extends VideoupBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idti")
    private Integer idti;
    @Basic(optional = false)
    @Column(name = "itemtype")
    private int itemtype;
    @Basic(optional = false)
    @Column(name = "title")
    private String title;
    @Column(name = "texto")
    private String texto;
    @Column(name = "bcodetype")
    private String bcodetype;
    @Column(name = "bcodevalue")
    private String bcodevalue;
    @Lob
    @Column(name = "img")
    private byte[] img;
    @Basic(optional = false)
    @Column(name = "dimwidth")
    private int dimwidth;
    @Basic(optional = false)
    @Column(name = "dimheight")
    private int dimheight;
    @Basic(optional = false)
    @Column(name = "loc_x")
    private int locX;
    @Basic(optional = false)
    @Column(name = "loc_y")
    private int locY;
    @JoinColumn(name = "idtg", referencedColumnName = "idtg")
    @ManyToOne
    private VideoupTags idtg;

    public VideoupTagitems() {
    }

    public VideoupTagitems(Integer idti) {
        this.idti = idti;
    }

    @Override
    public Integer getId() {
        return idti;
    }

    public VideoupTagitems(Integer idti, int itemtype, String title, int dimwidth, int dimheight, int locX, int locY) {
        this.idti = idti;
        this.itemtype = itemtype;
        this.title = title;
        this.dimwidth = dimwidth;
        this.dimheight = dimheight;
        this.locX = locX;
        this.locY = locY;
    }

    public Integer getIdti() {
        return idti;
    }

    public void setIdti(Integer idti) {
        this.idti = idti;
    }

    public int getItemtype() {
        return itemtype;
    }

    public void setItemtype(int itemtype) {
        this.itemtype = itemtype;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getBcodetype() {
        return bcodetype;
    }

    public void setBcodetype(String bcodetype) {
        this.bcodetype = bcodetype;
    }

    public String getBcodevalue() {
        return bcodevalue;
    }

    public void setBcodevalue(String bcodevalue) {
        this.bcodevalue = bcodevalue;
    }

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }

    public int getDimwidth() {
        return dimwidth;
    }

    public void setDimwidth(int dimwidth) {
        this.dimwidth = dimwidth;
    }

    public int getDimheight() {
        return dimheight;
    }

    public void setDimheight(int dimheight) {
        this.dimheight = dimheight;
    }

    public int getLocX() {
        return locX;
    }

    public void setLocX(int locX) {
        this.locX = locX;
    }

    public int getLocY() {
        return locY;
    }

    public void setLocY(int locY) {
        this.locY = locY;
    }

    public VideoupTags getIdtg() {
        return idtg;
    }

    public void setIdtg(VideoupTags idtg) {
        this.idtg = idtg;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idti != null ? idti.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VideoupTagitems)) {
            return false;
        }
        VideoupTagitems other = (VideoupTagitems) object;
        if ((this.idti == null && other.idti != null) || (this.idti != null && !this.idti.equals(other.idti))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.videoup.entities.VideoupTagitems[ idti=" + idti + " ]";
    }
    
}
