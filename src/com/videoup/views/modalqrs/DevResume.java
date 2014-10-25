/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.views.modalqrs;

import com.vendors.utils.WrapLayout;
import com.videoup.entities.VideoupBaseEntity;
import com.videoup.entities.VideoupCustomers;
import com.videoup.entities.VideoupHistcredito;
import com.videoup.entities.VideoupItemsrnt;
import com.videoup.entities.VideoupRentas;
import com.videoup.entities.VideoupSoldBonos;
import com.videoup.entities.VideoupTaxes;
import com.videoup.utils.GenProccess;
import com.videoup.utils.GeneralDAO;
import com.videoup.views.utils.Utils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.JButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Pedro
 */
public class DevResume extends javax.swing.JPanel implements DocumentListener{

    private int idRent;
    private List<Component> items;
    private float totals;
    private float subtotals;
    private float cargTaxs;
    private Devolucion devl;
    private float credito;
    private float useCredit;
    private VideoupRentas rent;
    private VideoupCustomers cli;
    private List<VideoupTaxes> taxes;
    private NumberFormat frmCurr;
    private List<VideoupSoldBonos> bonos;
    
    /**
     * Creates new form DevResume
     */
    public DevResume(Devolucion devl){
        this.devl=devl;
        loadTaxes();
        idRent=0;
        totals=0;
        credito=0;
        cli=null;
        frmCurr=NumberFormat.getCurrencyInstance();
        items=new ArrayList<Component>();
        initComponents();
        pnlUnSelected.setLayout(new WrapLayout(java.awt.FlowLayout.CENTER,7,3));
        lblCargoCred.setVisible(false);
        txtCargCred.setVisible(false);
        items.add(pnlHeadList);
        txCredit.getDocument().addDocumentListener(this);
        txtAdeudo.getDocument().addDocumentListener(this);
    }

    private void loadTaxes(){
        GeneralDAO gDao=new GeneralDAO();
        taxes=gDao.getListEntities("from VideoupTaxes vt where vt.apRent=true", null, 0, false);
    }
    
    public int getIdRent(){
        return idRent;
    }

    public List<VideoupBaseEntity> getItems2Save(){
        List<VideoupBaseEntity> vitems=new ArrayList<VideoupBaseEntity>();
        VideoupItemsrnt tmpItm;
        VideoupHistcredito hcred;
        float fromCred=0;
        float toAdeudo=0;
        if(credito>0 && useCredit>0){
            hcred=new VideoupHistcredito(useCredit*-1,Calendar.getInstance().getTime(),cli);
            cli.setCredito(credito-useCredit);
            vitems.add(cli);
            vitems.add(hcred);
            fromCred=useCredit;
        }else if(credito<0  && useCredit>0){
            hcred=new VideoupHistcredito(useCredit,Calendar.getInstance().getTime(),cli);
            cli.setCredito(credito+useCredit);
            vitems.add(cli);
            vitems.add(hcred);
            toAdeudo=useCredit;
        }
        for(Component cmpp: items){
            if(cmpp instanceof ItemResume){
                tmpItm=((ItemResume)cmpp).getItemr(true);
                if( ((ItemResume)cmpp).isPrepaid() ){
                    tmpItm.getIdbc().setStatus("Prepagado");
                    tmpItm.setStatus(7);
                }else{
                    tmpItm.getIdbc().setStatus("Disponible");
                    tmpItm.setFTime(new Date());
                    tmpItm.setStatus(4);
                }
                vitems.add(tmpItm);
            }
        }
        if(bonos!=null && !bonos.isEmpty()){
            for(VideoupSoldBonos bono: bonos){
                bono.setUsed(bono.getUsed()+bono.getUsing());
                vitems.add(bono);
            }
        }
        rent.setCstFin(rent.getCstFin()+subtotals);
        rent.setImpuesto(cargTaxs);
        rent.setFactura(jckFactura.isSelected());
        rent.setFromCredito(fromCred);
        rent.setToAdeudo(toAdeudo);
        vitems.add(rent);
        return vitems;
    }
    
    public boolean contains(VideoupItemsrnt itm){
        for(Component cmpp: items){
            if( cmpp instanceof ItemResume){
                if( ((ItemResume)cmpp).getItemr(false).getIdir()==itm.getIdir() ){
                    return true;
                }
            }
        }
        return false;
    }
    
    public void setFinalize(){
        for(Component cmpp: items){
            if( cmpp instanceof ItemResume){
                ((ItemResume)cmpp).setFinalized();
            }
        }
        jckFactura.setEnabled(false);
        btnFinalize.setEnabled(false); btnFinalize.setVisible(false);
        btnCerrar.setText("Cerrar"); txCredit.setEditable(false);
        devl.getAlqsCtrl().reloadFicha(idRent,rent,"dev");
    }
    
    public void addAllItems(){
        boolean isIn;
        for(VideoupItemsrnt itm: rent.getVideoupItemsrntList()){
            isIn=false;
            for(Component cp: items){
                if( cp instanceof ItemResume && ((ItemResume)cp).getIdIt()==itm.getIdir() ){
                    isIn=true; break; 
                }
            }
            if(!isIn && itm.getStatus()==3){ addItem(itm,0); }
        }
    }
    
    public void addItem(VideoupItemsrnt itm, int h){
        ItemResume itmr;
        String tit;
        if(idRent==0){
            rent=itm.getIdrt();
            idRent=rent.getIdrt();
            cli=rent.getIdcli();
            tit="<html><p style=\"text-align:center;\">Finalizacion de alquiler de articulos<br /> Alquiler: <u>";
            tit+=idRent+"</u>, socio; <u>"+cli.getCodCst()+", "+cli.getName()+" "+cli.getApplldos()+"</u></p></html>";
            lblTitResume.setText(tit);
            credito=cli.getCredito();
            loadBonos();
        }
        items.remove(pnlCredit);
        items.remove(pnlSubtotal);
        items.remove(pnlUnSelected);
        itmr=new ItemResume(this,itm,h);
        if(h>0){ btnFinalize.setText("Registar prepago"); }
        else{ btnFinalize.setText("Registar alquiler finalizado"); }
        items.add(itmr);
        refreshItems();
    }
    
    private void loadBonos(){
        bonos=devl.getCliBonos(cli.getIdct());
    }
    
    private void refreshItems(){
        pnlDetail.setVisible(false);
        items.add(pnlSubtotal);
        if(credito>0){
            lblCredito.setText("Cliente con credito disponible: "+frmCurr.format(credito));
            txCredit.setText("0");
            items.add(pnlCredit);
            lblCargoCred.setVisible(true);
            txtCargCred.setVisible(true);
        }else{
            lblCargoCred.setVisible(false);
            txtCargCred.setVisible(false);
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
        showUnSelected();
        Utils.loadAsTableLayout(items, pnlDetail, "No hay articulos agregados");
        updateTotals();
        pnlDetail.setVisible(true);
    }
    
    private void showUnSelected(){
        boolean isIn;
        JButton btnItm;
        int cnt=0;
        pnlUnSelected.removeAll();
        pnlUnSelected.add(lnlUnsel);
        for(VideoupItemsrnt itm: rent.getVideoupItemsrntList()){
            isIn=false;
            for(Component cp: items){
                if(cp instanceof ItemResume && ((ItemResume)cp).getIdIt()==itm.getIdir()){
                    isIn=true; break; 
                }
            }
            if(!isIn && itm.getStatus()==3){
                btnItm=new JButton( GenProccess.getItemName(itm) );
                btnItm.setActionCommand( itm.getIdbc().getBarcode() );
                btnItm.setToolTipText("Agregar a devolucion");
                btnItm.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent event){
                        devl.addItem2Resume( event.getActionCommand() );
                    }
                });
                pnlUnSelected.add(btnItm); cnt++;
            }
        }
        if(cnt>1){
                btnItm=new JButton("Agregar todos");
                btnItm.setActionCommand(""+rent.getIdrt());
                btnItm.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent event){
                        devl.addAllItemsRent( Integer.parseInt(event.getActionCommand()) );
                    }
                });
                pnlUnSelected.add(btnItm);
        }
        if(cnt>0){
            items.add(pnlUnSelected);
        }
    }
    
    public void updateTotals(){
        float tcargo;
        subtotals=0;
        boolean factr=jckFactura.isSelected();
        List<Component> appTxes;
        ATax aTax;
        float taxPrc;
        totals=0;
        cargTaxs=0;
        useBonos();
        for(Component cmpp: items){
            if( cmpp instanceof ItemResume){
                tcargo=((ItemResume)cmpp).getCargo();
                if(tcargo>=0){
                    totals+=tcargo;
                }else{
                    totals=-1; break;
                }   
            }
        }
        if(totals>=0){
            subtotals=totals;
        }
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
            try{ useCredit=Float.parseFloat(txCredit.getText()); }
            catch(NumberFormatException nfe){ useCredit=-1; }
            if(useCredit>credito){ useCredit=-1; }
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
        txSubtotal.setText( (totals>=0?""+frmCurr.format(subtotals):"--") );
        txPagoTt.setText( (totals>=0?""+frmCurr.format(totals):"error!") );
        devl.validate();
    }
    
    private void useBonos(){
        ItemResume curri;
        int touse, freeBonos;
        boolean isFirts;
        if(bonos==null || bonos.isEmpty()){
            return;
        }
        resetUsingBonos();
        for(Component cmpp: items){
            if( cmpp instanceof ItemResume){
                curri=((ItemResume)cmpp);
                isFirts=true;
                for(VideoupSoldBonos bono: bonos){
                    freeBonos=(bono.getBonos()-(bono.getUsing()+bono.getUsed()));
                    if( freeBonos>0 && (curri.canUseBonos()||isFirts) ){
                        touse=curri.useBonos(freeBonos, bono.getHours(), isFirts);
                        bono.setUsing(bono.getUsing()+touse); isFirts=false;
                    }
                }
            }
        }
    }
    
    private void resetUsingBonos(){   
        for(VideoupSoldBonos bono: bonos){
            bono.setUsing(0);
        }
    }
    
    public boolean hasErrors(){
        return totals<0;
    }
    
    public boolean hasBeenEdited(){
        for(Component cmpp: items){
            if( cmpp instanceof ItemResume){
                if( ((ItemResume)cmpp).isCargoEdited() ){
                    return true;
                }
                
            }
        }
        return false;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlHeadList = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        pnlCredit = new javax.swing.JPanel();
        lblCredito = new javax.swing.JLabel();
        txCredit = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        pnlSubtotal = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        txSubtotal = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jckFactura = new javax.swing.JCheckBox();
        pnlTaxes = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        lblTaxFail = new javax.swing.JLabel();
        pnlUnSelected = new javax.swing.JPanel();
        lnlUnsel = new javax.swing.JLabel();
        lblTitResume = new javax.swing.JLabel();
        pnlDetail = new javax.swing.JPanel();
        pnlSumm = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        btnFinalize = new javax.swing.JButton();
        btnCerrar = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        lblTotalPago = new javax.swing.JLabel();
        lblCargoCred = new javax.swing.JLabel();
        txtCargCred = new javax.swing.JTextField();
        txPagoTt = new javax.swing.JTextField();
        txtAdeudo = new javax.swing.JTextField();
        lblAdeudo1 = new javax.swing.JLabel();
        lblAdeudo2 = new javax.swing.JLabel();

        pnlHeadList.setLayout(new java.awt.BorderLayout());

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText("Articulo(s) a devolver");
        pnlHeadList.add(jLabel2, java.awt.BorderLayout.CENTER);

        jLabel3.setText(" Cargos          ");
        pnlHeadList.add(jLabel3, java.awt.BorderLayout.LINE_END);

        lblCredito.setText("Credito disponible");

        txCredit.setColumns(6);

        jLabel1.setText("Cargar el siguiente monto al credito disponible");

        javax.swing.GroupLayout pnlCreditLayout = new javax.swing.GroupLayout(pnlCredit);
        pnlCredit.setLayout(pnlCreditLayout);
        pnlCreditLayout.setHorizontalGroup(
            pnlCreditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCreditLayout.createSequentialGroup()
                .addContainerGap(154, Short.MAX_VALUE)
                .addGroup(pnlCreditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlCreditLayout.createSequentialGroup()
                        .addComponent(lblCredito)
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlCreditLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txCredit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        pnlCreditLayout.setVerticalGroup(
            pnlCreditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCreditLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblCredito)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCreditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txCredit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlSubtotal.setLayout(new java.awt.BorderLayout());

        txSubtotal.setEditable(false);
        txSubtotal.setColumns(6);

        jLabel4.setText("Subtotal:");

        jckFactura.setText("Aplica factura a este alquiler");
        jckFactura.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jckFacturaItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(378, Short.MAX_VALUE)
                .addComponent(jckFactura)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jckFactura)))
        );

        pnlSubtotal.add(jPanel3, java.awt.BorderLayout.NORTH);

        pnlTaxes.setLayout(null);
        pnlSubtotal.add(pnlTaxes, java.awt.BorderLayout.CENTER);
        pnlSubtotal.add(jSeparator1, java.awt.BorderLayout.SOUTH);

        lblTaxFail.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblTaxFail.setForeground(new java.awt.Color(205, 0, 0));
        lblTaxFail.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTaxFail.setText("Falló al cargar registros de impuestos!");

        pnlUnSelected.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        lnlUnsel.setText("Tambien en este alquiler:");
        pnlUnSelected.add(lnlUnsel);

        setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED, java.awt.Color.blue, java.awt.Color.lightGray));
        setLayout(new java.awt.BorderLayout());

        lblTitResume.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        lblTitResume.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitResume.setText("<html><p style=\"text-align:center;\">Finalizacion de alquiler de articulos<br /> renta id: <u>X</u> socio <u>Jhon Perez</u></p></html>");
        add(lblTitResume, java.awt.BorderLayout.NORTH);

        javax.swing.GroupLayout pnlDetailLayout = new javax.swing.GroupLayout(pnlDetail);
        pnlDetail.setLayout(pnlDetailLayout);
        pnlDetailLayout.setHorizontalGroup(
            pnlDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 770, Short.MAX_VALUE)
        );
        pnlDetailLayout.setVerticalGroup(
            pnlDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 147, Short.MAX_VALUE)
        );

        add(pnlDetail, java.awt.BorderLayout.CENTER);

        pnlSumm.setLayout(new java.awt.BorderLayout());

        btnFinalize.setText("Registrar alquiler finalizado");
        btnFinalize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinalizeActionPerformed(evt);
            }
        });
        jPanel1.add(btnFinalize);

        btnCerrar.setText("Cancelar");
        btnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarActionPerformed(evt);
            }
        });
        jPanel1.add(btnCerrar);

        pnlSumm.add(jPanel1, java.awt.BorderLayout.CENTER);

        lblTotalPago.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotalPago.setText("Pago total:");

        lblCargoCred.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCargoCred.setText("Cargo al credito:");

        txtCargCred.setEditable(false);
        txtCargCred.setColumns(6);

        txPagoTt.setEditable(false);
        txPagoTt.setColumns(6);

        txtAdeudo.setColumns(6);

        lblAdeudo1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblAdeudo1.setForeground(new java.awt.Color(104, 0, 0));
        lblAdeudo1.setText("El cliente tiene deuda por: XX");

        lblAdeudo2.setText("Cobrar del adeudo:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(233, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(lblCargoCred)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(lblTotalPago)
                                .addGap(4, 4, 4)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCargCred, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txPagoTt, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(lblAdeudo1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(lblAdeudo2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtAdeudo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCargCred, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCargoCred))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblAdeudo1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAdeudo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAdeudo2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txPagoTt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotalPago)))
        );

        pnlSumm.add(jPanel2, java.awt.BorderLayout.EAST);

        add(pnlSumm, java.awt.BorderLayout.PAGE_END);
    }// </editor-fold>//GEN-END:initComponents

    private void btnFinalizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinalizeActionPerformed
        devl.finalizeDevRs(this);
    }//GEN-LAST:event_btnFinalizeActionPerformed

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        devl.removeDevResume(this);
    }//GEN-LAST:event_btnCerrarActionPerformed

    private void jckFacturaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jckFacturaItemStateChanged
        updateTotals();
    }//GEN-LAST:event_jckFacturaItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnFinalize;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JCheckBox jckFactura;
    private javax.swing.JLabel lblAdeudo1;
    private javax.swing.JLabel lblAdeudo2;
    private javax.swing.JLabel lblCargoCred;
    private javax.swing.JLabel lblCredito;
    private javax.swing.JLabel lblTaxFail;
    private javax.swing.JLabel lblTitResume;
    private javax.swing.JLabel lblTotalPago;
    private javax.swing.JLabel lnlUnsel;
    private javax.swing.JPanel pnlCredit;
    private javax.swing.JPanel pnlDetail;
    private javax.swing.JPanel pnlHeadList;
    private javax.swing.JPanel pnlSubtotal;
    private javax.swing.JPanel pnlSumm;
    private javax.swing.JPanel pnlTaxes;
    private javax.swing.JPanel pnlUnSelected;
    private javax.swing.JTextField txCredit;
    private javax.swing.JTextField txPagoTt;
    private javax.swing.JTextField txSubtotal;
    private javax.swing.JTextField txtAdeudo;
    private javax.swing.JTextField txtCargCred;
    // End of variables declaration//GEN-END:variables

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
}
