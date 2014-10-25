/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Pedro
 */
@Entity
@Table(name = "videoup_bcodes")
public class VideoupBcodes extends VideoupBaseEntity implements Serializable {
    @OneToMany(mappedBy = "idbc")
    private List<VideoupItemsvnt> videoupItemsvntList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idbc")
    private Integer idbc;
    @Column(name = "barcode")
    private String barcode;
    @Basic(optional = false)
    @Column(name = "vendible")
    private boolean vendible;
    @Basic(optional = false)
    @Column(name = "pr_venta")
    private float prVenta;
    @Column(name = "pr_compra")
    private Float prCompra;
    @Column(name = "status")
    private String status;
    @ManyToMany(mappedBy = "videoupMBcodesList")
    private List<VideoupMovies> videoupMoviesList;
    @ManyToMany(mappedBy = "videoupGBcodesList")
    private List<VideoupGames> videoupGamesList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idbc")
    private List<VideoupItemsrnt> videoupItemsrntList;
    @JoinColumn(name = "frmt", referencedColumnName = "idcf")
    @ManyToOne
    private VideoupFormts frmt;

    public VideoupBcodes() {
    }

    @Override
    public Integer getId() {
        return idbc;
    }

    public VideoupBcodes(Integer idbc) {
        this.idbc = idbc;
    }

    public VideoupBcodes(Integer idbc, boolean vendible, float prVenta) {
        this.idbc = idbc;
        this.vendible = vendible;
        this.prVenta = prVenta;
    }

    public VideoupBcodes(String barcode, boolean vendible, float prVenta, String status) {
        this.barcode = barcode;
        this.vendible = vendible;
        this.prVenta = prVenta;
        this.status = status;
    }

    public Integer getIdbc() {
        return idbc;
    }

    public void setIdbc(Integer idbc) {
        this.idbc = idbc;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public boolean getVendible() {
        return vendible;
    }

    public void setVendible(boolean vendible) {
        this.vendible = vendible;
    }

    public float getPrVenta() {
        return prVenta;
    }

    public void setPrVenta(float prVenta) {
        this.prVenta = prVenta;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsmov() {
        if(videoupMoviesList!=null && videoupMoviesList.size()>0){
            return true;
        }
        return false;
    }

    public VideoupMovies getVideoupMovie(){
        if(videoupMoviesList!=null && videoupMoviesList.size()>0){
            return videoupMoviesList.get(0);
        }
        return null;
    }

    public List<VideoupMovies> getVideoupMoviesList() {
        return videoupMoviesList;
    }

    public Float getPrCompra() {
        return prCompra;
    }

    public void setPrCompra(float prCompra) {
        this.prCompra = prCompra;
    }

    public void addVideoupMovies(VideoupMovies mov){
        if(videoupMoviesList==null){
            videoupMoviesList=new ArrayList<VideoupMovies>();
        }
        videoupMoviesList.add(mov);
    }

    public void setVideoupMoviesList(List<VideoupMovies> videoupMoviesList) {
        this.videoupMoviesList = videoupMoviesList;
    }

    public int getCountVideoupMovies(){
        if(videoupMoviesList==null){ return 0; }
        return videoupMoviesList.size();
    }

    public VideoupGames getVideoupGame(){
        if(videoupGamesList!=null && videoupGamesList.size()>0){
            return videoupGamesList.get(0);
        }
        return null;
    }

    public List<VideoupGames> getVideoupGamesList() {
        return videoupGamesList;
    }

    public void addVideoupGames(VideoupGames gam){
        if(videoupGamesList==null){
            videoupGamesList=new ArrayList<VideoupGames>();
        }
        videoupGamesList.add(gam);
    }
    
    public void setVideoupGamesList(List<VideoupGames> videoupGamesList) {
        this.videoupGamesList = videoupGamesList;
    }

    public int getCountVideoupGames(){
        if(videoupGamesList==null){ return 0; }
        return videoupGamesList.size();
    }

    public VideoupFormts getFrmt() {
        return frmt;
    }

    public void setFrmt(VideoupFormts frmt) {
        this.frmt = frmt;
    }

    public String getFormatName(){
        return frmt.getFrmt();
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idbc != null ? idbc.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VideoupBcodes)) {
            return false;
        }
        VideoupBcodes other = (VideoupBcodes) object;
        if ((this.idbc == null && other.idbc != null) || (this.idbc != null && !this.idbc.equals(other.idbc))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.videoup.entities.VideoupBcodes[ idbc=" + idbc + " ]";
    }

    public List<VideoupItemsrnt> getVideoupItemsrntList() {
        return videoupItemsrntList;
    }

    public void setVideoupItemsrntList(List<VideoupItemsrnt> videoupItemsrntList) {
        this.videoupItemsrntList = videoupItemsrntList;
    }

    public List<VideoupItemsvnt> getVideoupItemsvntList() {
        return videoupItemsvntList;
    }

    public void setVideoupItemsvntList(List<VideoupItemsvnt> videoupItemsvntList) {
        this.videoupItemsvntList = videoupItemsvntList;
    }

    /**
     * @return  -1_str=invalid_message; 0_str=warning_message; null=totally valid
     */
    public String validate2Import(){
        String warn="";
        if(barcode==null){
            return "-1_Valor de codigo de barras sin especificar";
        }else if(barcode.length()>12){
            return "-1_Valor de codigo de barras muy largo (maximo 12 caracteres)";
        }
        if(status!=null && status.length()>32){
            warn+="0_Estado de copia demasiado largo, ha sido truncado a 32 caracteres ";
            status=status.substring(0,32);
        }
        if(warn.length()>0){
            return "0_"+warn;
        }
        return null;
    }
}
