/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.views.modsocios;

import com.videoup.controllers.Controller;
import com.videoup.entities.VideoupAutrz;
import com.videoup.entities.VideoupBaseEntity;
import com.videoup.entities.VideoupCimgs;
import com.videoup.entities.VideoupCstmrnotes;
import com.videoup.entities.VideoupCustomers;
import com.videoup.entities.VideoupDocs;
import com.videoup.entities.VideoupHistcredito;
import com.videoup.entities.VideoupItemsrnt;
import com.videoup.entities.VideoupItemsvnt;
import com.videoup.entities.VideoupPuntos;
import com.videoup.entities.VideoupRentas;
import com.videoup.entities.VideoupSoldBonos;
import com.videoup.entities.VideoupVentas;
import com.videoup.utils.GenProccess;
import com.videoup.utils.Globals;
import com.videoup.utils.ValidateOnFly;
import com.videoup.views.modbonos.SellBonos;
import com.videoup.views.modbonos.SellingBono;
import com.videoup.views.utils.Ask4Image;
import com.videoup.views.utils.ImagePanel;
import com.videoup.views.utils.PrintDiag;
import com.videoup.views.utils.Utils;
import com.videoup.views.utils.ViewFicha;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.JXTable;

/**
 *
 * @author Pedro
 */
public class Ficha extends ViewFicha implements ActionListener{
    
    private VideoupCustomers ent;
    private VideoupCimgs photo;
    private List<VideoupDocs> cstDocs;
    private int intSaveCustom; // 1=puntos, 2=credito, 3=persona, 4=update child, 5=documentos, 6=nota
    private int intDelCustom; // 1=persona autorizada, 2=documento, 3=nota, 4=puntos
    private ImagePanel imgPort;

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
        ent=(VideoupCustomers)getEntity("from VideoupCustomers where idct="+id);
    }
    
    private void initComps(Object ent){
        putMainContain(mainPan);
        cstDocs=new ArrayList<VideoupDocs>();
        lblDni.setText((String)Globals.getConfig("dniDef"));
        if(ent==null){
            this.ent=new VideoupCustomers();
            photo=new VideoupCimgs();
        }else{
            loadEntity(ent);
        }
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
        addJText2ListenChanges(txCodSocio);
        addJText2ListenChanges(jxDateAlta.getEditor());
        addJText2ListenChanges(jxDateVigen.getEditor());
    }
    
    @Override
    public boolean hasChanges(){
        boolean ochanges;
        List<Component> comps=new ArrayList();
        comps.addAll(Arrays.asList(pnlListPersonas.getComponents()));
        comps.addAll(Arrays.asList(pnlDocs.getComponents()));
        comps.addAll(Arrays.asList(pnlNotes.getComponents()));
        ochanges=super.hasChanges();
        for(Component ocmp: comps){
            if(ocmp instanceof PAutrz){
                ochanges=ochanges||((PAutrz)ocmp).hasChanges();
            }else if(ocmp instanceof SDocument){
                ochanges=ochanges||((SDocument)ocmp).hasChanges();
            }else if(ocmp instanceof SNote){
                ochanges=ochanges||((SNote)ocmp).hasChanges();
            }
        }
        return ochanges;
    }
    
    @Override
    protected void setUpValidator(){
        super.setUpValidator();
        vltor.setJTabbedPane(tabbs);
        vltor.addStringField(txNombre, "Nombre", true, 2, 255,0);
        vltor.addStringField(txApellidos, "Apellidos", true, 2, 255,0);
        vltor.addRegExpField(txDNI, "DNI", true,((String)Globals.getConfig("dniRgexp")),0);
        vltor.addStringField(txDireccion, "Direccion", true, 5, 105,0);
        vltor.addStringField(txCiudad, "Ciudad", false, 3, 55,0);
        vltor.addRegExpField(txCodigop, "Codigo postal", false,((String)Globals.getConfig("pcodeRgexp")),0);
        vltor.addStringField(txProvincia, "Provincia", false, 3, 55,0);
        vltor.addStringField(txPoblacion, "Poblacion", false, 3, 55,0);
        vltor.addRegExpField(txEmail, "E-mail", false,"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$",1);
        vltor.addRegExpField(txTelHome, "Telefono de casa",false,"^\\d{3,12}$",1);
        vltor.addRegExpField(txTelMovil, "Telefono movil", false,"^\\d{3,12}$",1);
        vltor.addStringField(txCodSocio, "Codigo de socio", true, 3, 12,2);
        vltor.addDateField(jxDateAlta,"Fecha de alta",true,2);
        vltor.addDateField(jxDateVigen,"Fecha de vigencia",true,2);
    }
        
    private void updatePicture(){
        InputStream inpstr;
        BufferedImage bfImg;
        if(photo.getFoto()==null){
            return;
        }
        inpstr=new ByteArrayInputStream(photo.getFoto());
        try {
            bfImg = ImageIO.read(inpstr);
        }catch (IOException ex) {
            lblImgError.setText("Error: "+ex.getMessage());
            pnlFoto.add(lblImgError,BorderLayout.CENTER);
            return;
        }
        if(imgPort!=null){
            pnlFoto.remove(imgPort);
        }
        imgPort=new ImagePanel(bfImg,new Dimension(217,275));
        pnlFoto.add(imgPort,BorderLayout.CENTER);
        validate();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblImgError = new javax.swing.JLabel();
        mainPan = new javax.swing.JPanel();
        tabbs = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txNombre = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txApellidos = new javax.swing.JTextField();
        lblDni = new javax.swing.JLabel();
        txDNI = new javax.swing.JTextField();
        lblDireccion = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txDireccion = new javax.swing.JTextField();
        txCiudad = new javax.swing.JTextField();
        txCodigop = new javax.swing.JTextField();
        txProvincia = new javax.swing.JTextField();
        txPoblacion = new javax.swing.JTextField();
        pnlFoto = new javax.swing.JPanel();
        btnPhoto = new javax.swing.JButton();
        jLabel23 = new javax.swing.JLabel();
        jxdFNac = new org.jdesktop.swingx.JXDatePicker();
        jScrollPane5 = new javax.swing.JScrollPane();
        jPanel5 = new javax.swing.JPanel();
        txTelMovil = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txTelHome = new javax.swing.JTextField();
        txEmail = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jPanel4 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txCodSocio = new javax.swing.JTextField();
        jxDateAlta = new org.jdesktop.swingx.JXDatePicker();
        jxDateVigen = new org.jdesktop.swingx.JXDatePicker();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel16 = new javax.swing.JLabel();
        txtCredito = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtMntCredito = new javax.swing.JTextField();
        btnAddCredito = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        pnlPuntos = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        txNPuntos = new javax.swing.JTextField();
        pnlHistPuntos = new javax.swing.JPanel();
        btnAddPuntos = new javax.swing.JButton();
        txtPuntos = new javax.swing.JTextField();
        jxDteVigPuntos = new org.jdesktop.swingx.JXDatePicker();
        jLabel3 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel17 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        lblBonosHist = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        pnlPersonas = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        btnAddPerson = new javax.swing.JButton();
        pnlListPersonas = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        btnAddDoc = new javax.swing.JButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        pnlBtnImgs = new javax.swing.JPanel();
        pnlDocs = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        pnlNotes = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        btnAddNote = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane11 = new javax.swing.JScrollPane();
        jxtHAlqs = new org.jdesktop.swingx.JXTable();
        jPanel13 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        txBCAlq = new javax.swing.JTextField();
        btnBuscaPrAlq = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane12 = new javax.swing.JScrollPane();
        jxtHComps = new org.jdesktop.swingx.JXTable();
        jPanel15 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        txBCVnts = new javax.swing.JTextField();
        btnSearchVntProd = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        btnContrato = new javax.swing.JButton();
        btnGenCred = new javax.swing.JButton();
        btnNewAlq = new javax.swing.JButton();
        btnNewSell = new javax.swing.JButton();
        btnBonos = new javax.swing.JButton();

        lblImgError.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblImgError.setForeground(new java.awt.Color(173, 0, 0));
        lblImgError.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImgError.setText("Error");

        mainPan.setLayout(new java.awt.BorderLayout());

        jLabel1.setText("Nombre:");

        txNombre.setColumns(23);

        jLabel2.setText("Apellidos:");

        txApellidos.setColumns(23);

        lblDni.setText("DNI:");

        txDNI.setColumns(23);

        lblDireccion.setText("Direcion:");

        jLabel6.setText("Codigo postal:");

        jLabel7.setText("Ciudad:");

        jLabel8.setText("Provincia:");

        jLabel9.setText("Poblacion:");

        txDireccion.setColumns(23);

        txCiudad.setColumns(23);

        txCodigop.setColumns(23);

        txProvincia.setColumns(23);

        txPoblacion.setColumns(23);

        pnlFoto.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        pnlFoto.setMaximumSize(new java.awt.Dimension(217, 275));
        pnlFoto.setMinimumSize(new java.awt.Dimension(217, 275));
        pnlFoto.setPreferredSize(new java.awt.Dimension(217, 275));
        pnlFoto.setLayout(new java.awt.BorderLayout());

        btnPhoto.setText("Foto");
        btnPhoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPhotoActionPerformed(evt);
            }
        });

        jLabel23.setText("fecha de nacimiento:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel23)
                            .addComponent(lblDni)
                            .addComponent(jLabel1)
                            .addComponent(lblDireccion)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(jLabel2)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txProvincia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txCodigop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txDNI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txApellidos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txPoblacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jxdFNac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 193, Short.MAX_VALUE)
                        .addComponent(pnlFoto, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnPhoto)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txApellidos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblDni)
                            .addComponent(txDNI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblDireccion)
                            .addComponent(txDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(txCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(txCodigop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(txProvincia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(txPoblacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(9, 9, 9)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel23)
                            .addComponent(jxdFNac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(pnlFoto, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPhoto)
                .addContainerGap(144, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jPanel1);

        tabbs.addTab("Datos personales", jScrollPane1);

        txTelMovil.setColumns(15);

        jLabel11.setText("Telefono movil:");

        jLabel4.setText("Email:");

        txTelHome.setColumns(15);

        txEmail.setColumns(15);

        jLabel10.setText("Telefono casa");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txTelMovil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txTelHome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(515, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txTelHome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txTelMovil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(376, Short.MAX_VALUE))
        );

        jScrollPane5.setViewportView(jPanel5);

        tabbs.addTab("Datos de contacto", jScrollPane5);

        jLabel12.setText("Codigo de socio:");

        jLabel13.setText("Fecha de alta:");

        jLabel14.setText("Vigencia hasta:");

        txCodSocio.setColumns(12);

        jLabel16.setText("Credito disponible:");

        txtCredito.setEditable(false);
        txtCredito.setColumns(12);

        jLabel5.setText("Agregar monto de credito:");

        txtMntCredito.setColumns(7);

        btnAddCredito.setText("Agregar");
        btnAddCredito.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddCreditoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txCodSocio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(jxDateAlta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel14)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jxDateVigen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCredito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtMntCredito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAddCredito)))
                        .addGap(0, 225, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txCodSocio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jxDateAlta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(jxDateVigen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(txtCredito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(txtMntCredito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddCredito))
                .addContainerGap(355, Short.MAX_VALUE))
        );

        jScrollPane4.setViewportView(jPanel4);

        tabbs.addTab("Datos de socio", jScrollPane4);

        jLabel17.setText("Puntos para uso en promociones:");

        txNPuntos.setColumns(5);

        pnlHistPuntos.setLayout(null);

        btnAddPuntos.setText("Agregar");
        btnAddPuntos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddPuntosActionPerformed(evt);
            }
        });

        txtPuntos.setEditable(false);
        txtPuntos.setColumns(9);

        jLabel3.setText("Vigentes hasta:");

        jLabel18.setText("Agregar puntos:");

        javax.swing.GroupLayout pnlPuntosLayout = new javax.swing.GroupLayout(pnlPuntos);
        pnlPuntos.setLayout(pnlPuntosLayout);
        pnlPuntosLayout.setHorizontalGroup(
            pnlPuntosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPuntosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPuntosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlHistPuntos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlPuntosLayout.createSequentialGroup()
                        .addGroup(pnlPuntosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlPuntosLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txNPuntos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jxDteVigPuntos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAddPuntos))
                            .addGroup(pnlPuntosLayout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtPuntos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 338, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlPuntosLayout.setVerticalGroup(
            pnlPuntosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlPuntosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPuntosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(txtPuntos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlPuntosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(txNPuntos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jxDteVigPuntos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddPuntos))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlHistPuntos, javax.swing.GroupLayout.DEFAULT_SIZE, 382, Short.MAX_VALUE)
                .addContainerGap())
        );

        jScrollPane6.setViewportView(pnlPuntos);

        tabbs.addTab("Puntos", jScrollPane6);

        jLabel24.setText("Bonos comprados por el socio");

        lblBonosHist.setLayout(null);

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblBonosHist, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, 708, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblBonosHist, javax.swing.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
                .addContainerGap())
        );

        jScrollPane2.setViewportView(jPanel17);

        tabbs.addTab("Bonos", jScrollPane2);

        jLabel15.setText(" Personas autorizadas a utilizar la cuenta de socio");

        btnAddPerson.setText("Agregar");
        btnAddPerson.setToolTipText("Agregar persona");
        btnAddPerson.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddPersonActionPerformed(evt);
            }
        });

        pnlListPersonas.setLayout(null);

        javax.swing.GroupLayout pnlPersonasLayout = new javax.swing.GroupLayout(pnlPersonas);
        pnlPersonas.setLayout(pnlPersonasLayout);
        pnlPersonasLayout.setHorizontalGroup(
            pnlPersonasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPersonasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPersonasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlListPersonas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlPersonasLayout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 399, Short.MAX_VALUE)
                        .addComponent(btnAddPerson)))
                .addContainerGap())
        );
        pnlPersonasLayout.setVerticalGroup(
            pnlPersonasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPersonasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPersonasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(btnAddPerson))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlListPersonas, javax.swing.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE)
                .addContainerGap())
        );

        jScrollPane7.setViewportView(pnlPersonas);

        tabbs.addTab("Representantes", jScrollPane7);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel6.setLayout(new java.awt.BorderLayout());

        jLabel19.setText("Documentos de socio (DNI, identificacion, etc)");

        btnAddDoc.setText("Agregar");
        btnAddDoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddDocActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 418, Short.MAX_VALUE)
                .addComponent(btnAddDoc)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(btnAddDoc)))
        );

        jPanel6.add(jPanel11, java.awt.BorderLayout.NORTH);

        java.awt.FlowLayout flowLayout1 = new java.awt.FlowLayout();
        flowLayout1.setAlignOnBaseline(true);
        pnlBtnImgs.setLayout(flowLayout1);
        jScrollPane8.setViewportView(pnlBtnImgs);

        jPanel6.add(jScrollPane8, java.awt.BorderLayout.CENTER);

        jPanel2.add(jPanel6, java.awt.BorderLayout.PAGE_START);

        pnlDocs.setLayout(new java.awt.BorderLayout());
        jPanel2.add(pnlDocs, java.awt.BorderLayout.CENTER);

        tabbs.addTab("Documentos", jPanel2);

        jPanel3.setLayout(new java.awt.BorderLayout());

        pnlNotes.setLayout(new javax.swing.BoxLayout(pnlNotes, javax.swing.BoxLayout.PAGE_AXIS));
        jPanel3.add(pnlNotes, java.awt.BorderLayout.CENTER);

        jLabel20.setText("Agregar nota textual");

        btnAddNote.setText("Agregar");
        btnAddNote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddNoteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 536, Short.MAX_VALUE)
                .addComponent(btnAddNote)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAddNote)
                    .addComponent(jLabel20))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.add(jPanel7, java.awt.BorderLayout.NORTH);

        jScrollPane3.setViewportView(jPanel3);

        tabbs.addTab("Notas", jScrollPane3);

        jPanel8.setLayout(new java.awt.BorderLayout());

        jSplitPane1.setDividerLocation(199);
        jSplitPane1.setDividerSize(7);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setPreferredSize(new java.awt.Dimension(634, 461));

        jPanel10.setLayout(new java.awt.BorderLayout());

        jxtHAlqs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Producto", "Codigo", "Titulo", "Estado", "Alquiler", "Devolucion"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jxtHAlqs.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane11.setViewportView(jxtHAlqs);
        jxtHAlqs.getColumnModel().getColumn(0).setPreferredWidth(90);
        jxtHAlqs.getColumnModel().getColumn(1).setPreferredWidth(70);
        jxtHAlqs.getColumnModel().getColumn(2).setPreferredWidth(320);
        jxtHAlqs.getColumnModel().getColumn(3).setPreferredWidth(100);
        jxtHAlqs.getColumnModel().getColumn(4).setPreferredWidth(160);
        jxtHAlqs.getColumnModel().getColumn(5).setPreferredWidth(160);

        jPanel10.add(jScrollPane11, java.awt.BorderLayout.CENTER);

        jPanel13.setLayout(new java.awt.BorderLayout());

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("Historial de alquileres");
        jPanel13.add(jLabel21, java.awt.BorderLayout.CENTER);

        jPanel14.setLayout(new java.awt.BorderLayout());

        txBCAlq.setColumns(13);
        txBCAlq.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txBCAlqKeyPressed(evt);
            }
        });
        jPanel14.add(txBCAlq, java.awt.BorderLayout.CENTER);

        btnBuscaPrAlq.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/videoup/imgs/buscar16.png"))); // NOI18N
        btnBuscaPrAlq.setToolTipText("Buscar codigo o titulo");
        btnBuscaPrAlq.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscaPrAlqActionPerformed(evt);
            }
        });
        jPanel14.add(btnBuscaPrAlq, java.awt.BorderLayout.LINE_END);

        jPanel13.add(jPanel14, java.awt.BorderLayout.EAST);

        jPanel10.add(jPanel13, java.awt.BorderLayout.NORTH);

        jSplitPane1.setLeftComponent(jPanel10);

        jPanel9.setLayout(new java.awt.BorderLayout());

        jxtHComps.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Producto", "Codigo", "Titulo", "Fecha"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jxtHComps.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane12.setViewportView(jxtHComps);
        jxtHComps.getColumnModel().getColumn(0).setPreferredWidth(90);
        jxtHComps.getColumnModel().getColumn(1).setPreferredWidth(80);
        jxtHComps.getColumnModel().getColumn(2).setPreferredWidth(320);
        jxtHComps.getColumnModel().getColumn(3).setPreferredWidth(160);

        jPanel9.add(jScrollPane12, java.awt.BorderLayout.CENTER);

        jPanel15.setLayout(new java.awt.BorderLayout());

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("Historial de compras");
        jPanel15.add(jLabel22, java.awt.BorderLayout.CENTER);

        jPanel16.setLayout(new java.awt.BorderLayout());

        txBCVnts.setColumns(13);
        txBCVnts.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txBCVntsKeyPressed(evt);
            }
        });
        jPanel16.add(txBCVnts, java.awt.BorderLayout.CENTER);

        btnSearchVntProd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/videoup/imgs/buscar16.png"))); // NOI18N
        btnSearchVntProd.setToolTipText("Buscar codigo o titulo");
        btnSearchVntProd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchVntProdActionPerformed(evt);
            }
        });
        jPanel16.add(btnSearchVntProd, java.awt.BorderLayout.LINE_END);

        jPanel15.add(jPanel16, java.awt.BorderLayout.EAST);

        jPanel9.add(jPanel15, java.awt.BorderLayout.NORTH);

        jSplitPane1.setRightComponent(jPanel9);

        jPanel8.add(jSplitPane1, java.awt.BorderLayout.CENTER);

        tabbs.addTab("Historial", jPanel8);

        mainPan.add(tabbs, java.awt.BorderLayout.CENTER);

        btnContrato.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/videoup/imgs/print.png"))); // NOI18N
        btnContrato.setText("Contrato");
        btnContrato.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnContratoActionPerformed(evt);
            }
        });
        jPanel12.add(btnContrato);

        btnGenCred.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/videoup/imgs/rSocios32.png"))); // NOI18N
        btnGenCred.setText("Credencial");
        btnGenCred.setToolTipText("Generar credencial de socio");
        btnGenCred.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenCredActionPerformed(evt);
            }
        });
        jPanel12.add(btnGenCred);

        btnNewAlq.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/videoup/imgs/alq32.png"))); // NOI18N
        btnNewAlq.setText("Nuevo Alquiler");
        btnNewAlq.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewAlqActionPerformed(evt);
            }
        });
        jPanel12.add(btnNewAlq);

        btnNewSell.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/videoup/imgs/vnts32.png"))); // NOI18N
        btnNewSell.setText("Nueva Compra");
        btnNewSell.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewSellActionPerformed(evt);
            }
        });
        jPanel12.add(btnNewSell);

        btnBonos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/videoup/imgs/bonos32.png"))); // NOI18N
        btnBonos.setText("Comprar Bonos");
        btnBonos.setToolTipText("Comprar bonos");
        btnBonos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBonosActionPerformed(evt);
            }
        });
        jPanel12.add(btnBonos);

        mainPan.add(jPanel12, java.awt.BorderLayout.SOUTH);

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddPersonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddPersonActionPerformed
        VideoupAutrz persona;
        PerformingTask task;
        String nombre;
        if(ent.getId()==null || ent.getId()==0){
            ctrl.showDialogErr("No puede agregar personas en un registro sin guardar");
            return;
        }
        nombre=ctrl.askForString("Ingrese el nombre de la persona", "Agregar persona");
        if(nombre==null){
            return;
        }
        if(nombre.length()<7 || nombre.length()>255){
            ctrl.showDialogErr("El nombre debe tener una longitud entre 7 y 255 caracteres");
            return;
        }
        persona=new VideoupAutrz(nombre);
        persona.setIdct(ent);
        intSaveCustom=3;
        task=new PerformingTask();
        setBusy("Agregando persona",true);
        task.setTask(2); task.setParam(persona,false);
        validate(); task.execute();
    }//GEN-LAST:event_btnAddPersonActionPerformed

    private void btnAddPuntosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddPuntosActionPerformed
        int pnts;
        VideoupPuntos punts;
        PerformingTask task;
        Date vigen=jxDteVigPuntos.getDate();
        if(ent.getId()==null || ent.getId()==0){
            ctrl.showDialogErr("No puede agregar personas en un registro sin guardar");
            return;
        }
        if(!ValidateOnFly.validateInteger(txNPuntos, true)){
            showError(ValidateOnFly.getError());
        }else if(vigen==null || !vigen.after(Globals.getToday())){
            showError("Debe indicar una fecha posterior al dia actual");
        }else{
            pnts=ValidateOnFly.getLastInt();
            punts=new VideoupPuntos(null,pnts,Globals.getToday(),vigen);
            punts.setIdct(ent);
            intSaveCustom=1;
            task=new PerformingTask();
            setBusy("Agregando puntos",true);
            task.setTask(2); task.setParam(punts,false);
            validate(); task.execute();
        }
    }//GEN-LAST:event_btnAddPuntosActionPerformed

    private void btnAddCreditoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddCreditoActionPerformed
        float credit;
        PerformingTask task;
        VideoupHistcredito hcred;
        List<VideoupBaseEntity> ents;
        if(ent.getId()==null || ent.getId()==0){
            ctrl.showDialogErr("No puede agregar credito en un registro sin guardar");
            return;
        }
        if(!ValidateOnFly.validateFloat(txtMntCredito, false)){
            showError(ValidateOnFly.getError());
        }else{
            ents=new ArrayList<VideoupBaseEntity>();
            credit=ValidateOnFly.getLastFloat();
            hcred=new VideoupHistcredito(credit,Calendar.getInstance().getTime(),ent);
            ent.setCredito( ent.getCredito()+credit );
            intSaveCustom=2;
            ents.add(ent); ents.add(hcred);
            task=new PerformingTask();
            setBusy("Agregando credito",true);
            task.setTask(4); task.setListParam(ents);
            validate(); task.execute();
        }
    }//GEN-LAST:event_btnAddCreditoActionPerformed

    private void btnAddDocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddDocActionPerformed
        PerformingTask task;
        JFileChooser selFile=new JFileChooser();
        String[] suffices=ImageIO.getReaderFileSuffixes();
        String nombre;
        File imgfile;
        Image img;
        boolean isImage;
        byte[] blob;
        FileInputStream fileInpStr;
        VideoupDocs vdoc;
        int slFile;
        if(ent.getId()==null || ent.getId()==0){
            ctrl.showDialogErr("No puede agregar documentos en un registro sin guardar");
            return;
        }
        nombre=ctrl.askForString("Ingrese el nombre del documento", "Agregar documento");
        if(nombre==null){ return; }
        if(nombre.length()<7 || nombre.length()>255){
            ctrl.showDialogErr("El nombre debe tener una longitud entre 7 y 155 caracteres");
            return;
        }
        for(int i=0;i<suffices.length;i++){
            selFile.addChoosableFileFilter(new FileNameExtensionFilter(suffices[i]+" archivos",suffices[i]));
        }
        slFile=selFile.showDialog(ctrl.getMainWindow(),"Elegir imagen");
        if(slFile==JFileChooser.APPROVE_OPTION){
            imgfile=selFile.getSelectedFile();
            isImage=true;
            try{
                img=ImageIO.read(imgfile);
                if(img==null){ isImage=false; }
            }catch(IOException ex){ isImage=false; }
            if(!isImage){
                ctrl.showDialogErr("El archivo deb ser una imagen");
                return;
            }
            blob=new byte[(int)imgfile.length()];
            try {
                fileInpStr=new FileInputStream(imgfile);
                fileInpStr.read(blob);
            }catch (FileNotFoundException ex){
                ctrl.showDialogErr("Error: "+ex.getMessage());
                return;
            }catch (IOException ex){
                ctrl.showDialogErr("Error: "+ex.getMessage());
                return;
            }
            vdoc=new VideoupDocs(nombre,blob);
            vdoc.setIdct(ent.getId());
            intSaveCustom=5;
            task=new PerformingTask();
            setBusy("Agregando documento",true);
            task.setTask(2); task.setParam(vdoc,false);
            validate(); task.execute();
        }
    }//GEN-LAST:event_btnAddDocActionPerformed

    private void btnAddNoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddNoteActionPerformed
        PerformingTask task;
        VideoupCstmrnotes note;
        Date today= Calendar.getInstance().getTime();
        if(ent.getId()==null || ent.getId()==0){
            ctrl.showDialogErr("No puede agregar notas en un registro sin guardar");
            return;
        }
        note=new VideoupCstmrnotes("escriba su nota aqui...",today);
        note.setIdct(ent);
        note.setNtype(0);
        intSaveCustom=6;
        task=new PerformingTask();
        setBusy("Agregando nota",true);
        task.setTask(2); task.setParam(note,false);
        validate(); task.execute();
    }//GEN-LAST:event_btnAddNoteActionPerformed

    private void btnPhotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPhotoActionPerformed
        Ask4Image dgAskPhoto=new Ask4Image(getMainWindow(),true,this,1);
        dgAskPhoto.setVisible(true);
    }//GEN-LAST:event_btnPhotoActionPerformed

    @Override
    public void setGettedImage(byte[] img, int key){
        if(img==null){
            return;
        }
        photo.setFoto(img);
        changes=true;
        updatePicture();
    }
    
    private void btnContratoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContratoActionPerformed
        PrintDiag pDiag;
        if(ent.getId()==null || ent.getId()==0){
            ctrl.showDialogErr("No puede imprimir contrato en un registro sin guardar");
            return;
        }
        pDiag=new PrintDiag(getMainWindow(),true,"Contrato de cliente");
        pDiag.cargaReporte(getParams(),null,2);
        pDiag.setVisible(true);
    }//GEN-LAST:event_btnContratoActionPerformed

    private void btnGenCredActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenCredActionPerformed
        if(ent.getId()==null || ent.getId()==0){
            ctrl.showDialogErr("No puede generar credencial de un registro sin guardar");
            return;
        }
        getMainWindow().generateCodesReport(1,ent.getCodCst());
    }//GEN-LAST:event_btnGenCredActionPerformed

    private void btnNewAlqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewAlqActionPerformed
        if(ent.getId()==null || ent.getId()==0){
            ctrl.showDialogErr("No puede crear alquiler de un registro sin guardar");
            return;
        }
        getMainWindow().startSociosAlq(ent);
    }//GEN-LAST:event_btnNewAlqActionPerformed

    private void btnNewSellActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewSellActionPerformed
        if(ent.getId()==null || ent.getId()==0){
            ctrl.showDialogErr("No puede crear alquiler de un registro sin guardar");
            return;
        }
        getMainWindow().startSociosVenta(ent);
    }//GEN-LAST:event_btnNewSellActionPerformed

    private void btnBuscaPrAlqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscaPrAlqActionPerformed
        searchHistory(true);
    }//GEN-LAST:event_btnBuscaPrAlqActionPerformed

    private void txBCAlqKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txBCAlqKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            searchHistory(true);
        }
    }//GEN-LAST:event_txBCAlqKeyPressed

    private void btnSearchVntProdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchVntProdActionPerformed
        searchHistory(false);
    }//GEN-LAST:event_btnSearchVntProdActionPerformed

    private void txBCVntsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txBCVntsKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            searchHistory(false);
        }
    }//GEN-LAST:event_txBCVntsKeyPressed

    private void btnBonosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBonosActionPerformed
        if(ent.getId()==null || ent.getId()==0){
            ctrl.showDialogErr("No puede comprar bonos de un registro sin guardar");
            return;
        }
        SellBonos seller=new SellBonos(ctrl.getMainWindow(),true,ent,this);
        seller.setVisible(true);
    }//GEN-LAST:event_btnBonosActionPerformed
    
    private Map<String,Object> getParams(){
        Map<String,Object> parametros = new HashMap<String,Object>();
        String htmldoc=Globals.getConfig("contrato").toString();
        Date today=Calendar.getInstance().getTime();
        DateFormat df=DateFormat.getDateInstance(DateFormat.MEDIUM);
        htmldoc=htmldoc.replaceAll("\\[vclub\\]",Globals.getConfig("nVclub").toString());
        htmldoc=htmldoc.replaceAll("\\[today\\]",df.format(today));
        htmldoc=htmldoc.replaceAll("\\[nomsc\\]",ent.getName());
        htmldoc=htmldoc.replaceAll("\\[appsc\\]",ent.getApplldos());
        htmldoc=htmldoc.replaceAll("\\[dnisc\\]",ent.getDni());
        htmldoc=htmldoc.replaceAll("\\[codesc\\]",ent.getCodCst());
        htmldoc=htmldoc.replaceAll("\\[altasc\\]",df.format(ent.getFAlta()));
        htmldoc=htmldoc.replaceAll("\\[vignsc\\]",df.format(ent.getFVigen()));
        htmldoc=htmldoc.replaceAll("\\[replstsc\\]",ent.getHtmlListAutrz());
        htmldoc=htmldoc.replaceAll("\\[repssc\\]",ent.getListAutrz());
        htmldoc=htmldoc.replaceAll("\\[dirsc\\]",(ent.getAddr()!=null?ent.getAddr():""));
        htmldoc=htmldoc.replaceAll("\\[poblsc\\]",(ent.getPobl()!=null?ent.getPobl():""));
        htmldoc=htmldoc.replaceAll("\\[provsc\\]",(ent.getProv()!=null?ent.getProv():""));
        htmldoc=htmldoc.replaceAll("\\[cddsc\\]",(ent.getCity()!=null?ent.getCity():""));
        htmldoc=htmldoc.replaceAll("\\[cpsc\\]",(ent.getCodp()!=null?ent.getCodp():""));
        htmldoc=htmldoc.replaceAll("\\[mailsc\\]",(ent.getEmail()!=null?ent.getEmail():""));
        htmldoc=htmldoc.replaceAll("\\[telcasasc\\]",(ent.getTelHome()!=null?ent.getTelHome():""));
        htmldoc=htmldoc.replaceAll("\\[telmovsc\\]",(ent.getTelMovil()!=null?ent.getTelMovil():""));
        parametros.put("htmldocument",htmldoc);
        return parametros;
    }
    
    public boolean updateChildEntity(VideoupBaseEntity chlEnt){
        PerformingTask task;
        boolean saved;
        intSaveCustom=4;
        task=new PerformingTask();
        setBusy("Actualizando registro",true);
        task.setTask(2); task.setParam(chlEnt,false);
        validate(); task.execute();
        try {
            saved=task.get();
        } catch (InterruptedException ex) {
            saved=false;
        } catch (ExecutionException ex) {
            saved=false;
        }
        return saved;
    }
    
    public void deleteChildEntity(VideoupBaseEntity chlEnt,int type){
        PerformingTask task;
        if(!ctrl.confirm("Va a eliminar un registro", "Esta accion NO se podra deshacer, ¿desea continuar?")){
            return;
        }
        intDelCustom=type;
        task=new PerformingTask();
        setBusy("Eliminado registro",true);
        task.setTask(3); task.setParam(chlEnt,false);
        validate(); task.execute();
    }
    
    @Override
    protected boolean saveCustom(VideoupBaseEntity entity,boolean asNew){
        boolean saved=super.saveEntity(entity,false);
        if(intSaveCustom==1 && saved){
            ent.addVideoupPuntos( ((VideoupPuntos)entity) );
            txNPuntos.setText("");
            jxDteVigPuntos.setDate(null);
            refreshPuntos();
        }else if(intSaveCustom==2 && saved){
            txtMntCredito.setText("");
            refreshCredito();
        }else if(intSaveCustom==3 && saved){
            ent.addVideoupAutrz( ((VideoupAutrz)entity) );
            refreshPersonas();
        }else if(intSaveCustom==4 && saved){}
        else if(intSaveCustom==5 && saved){
            cstDocs.add((VideoupDocs)entity);
            refreshDocuments();
        }else if(intSaveCustom==6 && saved){
            ent.addVideoupCstmrnotes((VideoupCstmrnotes)entity);
            refreshNotes();
        }
        return saved;
    }
    
    @Override
    protected boolean saveEntities(List<VideoupBaseEntity> entities){
        boolean saveok=super.saveEntities(entities);
        if(intSaveCustom==2 && saveok){
            txtMntCredito.setText("");
            refreshCredito();
        }
        return saveok;
    }
    
    @Override
    protected boolean deleteEntity(VideoupBaseEntity entity){
        boolean deleted=super.deleteEntity(entity);
        if(intDelCustom==1 && deleted){
            ent.removeVideoupAutrz( ((VideoupAutrz)entity) );
            refreshPersonas();
        }else if(intDelCustom==2 && deleted){
            cstDocs.remove((VideoupDocs)entity);
            refreshDocuments();
        }else if(intDelCustom==3 && deleted){
            ent.removeVideoupCstmrnotes((VideoupCstmrnotes)entity);
            refreshNotes();
        }else if(intDelCustom==4 && deleted){
            ent.removeVideoupPuntos( (VideoupPuntos)entity  );
            refreshPuntos();
        }
        return deleted;
    }
    
    @Override
    public String getTitle(){
        return "Ficha de socio"+(getId()==0?" nuevo":(ent==null?" ID: "+getId():": "+ent.getName()+" "+ent.getApplldos()));
    }
    
    @Override
    protected boolean saveEntity(VideoupBaseEntity ignore,boolean forceAsNew){
        boolean saved;
        boolean isPortNew=false;
        ent.setName(txNombre.getText());
        ent.setApplldos(txApellidos.getText());
        ent.setDni(txDNI.getText());
        ent.setCodCst(txCodSocio.getText());
        ent.setEmail(txEmail.getText());
        ent.setAddr(txDireccion.getText());
        ent.setCodp(txCodigop.getText());
        ent.setCity(txCiudad.getText());
        ent.setProv(txProvincia.getText());
        ent.setPobl(txPoblacion.getText());
        ent.setTelHome(txTelHome.getText());
        ent.setTelMovil(txTelMovil.getText());
        ent.setFAlta(jxDateAlta.getDate());
        ent.setFVigen(jxDateVigen.getDate());
        ent.setfNac(jxdFNac.getDate());
        saved=super.saveEntity(ent,false);
        if(saved && photo.getFoto()!=null){
            if(photo.getId()==null || photo.getId()==0){
                photo.setIdrl(ent.getId());
                isPortNew=true;
            }
            saved=super.saveEntity(photo,isPortNew);
        }
        changes=!saved;
        return saved;
    }
    
    @Override
    protected void updateIdTitle(){
        setId(ent.getIdct());
        lblTitle.setText(getTitle());
        ctrl.changeViewName(this, getTitle());
    }
    
    @Override
    public void loadEntity(Object oEnt){
        ent=((VideoupCustomers)oEnt);
        txNombre.setText(ent.getName());
        txApellidos.setText(ent.getApplldos());
        txDNI.setText(ent.getDni());
        txCodSocio.setText(ent.getCodCst());
        txEmail.setText(ent.getEmail());
        txDireccion.setText(ent.getAddr());
        txCodigop.setText(ent.getCodp());
        txCiudad.setText(ent.getCity());
        txProvincia.setText(ent.getProv());
        txPoblacion.setText(ent.getPobl());
        txTelHome.setText(ent.getTelHome());
        txTelMovil.setText(ent.getTelMovil());
        jxDateAlta.setDate(ent.getFAlta());
        jxDateVigen.setDate(ent.getFVigen());
        jxdFNac.setDate(ent.getfNac());
        lblTitle.setText(getTitle());
        photo=(VideoupCimgs)getEntity("from VideoupCimgs where idrl="+ent.getId());
        if(photo==null && getError()==null){
            photo=new VideoupCimgs();
        }
        cstDocs=loadList("from VideoupDocs where idct="+ent.getId(), null, 0, false);
        refreshCredito();
        refreshPuntos();
        refreshPersonas();
        refreshDocuments();
        refreshNotes();
        refreshBonos();
        updatePicture();
        updateIdTitle();
        loadHistory();
        changes=false;
    }
    
    private void loadHistory(){
        String[] nstts={"Solicitado","Cancelado","Finalizado","En curso","Finalizado pagada","Finalizado por pagar","Cambiado"};
        List<VideoupRentas> alqs=ent.getVideoupRentasList();
        List<VideoupVentas> vnts;
        List<VideoupItemsrnt> prods;
        List<VideoupItemsvnt> compr;
        DefaultTableModel tmodel=((DefaultTableModel)jxtHAlqs.getModel());
        Object[] row;
        if(alqs!=null && !alqs.isEmpty()){
            for(VideoupRentas alq: alqs){
                prods=alq.getVideoupItemsrntList();
                for(VideoupItemsrnt prod: prods){
                    row=new Object[6];
                    row[0]=(prod.getIsmov()?"Pelicula":"Videojuego");
                    row[1]=prod.getIdbc().getBarcode();
                    row[2]=(prod.getIsmov()?prod.getIdbc().getVideoupMovie().getTitulo():prod.getIdbc().getVideoupGame().getTitulo());
                    row[3]=(prod.getFTime()==null?"En curso":"Finalizada");
                    row[4]=nstts[prod.getStatus()];
                    row[5]=asDate(prod.getFTime(),DateFormat.LONG,DateFormat.SHORT);
                    tmodel.addRow(row);
                }
            }
            jxtHAlqs.setModel(tmodel);
        }
        vnts=loadList("from VideoupVentas where idcli='"+ent.getId()+"'", null, 0, false);
        if(vnts!=null && !vnts.isEmpty()){
            tmodel=((DefaultTableModel)jxtHComps.getModel());
            for(VideoupVentas vnt: vnts){
                compr=vnt.getVideoupItemsvntList();
                for(VideoupItemsvnt prod: compr){
                    row=new Object[4];
                    row[0]=(prod.getIsmov()?"Pelicula":"Videojuego");
                    row[1]=prod.getIdbc().getBarcode();
                    row[2]=(prod.getIsmov()?prod.getIdbc().getVideoupMovie().getTitulo():prod.getIdbc().getVideoupGame().getTitulo());
                    row[3]=vnt.getOnFecha();
                    tmodel.addRow(row);
                }
            }
            jxtHComps.setModel(tmodel);
        }
    }
    
    private void searchHistory(boolean alq){
        String code=(alq?txBCAlq.getText().trim():txBCVnts.getText().trim());
        JXTable table=(alq?jxtHAlqs:jxtHComps);
        DefaultTableModel tmodel=((DefaultTableModel)table.getModel());
        boolean found=false;
        if(code.length()>0){
            for(int h=0;h<tmodel.getRowCount();h++){
                if(tmodel.getValueAt(h,1).toString().equals(code)
                        || tmodel.getValueAt(h,2).toString().toLowerCase().equals(code.toLowerCase()) ){
                    table.getSelectionModel().setSelectionInterval(h, h);
                    table.scrollRectToVisible(table.getCellRect(h,0,true));
                    found=true; break;
                }
            }
            if(!found){
                table.getSelectionModel().clearSelection();
                showInfo("Busqueda","No se encontro codigo o titulo: "+code);
            }
        }
    }
    
    private void refreshPersonas(){
        List<VideoupAutrz> personas=ent.getVideoupAutrzList();
        List<Component> perns=new ArrayList<Component>();
        if(personas!=null){
            for(VideoupAutrz persn: personas){
                perns.add(new PAutrz(persn,this,ctrl));
            }
        }
        Utils.loadAsTableLayout(perns, pnlListPersonas, "No hay personas registradas");
    }
    
    private void refreshCredito(){
        NumberFormat nmf=NumberFormat.getCurrencyInstance();
        txtCredito.setText(nmf.format(ent.getCredito()==null?0.0:ent.getCredito()));
    }
    
    private void refreshPuntos(){
        List<VideoupPuntos> puntos=ent.getVideoupPuntosList();
        List<Component> punts=new ArrayList<Component>();
        int totalPuntos=0;
        if(puntos!=null){
            for(VideoupPuntos pnt: puntos){
                punts.add(new Spuntos(pnt,this));
                totalPuntos+=pnt.getPuntos();
            }
        }
        Utils.loadAsTableLayout(punts, pnlHistPuntos, "No hay puntos para este socio");
        txtPuntos.setText(""+totalPuntos);
    }
    
    public void refreshBonos(){
        List<VideoupSoldBonos> bonos=loadList("from VideoupSoldBonos where idcli='"+ent.getIdct()+"'", null, 0, false);
        List<Component> cmBonos=new ArrayList<Component>();
        if(bonos!=null){
            for(VideoupSoldBonos bono: bonos){
                cmBonos.add(new SellingBono(bono));
            }
        }
        Utils.loadAsTableLayout(cmBonos, lblBonosHist, "No hay bonos para este socio");
    }
    
    private void refreshDocuments(){
        JButton[] btnImg;
        int cnt;
        pnlBtnImgs.removeAll();
        if(cstDocs==null || cstDocs.isEmpty()){
            pnlBtnImgs.add(new JLabel("No hay documentos registrados"));
        }else{
            btnImg=new JButton[cstDocs.size()];
            for(cnt=0;cnt<cstDocs.size();cnt++){
                btnImg[cnt]=new JButton(cstDocs.get(cnt).getDocname());
                btnImg[cnt].setActionCommand(""+cnt);
                btnImg[cnt].addActionListener(this);
                pnlBtnImgs.add(btnImg[cnt]);
            }
        }
    }
    
    private void refreshNotes(){
        List<VideoupCstmrnotes> notes=ent.getVideoupCstmrnotesList();
        List<Component> snotes=new ArrayList<Component>();
        if(notes!=null){
            for(VideoupCstmrnotes nte: notes){
                snotes.add(new SNote(nte,this,ctrl));
            }
        }
        Utils.loadAsTableLayout(snotes, pnlNotes, "No hay notas registradas");
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddCredito;
    private javax.swing.JButton btnAddDoc;
    private javax.swing.JButton btnAddNote;
    private javax.swing.JButton btnAddPerson;
    private javax.swing.JButton btnAddPuntos;
    private javax.swing.JButton btnBonos;
    private javax.swing.JButton btnBuscaPrAlq;
    private javax.swing.JButton btnContrato;
    private javax.swing.JButton btnGenCred;
    private javax.swing.JButton btnNewAlq;
    private javax.swing.JButton btnNewSell;
    private javax.swing.JButton btnPhoto;
    private javax.swing.JButton btnSearchVntProd;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSplitPane jSplitPane1;
    private org.jdesktop.swingx.JXDatePicker jxDateAlta;
    private org.jdesktop.swingx.JXDatePicker jxDateVigen;
    private org.jdesktop.swingx.JXDatePicker jxDteVigPuntos;
    private org.jdesktop.swingx.JXDatePicker jxdFNac;
    private org.jdesktop.swingx.JXTable jxtHAlqs;
    private org.jdesktop.swingx.JXTable jxtHComps;
    private javax.swing.JPanel lblBonosHist;
    private javax.swing.JLabel lblDireccion;
    private javax.swing.JLabel lblDni;
    private javax.swing.JLabel lblImgError;
    private javax.swing.JPanel mainPan;
    private javax.swing.JPanel pnlBtnImgs;
    private javax.swing.JPanel pnlDocs;
    private javax.swing.JPanel pnlFoto;
    private javax.swing.JPanel pnlHistPuntos;
    private javax.swing.JPanel pnlListPersonas;
    private javax.swing.JPanel pnlNotes;
    private javax.swing.JPanel pnlPersonas;
    private javax.swing.JPanel pnlPuntos;
    private javax.swing.JTabbedPane tabbs;
    private javax.swing.JTextField txApellidos;
    private javax.swing.JTextField txBCAlq;
    private javax.swing.JTextField txBCVnts;
    private javax.swing.JTextField txCiudad;
    private javax.swing.JTextField txCodSocio;
    private javax.swing.JTextField txCodigop;
    private javax.swing.JTextField txDNI;
    private javax.swing.JTextField txDireccion;
    private javax.swing.JTextField txEmail;
    private javax.swing.JTextField txNPuntos;
    private javax.swing.JTextField txNombre;
    private javax.swing.JTextField txPoblacion;
    private javax.swing.JTextField txProvincia;
    private javax.swing.JTextField txTelHome;
    private javax.swing.JTextField txTelMovil;
    private javax.swing.JTextField txtCredito;
    private javax.swing.JTextField txtMntCredito;
    private javax.swing.JTextField txtPuntos;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent e) {
        String aComm=e.getActionCommand();
        int num=Integer.parseInt(aComm);
        SDocument sdoc=new SDocument(cstDocs.get(num),this,ctrl);
        pnlDocs.removeAll();
        pnlDocs.add(sdoc,BorderLayout.CENTER);
        pnlDocs.validate();
    }
}
