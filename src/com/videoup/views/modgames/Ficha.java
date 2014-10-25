/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.views.modgames;

import com.hexidec.ekit.EkitCore;
import com.videoup.controllers.Controller;
import com.videoup.entities.VideoupBaseEntity;
import com.videoup.entities.VideoupBcodes;
import com.videoup.entities.VideoupCatgs;
import com.videoup.entities.VideoupCtprrentas;
import com.videoup.entities.VideoupFormts;
import com.videoup.entities.VideoupGames;
import com.videoup.entities.VideoupGimgs;
import com.videoup.utils.GenProccess;
import com.videoup.utils.Globals;
import com.videoup.views.modmovs.MvGmCopy;
import com.videoup.views.utils.Ask4Image;
import com.videoup.views.utils.ImagePanel;
import com.videoup.views.utils.Utils;
import com.videoup.views.utils.ViewFicha;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.imageio.ImageIO;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Pedro
 */
public class Ficha extends ViewFicha{

    private VideoupGames ent;
    private VideoupGimgs portada;
    private EkitCore jtaCond;
    private ImagePanel imgPort;
    private int intDelCustom; // 1=relacion idioma, 2=copia de producto
    private List<MvGmCopy> copies;
    private VideoupCtprrentas ctEstreno;
    private VideoupCtprrentas ctNormal;
            
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
        ent=(VideoupGames)getEntity("from VideoupGames where idca="+id);
    }

    private void initComps(Object ent){
        Date today=Calendar.getInstance().getTime();
        putMainContain(mainContain);
        jtaCond=new EkitCore(false,true,true);
        jtaCond.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jtaCond.setJDialogIcon(new javax.swing.ImageIcon(getClass().getResource("/com/videoup/imgs/logo.jpg")).getImage());
        pnlSinop.add(jtaCond,BorderLayout.CENTER);
        loadCatgs();
        loadCatgsPrAlqs();
        if(ent==null){
            this.ent=new VideoupGames();
            portada=new VideoupGimgs();
            jxdLanz.setDate(today);
            jxdEstr.setDate(today);
        }else{
            loadEntity(ent);
        }
        setUpValidator();
        addJText2ListenChanges(txTitulo);
        addJText2ListenChanges(txClasif);
        addJText2ListenChanges(txTrailer);
        addJText2ListenChanges(txProd);
        addJText2ListenChanges(jxdLanz.getEditor());
        addJText2ListenChanges(jxdEstr.getEditor());
        addJText2ListenChanges(jtaCond.getSourcePane());
        changes=false;
    }
    
    @Override
    protected void setUpValidator(){
        int year=Calendar.getInstance().get(Calendar.YEAR);
        super.setUpValidator();
        vltor.setJTabbedPane(mainContain);
        vltor.addStringField(txTitulo, "Titulo", true, 2, 155,0);
        vltor.addDateField(jxdLanz,"Fecha de lanzamiento",true,0);
        vltor.addDateField(jxdEstr,"Fecha de estreno",true,0);
        vltor.addStringField(txClasif, "Clasificacion", true, 1, 5,0);
        vltor.addStringField(txTrailer, "Trailer", false,5,4999,0);
        vltor.addStringField(txProd, "Compañia productora", false, 5, 155,0);
        vltor.addStringField(jtaCond.getSourcePane(), "Sinopsis", false, 5, 4999,2);
        vltor.addIntField(txAnyo, "Año", false, 1925, year, 0);
    }
    
    private void loadCatgs(){
        List<VideoupCatgs> lista;
        lista=loadList("From VideoupCatgs",null,0,false);
        jcbCatgs.removeAllItems();
        if(lista==null){
            jcbCatgs.addItem("Error al cargar generos");
            return;
        }
        for(VideoupCatgs row: lista){
            jcbCatgs.addItem(row);
        }
    }

    private void loadCatgsPrAlqs(){
        List<VideoupCtprrentas> lista;
        lista=loadList("From VideoupCtprrentas",null,0,false);
        jcbCtgPres.removeAllItems();
        if(lista==null){
            jcbCtgPres.addItem("Error al cargar catalogos");
            return;
        }
        for(VideoupCtprrentas row: lista){
            if(row.getIdcpr()==5){
                ctEstreno=row;
            }else if(row.getIdcpr()==1){
                ctNormal=row;
            }
            jcbCtgPres.addItem(row);
        }
    }
    
    private void updatePortada(){
        InputStream inpstr;
        BufferedImage bfImg;
        if(portada.getImg()==null){
            return;
        }
        inpstr=new ByteArrayInputStream(portada.getImg());
        try {
            bfImg = ImageIO.read(inpstr);
        }catch (IOException ex) {
            lblImgError.setText("Error: "+ex.getMessage());
            pnlPortada.add(lblImgError,BorderLayout.CENTER);
            return;
        }
        if(imgPort!=null){
            pnlPortada.remove(imgPort);
        }
        imgPort=new ImagePanel(bfImg);
        pnlPortada.add(imgPort,BorderLayout.CENTER);
        validate();
    }
    
    private void loadHistory(){
        String[] nstts={"Solicitado","Cancelado","Finalizado","En curso","Finalizado pagada","Finalizado por pagar","Cambiado"};
        String[] vstts={"Apartado","Finalizado","Cancelado"};
        String qry="select cod_cst,applldos,name,vi.status,i_time,f_time,cst_apli from videoup_rentas vr,videoup_customers vc,";
        qry+="videoup_itemsrnt vi,videoup_bcodes vb where vc.idct=vr.idcli and vi.idbc=vb.idbc and vr.idrt=vi.idrt ";
        qry+="and ("+getWhereBCodes()+")";
        List<Object[]> alqs=loadListUseSQL(qry, null, 0, false);
        List<Object[]> vnts;
        DefaultTableModel tmodel;
        Object[] row;
        NumberFormat frmCurr=NumberFormat.getCurrencyInstance();
        float inga=0f;
        float ingv=0f;
        float costo;
        if(alqs!=null && !alqs.isEmpty()){
            tmodel=((DefaultTableModel)jxtHAlqs.getModel());
            for(Object[] alq: alqs){
                    row=new Object[4];
                    row[0]="("+alq[0]+") "+alq[1]+" "+alq[2];
                    row[1]=nstts[ Integer.parseInt(alq[3].toString()) ];
                    row[2]=asDate(alq[4],DateFormat.LONG,DateFormat.SHORT);
                    row[3]=asDate(alq[5],DateFormat.LONG,DateFormat.SHORT);
                    inga+=Float.parseFloat(alq[6].toString());
                    tmodel.addRow(row);
            }
            jxtHAlqs.setModel(tmodel);
        }
        qry="select cod_cst,applldos,name,vv.status,onfecha,pr_venta from videoup_ventas vv,videoup_customers vc,";
        qry+="videoup_itemsvnt vi,videoup_bcodes vb where vc.idct=vv.idcli and vi.idbc=vb.idbc and vv.idvn=vi.idvn ";
        qry+="and ("+getWhereBCodes()+")";
        vnts=loadListUseSQL(qry, null, 0, false);
        if(vnts!=null && !vnts.isEmpty()){
            tmodel=((DefaultTableModel)jxtHComps.getModel());
            for(Object[] vnt: vnts){
                    row=new Object[3];
                    row[0]="("+vnt[0]+") "+vnt[1]+" "+vnt[2];
                    row[1]=asDate(vnt[4],DateFormat.LONG,DateFormat.SHORT);
                    row[2]=vstts[ Integer.parseInt(vnt[3].toString()) ];
                    ingv+=Float.parseFloat(vnt[5].toString());
                    tmodel.addRow(row);
            }
            jxtHComps.setModel(tmodel);
        }
        costo=getCostoEjs();
        lblCosto.setText( frmCurr.format(costo) );
        lblIngAlq.setText( frmCurr.format(inga) );
        lblIngVnt.setText( frmCurr.format(ingv) );
        lblBenef.setText( frmCurr.format((inga+ingv)-costo) );
    }
    
    private float getCostoEjs(){
        float costo=0f;
        List<VideoupBcodes> bcodes=ent.getvideoupGBcodesList();
        if(bcodes!=null){
            for(VideoupBcodes code: bcodes){
                costo+=code.getPrCompra();
            }
        }
        return costo;
    }
    
    private String getWhereBCodes(){
        copies=new ArrayList<MvGmCopy>();
        List<VideoupBcodes> bcodes=ent.getvideoupGBcodesList();
        String wbcodes="";
        if(bcodes==null){
            return null;
        }
        for(VideoupBcodes code: bcodes){
            wbcodes+=(wbcodes.length()>0?" or ":"")+" barcode='"+code.getBarcode()+"'";
        }
        return wbcodes;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainContain = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jcbCtgPres = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        jcbCatgs = new javax.swing.JComboBox();
        txTitulo = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txProd = new javax.swing.JTextField();
        jScrollPane5 = new javax.swing.JScrollPane();
        txTrailer = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jxdLanz = new org.jdesktop.swingx.JXDatePicker();
        jLabel7 = new javax.swing.JLabel();
        txClasif = new javax.swing.JTextField();
        jxdEstr = new org.jdesktop.swingx.JXDatePicker();
        jLabel12 = new javax.swing.JLabel();
        btnClone = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txAnyo = new javax.swing.JTextField();
        btnCreateTag = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        pnlPortada = new javax.swing.JPanel();
        pnlNorhtPortada = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        btnChoosePorta = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        pnlSinop = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        btnAddEjmp = new javax.swing.JButton();
        pnlEjs = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane11 = new javax.swing.JScrollPane();
        jxtHAlqs = new org.jdesktop.swingx.JXTable();
        jPanel13 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane12 = new javax.swing.JScrollPane();
        jxtHComps = new org.jdesktop.swingx.JXTable();
        jPanel15 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        lblCosto = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        lblIngAlq = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        lblIngVnt = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        lblBenef = new javax.swing.JLabel();
        lblImgError = new javax.swing.JLabel();

        jLabel1.setText("Titulo");

        jLabel10.setText("Catalogo de alquiler:");

        jcbCtgPres.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jcbCtgPres.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcbCtgPresItemStateChanged(evt);
            }
        });

        jLabel8.setText("Genero:");

        jcbCatgs.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jcbCatgs.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcbCatgsItemStateChanged(evt);
            }
        });

        jLabel9.setText("Productora:");

        txTrailer.setColumns(20);
        txTrailer.setRows(5);
        jScrollPane5.setViewportView(txTrailer);

        jLabel3.setText("Trailer:");

        jLabel11.setText("Lanzamiento");

        jLabel7.setText("Clasificacion:");

        jLabel12.setText("Estreno hasta:");

        btnClone.setText("Duplicar");
        btnClone.setToolTipText("Duplicar registro");
        btnClone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloneActionPerformed(evt);
            }
        });

        jLabel2.setText("Año:");

        txAnyo.setColumns(6);

        btnCreateTag.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/videoup/imgs/rMovs.png"))); // NOI18N
        btnCreateTag.setText("Etiquetas");
        btnCreateTag.setToolTipText("Generar etiquetas");
        btnCreateTag.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateTagActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnClone))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txClasif, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txAnyo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel3)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel9)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txProd))
                                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jcbCtgPres, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jcbCatgs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jxdLanz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jxdEstr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 146, Short.MAX_VALUE)
                        .addComponent(btnCreateTag)))
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
                            .addComponent(txTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnClone))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(jcbCtgPres, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addComponent(jcbCatgs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(jxdLanz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12)
                            .addComponent(jxdEstr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btnCreateTag, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txClasif, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txAnyo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txProd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(144, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jPanel1);

        mainContain.addTab("Datos generales", jScrollPane1);

        pnlPortada.setLayout(new java.awt.BorderLayout());

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Imagen de portada");

        btnChoosePorta.setText("Elegir");
        btnChoosePorta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChoosePortaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlNorhtPortadaLayout = new javax.swing.GroupLayout(pnlNorhtPortada);
        pnlNorhtPortada.setLayout(pnlNorhtPortadaLayout);
        pnlNorhtPortadaLayout.setHorizontalGroup(
            pnlNorhtPortadaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNorhtPortadaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 621, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnChoosePorta)
                .addContainerGap())
        );
        pnlNorhtPortadaLayout.setVerticalGroup(
            pnlNorhtPortadaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNorhtPortadaLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlNorhtPortadaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(btnChoosePorta)))
        );

        pnlPortada.add(pnlNorhtPortada, java.awt.BorderLayout.NORTH);

        jScrollPane2.setViewportView(pnlPortada);

        mainContain.addTab("Portada", jScrollPane2);

        pnlSinop.setLayout(new java.awt.BorderLayout());
        jScrollPane3.setViewportView(pnlSinop);

        mainContain.addTab("Sinopsis", jScrollPane3);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jLabel13.setText("Ejemplares registrados");

        btnAddEjmp.setText("Agregar ejemplar");
        btnAddEjmp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddEjmpActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 394, Short.MAX_VALUE)
                .addComponent(btnAddEjmp)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(btnAddEjmp)))
        );

        jPanel3.add(jPanel4, java.awt.BorderLayout.NORTH);

        pnlEjs.setLayout(new javax.swing.BoxLayout(pnlEjs, javax.swing.BoxLayout.PAGE_AXIS));
        jPanel3.add(pnlEjs, java.awt.BorderLayout.CENTER);

        jScrollPane4.setViewportView(jPanel3);

        mainContain.addTab("Ejemplares", jScrollPane4);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jSplitPane1.setDividerLocation(199);
        jSplitPane1.setDividerSize(7);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setPreferredSize(new java.awt.Dimension(634, 461));

        jPanel10.setLayout(new java.awt.BorderLayout());

        jxtHAlqs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Socio", "Estado", "Alquiler", "Devolucion"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jxtHAlqs.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane11.setViewportView(jxtHAlqs);
        if (jxtHAlqs.getColumnModel().getColumnCount() > 0) {
            jxtHAlqs.getColumnModel().getColumn(0).setPreferredWidth(310);
            jxtHAlqs.getColumnModel().getColumn(1).setPreferredWidth(170);
            jxtHAlqs.getColumnModel().getColumn(2).setPreferredWidth(210);
            jxtHAlqs.getColumnModel().getColumn(3).setResizable(false);
            jxtHAlqs.getColumnModel().getColumn(3).setPreferredWidth(210);
        }

        jPanel10.add(jScrollPane11, java.awt.BorderLayout.CENTER);

        jPanel13.setLayout(new java.awt.BorderLayout());

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("Historial de alquileres");
        jPanel13.add(jLabel21, java.awt.BorderLayout.CENTER);

        jPanel14.setLayout(new java.awt.BorderLayout());
        jPanel13.add(jPanel14, java.awt.BorderLayout.EAST);

        jPanel10.add(jPanel13, java.awt.BorderLayout.NORTH);

        jSplitPane1.setLeftComponent(jPanel10);

        jPanel9.setLayout(new java.awt.BorderLayout());

        jxtHComps.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Cliente", "Fecha"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jxtHComps.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane12.setViewportView(jxtHComps);
        if (jxtHComps.getColumnModel().getColumnCount() > 0) {
            jxtHComps.getColumnModel().getColumn(0).setPreferredWidth(310);
            jxtHComps.getColumnModel().getColumn(1).setPreferredWidth(210);
        }

        jPanel9.add(jScrollPane12, java.awt.BorderLayout.CENTER);

        jPanel15.setLayout(new java.awt.BorderLayout());

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("Historial de ventas");
        jPanel15.add(jLabel22, java.awt.BorderLayout.CENTER);

        jPanel16.setLayout(new java.awt.BorderLayout());
        jPanel15.add(jPanel16, java.awt.BorderLayout.EAST);

        jPanel9.add(jPanel15, java.awt.BorderLayout.NORTH);

        jSplitPane1.setRightComponent(jPanel9);

        jPanel2.add(jSplitPane1, java.awt.BorderLayout.CENTER);

        jLabel15.setText("Costo:");

        lblCosto.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        lblCosto.setText("costo");

        jLabel17.setText("Ingreso por alquileres:");

        lblIngAlq.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        lblIngAlq.setText("ingreso_alq");

        jLabel19.setText("Ingreso por ventas:");

        lblIngVnt.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        lblIngVnt.setText("ing_v");

        jLabel23.setText("Beneficio:");

        lblBenef.setFont(new java.awt.Font("Ubuntu", 1, 17)); // NOI18N
        lblBenef.setText("benef");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCosto)
                .addGap(18, 18, 18)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblIngAlq)
                .addGap(18, 18, 18)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblIngVnt)
                .addGap(18, 18, 18)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblBenef)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(lblCosto)
                    .addComponent(jLabel17)
                    .addComponent(lblIngAlq)
                    .addComponent(jLabel19)
                    .addComponent(lblIngVnt)
                    .addComponent(jLabel23)
                    .addComponent(lblBenef))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel5, java.awt.BorderLayout.NORTH);

        mainContain.addTab("Historial", jPanel2);

        lblImgError.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblImgError.setForeground(new java.awt.Color(173, 0, 0));
        lblImgError.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImgError.setText("Error");

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    private void jcbCtgPresItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcbCtgPresItemStateChanged
        changes=true;
    }//GEN-LAST:event_jcbCtgPresItemStateChanged

    private void jcbCatgsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcbCatgsItemStateChanged
        changes=true;
    }//GEN-LAST:event_jcbCatgsItemStateChanged

    private void btnChoosePortaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChoosePortaActionPerformed
        Ask4Image dgAskPhoto=new Ask4Image(getMainWindow(),true,this,1);
        dgAskPhoto.setVisible(true);
    }//GEN-LAST:event_btnChoosePortaActionPerformed

    @Override
    public void setGettedImage(byte[] img, int key){
        if(img==null){
            return;
        }
        portada.setImg(img);
        changes=true;
        updatePortada();
    }
    
    private void btnAddEjmpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddEjmpActionPerformed
        PerformingTask task;
        VideoupBcodes newcd;
        if(ent.getId()==null || ent.getId()==0){
            ctrl.showDialogErr("No puede agregar ejemplares en un registro sin guardar");
            return;
        }
        newcd=new VideoupBcodes();
        newcd.setStatus("Sin datos");
        ent.addVideoupBcodes(newcd);
        newcd.addVideoupGames(ent);
        task=new PerformingTask();
        setBusy("Agregando ejemplar",true);
        task.setTask(2); task.setParam(newcd,false);
        validate(); task.execute();
    }//GEN-LAST:event_btnAddEjmpActionPerformed

    private void btnCloneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloneActionPerformed
        if(ent.getId()==null || ent.getId()==0){
            ctrl.showDialogErr("No puede duplicar un registro sin guardar");
            return;
        }
        cloneRecord();
    }//GEN-LAST:event_btnCloneActionPerformed

    private void btnCreateTagActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateTagActionPerformed
        String codes="";
        List<VideoupBcodes> bcodes;
        if(ent.getId()==null || ent.getId()==0){
            ctrl.showDialogErr("No puede generar etiquetas de un registro sin guardar");
            return;
        }
        bcodes=ent.getvideoupGBcodesList();
        for(VideoupBcodes code: bcodes){
            codes+=code.getBarcode()+" ";
        }
        if(codes.length()==0){
            ctrl.showDialogErr("No hay ejemplares registrados");
            return;
        }
        getMainWindow().generateCodesReport(2,codes);
    }//GEN-LAST:event_btnCreateTagActionPerformed

    protected void cloneRecord(){
        PerformingTask task=new PerformingTask();
        VideoupGames newEnt=ent.clone();
        VideoupGimgs newPortada=portada.clone();
        setBusy("Guardando registro",true);
        task.setTask(6); task.setParam(newEnt,false);
        task.setClon2(newPortada);
        validate(); task.execute(); 
    }
    
    @Override
    protected boolean makeAClone(VideoupBaseEntity nEnt,VideoupBaseEntity img){
        boolean saved;
        saved=super.saveEntity(nEnt,false);
        if(saved){
            ((VideoupGimgs)img).setIdrl(nEnt.getId());
            if( ((VideoupGimgs)img).getImg()!=null ){
                super.saveEntity(img,true);
            }
            ctrl.loadFicha(nEnt.getId());
        }
        return saved;
    }
    
    public boolean updateChildEntity(VideoupBaseEntity chlEnt){
        PerformingTask task;
        boolean saved;
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
    
    @Override
    protected boolean saveCustom(VideoupBaseEntity entity, boolean asNew){
        boolean saved=super.saveEntity(entity,asNew);
        //ent.addVideoupBcodes((VideoupBcodes)entity);
        //store();
        refreshBCodes();
        return saved;
    }        
    
    @Override
    protected boolean saveEntities(List<VideoupBaseEntity> entities){
        boolean saved=super.saveEntities(entities);
        refreshLangs();
        return saved;
    }
    
    public void saveLangs(List<VideoupBaseEntity> lngs){
        PerformingTask task;
        task=new PerformingTask();
        setBusy("Agregando idiomas",true);
        task.setTask(4); task.setListParam(lngs);
        validate(); task.execute();
    }
    
    private void refreshBCodes(){
        copies=new ArrayList<MvGmCopy>();
        List<VideoupBcodes> bcodes=ent.getvideoupGBcodesList();
        List<VideoupFormts> formts=this.loadList("from VideoupFormts",null,0,true);
        List<Component> bcds=new ArrayList<Component>();
        MvGmCopy copy;
        if(bcodes==null){
            return;
        }
        for(VideoupBcodes code: bcodes){
            copy=new MvGmCopy(code,formts,((String)Globals.getConfig("movtypebarcode")),this,false);
            bcds.add(copy);
            copies.add(copy);
        }
        Utils.loadAsTableLayout(bcds,pnlEjs,"No hay ejemplares para este videojuego");
    }
    
    private void refreshLangs(){
        for(MvGmCopy cpy: copies){
            cpy.refreshLangs();
        }
    }
    
    @Override
    public String getTitle(){
        return "Ficha de videojuego"+(getId()==0?" nuevo":(ent==null?" ID: "+getId():": "+ent.getTitulo()));
    }
    
    @Override
    protected boolean saveEntity(VideoupBaseEntity ignore,boolean forceAsNew){
        boolean saved;
        boolean portIsNew=false;
        String txYear=txAnyo.getText().trim();
        ent.setTitulo(txTitulo.getText());
        ent.setTrailerUrl(txTrailer.getText());
        ent.setSinopsis(jtaCond.getDocumentBody());
        ent.setLdate(jxdLanz.getDate());
        ent.setProcmpy(txProd.getText());
        ent.setClasif(txClasif.getText());
        ent.setCatg((VideoupCatgs)jcbCatgs.getSelectedItem());
        ent.setIdcpr((VideoupCtprrentas)jcbCtgPres.getSelectedItem());
        ent.setEstrenoUntil(jxdEstr.getDate());
        if(!txYear.equals("")){
            ent.setAnyo( Integer.parseInt(txYear) );
        }
        saved=super.saveEntity(ent,false);
        if(saved && portada.getImg()!=null){
            if(portada.getIdrl()==null || portada.getIdrl()==0){
                portada.setIdrl(ent.getId());
                portIsNew=true;
            }
            saved=super.saveEntity(portada,portIsNew);
        }
        changes=!saved;
        return saved;
    }
    
    @Override
    public void loadEntity(Object oEnt){
        ent=((VideoupGames)oEnt);
        if(ent.getIdcpr().getIdcpr()==5){
            if(!GenProccess.productApplyEstreno(ent)){
                GenProccess.switchCatgprNormal(ent);
            }
        }
        txTitulo.setText(ent.getTitulo());
        txTrailer.setText(ent.getTrailerUrl());
        jtaCond.setDocumentText(ent.getSinopsis());
        jxdLanz.setDate(ent.getLdate());
        txProd.setText(ent.getProcmpy());
        txClasif.setText(ent.getClasif());
        jcbCatgs.setSelectedItem(ent.getCatg());
        jcbCtgPres.setSelectedItem(ent.getIdcpr());
        jxdEstr.setDate(ent.getEstrenoUntil());
        txAnyo.setText(""+(ent.getAnyo()>0?ent.getAnyo():""));
        portada=(VideoupGimgs)getEntity("from VideoupGimgs where idrl="+ent.getId());
        if(portada==null && getError()==null){
            portada=new VideoupGimgs();
        }
        updatePortada();
        refreshBCodes();
        updateIdTitle();
        loadHistory();
        changes=false;
    }
    
    public void deleteChildEntities(List<VideoupBaseEntity> chlEnt,int type, boolean ask1st){
        PerformingTask task;
        if(ask1st){
            if(!ctrl.confirm("Va a eliminar un registro", "Esta accion NO se podra deshacer, ¿desea continuar?")){
                return;
            }
        }
        intDelCustom=type;
        task=new PerformingTask();
        setBusy("Eliminado registro",true);
        task.setTask(5); task.setListParam(chlEnt);
        validate(); task.execute();
    }
    
    @Override
    protected boolean deleteEntities(List<VideoupBaseEntity> entities){
        boolean deleted;
        if(intDelCustom==2){
            for(VideoupBaseEntity bcode: entities){
                if(bcode instanceof VideoupBcodes){
                    ent.removeVideoupBcodes((VideoupBcodes)bcode);
                    saveEntity(ent,false);
                }
            }
        }
        deleted=super.deleteEntities(entities);
        if(intDelCustom==1 && deleted){
            refreshLangs();
        }else if(intDelCustom==2 && deleted){
            refreshBCodes();
        }
        return deleted;
    }
    
    @Override
    protected void updateIdTitle(){
        setId(ent.getIdca());
        lblTitle.setText(getTitle());
        ctrl.changeViewName(this, getTitle());
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddEjmp;
    private javax.swing.JButton btnChoosePorta;
    private javax.swing.JButton btnClone;
    private javax.swing.JButton btnCreateTag;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JComboBox jcbCatgs;
    private javax.swing.JComboBox jcbCtgPres;
    private org.jdesktop.swingx.JXDatePicker jxdEstr;
    private org.jdesktop.swingx.JXDatePicker jxdLanz;
    private org.jdesktop.swingx.JXTable jxtHAlqs;
    private org.jdesktop.swingx.JXTable jxtHComps;
    private javax.swing.JLabel lblBenef;
    private javax.swing.JLabel lblCosto;
    private javax.swing.JLabel lblImgError;
    private javax.swing.JLabel lblIngAlq;
    private javax.swing.JLabel lblIngVnt;
    private javax.swing.JTabbedPane mainContain;
    private javax.swing.JPanel pnlEjs;
    private javax.swing.JPanel pnlNorhtPortada;
    private javax.swing.JPanel pnlPortada;
    private javax.swing.JPanel pnlSinop;
    private javax.swing.JTextField txAnyo;
    private javax.swing.JTextField txClasif;
    private javax.swing.JTextField txProd;
    private javax.swing.JTextField txTitulo;
    private javax.swing.JTextArea txTrailer;
    // End of variables declaration//GEN-END:variables
}
