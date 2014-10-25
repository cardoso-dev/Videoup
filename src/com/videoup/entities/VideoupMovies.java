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
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Pedro
 */
@Entity
@Table(name = "videoup_movies")
public class VideoupMovies extends VideoupBaseEntity implements Serializable, Cloneable{
    @Lob
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idcm")
    private Integer idcm;
    @Basic(optional = false)
    @Column(name = "titulo")
    private String titulo;
    @Column(name = "director")
    private String director;
    @Lob
    @Column(name = "trailer_url")
    private String trailerUrl;
    @Column(name = "prgistas")
    private String prgistas;
    @Basic(optional = false)
    @Lob
    @Column(name = "sinopsis")
    private String sinopsis;
    @Basic(optional = false)
    @Column(name = "ldate")
    @Temporal(TemporalType.DATE)
    private Date ldate;
    @Column(name = "procmpy")
    private String procmpy;
    @Basic(optional = false)
    @Column(name = "dmin")
    private int dmin;
    @Basic(optional = false)
    @Column(name = "clasif")
    private String clasif;
    @Column(name = "estreno_until")
    @Temporal(TemporalType.TIMESTAMP)
    private Date estrenoUntil;
    @Basic(optional = false)
    @Column(name = "num_alqs")
    private int numAlqs;
    @Basic(optional = false)
    @Column(name = "num_solds")
    private int numSolds;
    @Basic(optional = false)
    @Column(name = "num_changes")
    private int numChanges;
    @Column(name = "anyo")
    private int anyo;
    @Column(name = "valoracion")
    private Integer valoracion;
    @JoinTable(name = "videoup_bcdmov", joinColumns = {
        @JoinColumn(name = "idcm", referencedColumnName = "idcm")}, inverseJoinColumns = {
        @JoinColumn(name = "idbc", referencedColumnName = "idbc")})
    @ManyToMany
    private List<VideoupBcodes> videoupMBcodesList;
    @JoinColumn(name = "catg", referencedColumnName = "idcg")
    @ManyToOne
    private VideoupCatgs catg;
    @JoinColumn(name = "idcpr", referencedColumnName = "idcpr")
    @ManyToOne
    private VideoupCtprrentas idcpr;

    public VideoupMovies() {
    }

    @Override
    public Integer getId() {
        return idcm;
    }

    public VideoupMovies(Integer idcm) {
        this.idcm = idcm;
    }

    public VideoupMovies(Integer idcm, String titulo, String sinopsis, Date ldate, int dmin, String clasif) {
        this.idcm = idcm;
        this.titulo = titulo;
        this.sinopsis = sinopsis;
        this.ldate = ldate;
        this.dmin = dmin;
        this.clasif = clasif;
    }

    public Integer getIdcm() {
        return idcm;
    }

    public void setIdcm(Integer idcm) {
        this.idcm = idcm;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getTrailerUrl() {
        return trailerUrl;
    }

    public void setNumAlqs(int numAlqs) {
        this.numAlqs = numAlqs;
    }

    public void setNumSolds(int numSolds) {
        this.numSolds = numSolds;
    }

    public void setNumChanges(int numChanges) {
        this.numChanges = numChanges;
    }

    public void setAnyo(Integer anyo) {
        if(anyo==null){
            this.anyo=0;
        }else{
            this.anyo = anyo;
        }
    }

    public int getAnyo() {
        return anyo;
    }

    public int getNumAlqs() {
        return numAlqs;
    }

    public int getNumSolds() {
        return numSolds;
    }

    public int getNumChanges() {
        return numChanges;
    }

    public void setTrailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
    }

    public String getPrgistas() {
        return prgistas;
    }

    public void setPrgistas(String prgistas) {
        this.prgistas = prgistas;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    public Date getLdate() {
        return ldate;
    }

    public void setLdate(Date ldate) {
        this.ldate = ldate;
    }

    public String getProcmpy() {
        return procmpy;
    }

    public void setProcmpy(String procmpy) {
        this.procmpy = procmpy;
    }

    public int getDmin() {
        return dmin;
    }

    public void setDmin(int dmin) {
        this.dmin = dmin;
    }

    public String getClasif() {
        return clasif;
    }

    public void setClasif(String clasif) {
        this.clasif = clasif;
    }

    public Date getEstrenoUntil() {
        return estrenoUntil;
    }

    public void setEstrenoUntil(Date estrenoUntil) {
        this.estrenoUntil = estrenoUntil;
    }

    public Integer getValoracion() {
        return valoracion;
    }

    public void setValoracion(Integer valoracion) {
        this.valoracion = valoracion;
    }

    public List<VideoupBcodes> getvideoupMBcodesList() {
        return videoupMBcodesList;
    }

    public void setvideoupMBcodesList(List<VideoupBcodes> videoupMBcodesList) {
        this.videoupMBcodesList = videoupMBcodesList;
    }
    
    public void addVideoupBcodes(VideoupBcodes bcd) {
        if(videoupMBcodesList==null){
            videoupMBcodesList=new ArrayList<VideoupBcodes>();
        }
        if(!videoupMBcodesList.contains(bcd)){
            videoupMBcodesList.add(bcd);
        }
    }

    public void removeVideoupBcodes(VideoupBcodes bcd) {
        videoupMBcodesList.remove(bcd);
    }

    public VideoupCatgs getCatg() {
        return catg;
    }

    public void setCatg(VideoupCatgs catg) {
        this.catg = catg;
    }

    public VideoupCtprrentas getIdcpr() {
        return idcpr;
    }

    public void setIdcpr(VideoupCtprrentas idcpr) {
        this.idcpr = idcpr;
    }

    public String getFormatos(){
        String formts="";
        if(videoupMBcodesList!=null && !videoupMBcodesList.isEmpty()){
            for(VideoupBcodes cpy: videoupMBcodesList){
                formts+=(formts.length()>0?", ":"")+cpy.getFormatName();
            }
        }
        return formts;
    }
    
    public String getCodigosBar(){
        String cbars="";
        if(videoupMBcodesList!=null && !videoupMBcodesList.isEmpty()){
            for(VideoupBcodes cpy: videoupMBcodesList){
                cbars+=(cbars.length()>0?", ":"")+cpy.getBarcode();
            }
        }
        return cbars;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idcm != null ? idcm.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VideoupMovies)) {
            return false;
        }
        VideoupMovies other = (VideoupMovies) object;
        if ((this.idcm == null && other.idcm != null) || (this.idcm != null && !this.idcm.equals(other.idcm))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return titulo;
    }
    
    /**
     * @return  -1_str=invalid_message; 0_str=warning_message; null=totally valid
     */
    public String validate2Import(){
        String warn="";
        if(titulo==null){
            return "-1_Titulo sin especificar";
        }else if(titulo.length()>155){
            warn+="0_Titulo demasiado largo, ha sido truncado a 155 caracteres ";
            titulo=titulo.substring(0,155);
        }
        if(sinopsis==null){
            return "-1_Sinopsis sin especificar";
        }
        if(director!=null && director.length()>155){
            warn+="0_Director demasiado largo, ha sido truncado a 155 caracteres ";
            director=director.substring(0,155);
        }
        if(prgistas!=null && prgistas.length()>255){
            warn+="0_Texto de protagonistas demasiado largo, ha sido truncado a 255 caracteres ";
            prgistas=prgistas.substring(0,155);
        }
        if(procmpy!=null && procmpy.length()>155){
            warn+="0_Nombre de compañia productora demasiado largo, ha sido truncado a 255 caracteres ";
            procmpy=procmpy.substring(0,155);
        }
        if(clasif!=null && clasif.length()>5){
            warn+="0_Clasificacion demasiado largo, ha sido truncado a 5 caracteres ";
            clasif=clasif.substring(0,5);
        }
        if(warn.length()>0){
            return "0_"+warn;
        }
        return null;
    }

    @Override
    public VideoupMovies clone(){
        VideoupMovies newIns=new VideoupMovies();
        newIns.setTitulo(titulo+" **Duplicado");
        newIns.setDirector(director);
        newIns.setTrailerUrl(trailerUrl);
        newIns.setPrgistas(prgistas);
        newIns.setSinopsis(sinopsis);
        newIns.setLdate(ldate);
        newIns.setProcmpy(procmpy);
        newIns.setDmin(dmin);
        newIns.setClasif(clasif);
        newIns.setCatg(catg);
        newIns.setIdcpr(idcpr);
        newIns.setEstrenoUntil(estrenoUntil);
        return newIns;
    }
}
