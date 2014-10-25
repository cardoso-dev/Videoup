/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.entities;

import com.videoup.utils.Globals;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Pedro
 */
@Entity
@Table(name = "videoup_customers")
@XmlRootElement
public class VideoupCustomers extends VideoupBaseEntity implements Serializable {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idcli")
    private List<VideoupVentas> videoupVentasList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idct")
    private Integer idct;
    @Column(name = "idweb")
    private Integer idweb;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @Column(name = "applldos")
    private String applldos;
    @Basic(optional = false)
    @Column(name = "dni")
    private String dni;
    @Column(name = "cod_cst")
    private String codCst;
    @Column(name = "email")
    private String email;
    @Basic(optional = false)
    @Column(name = "addr")
    private String addr;
    @Column(name = "codp")
    private String codp;
    @Column(name = "city")
    private String city;
    @Column(name = "prov")
    private String prov;
    @Column(name = "pobl")
    private String pobl;
    @Column(name = "tel_home")
    private String telHome;
    @Column(name = "tel_movil")
    private String telMovil;
    @Column(name = "f_alta")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fAlta;
    @Column(name = "f_vigen")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fVigen;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "credito")
    private Float credito;
    @Basic(optional = false)
    @Column(name = "num_alqs")
    private int numAlqs;
    @Basic(optional = false)
    @Column(name = "num_solds")
    private int numSolds;
    @Basic(optional = false)
    @Column(name = "num_changes")
    private int numChanges; 
    @Column(name = "fnac")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fNac;  
    
    @OneToMany(orphanRemoval = true, mappedBy = "idct")
    private List<VideoupPuntos> videoupPuntosList;
    @OneToMany(orphanRemoval = true, mappedBy = "idct")
    private List<VideoupCstmrnotes> videoupCstmrnotesList;
    @OneToMany(orphanRemoval = true, mappedBy = "idct")
    private List<VideoupAutrz> videoupAutrzList;
    @OneToMany(orphanRemoval = true, mappedBy = "idcli")
    private List<VideoupRentas> videoupRentasList;

    public VideoupCustomers() {
    }

    public VideoupCustomers(Integer idct) {
        this.idct = idct;
    }

    public VideoupCustomers(Integer idct, String name, String applldos, String dni, String addr) {
        this.idct = idct;
        this.name = name;
        this.applldos = applldos;
        this.dni = dni;
        this.addr = addr;
    }

    @Override
    public Integer getId() {
        return idct;
    }

    public Integer getIdct() {
        return idct;
    }

    public void setIdct(Integer idct) {
        this.idct = idct;
    }

    public Integer getIdweb() {
        return idweb;
    }

    public void setIdweb(Integer idweb) {
        this.idweb = idweb;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApplldos() {
        return applldos;
    }

    public void setApplldos(String applldos) {
        this.applldos = applldos;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getCodCst() {
        return codCst;
    }

    public void setCodCst(String codCst) {
        this.codCst = codCst;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getCodp() {
        return codp;
    }

    public Date getfNac() {
        return fNac;
    }

    public void setfNac(Date fNac) {
        this.fNac = fNac;
    }

    public void setCodp(String codp) {
        this.codp = codp;
    }

    public void setfAlta(Date fAlta) {
        this.fAlta = fAlta;
    }

    public void setfVigen(Date fVigen) {
        this.fVigen = fVigen;
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

    public Date getfAlta() {
        return fAlta;
    }

    public Date getfVigen() {
        return fVigen;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProv() {
        return prov;
    }

    public void setProv(String prov) {
        this.prov = prov;
    }

    public String getPobl() {
        return pobl;
    }

    public void setPobl(String pobl) {
        this.pobl = pobl;
    }

    public String getTelHome() {
        return telHome;
    }

    public void setTelHome(String telHome) {
        this.telHome = telHome;
    }

    public String getTelMovil() {
        return telMovil;
    }

    public void setTelMovil(String telMovil) {
        this.telMovil = telMovil;
    }

    public Date getFAlta() {
        return fAlta;
    }

    public void setFAlta(Date fAlta) {
        this.fAlta = fAlta;
    }

    public Date getFVigen() {
        return fVigen;
    }

    public void setFVigen(Date fVigen) {
        this.fVigen = fVigen;
    }

    public Float getCredito() {
        if(credito==null){
            return 0f;
        }
        return credito;
    }

    public void setCredito(Float credito) {
        this.credito = credito;
    }

    @XmlTransient
    public List<VideoupPuntos> getVideoupPuntosList() {
        return videoupPuntosList;
    }

    public void setVideoupPuntosList(List<VideoupPuntos> videoupPuntosList) {
        this.videoupPuntosList = videoupPuntosList;
    }

    public void addVideoupPuntos(VideoupPuntos pnts) {
        if(videoupPuntosList==null){
            videoupPuntosList=new ArrayList<VideoupPuntos>();
        }
        videoupPuntosList.add(pnts);
    }
    
    public void removeVideoupPuntos(VideoupPuntos pnts) {
        if(videoupPuntosList!=null){
            videoupPuntosList.remove(pnts);
        }
    }
    
    public boolean subtractVideoupPuntos(int pnts) {
        boolean done=false;
        Calendar cal=Calendar.getInstance();
        if(videoupPuntosList!=null){
            Collections.sort( videoupPuntosList );
            for(VideoupPuntos vpnts: videoupPuntosList){
                if( !cal.getTime().after(vpnts.getVHasta()) && vpnts.getPuntos()>0 ){
                    vpnts.setUsedOn(cal.getTime());
                    if(vpnts.getPuntos()>pnts){
                        vpnts.setPuntos(vpnts.getPuntos()-pnts);
                        vpnts.setUsedPuntos( vpnts.getUsedPuntos()+pnts );
                        pnts=0;
                        break;
                    }else{
                        vpnts.setUsedPuntos( vpnts.getUsedPuntos()+vpnts.getPuntos() );
                        pnts=pnts-vpnts.getPuntos();
                        vpnts.setPuntos(0);
                    }
                }
            }
            done=(pnts==0);
        }
        return done;
    }
    
    public int getAlivePuntos(){
        Date today=Calendar.getInstance().getTime();
        int pnts=0;
        if(videoupPuntosList!=null){
            for(VideoupPuntos pnt: videoupPuntosList){
                if( pnt.getPuntos()>0 && !today.after(pnt.getVHasta()) ){
                    pnts+=pnt.getPuntos();
                }
            }
        }
        return pnts;
    }
    
    @XmlTransient
    public List<VideoupCstmrnotes> getVideoupCstmrnotesList() {
        return videoupCstmrnotesList;
    }

    public void setVideoupCstmrnotesList(List<VideoupCstmrnotes> videoupCstmrnotesList) {
        this.videoupCstmrnotesList = videoupCstmrnotesList;
    }
    
    public void addVideoupCstmrnotes(VideoupCstmrnotes note) {
        if(videoupCstmrnotesList==null){
            videoupCstmrnotesList=new ArrayList<VideoupCstmrnotes>();
        }
        videoupCstmrnotesList.add(note);
    }
    
    public void removeVideoupCstmrnotes(VideoupCstmrnotes note) {
        if(videoupCstmrnotesList!=null){
            videoupCstmrnotesList.remove(note);
        }
    }
    
    public int countNotesByType(int type) {
        int cnt=0;
        if(videoupCstmrnotesList!=null){
            for(VideoupCstmrnotes note: videoupCstmrnotesList){
                if(note.getNtype()==type){ cnt++; }
            }
        }
        return cnt;
    }
    
    public String[] getNotesByType(int type) {
        String[] tx=null;
        int nNotes=countNotesByType(type);
        int cnt=0;
        if(nNotes>0){
            tx=new String[nNotes];
            for(VideoupCstmrnotes note: videoupCstmrnotesList){
                if(note.getNtype()==type){
                    tx[cnt]=note.getNote();
                    cnt++;
                }
            }
        }
        return tx;
    }

    @XmlTransient
    public List<VideoupAutrz> getVideoupAutrzList() {
        return videoupAutrzList;
    }

    public void setVideoupAutrzList(List<VideoupAutrz> videoupAutrzList) {
        this.videoupAutrzList = videoupAutrzList;
    }

    public void addVideoupAutrz(VideoupAutrz persona) {
        if(videoupAutrzList==null){
            videoupAutrzList=new ArrayList<VideoupAutrz>();
        }
        videoupAutrzList.add(persona);
    }

    public String getHtmlListAutrz(){
        String lst="";
        if(videoupAutrzList!=null && !videoupAutrzList.isEmpty()){
            lst="<ul>";
            for(VideoupAutrz pr: videoupAutrzList){
                lst+="<li>"+pr.getPname()+"</li>";
            } lst+="</ul>";
        }
        return lst;
    }
    
    public String getListAutrz(){
        String lst="";
        if(videoupAutrzList!=null && !videoupAutrzList.isEmpty()){
            for(VideoupAutrz pr: videoupAutrzList){
                lst+=(lst.length()>0?", ":"")+pr.getPname();
            }
        }
        return lst;
    }

    public void removeVideoupAutrz(VideoupAutrz persona) {
        videoupAutrzList.remove(persona);
    }

    @XmlTransient
    public List<VideoupRentas> getVideoupRentasList() {
        return videoupRentasList;
    }

    public void setVideoupRentasList(List<VideoupRentas> videoupRentasList) {
        this.videoupRentasList = videoupRentasList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idct != null ? idct.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof VideoupCustomers)) {
            return false;
        }
        VideoupCustomers other = (VideoupCustomers) object;
        if ((this.idct == null && other.idct != null) || (this.idct != null && !this.idct.equals(other.idct))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return codCst;
    }

    public List<VideoupVentas> getVideoupVentasList() {
        return videoupVentasList;
    }

    public void setVideoupVentasList(List<VideoupVentas> videoupVentasList) {
        this.videoupVentasList = videoupVentasList;
    }
 
    /**
     * @return  -1_str=invalid_message; 0_str=warning_message; null=totally valid
     */
    public String validate2Import(){
        String warn="";
        if(name==null){
            return "-1_Nombre sin especificar";
        }else if(name.length()>255){
            warn="Titulo demasiado largo, ha sido truncado a 255 caracteres ";
            name=name.substring(0,255);
        }
        if(applldos==null){
            return "-1_Apellidos sin especificar";
        }else if(applldos!=null && applldos.length()>155){
            warn+="Director demasiado largo, ha sido truncado a 255 caracteres ";
            applldos=applldos.substring(0,255);
        }
        if(dni==null){
            return "-1_"+Globals.getConfig("dniDef")+" sin especificar ";
        }else if(dni!=null && dni.length()>255){
            warn+=Globals.getConfig("dniDef")+" demasiado largo, ha sido truncado a 35 caracteres";
            dni=dni.substring(0,35);
        }
        if(codCst!=null && codCst.length()>12){
            return "-1_Codigo de socio demasiado largo, maximo 12 caracteres ";
        }
        if(email!=null && email.length()>100){
            warn+="Correo electronico demasiado largo, ha sido truncado a 100 caracteres ";
            email=email.substring(0,100);
        }
        if(addr==null){
            return "-1_Direccion sin especificar";
        }else if(addr!=null && addr.length()>105){
            warn+="Direccion demasiado largo, ha sido truncado a 105 caracteres ";
            addr=addr.substring(0,105);
        }
        if(codp!=null && codp.length()>12){
            warn+="Codigo postal demasiado largo, ha sido truncado a 12 caracteres ";
            codp=codp.substring(0,12);
        }
        if(city!=null && city.length()>55){
            warn+="Ciudad demasiado largo, ha sido truncado a 55 caracteres ";
            city=city.substring(0,55);
        }
        if(prov!=null && prov.length()>55){
            warn+="Provincia demasiado largo, ha sido truncado a 55 caracteres ";
            prov=prov.substring(0,55);
        }
        if(pobl!=null && pobl.length()>55){
            warn+="Poblacion demasiado largo, ha sido truncado a 55 caracteres ";
            pobl=pobl.substring(0,55);
        }
        if(telHome!=null && telHome.length()>12){
            warn+="Telefono casa demasiado largo, ha sido truncado a 12 caracteres ";
            telHome=telHome.substring(0,12);
        }
        if(telMovil!=null && telMovil.length()>12){
            warn+="Telefono movil demasiado largo, ha sido truncado a 12 caracteres ";
            telMovil=telMovil.substring(0,12);
        }
        if(credito<0){
            warn+="Credito menor a cero, ha sido cambiado a cero ";
            credito=0f;
        }
        if(warn.length()>0){
            return "0_"+warn;
        }
        return null;
    }
}
