/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.views.modconf;

import com.videoup.controllers.CConf;
import com.videoup.controllers.Controller;
import com.videoup.entities.VideoupBaseEntity;
import com.videoup.entities.VideoupConf;
import com.videoup.entities.VideoupTagitems;
import com.videoup.entities.VideoupTags;
import com.videoup.views.utils.BarcodePan;
import com.videoup.views.utils.TagEditor;
import com.videoup.views.utils.ViewFicha;
import java.awt.BorderLayout;
import java.util.ArrayList;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Pedro
 */
public class BarCode extends ViewFicha implements DocumentListener{

    private BarcodePan bcMov;
    private BarcodePan bcGam;
    private BarcodePan bcSoc;
    private TagEditor teMovs;
    private TagEditor teGames;
    private TagEditor teSocs;
    private int delFromTag;
    
    /**
     * Creates new form BarCode
     */
    public BarCode(Controller ctrl, int id) {
        super(ctrl,id, false);
        initComponents();
        initComps();
    }
    
    private void initComps(){
        VideoupConf ent=((CConf)ctrl).getConfEnt();
        VideoupTags tMovs=getEntiTag(1);
        VideoupTags tGams=getEntiTag(2);
        VideoupTags tMembs=getEntiTag(3);
        putMainContain(mainContain);
        bcMov=new BarcodePan();
        bcGam=new BarcodePan();
        bcSoc=new BarcodePan();
        pnlShowCodeMov.add(bcMov,BorderLayout.CENTER);
        pnlShowCodeGame.add(bcGam,BorderLayout.CENTER);
        pnlShowCodeSoc.add(bcSoc,BorderLayout.CENTER);
        txMCodeMov.getDocument().addDocumentListener(this);
        txMCodeGame.getDocument().addDocumentListener(this);
        txMCodeSoc.getDocument().addDocumentListener(this);
        if(tMovs!=null){
            teMovs=new TagEditor(this,ctrl,tMovs,false);
            pnlTagMovs.add(teMovs,BorderLayout.CENTER);
        }else{ pnlTagMovs.add(new javax.swing.JLabel("Error al cargar"),BorderLayout.CENTER); }
        if(tGams!=null){
            teGames=new TagEditor(this,ctrl,tGams,false);
            pnlTagGames.add(teGames,BorderLayout.CENTER);
        }else{ pnlTagGames.add(new javax.swing.JLabel("Error al cargar"),BorderLayout.CENTER); }
        if(tMembs!=null){
            teSocs=new TagEditor(this,ctrl,tMembs,true);
            pnlTagSocios.add(teSocs,BorderLayout.CENTER);
        }else{ pnlTagSocios.add(new javax.swing.JLabel("Error al cargar"),BorderLayout.CENTER); }
        jcbTCodeMov.setSelectedItem(ent.getMovtypebarcode());
        jcbTCodeGame.setSelectedItem(ent.getGamtypebarcode());
        jcbTCodeSoc.setSelectedItem(ent.getSocvtypebarcode());
        removeObtnNew();
        changes=false;
    }
    
    private VideoupTags getEntiTag(int idtg){
        VideoupTags enttg;
        enttg=(VideoupTags)getEntity("from VideoupTags where idtg="+idtg);
        return enttg;
    }
    
    private void updatePreviews(){
        bcMov.setCode(txMCodeMov.getText(),jcbTCodeMov.getSelectedItem().toString());
        bcGam.setCode(txMCodeGame.getText(),jcbTCodeGame.getSelectedItem().toString());
        bcSoc.setCode(txMCodeSoc.getText(),jcbTCodeSoc.getSelectedItem().toString());
    }
    
    @Override
    public void insertUpdate(DocumentEvent e) {
        updatePreviews();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        updatePreviews();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        updatePreviews();
    }
    
    @Override
    public String getTitle(){
        return "Configuracion de uso de codigos de barras";
    }
    
    @Override
    protected boolean isDataValid(){
        boolean val;
        val=teMovs.isValidTagItems();
        if(!val){ error=teMovs.getError(); return false; }
        val=teGames.isValidTagItems();
        if(!val){ error=teGames.getError(); return false; }
        val=teSocs.isValidTagItems();
        if(!val){ error=teSocs.getError(); }
        return val;
    }
        
    @Override
    protected boolean saveEntity(VideoupBaseEntity ignore,boolean forceAsNew){
        boolean saved;
        ArrayList<VideoupBaseEntity> ents;
        VideoupConf confEnt=((CConf)ctrl).getConfEnt();
        confEnt.setMovtypebarcode(jcbTCodeMov.getSelectedItem().toString());
        confEnt.setGamtypebarcode(jcbTCodeGame.getSelectedItem().toString());
        confEnt.setSocvtypebarcode(jcbTCodeSoc.getSelectedItem().toString());
        ents=teMovs.getEntities();
        ents.addAll(teGames.getEntities());
        ents.addAll(teSocs.getEntities());
        ents.add(confEnt);
        saved=super.saveEntities(ents);
        changes=!saved;
        return saved;
    }
    
    /**
     * 
     * @param vti
     * @param tag 1=movs, 2=games, 3=members
     */
    public void deleteTagItem(VideoupTagitems vti, int wtag){
        PerformingTask task=new PerformingTask();
        delFromTag=wtag;
        setBusy("Actualizando registro",true);
        task.setTask(3); task.setParam(vti,false);
        validate(); task.execute();
    }
    
    @Override
    protected boolean deleteEntity(VideoupBaseEntity entity){
        boolean deleted=super.deleteEntity(entity);
        if(deleted){
            if(delFromTag==1){
                teMovs.removeItemTag();
            }else if(delFromTag==2){
                teGames.removeItemTag();
            }else if(delFromTag==3){
                teSocs.removeItemTag();
            }
        }
        return deleted;
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
        jcbTCodeMov = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        txMCodeMov = new javax.swing.JTextField();
        pnlShowCodeMov = new javax.swing.JPanel();
        pnlShowCodeGame = new javax.swing.JPanel();
        txMCodeGame = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jcbTCodeGame = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jcbTCodeSoc = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        txMCodeSoc = new javax.swing.JTextField();
        pnlShowCodeSoc = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        pnlTagMovs = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        pnlTagGames = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        pnlTagSocios = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Elija el estandar de codigo de barras a utilizar en producto peliculas:");

        jLabel2.setText("Estandar:");

        jcbTCodeMov.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Code 128", "Code 39", "Codabar", "EAN-13", "EAN-8", "intl2of5", "EAN-128", "Royal-mail-cbc", "ITF-14", "DataMatrix", "PDF 417", "UPC-A", "UPC-E", "Usps4cb" }));
        jcbTCodeMov.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcbTCodeMovItemStateChanged(evt);
            }
        });

        jLabel3.setText("Valor de muestra:");

        txMCodeMov.setColumns(12);

        pnlShowCodeMov.setBackground(new java.awt.Color(255, 255, 255));
        pnlShowCodeMov.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        pnlShowCodeMov.setMinimumSize(new java.awt.Dimension(190, 77));
        pnlShowCodeMov.setLayout(new java.awt.BorderLayout());

        pnlShowCodeGame.setBackground(new java.awt.Color(255, 255, 255));
        pnlShowCodeGame.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        pnlShowCodeGame.setMinimumSize(new java.awt.Dimension(190, 77));
        pnlShowCodeGame.setLayout(new java.awt.BorderLayout());

        txMCodeGame.setColumns(12);

        jLabel4.setText("Valor de muestra:");

        jcbTCodeGame.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Code 128", "Code 39", "Codabar", "EAN-13", "EAN-8", "intl2of5", "EAN-128", "Royal-mail-cbc", "ITF-14", "DataMatrix", "PDF 417", "UPC-A", "UPC-E", "Usps4cb" }));
        jcbTCodeGame.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcbTCodeGameItemStateChanged(evt);
            }
        });

        jLabel5.setText("Estandar:");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel6.setText("Elija el estandar de codigo de barras a utilizar en producto videojuegos:");

        jcbTCodeSoc.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Code 128", "Code 39", "Codabar", "EAN-13", "EAN-8", "intl2of5", "EAN-128", "Royal-mail-cbc", "ITF-14", "DataMatrix", "PDF 417", "UPC-A", "UPC-E", "Usps4cb" }));
        jcbTCodeSoc.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcbTCodeSocItemStateChanged(evt);
            }
        });

        jLabel7.setText("Valor de muestra:");

        txMCodeSoc.setColumns(12);

        pnlShowCodeSoc.setBackground(new java.awt.Color(255, 255, 255));
        pnlShowCodeSoc.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        pnlShowCodeSoc.setMinimumSize(new java.awt.Dimension(190, 77));
        pnlShowCodeSoc.setLayout(new java.awt.BorderLayout());

        jLabel8.setText("Estandar:");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel9.setText("Elija el estandar de codigo de barras a utilizar en credenciales de socios:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel6)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel4)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(10, 10, 10)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jcbTCodeGame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(txMCodeGame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addComponent(jLabel5))
                                        .addGap(18, 18, 18)
                                        .addComponent(pnlShowCodeGame, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel2)
                                            .addComponent(jLabel3)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(10, 10, 10)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jcbTCodeMov, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(txMCodeMov, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                        .addGap(18, 18, 18)
                                        .addComponent(pnlShowCodeMov, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel8)
                                            .addComponent(jLabel7)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(10, 10, 10)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jcbTCodeSoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(txMCodeSoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                        .addGap(18, 18, 18)
                                        .addComponent(pnlShowCodeSoc, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addContainerGap(113, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcbTCodeMov, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txMCodeMov, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlShowCodeMov, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcbTCodeGame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txMCodeGame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlShowCodeGame, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcbTCodeSoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txMCodeSoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlShowCodeSoc, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(49, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jPanel1);

        mainContain.addTab("Estandares", jScrollPane1);

        pnlTagMovs.setLayout(new java.awt.BorderLayout());

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel10.setText("Diseño de etiquetas para producto: Peliculas");
        pnlTagMovs.add(jLabel10, java.awt.BorderLayout.PAGE_START);

        jScrollPane2.setViewportView(pnlTagMovs);

        mainContain.addTab("Etiquetas peliculas", jScrollPane2);

        pnlTagGames.setLayout(new java.awt.BorderLayout());

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel11.setText("Diseño de etiquetas para producto: VideoJuegos");
        pnlTagGames.add(jLabel11, java.awt.BorderLayout.PAGE_START);

        jScrollPane3.setViewportView(pnlTagGames);

        mainContain.addTab("Etiquetas videojuegos", jScrollPane3);

        pnlTagSocios.setLayout(new java.awt.BorderLayout());

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel12.setText("Diseño de credenciales para Socios");
        pnlTagSocios.add(jLabel12, java.awt.BorderLayout.PAGE_START);

        jScrollPane4.setViewportView(pnlTagSocios);

        mainContain.addTab("Etiquetas socios", jScrollPane4);

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    private void jcbTCodeMovItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcbTCodeMovItemStateChanged
        updatePreviews();
    }//GEN-LAST:event_jcbTCodeMovItemStateChanged

    private void jcbTCodeGameItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcbTCodeGameItemStateChanged
        updatePreviews();
    }//GEN-LAST:event_jcbTCodeGameItemStateChanged

    private void jcbTCodeSocItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcbTCodeSocItemStateChanged
        updatePreviews();
    }//GEN-LAST:event_jcbTCodeSocItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JComboBox jcbTCodeGame;
    private javax.swing.JComboBox jcbTCodeMov;
    private javax.swing.JComboBox jcbTCodeSoc;
    private javax.swing.JTabbedPane mainContain;
    private javax.swing.JPanel pnlShowCodeGame;
    private javax.swing.JPanel pnlShowCodeMov;
    private javax.swing.JPanel pnlShowCodeSoc;
    private javax.swing.JPanel pnlTagGames;
    private javax.swing.JPanel pnlTagMovs;
    private javax.swing.JPanel pnlTagSocios;
    private javax.swing.JTextField txMCodeGame;
    private javax.swing.JTextField txMCodeMov;
    private javax.swing.JTextField txMCodeSoc;
    // End of variables declaration//GEN-END:variables
}
