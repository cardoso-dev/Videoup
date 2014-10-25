/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.views.modvnts;

import com.videoup.controllers.Controller;
import com.videoup.entities.VideoupBaseEntity;
import com.videoup.entities.VideoupBcodes;
import com.videoup.entities.VideoupCustomers;
import com.videoup.entities.VideoupGames;
import com.videoup.entities.VideoupHistcredito;
import com.videoup.entities.VideoupItemsvnt;
import com.videoup.entities.VideoupMovies;
import com.videoup.entities.VideoupTaxes;
import com.videoup.entities.VideoupVentas;
import com.videoup.utils.Globals;
import com.videoup.views.modalqrs.ATax;
import com.videoup.views.modalqrs.CustmrOption;
import com.videoup.views.modsocios.MiniFicha;
import com.videoup.views.utils.ProductBriefData;
import com.videoup.views.utils.Utils;
import com.videoup.views.utils.ViewFicha;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Pedro
 */
public class Ficha extends ViewFicha implements DocumentListener{
    
    private VideoupVentas ent;
    private VideoupCustomers soc;
    private boolean searchSocByCode;
    private List<Component> prods;
    private MiniFicha mfich;
    private boolean saved;
    private float totals;
    private float subtotals;
    private float cargTaxs;
    private float credito;
    private float useCredit;
    private List<VideoupTaxes> taxes;
    private NumberFormat frmCurr;
    
    /**
     * Creates new form Ficha
     */
    public Ficha(Controller ctrl, int id) {
        super(ctrl,id,true);
        initComponents();
        initComps(ent);
    }

    public Ficha(Controller ctrl, Object ent, int id) {
        super(ctrl,id,false);
        initComponents();
        initComps(ent);
    }
    
    @Override
    protected void loadEntity(int id){
        ent=(VideoupVentas)getEntity("from VideoupVentas where idvn="+id);
    }
    
    private void initComps(Object ent){
        frmCurr=NumberFormat.getCurrencyInstance();
        Currency currency=Currency.getInstance(Locale.getDefault());
        jLabel12.setText("Subtotal "+currency.getSymbol()+":");
        jLabel13.setText("Cargar el siguiente monto al credito disponible "+currency.getSymbol()+":");
        putMainContain(mainContain);
        loadTaxes();
        prods=new ArrayList<Component>();
        prods.add(pnlResume);
        soc=null; saved=false; searchSocByCode=true;
        if(ent==null){
            this.ent=new VideoupVentas();
        }else{
            loadEntity(ent);
        }
        updateCredito();
        txCredit.getDocument().addDocumentListener(this);
        txtAdeudo.getDocument().addDocumentListener(this);
        setUpValidator();
        addJText2ListenChanges(txNombre);
        addJText2ListenChanges(txApellidos);
        addJText2ListenChanges(txDNI);
        addJText2ListenChanges(txDireccion);
        addJText2ListenChanges(txCiudad);
        addJText2ListenChanges(txCodigop);
        addJText2ListenChanges(txProvincia);
        addJText2ListenChanges(txPoblacion);
        addJText2ListenChanges(txEmail);
        addJText2ListenChanges(txTelHome);
        addJText2ListenChanges(txTelMovil);
    }

    private void loadTaxes(){
        taxes=this.loadList("from VideoupTaxes vt where vt.apVent=true", null, 0, false);
    }
    
    @Override
    protected void setUpValidator(){
        super.setUpValidator();
        vltor.addStringField(txNombre, "Nombre", true, 5, 255,0);
        vltor.addStringField(txApellidos, "Apellidos", true, 10, 255,0);
        vltor.addRegExpField(txDNI, "DNI", true,((String)Globals.getConfig("dniRgexp")),0);
        vltor.addStringField(txDireccion, "Direccion", true, 10, 105,0);
        vltor.addStringField(txCiudad, "Ciudad", true, 6, 55,0);
        vltor.addRegExpField(txCodigop, "Codigo postal", true,((String)Globals.getConfig("pcodeRgexp")),0);
        vltor.addStringField(txProvincia, "Provincia", true, 5, 55,0);
        vltor.addStringField(txPoblacion, "Poblacion", true, 5, 55,0);
        vltor.addRegExpField(txEmail, "E-mail", true,"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$",1);
        vltor.addRegExpField(txTelHome, "Telefono de casa",true,"\\+?\\d{1,3}?[- .]?\\(?(?:\\d{2,3})\\)?[- .]?\\d\\d\\d[- .]?\\d\\d\\d\\d$",1);
        vltor.addRegExpField(txTelMovil, "Telefono movil", false,"\\+?\\d{1,3}?[- .]?\\(?(?:\\d{2,3})\\)?[- .]?\\d\\d\\d[- .]?\\d\\d\\d\\d$",1);
        ignoreFieldExtrn(true);
    }
    
    private void ignoreFieldExtrn(boolean ign){
        vltor.setIgnoreField(txNombre,ign);
        vltor.setIgnoreField(txApellidos,ign);
        vltor.setIgnoreField(txDNI,ign);
        vltor.setIgnoreField(txDireccion,ign);
        vltor.setIgnoreField(txCiudad,ign);
        vltor.setIgnoreField(txProvincia,ign);
        vltor.setIgnoreField(txPoblacion,ign);
        vltor.setIgnoreField(txCodigop,ign);
        vltor.setIgnoreField(txEmail,ign);
        vltor.setIgnoreField(txTelHome,ign);
        vltor.setIgnoreField(txTelMovil,ign);
    }
        
    private void setEdtableFieldExtrn(boolean ign){
        txNombre.setEditable(ign);
        txApellidos.setEditable(ign);
        txDNI.setEditable(ign);
        txDireccion.setEditable(ign);
        txCiudad.setEditable(ign);
        txProvincia.setEditable(ign);
        txPoblacion.setEditable(ign);
        txCodigop.setEditable(ign);
        txEmail.setEditable(ign);
        txTelHome.setEditable(ign);
        txTelMovil.setEditable(ign);
    }
    
    private void addItem(){
        List<VideoupBcodes> copies;
        String code=txCodeBar.getText().trim();
        int prodsSzPre=prods.size();
        if(code.equals("")){
            return;
        }
        copies=loadList("from VideoupBcodes where barcode='"+code+"'",null,0,false);
        if(copies==null){
            showError("Error al consultar: "+getError());
        }else if(copies.isEmpty()){
            showError("No se encontraron productos con codigo "+code);
        }else{
            txCodeBar.setText("");
            for(VideoupBcodes copy: copies){
                if(copy.getStatus().startsWith("Apartada")){
                    showInfo("No se puede completar la accion","El producto esta apartado");
                }else if(!copy.getStatus().equals("Disponible") || !copy.getVendible()){
                    showInfo("No se puede completar la accion","El producto no esta disponible para venta");
                }else if(copy.getVideoupMoviesList()!=null && !copy.getVideoupMoviesList().isEmpty()){
                    for(VideoupMovies mov: copy.getVideoupMoviesList()){
                        if(!hasCopy(copy)){
                            prods.add(new ProductBriefData(mov,copy,this,false));
                        }
                    }
               }else if(copy.getVideoupGamesList()!=null && !copy.getVideoupGamesList().isEmpty()){
                    for(VideoupGames gam: copy.getVideoupGamesList()){
                        if(!hasCopy(copy)){
                            prods.add(new ProductBriefData(gam,copy,this,false));                            
                        }
                    }
                }
            }
            if(prods.size()==prodsSzPre){
                showError("No se encontraron registros del producto");
            }else{
                refreshItems();
            }
        }
    }
    
    private boolean hasCopy(VideoupBcodes copy){
        boolean hasIt=false;
        for(Component sItem: prods){
            if(sItem instanceof ProductBriefData){
                hasIt=((ProductBriefData)sItem).isBCode(copy.getBarcode());
                if(hasIt){ break; }
            }
        }
        return hasIt;
    }
    
    private void refreshItems(){
        pnlCenter.setVisible(false);
        if(prods.size()>1 || saved){
            updateTotals();
            Utils.loadAsTableLayout(prods, pnlCenter, "No hay productos agregados");
        }else{
            pnlCenter.removeAll();
        }
        pnlCenter.setVisible(true);
    }
    
    private void updateCredito(){
        if(soc!=null){
            credito=(soc.getCredito()!=null?soc.getCredito():0);
        }else{
            credito=0;
        }
        useCredit=0;
        if(credito>0){
            lblCredito.setText("Cliente con credito disponible: "+frmCurr.format(credito));
            txCredit.setText("0"); pnlCredit.setVisible(true);
            lblCargoCred.setVisible(true); txtCargCred.setVisible(true);
        }else{
            lblCargoCred.setVisible(false); txtCargCred.setVisible(false);
            pnlCredit.setVisible(false);
        }
        if(credito<0){
            lblAdeudo1.setText("El cliente tiene deuda por: "+frmCurr.format( Math.abs(credito) ));
            lblAdeudo1.setVisible(true); lblAdeudo2.setVisible(true);
            txtAdeudo.setEditable(true); txtAdeudo.setVisible(true);
            txtAdeudo.setText(""+Math.abs(credito));
        }else{
            lblAdeudo1.setVisible(false); txtAdeudo.setEditable(false);
            lblAdeudo2.setVisible(false); txtAdeudo.setVisible(false);
        }
        updateTotals();
    }
    
    public void removeItem(ProductBriefData pbd){
        if(saved){
            return;
        }
        for(Component sItem: prods){
            if(sItem instanceof ProductBriefData){
                if( pbd.equals( (ProductBriefData)sItem ) ){
                    prods.remove(sItem);
                    break;
                }
            }
        }
        refreshItems();
    }
    
    private void setSocio(){
        String code=txSCode.getText().trim();
        if(mfich!=null){
            pnlEast.setVisible(false);
            pnlEast.remove(mfich);
            pnlEast.setVisible(true);
        }
        if(code.equals("") && soc==null){
            updateCredito();
            return;
        }
        if(soc==null && searchSocByCode){
            soc=(VideoupCustomers)getEntity("from VideoupCustomers where codCst='"+code+"'");
        }else if(soc==null && !searchSocByCode){
            search4Name(code); updateCredito(); return;
        }
        pnlEast.setVisible(false);
        pnlEast.remove(lblSocioErr);
        pnlEast.remove(pnlLstPers);
        if(soc==null){
            lblSocioErr.setText("<html>Error!<br /> verifica el <br />codigo <br />ingresado</html>");
            pnlEast.add(lblSocioErr,BorderLayout.CENTER);
        }else{
            mfich=new MiniFicha(soc,this);
            credito=(soc.getCredito()!=null?soc.getCredito():0);
            pnlEast.add(mfich,BorderLayout.CENTER);
        }
        updateCredito();
        pnlEast.setVisible(true);
    }
    
    private void search4Name(String name){
        List<VideoupCustomers> socs;
        List<Component> opts;
        CustmrOption cOption;
        String qry="select DISTINCT vc From VideoupCustomers vc left join fetch vc.videoupAutrzList va where ";
        qry+=buildLike4Where(name,"va.pname")+" or ";
        qry+=buildLike4Where(name,"name")+" or "+buildLike4Where(name,"applldos");
        socs=loadList(qry,null,0, false);
        pnlEast.setVisible(false);
        pnlEast.remove(lblSocioErr); pnlEast.remove(pnlLstPers);
        if(mfich!=null){
            pnlEast.remove(mfich);
        }
        if(socs==null){
            showError("Error: "+this.getError());
            pnlEast.remove(lblSocioErr); pnlEast.validate();
        }else if(socs.isEmpty()){
            lblSocioErr.setText("<html>Vacio!<br /> no se encontraron <br />datos con<br />el nombre ingresado</html>");
            pnlEast.add(lblSocioErr,BorderLayout.CENTER);
        }else{
            opts=new ArrayList<Component>();
            opts.add(new javax.swing.JLabel(socs.size()+" encontrados"));
            for(VideoupCustomers cstmr: socs){
                cOption=new CustmrOption(cstmr,this);
                opts.add(cOption);
            }
            pnlSelSoc.removeAll();
            Utils.loadAsTableLayout(opts, pnlSelSoc, "No se encontraron personas");
            pnlEast.add(pnlLstPers,BorderLayout.CENTER);
        }
        pnlEast.setVisible(true); mainContain.validate();
    }
    
    public void setCustomr(VideoupCustomers sc){
        soc=sc;
        pnlSelSoc.removeAll();
        pnlEast.remove(pnlLstPers);
        setSocio();
    }
    
    public int getSocioCode(){
        if(soc!=null){
            return soc.getIdct();
        }
       return 0;
    }
    
    @Override
    public String getTitle(){
        return "Ficha de "+(saved?"venta "+ent.getIdvn():"nueva venta");
    }
    
    @Override
    protected void updateIdTitle(){
        setId(ent.getIdvn());
        lblTitle.setText(getTitle());
        ctrl.changeViewName(this, getTitle());
    }
    
    @Override
    protected boolean isDataValid(){
        boolean valid=!prods.isEmpty();
        String err2show="Debe indicar socio o cliente y al menos un articulo";
        if(rbntTpSoc.isSelected()){
            valid=(valid && (soc!=null));
        }else{
            if(!vltor.validate()){
                err2show=vltor.getError();
                valid=false;
            }
        }
        setError(err2show);
        return valid;
    }
    
    private void updateTotals(){
        subtotals=0;
        boolean factr=jckFactura.isSelected();
        List<Component> appTxes;
        ATax aTax;
        float taxPrc;
        totals=0;
        cargTaxs=0;
        for(Component cmpp: prods){
            if( cmpp instanceof ProductBriefData){
                subtotals+=((ProductBriefData)cmpp).getCargoVenta();
            }
        }
        totals=subtotals;
        if(taxes==null){
            pnlTaxes.setLayout(new BorderLayout());
            pnlTaxes.add(lblTaxFail,BorderLayout.CENTER);
            pnlTaxes.setVisible(true);
        }else if(totals>0){
            appTxes=new ArrayList<Component>();
            for(VideoupTaxes tax: taxes){
                if( (tax.getFacturOnl()&&factr) || !tax.getFacturOnl() ){
                    taxPrc=((float)tax.getPorcent()/100f);
                    aTax=new ATax(tax.getNamet()+" "+tax.getPorcent()+"%",(taxPrc*subtotals) );
                    appTxes.add(aTax);
                    totals+=(taxPrc*subtotals);
                    cargTaxs+=(taxPrc*subtotals);
                }
            }
            Utils.loadAsTableLayout(appTxes, pnlTaxes, "No hay impuestos agregados");
            pnlTaxes.setVisible(!appTxes.isEmpty());
        }
        if(credito>0 && totals>0){
            try{
                useCredit=Float.parseFloat(txCredit.getText());
            }catch(NumberFormatException nfe){
                useCredit=-1;
            }
            if(useCredit>credito){
                useCredit=-1;
            }
            if(useCredit<0){
                txtCargCred.setText("error!");
                totals=-1;
            }else{
                txtCargCred.setText( frmCurr.format(useCredit) );
                totals-=useCredit;
            }
        }else if(credito<0 && totals>0){
            try{ useCredit=Float.parseFloat(txtAdeudo.getText()); }
            catch(NumberFormatException nfe){ useCredit=-1; }
            if(useCredit>Math.abs(credito) || useCredit<0){
                txtAdeudo.setForeground(new Color(255,10,10));
                txtAdeudo.setBackground(new Color(255,255,25));
                txtAdeudo.setToolTipText("Valor invalido, sera ignorado");
                useCredit=-1;
            }else{
                txtAdeudo.setForeground(new Color(0,0,0)); txtAdeudo.setToolTipText(null);
                txtAdeudo.setBackground(new Color(255,255,255));
            }
            if(useCredit>0){ totals+=useCredit; }
        }
        txSubtotal.setText(""+subtotals);
        txPagoTotal.setText( (totals>=0?""+frmCurr.format(totals):"error!") );
        validate();
    }
    
    @Override
    protected boolean saveEntity(VideoupBaseEntity ignore,boolean forceAsNew){
        List<VideoupBaseEntity> entities=new ArrayList<VideoupBaseEntity>();
        Date today=Calendar.getInstance().getTime();
        VideoupHistcredito hcred;
        float fromCred=0;
        float toAdeudo=0;
        ent=new VideoupVentas();
        if(soc==null && !createExtern()){
            return false;
        }
        if(credito>0 && useCredit>0){
            hcred=new VideoupHistcredito(useCredit*-1,Calendar.getInstance().getTime(),soc);
            soc.setCredito(credito-useCredit);
            entities.add(hcred);
            fromCred=useCredit;
        }else if(credito<0  && useCredit>0){
            hcred=new VideoupHistcredito(useCredit,Calendar.getInstance().getTime(),soc);
            soc.setCredito(credito+useCredit);
            entities.add(hcred);
            toAdeudo=useCredit;
        }
        soc.setNumSolds(soc.getNumSolds()+1);
        ent.setIdcli(soc);
        ent.setTpEnv(null);
        ent.setCstEnv(0);
        ent.setStatus(1);
        ent.setFactura(jckFactura.isSelected());
        ent.setImpuesto(cargTaxs);
        ent.setOnFecha(today);
        ent.setSubtotal(subtotals);
        ent.setFromCredito(fromCred);
        ent.setToAdeudo(toAdeudo);
        entities.add(ent);
        entities.add(soc);
        for(Component sItem: prods){
            if(sItem instanceof ProductBriefData){
                VideoupItemsvnt item=((ProductBriefData)sItem).getVentEntity();
                VideoupBcodes cp=((ProductBriefData)sItem).getCopy();
                cp.setStatus("Vendida");
                entities.add( ((ProductBriefData)sItem).getProductEntity(2) );
                entities.add(item);
                entities.add(cp);
                item.setIdvn(ent);
                ent.addVideoupItemsvnt(item);
            }
        }
        saved=super.saveEntities(entities);
        if(saved){
            txCodeBar.setEditable(false); pnlNorth.setVisible(false);
            txSCode.setEditable(false); rbntTpSoc.setEnabled(false);
            rbtnTpExtr.setEnabled(false); pnlEastNorth.setVisible(false);
            txSCode.setEditable(false); setEdtableFieldExtrn(false);
            jckFactura.setEnabled(false); txSubtotal.setEditable(false);
            txCredit.setEditable(false);
            for(Component sItem: prods){
                if(sItem instanceof ProductBriefData){
                    ((ProductBriefData)sItem).hideRemovalBtn();
                }
            }
        }
        return saved;
    }
    
    private boolean createExtern(){
        boolean created=false;
        soc=new VideoupCustomers();
        soc.setName(txNombre.getText());
        soc.setApplldos(txApellidos.getText());
        soc.setDni(txDNI.getText());
        soc.setCodCst(null);
        soc.setEmail(txEmail.getText());
        soc.setAddr(txDireccion.getText());
        soc.setCodp(txCodigop.getText());
        soc.setCity(txCiudad.getText());
        soc.setProv(txProvincia.getText());
        soc.setPobl(txPoblacion.getText());
        soc.setTelHome(txTelHome.getText());
        soc.setTelMovil(txTelMovil.getText());
        soc.setFAlta(null);
        soc.setFVigen(null);
        created=super.saveEntity(soc,false);
        if(created){
            credito=0; useCredit=0;
        }
        return created;
    }
    
    @Override
    public void loadEntity(Object oEnt){
        ProductBriefData tmpp;
        String tRresume;
        ent=((VideoupVentas)oEnt);
        soc=ent.getIdcli();
        setSocio(); prods.remove(pnlResume);
        for(VideoupItemsvnt itemvn: ent.getVideoupItemsvntList()){
            tmpp=new ProductBriefData(itemvn,this);
            prods.add(tmpp);
        }
        if(ent.getImpuesto()!=null){ cargTaxs=ent.getImpuesto(); }else{ cargTaxs=0; }
        tRresume="<html>Venta ID"+ent.getIdvn()+", Cargos: "+frmCurr.format(ent.getSubtotal()+ent.getImpuesto());
        tRresume+=(ent.getFactura()?" con":" sin")+" factura<br/>(Cargos: ";
        tRresume+=frmCurr.format(ent.getSubtotal())+"+ Impuestos: "+frmCurr.format(ent.getImpuesto())+")</html>";
        txCodeBar.setEditable(false); pnlNorth.setVisible(false);
        txSCode.setEditable(false); rbntTpSoc.setEnabled(false);
        rbtnTpExtr.setEnabled(false); pnlEastNorth.setVisible(false);
        txSCode.setEditable(false); setEdtableFieldExtrn(false);
        jckFactura.setEnabled(false); txSubtotal.setEditable(false);
        txCredit.setEditable(false); pnlResume.setVisible(false);
        lblClosedVnt.setText(tRresume);
        mainContain.remove(pnlNorth);
        mainContain.add(lblClosedVnt,BorderLayout.NORTH);
        saved=true; updateIdTitle(); refreshItems();
    }
    
    @Override
    public boolean hasChanges(){
        if(saved){
            return false;
        }
        return (!prods.isEmpty() || (soc!=null) || changes);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainContain = new javax.swing.JPanel();
        pnlEast = new javax.swing.JPanel();
        pnlEastNorth = new javax.swing.JPanel();
        pnlAskSocio = new javax.swing.JPanel();
        lblAskCustmr = new javax.swing.JLabel();
        txSCode = new javax.swing.JTextField();
        soBCode = new javax.swing.JLabel();
        soBName = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        rbntTpSoc = new javax.swing.JRadioButton();
        rbtnTpExtr = new javax.swing.JRadioButton();
        pnlNorth = new javax.swing.JPanel();
        lblAskItems = new javax.swing.JLabel();
        txCodeBar = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        pnlCenter = new javax.swing.JPanel();
        grbtnTpCli = new javax.swing.ButtonGroup();
        pnlCliExtr = new javax.swing.JPanel();
        txPoblacion = new javax.swing.JTextField();
        txProvincia = new javax.swing.JTextField();
        txCodigop = new javax.swing.JTextField();
        txCiudad = new javax.swing.JTextField();
        txDireccion = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txNombre = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txDNI = new javax.swing.JTextField();
        lblDireccion = new javax.swing.JLabel();
        txApellidos = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        lblDni = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txEmail = new javax.swing.JTextField();
        txTelHome = new javax.swing.JTextField();
        txTelMovil = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        lblSocioErr = new javax.swing.JLabel();
        pnlLstPers = new javax.swing.JScrollPane();
        pnlSelSoc = new javax.swing.JPanel();
        pnlResume = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        txSubtotal = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jckFactura = new javax.swing.JCheckBox();
        pnlTaxes = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        txPagoTotal = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        lblCargoCred = new javax.swing.JLabel();
        txtCargCred = new javax.swing.JTextField();
        lblAdeudo2 = new javax.swing.JLabel();
        lblAdeudo1 = new javax.swing.JLabel();
        txtAdeudo = new javax.swing.JTextField();
        pnlCredit = new javax.swing.JPanel();
        lblCredito = new javax.swing.JLabel();
        txCredit = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        lblTaxFail = new javax.swing.JLabel();
        lblClosedVnt = new javax.swing.JLabel();

        mainContain.setLayout(new java.awt.BorderLayout());

        pnlEast.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlEast.setLayout(new java.awt.BorderLayout());

        pnlEastNorth.setLayout(new java.awt.BorderLayout());

        lblAskCustmr.setText("Socio que realiza el alquiler:");

        txSCode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txSCodeKeyPressed(evt);
            }
        });

        soBCode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/videoup/imgs/codebar16.png"))); // NOI18N
        soBCode.setToolTipText("Buscar por codigo de socio");
        soBCode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(23, 33, 36), 2));
        soBCode.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                soBCodeMouseClicked(evt);
            }
        });

        soBName.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/videoup/imgs/miniSoc.png"))); // NOI18N
        soBName.setToolTipText("Buscar por nombre");
        soBName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                soBNameMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnlAskSocioLayout = new javax.swing.GroupLayout(pnlAskSocio);
        pnlAskSocio.setLayout(pnlAskSocioLayout);
        pnlAskSocioLayout.setHorizontalGroup(
            pnlAskSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAskSocioLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlAskSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblAskCustmr)
                    .addGroup(pnlAskSocioLayout.createSequentialGroup()
                        .addComponent(txSCode)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(soBCode)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(soBName)))
                .addContainerGap(83, Short.MAX_VALUE))
        );
        pnlAskSocioLayout.setVerticalGroup(
            pnlAskSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAskSocioLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblAskCustmr)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlAskSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(soBCode)
                    .addComponent(soBName)
                    .addComponent(txSCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        pnlEastNorth.add(pnlAskSocio, java.awt.BorderLayout.CENTER);

        jLabel1.setText("Comprador:");

        grbtnTpCli.add(rbntTpSoc);
        rbntTpSoc.setSelected(true);
        rbntTpSoc.setText("Socio");
        rbntTpSoc.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rbntTpSocItemStateChanged(evt);
            }
        });

        grbtnTpCli.add(rbtnTpExtr);
        rbtnTpExtr.setText("Externo");
        rbtnTpExtr.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rbtnTpExtrItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(rbntTpSoc)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbtnTpExtr)
                .addContainerGap(26, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(rbntTpSoc)
                    .addComponent(rbtnTpExtr)))
        );

        pnlEastNorth.add(jPanel2, java.awt.BorderLayout.NORTH);

        pnlEast.add(pnlEastNorth, java.awt.BorderLayout.NORTH);

        mainContain.add(pnlEast, java.awt.BorderLayout.EAST);

        lblAskItems.setText("Ingrese codigo de barras de articulo:");

        txCodeBar.setColumns(15);
        txCodeBar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txCodeBarKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout pnlNorthLayout = new javax.swing.GroupLayout(pnlNorth);
        pnlNorth.setLayout(pnlNorthLayout);
        pnlNorthLayout.setHorizontalGroup(
            pnlNorthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNorthLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblAskItems)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txCodeBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(300, Short.MAX_VALUE))
        );
        pnlNorthLayout.setVerticalGroup(
            pnlNorthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNorthLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlNorthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAskItems)
                    .addComponent(txCodeBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mainContain.add(pnlNorth, java.awt.BorderLayout.NORTH);

        pnlCenter.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout pnlCenterLayout = new javax.swing.GroupLayout(pnlCenter);
        pnlCenter.setLayout(pnlCenterLayout);
        pnlCenterLayout.setHorizontalGroup(
            pnlCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 382, Short.MAX_VALUE)
        );
        pnlCenterLayout.setVerticalGroup(
            pnlCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 343, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(pnlCenter);

        mainContain.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        txPoblacion.setColumns(23);

        txProvincia.setColumns(23);

        txCodigop.setColumns(23);

        txCiudad.setColumns(23);

        txDireccion.setColumns(23);

        jLabel9.setText("Poblacion:");

        jLabel8.setText("Provincia:");

        txNombre.setColumns(23);

        jLabel2.setText("Apellidos:");

        jLabel3.setText("Nombre:");

        txDNI.setColumns(23);

        lblDireccion.setText("Direcion:");

        txApellidos.setColumns(23);

        jLabel6.setText("Codigo postal:");

        lblDni.setText("DNI:");

        jLabel7.setText("Ciudad:");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Datos del cliente");

        jLabel10.setText("Telefono casa");

        txEmail.setColumns(15);

        txTelHome.setColumns(15);

        txTelMovil.setColumns(15);

        jLabel5.setText("Email:");

        jLabel11.setText("Telefono movil:");

        javax.swing.GroupLayout pnlCliExtrLayout = new javax.swing.GroupLayout(pnlCliExtr);
        pnlCliExtr.setLayout(pnlCliExtrLayout);
        pnlCliExtrLayout.setHorizontalGroup(
            pnlCliExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCliExtrLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlCliExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlCliExtrLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(pnlCliExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlCliExtrLayout.createSequentialGroup()
                                .addGroup(pnlCliExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlCliExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txTelMovil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txTelHome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(pnlCliExtrLayout.createSequentialGroup()
                                .addGroup(pnlCliExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblDni, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblDireccion, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlCliExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txDNI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txProvincia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txCodigop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txApellidos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txPoblacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnlCliExtrLayout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        pnlCliExtrLayout.setVerticalGroup(
            pnlCliExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCliExtrLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCliExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCliExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txApellidos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCliExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDni)
                    .addComponent(txDNI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlCliExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDireccion)
                    .addComponent(txDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCliExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCliExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txCodigop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCliExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txProvincia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCliExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txPoblacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlCliExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCliExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txTelHome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCliExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txTelMovil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblSocioErr.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblSocioErr.setForeground(new java.awt.Color(172, 0, 0));
        lblSocioErr.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSocioErr.setText("<html><center>Error!<br /> verifica el <br />codigo <br />ingresado</center></html>");

        pnlSelSoc.setLayout(null);
        pnlLstPers.setViewportView(pnlSelSoc);

        pnlResume.setBackground(new java.awt.Color(255, 255, 255));
        pnlResume.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        pnlResume.setLayout(new java.awt.BorderLayout());

        txSubtotal.setColumns(7);

        jLabel12.setText("Subtotal:");

        jckFactura.setText("Aplica factura a esta venta");
        jckFactura.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jckFacturaItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(219, Short.MAX_VALUE)
                .addComponent(jckFactura)
                .addGap(18, 18, 18)
                .addComponent(jLabel12)
                .addGap(18, 18, 18)
                .addComponent(txSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(txSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel12)
                .addComponent(jckFactura))
        );

        pnlResume.add(jPanel3, java.awt.BorderLayout.NORTH);

        javax.swing.GroupLayout pnlTaxesLayout = new javax.swing.GroupLayout(pnlTaxes);
        pnlTaxes.setLayout(pnlTaxesLayout);
        pnlTaxesLayout.setHorizontalGroup(
            pnlTaxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 526, Short.MAX_VALUE)
        );
        pnlTaxesLayout.setVerticalGroup(
            pnlTaxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 120, Short.MAX_VALUE)
        );

        pnlResume.add(pnlTaxes, java.awt.BorderLayout.CENTER);

        jPanel4.setLayout(new java.awt.BorderLayout());

        txPagoTotal.setEditable(false);
        txPagoTotal.setColumns(7);

        jLabel14.setText("Pago total:");

        lblCargoCred.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCargoCred.setText("Cargo al credito:");

        txtCargCred.setEditable(false);
        txtCargCred.setColumns(7);

        lblAdeudo2.setText("Cobrar del adeudo:");

        lblAdeudo1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblAdeudo1.setForeground(new java.awt.Color(104, 0, 0));
        lblAdeudo1.setText("El cliente tiene deuda por: XX");

        txtAdeudo.setColumns(6);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(402, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(lblCargoCred)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCargCred, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txPagoTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblAdeudo1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(lblAdeudo2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtAdeudo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCargCred, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCargoCred))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblAdeudo1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAdeudo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAdeudo2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txPagoTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)))
        );

        jPanel4.add(jPanel5, java.awt.BorderLayout.CENTER);

        lblCredito.setText("Credito disponible");

        txCredit.setColumns(7);

        jLabel13.setText("Cargar el siguiente monto al credito disponible");

        javax.swing.GroupLayout pnlCreditLayout = new javax.swing.GroupLayout(pnlCredit);
        pnlCredit.setLayout(pnlCreditLayout);
        pnlCreditLayout.setHorizontalGroup(
            pnlCreditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCreditLayout.createSequentialGroup()
                .addContainerGap(230, Short.MAX_VALUE)
                .addGroup(pnlCreditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlCreditLayout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txCredit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblCredito, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        pnlCreditLayout.setVerticalGroup(
            pnlCreditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCreditLayout.createSequentialGroup()
                .addComponent(lblCredito)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCreditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txCredit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)))
        );

        jPanel4.add(pnlCredit, java.awt.BorderLayout.NORTH);

        pnlResume.add(jPanel4, java.awt.BorderLayout.SOUTH);

        lblTaxFail.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblTaxFail.setForeground(new java.awt.Color(205, 0, 0));
        lblTaxFail.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTaxFail.setText("Falló al cargar registros de impuestos!");

        lblClosedVnt.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblClosedVnt.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblClosedVnt.setText("Venta cerrada, cargos XX.xx con factura");

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    /**
     *  type 1=socio, 2=extern
     */
    private void switchTypeCli(int type){
        if(type==1){
            pnlEast.remove(pnlCliExtr);
            pnlAskSocio.setVisible(true);
            ignoreFieldExtrn(true);
        }else{
            pnlEast.remove(lblSocioErr);
            pnlEast.remove(pnlLstPers);
            if(mfich!=null){
                pnlEast.remove(mfich);
            }
            soc=null; updateCredito();
            pnlAskSocio.setVisible(false);
            pnlEast.add(pnlCliExtr,BorderLayout.CENTER);
            ignoreFieldExtrn(false);
        }
    }
    
    private void txSCodeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txSCodeKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            soc=null; setSocio();
        }
    }//GEN-LAST:event_txSCodeKeyPressed

    private void soBCodeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_soBCodeMouseClicked
        soBName.setBorder(null); searchSocByCode=true;
        soBCode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(23, 33, 36), 2));
    }//GEN-LAST:event_soBCodeMouseClicked

    private void soBNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_soBNameMouseClicked
        soBCode.setBorder(null); searchSocByCode=false;
        soBName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(23, 33, 36), 2));
    }//GEN-LAST:event_soBNameMouseClicked

    private void txCodeBarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txCodeBarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            addItem();
        }
    }//GEN-LAST:event_txCodeBarKeyPressed

    private void rbtnTpExtrItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rbtnTpExtrItemStateChanged
        switchTypeCli(2);
    }//GEN-LAST:event_rbtnTpExtrItemStateChanged

    private void rbntTpSocItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rbntTpSocItemStateChanged
                switchTypeCli(1);
    }//GEN-LAST:event_rbntTpSocItemStateChanged

    private void jckFacturaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jckFacturaItemStateChanged
        updateTotals();
    }//GEN-LAST:event_jckFacturaItemStateChanged

    @Override
    public void insertUpdate(DocumentEvent e) {
        updateTotals();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        updateTotals();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        updateTotals();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup grbtnTpCli;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JCheckBox jckFactura;
    private javax.swing.JLabel lblAdeudo1;
    private javax.swing.JLabel lblAdeudo2;
    private javax.swing.JLabel lblAskCustmr;
    private javax.swing.JLabel lblAskItems;
    private javax.swing.JLabel lblCargoCred;
    private javax.swing.JLabel lblClosedVnt;
    private javax.swing.JLabel lblCredito;
    private javax.swing.JLabel lblDireccion;
    private javax.swing.JLabel lblDni;
    private javax.swing.JLabel lblSocioErr;
    private javax.swing.JLabel lblTaxFail;
    private javax.swing.JPanel mainContain;
    private javax.swing.JPanel pnlAskSocio;
    private javax.swing.JPanel pnlCenter;
    private javax.swing.JPanel pnlCliExtr;
    private javax.swing.JPanel pnlCredit;
    private javax.swing.JPanel pnlEast;
    private javax.swing.JPanel pnlEastNorth;
    private javax.swing.JScrollPane pnlLstPers;
    private javax.swing.JPanel pnlNorth;
    private javax.swing.JPanel pnlResume;
    private javax.swing.JPanel pnlSelSoc;
    private javax.swing.JPanel pnlTaxes;
    private javax.swing.JRadioButton rbntTpSoc;
    private javax.swing.JRadioButton rbtnTpExtr;
    private javax.swing.JLabel soBCode;
    private javax.swing.JLabel soBName;
    private javax.swing.JTextField txApellidos;
    private javax.swing.JTextField txCiudad;
    private javax.swing.JTextField txCodeBar;
    private javax.swing.JTextField txCodigop;
    private javax.swing.JTextField txCredit;
    private javax.swing.JTextField txDNI;
    private javax.swing.JTextField txDireccion;
    private javax.swing.JTextField txEmail;
    private javax.swing.JTextField txNombre;
    private javax.swing.JTextField txPagoTotal;
    private javax.swing.JTextField txPoblacion;
    private javax.swing.JTextField txProvincia;
    private javax.swing.JTextField txSCode;
    private javax.swing.JTextField txSubtotal;
    private javax.swing.JTextField txTelHome;
    private javax.swing.JTextField txTelMovil;
    private javax.swing.JTextField txtAdeudo;
    private javax.swing.JTextField txtCargCred;
    // End of variables declaration//GEN-END:variables
}
