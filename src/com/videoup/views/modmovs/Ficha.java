/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.views.modmovs;

import com.hexidec.ekit.EkitCore;
import com.videoup.controllers.Controller;
import com.videoup.entities.VideoupBaseEntity;
import com.videoup.entities.VideoupBcodes;
import com.videoup.entities.VideoupCatgs;
import com.videoup.entities.VideoupCtprrentas;
import com.videoup.entities.VideoupFormts;
import com.videoup.entities.VideoupMimgs;
import com.videoup.entities.VideoupMovies;
import com.videoup.utils.GenProccess;
import com.videoup.utils.Globals;
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

    private VideoupMovies ent;
    private VideoupMimgs portada;
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
        ent=(VideoupMovies)getEntity("from VideoupMovies where idcm="+id);
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
            this.ent=new VideoupMovies();
            portada=new VideoupMimgs();
            jxdLanz.setDate(today);
            jxdEstr.setDate(today);
        }else{
            loadEntity(ent);
        }
        setUpValidator();
        addJText2ListenChanges(txTitulo);
        addJText2ListenChanges(txDirector);
        addJText2ListenChanges(txProgs);
        addJText2ListenChanges(txDur);
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
        vltor.addStringField(txDirector, "Director", false, 3, 155,0);
        vltor.addStringField(txProgs, "Protagonistas", false,5,255,0);
        vltor.addDateField(jxdLanz,"Fecha de lanzamiento",true,0);
        vltor.addDateField(jxdEstr,"Fecha de estreno",true,0);
        vltor.addIntField(txDur, "Duracion en minutos", false, 1, 9999,0);
        vltor.addStringField(txClasif, "Clasificacion", false, 1, 5,0);
        vltor.addStringField(txTrailer, "Trailer", false,5,4999,0);
        vltor.addStringField(txProd, "Compañia productora", false, 3, 155,0);
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
    
    private String getWhereBCodes(){
        copies=new ArrayList<MvGmCopy>();
        List<VideoupBcodes> bcodes=ent.getvideoupMBcodesList();
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
        jLabel2 = new javax.swing.JLabel();
        txTitulo = new javax.swing.JTextField();
        txDirector = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        txTrailer = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        txProgs = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txDur = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txClasif = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jcbCatgs = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        txProd = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jcbCtgPres = new javax.swing.JComboBox();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jxdLanz = new org.jdesktop.swingx.JXDatePicker();
        jxdEstr = new org.jdesktop.swingx.JXDatePicker();
        btnClone = new javax.swing.JButton();
        txAnyo = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        btnCreateTag = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        pnlPortada = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        btnChoosePorta = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        pnlSinop = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        btnAddEjmp = new javax.swing.JButton();
        pnlEjs = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
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
        buttonGroup1 = new javax.swing.ButtonGroup();

        jLabel1.setText("Titulo:");

        jLabel2.setText("Director:");

        jLabel3.setText("Trailer:");

        txTrailer.setColumns(20);
        txTrailer.setRows(5);
        jScrollPane5.setViewportView(txTrailer);

        jLabel5.setText("Protagonistas:");

        jLabel6.setText("Duracion minutos:");

        jLabel7.setText("Clasificacion:");

        jLabel8.setText("Genero:");

        jcbCatgs.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jcbCatgs.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcbCatgsItemStateChanged(evt);
            }
        });

        jLabel9.setText("Productora:");

        jLabel10.setText("Catalogo de alquiler:");

        jcbCtgPres.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jcbCtgPres.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcbCtgPresItemStateChanged(evt);
            }
        });

        jLabel11.setText("Lanzamiento");

        jLabel12.setText("Estreno hasta:");

        btnClone.setText("Duplicar");
        btnClone.setToolTipText("Duplicar registro");
        btnClone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloneActionPerformed(evt);
            }
        });

        txAnyo.setColumns(6);

        jLabel14.setText("Año:");

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
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txDirector, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txProgs))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txTitulo)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 176, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnClone, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnCreateTag, javax.swing.GroupLayout.Alignment.TRAILING)))
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
                                .addComponent(jxdEstr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txDur, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txClasif, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel3)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel14)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txAnyo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel9)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txProd))
                                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnClone))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txDirector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCreateTag))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txProgs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addComponent(jxdEstr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txDur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(txClasif, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txAnyo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txProd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(134, Short.MAX_VALUE))
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

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 573, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnChoosePorta)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(btnChoosePorta)))
        );

        pnlPortada.add(jPanel6, java.awt.BorderLayout.NORTH);

        jScrollPane6.setViewportView(pnlPortada);

        mainContain.addTab("Portada", jScrollPane6);

        pnlSinop.setLayout(new java.awt.BorderLayout());
        jScrollPane2.setViewportView(pnlSinop);

        mainContain.addTab("Sinopsis", jScrollPane2);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jLabel13.setText("Ejemplares registrados");

        btnAddEjmp.setText("Agregar ejemplar");
        btnAddEjmp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddEjmpActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 412, Short.MAX_VALUE)
                .addComponent(btnAddEjmp)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(btnAddEjmp)))
        );

        jPanel2.add(jPanel3, java.awt.BorderLayout.NORTH);

        pnlEjs.setLayout(new javax.swing.BoxLayout(pnlEjs, javax.swing.BoxLayout.PAGE_AXIS));
        jPanel2.add(pnlEjs, java.awt.BorderLayout.CENTER);

        jScrollPane3.setViewportView(jPanel2);

        mainContain.addTab("Ejemplares", jScrollPane3);

        jPanel4.setLayout(new java.awt.BorderLayout());

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

        jPanel4.add(jSplitPane1, java.awt.BorderLayout.CENTER);

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
                .addContainerGap(30, Short.MAX_VALUE))
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

        jPanel4.add(jPanel5, java.awt.BorderLayout.NORTH);

        mainContain.addTab("Historial", jPanel4);

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
        newcd.addVideoupMovies(ent);
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
        bcodes=ent.getvideoupMBcodesList();
        for(VideoupBcodes code: bcodes){
            codes+=code.getBarcode()+" ";
        }
        if(codes.length()==0){
            ctrl.showDialogErr("No hay ejemplares registrados");
            return;
        }
        getMainWindow().generateCodesReport(1,codes);
    }//GEN-LAST:event_btnCreateTagActionPerformed
    
    protected void cloneRecord(){
        PerformingTask task=new PerformingTask();
        VideoupMovies newEnt=ent.clone();
        VideoupMimgs newPortada=portada.clone();
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
            ((VideoupMimgs)img).setIdrl(nEnt.getId());
            if( ((VideoupMimgs)img).getImg()!=null ){
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
    protected boolean saveCustom(VideoupBaseEntity entity,boolean asNew){
        boolean saved=super.saveEntity(entity,asNew);
        ent.addVideoupBcodes((VideoupBcodes)entity);
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
        List<VideoupBcodes> bcodes=ent.getvideoupMBcodesList();
        List<VideoupFormts> formts=this.loadList("from VideoupFormts",null,0,false);
        List<Component> bcds=new ArrayList<Component>();
        MvGmCopy copy;
        if(bcodes==null){
            return;
        }
        for(VideoupBcodes code: bcodes){
            copy=new MvGmCopy(code,formts,((String)Globals.getConfig("movtypebarcode")),this,true);
            bcds.add(copy);
            copies.add(copy);
        }
        Utils.loadAsTableLayout(bcds,pnlEjs,"No hay ejemplares para esta pelicula");
    }
    
    private float getCostoEjs(){
        float costo=0f;
        List<VideoupBcodes> bcodes=ent.getvideoupMBcodesList();
        if(bcodes!=null){
            for(VideoupBcodes code: bcodes){
                costo+=code.getPrCompra();
            }
        }
        return costo;
    }
    
    @Override
    public String getTitle(){
        return "Ficha de pelicula"+(getId()==0?" nueva":(ent==null?" ID: "+getId():": "+ent.getTitulo()));
    }
    
    @Override
    protected boolean saveEntity(VideoupBaseEntity ignore,boolean forceAsNew){
        boolean saved;
        boolean portIsNew=false;
        String txYear=txAnyo.getText().trim();
        ent.setTitulo(txTitulo.getText());
        ent.setTrailerUrl(txTrailer.getText());
        ent.setPrgistas(txProgs.getText());
        ent.setDirector(txDirector.getText());
        ent.setSinopsis(jtaCond.getDocumentBody());
        ent.setLdate(jxdLanz.getDate());
        ent.setProcmpy(txProd.getText());
        if(txDur.getText().trim().length()>0){
            ent.setDmin(Integer.parseInt(txDur.getText()));
        }else{
            ent.setDmin(0);
        }
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
        ent=((VideoupMovies)oEnt);
        if(ent.getIdcpr().getIdcpr()==5){
            if(!GenProccess.productApplyEstreno(ent)){
                GenProccess.switchCatgprNormal(ent);
            }
        }
        txTitulo.setText(ent.getTitulo());
        txTrailer.setText(ent.getTrailerUrl());
        txProgs.setText(ent.getPrgistas());
        txDirector.setText(ent.getDirector());
        jtaCond.setDocumentText(ent.getSinopsis());
        jxdLanz.setDate(ent.getLdate());
        txProd.setText(ent.getProcmpy());
        txDur.setText(""+(ent.getDmin()>0?ent.getDmin():""));
        txAnyo.setText(""+(ent.getAnyo()>0?ent.getAnyo():""));
        txClasif.setText(ent.getClasif());
        jcbCatgs.setSelectedItem(ent.getCatg());
        jcbCtgPres.setSelectedItem(ent.getIdcpr());
        jxdEstr.setDate(ent.getEstrenoUntil());
        portada=(VideoupMimgs)getEntity("from VideoupMimgs where idrl="+ent.getId());
        if(portada==null && getError()==null){
            portada=new VideoupMimgs();
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
    
    private void refreshLangs(){
        for(MvGmCopy cpy: copies){
            cpy.refreshLangs();
        }
    }
    
    @Override
    protected void updateIdTitle(){
        setId(ent.getIdcm());
        lblTitle.setText(getTitle());
        ctrl.changeViewName(this, getTitle());
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddEjmp;
    private javax.swing.JButton btnChoosePorta;
    private javax.swing.JButton btnClone;
    private javax.swing.JButton btnCreateTag;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
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
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
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
    private javax.swing.JPanel pnlPortada;
    private javax.swing.JPanel pnlSinop;
    private javax.swing.JTextField txAnyo;
    private javax.swing.JTextField txClasif;
    private javax.swing.JTextField txDirector;
    private javax.swing.JTextField txDur;
    private javax.swing.JTextField txProd;
    private javax.swing.JTextField txProgs;
    private javax.swing.JTextField txTitulo;
    private javax.swing.JTextArea txTrailer;
    // End of variables declaration//GEN-END:variables
}
