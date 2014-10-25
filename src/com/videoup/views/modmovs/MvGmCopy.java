/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.views.modmovs;

import com.videoup.entities.VideoupBaseEntity;
import com.videoup.entities.VideoupBcdlang;
import com.videoup.entities.VideoupBcodes;
import com.videoup.entities.VideoupFormts;
import com.videoup.entities.VideoupLangs;
import com.videoup.utils.GeneralDAO;
import com.videoup.views.modconf.SLang;
import com.videoup.views.utils.BarcodePan;
import com.videoup.views.utils.ChooseLangs;
import com.videoup.views.utils.Utils;
import com.videoup.views.utils.View;
import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Pedro
 */
public class MvGmCopy extends javax.swing.JPanel implements DocumentListener{

    private VideoupBcodes ent;
    private List<VideoupBcdlang> lsLangs;
    private BarcodePan bcdPan;
    private String bcdType;
    private View prnt;
    private boolean isMov;
    private Currency currency;
    
    /**
     * Creates new form MvGmCopy
     */
    public MvGmCopy(VideoupBcodes ent, List<VideoupFormts> frmts,String bcdType, View prnt, boolean isMov){
        this.ent=ent;
        this.bcdType=bcdType;
        this.prnt=prnt;
        this.isMov=isMov;
        bcdPan=new BarcodePan();
        currency = Currency.getInstance(Locale.getDefault());
        initComponents();
        jckVendible.setText("En venta "+currency.getSymbol()+":");
        jLabel2.setText("Costo compra "+currency.getSymbol()+":");
        pnlBCode.add(bcdPan,BorderLayout.CENTER);
        showData(frmts);
    }

    public void refreshLangs(){
        GeneralDAO genDao=new GeneralDAO();
        lsLangs=genDao.getListEntities("from VideoupBcdlang where idbc="+ent.getIdbc(), null, 0,false);
        List<Component> cmpLangs=new ArrayList<Component>();
        List<Component> cmpSubs=new ArrayList<Component>();
        for(VideoupBcdlang relLng: lsLangs){
            if(relLng.getAsLang()){
                cmpLangs.add(new SLang(relLng.getIdit(),false,this,true,true));
            }else{
                cmpSubs.add(new SLang(relLng.getIdit(),false,this,false,true));
            }
        }
        Utils.loadAsTableLayout(cmpLangs, pnlLangs, "No hay idiomas registrados");
        Utils.loadAsTableLayout(cmpSubs, pnlSubs, "No hay subtitulos registrados");
    }
    
    private void showData(List<VideoupFormts> frmts){
        jcbFormats.removeAllItems();
        for(VideoupFormts frmt: frmts){
            jcbFormats.addItem(frmt);
        }
        jcbFormats.setSelectedItem(ent.getFrmt());
        jcbStatus.setSelectedItem(ent.getStatus());
        txCosto.setText(""+ent.getPrVenta());
        txCosto.setEditable(false);
        jckVendible.setSelected(ent.getVendible());
        txtCstCompra.setText( ""+(ent.getPrCompra()!=null?ent.getPrCompra():"") );
        txBarcode.getDocument().addDocumentListener(this);
        txBarcode.setText(ent.getBarcode());
        refreshLangs(); canModify();
    }
    
   private void canModify(){
       String stts=ent.getStatus().toLowerCase();
       boolean modificable=true;
       if(stts.equals("vendida")||stts.equals("dañana")||stts.equals("sustraida")||stts.equals("perdida")){
           modificable=false;
       }
       setCanModify(modificable);
   }
   
   private void setCanModify(boolean cm){
       btnSave.setEnabled(cm); jcbFormats.setEnabled(cm);
       jcbStatus.setEnabled(cm); jckVendible.setEnabled(cm);
       txCosto.setEnabled(cm); txBarcode.setEditable(cm);
       addLang.setEnabled(cm); addSub.setEnabled(cm);
   }
    
    private void updateBarcodePan(){
        bcdPan.setCode(txBarcode.getText(),bcdType);
    }
    
    private void addLangSub(boolean asLang){
        ChooseLangs chLang=new ChooseLangs(prnt.getMainWindow(),true);
        chLang.setCallinForm(this,asLang,lsLangs);
        chLang.setVisible(true);
    }
    
    public void addLangs(List<VideoupLangs> lst, boolean asLang){
        List<VideoupBaseEntity> toadds=new ArrayList<VideoupBaseEntity>();
        if(!lst.isEmpty()){
            for(VideoupLangs lang: lst){
                toadds.add(new VideoupBcdlang(ent,lang,asLang));
            }
        }
        if(isMov){
            ((com.videoup.views.modmovs.Ficha)prnt).saveLangs(toadds);
        }else{
            ((com.videoup.views.modgames.Ficha)prnt).saveLangs(toadds);
        }
    }
    
    public void removeLang(VideoupLangs lng, boolean asLang){
        List<VideoupBaseEntity> list2del=new ArrayList<VideoupBaseEntity>();
        for(VideoupBcdlang lang: lsLangs){
            if(lang.getAsLang()==asLang && lang.getIdit().equals(lng)){
                list2del.add(lang);
                break;
            }
        }
        if(isMov){
            ((com.videoup.views.modmovs.Ficha)prnt).deleteChildEntities(list2del,1,false);
        }else{
            ((com.videoup.views.modgames.Ficha)prnt).deleteChildEntities(list2del,1,false);
        }
    }
    
    private void store(){
        ent.setBarcode(txBarcode.getText());
        ent.setVendible(jckVendible.isSelected());
        ent.setFrmt((VideoupFormts)jcbFormats.getSelectedItem());
        ent.setPrVenta(Float.parseFloat(txCosto.getText()));
        if(txtCstCompra.getText().trim().length()>0){
            ent.setPrCompra( Float.parseFloat(txtCstCompra.getText()) );
        }
        ent.setStatus(jcbStatus.getSelectedItem().toString());
        if(isMov){
            ((com.videoup.views.modmovs.Ficha)prnt).updateChildEntity(ent);
        }else{
            ((com.videoup.views.modgames.Ficha)prnt).updateChildEntity(ent);
        }
    }
    
    private boolean validateData(){
        boolean valid=true;
        String valBCode="from VideoupBcodes where barcode='"+txBarcode.getText()+"' and idbc<>"+ent.getIdbc();
        float compra;
        float costo;
        List<VideoupBcodes> existingBCds;
        int previousBcode=0;
        try{ compra=Float.parseFloat(txCosto.getText()); }
        catch(NumberFormatException nfe){ valid=false; compra=-1; }
        if(!valid || compra<0 ){ valid=false; prnt.showError("Costo de compra invalido"); }
        try{ costo=Float.parseFloat(txCosto.getText()); }
        catch(NumberFormatException nfe){ valid=false; costo=-1; }
        if(!valid || costo<0 || (jckVendible.isSelected()&&costo<=0)){
            valid=false;
            prnt.showError("Costo de venta invalido");
        }  
        if(txBarcode.getText().trim().length()>0){
            existingBCds=prnt.loadList(valBCode, null, 0, false);
            if(existingBCds==null){
                prnt.showError(prnt.getError());
                return false;
            }else if(!existingBCds.isEmpty()){
                for(VideoupBcodes bcd: existingBCds){
                    if( (isMov && bcd.getCountVideoupMovies()>0) || (!isMov && bcd.getCountVideoupGames()>0) ){
                        previousBcode=1; break;
                    }
                }
                if(previousBcode>0){
                    valid=false;
                    prnt.showError("Ya existe registro con el codigo "+txBarcode.getText());
                }
            }
        }
        return valid;
    }
    
    private void deleteMe(){
        List<VideoupBaseEntity> list2del=new ArrayList<VideoupBaseEntity>();
        for(VideoupBcdlang lang: lsLangs){
            list2del.add(lang);
        }
        list2del.add(ent);
        if(isMov){
            ((com.videoup.views.modmovs.Ficha)prnt).deleteChildEntities(list2del,2,true);
        }else{
            ((com.videoup.views.modgames.Ficha)prnt).deleteChildEntities(list2del,2,true);
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jcbFormats = new javax.swing.JComboBox();
        jckVendible = new javax.swing.JCheckBox();
        txCosto = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jcbStatus = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        txBarcode = new javax.swing.JTextField();
        btnSave = new javax.swing.JButton();
        btnDel = new javax.swing.JButton();
        pnlBCode = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        pnlLangs = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        pnlSubs = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        addLang = new javax.swing.JButton();
        addSub = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtCstCompra = new javax.swing.JTextField();

        setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel1.setText("Formato:");

        jcbFormats.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jckVendible.setText("En venta");
        jckVendible.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jckVendibleItemStateChanged(evt);
            }
        });

        txCosto.setColumns(7);

        jLabel3.setText("Estado:");

        jcbStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Disponible", "Apartada para alquiler", "Apartada para venta", "En alquiler", "Vendida", "Dañada", "Sustraida", "Perdida" }));

        jLabel4.setText("Codigo de barras:");

        txBarcode.setColumns(11);

        btnSave.setText("Guardar");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnDel.setText("Eliminar");
        btnDel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelActionPerformed(evt);
            }
        });

        pnlBCode.setBackground(new java.awt.Color(255, 255, 252));
        pnlBCode.setLayout(new java.awt.BorderLayout());

        jLabel5.setText("Idiomas");

        javax.swing.GroupLayout pnlLangsLayout = new javax.swing.GroupLayout(pnlLangs);
        pnlLangs.setLayout(pnlLangsLayout);
        pnlLangsLayout.setHorizontalGroup(
            pnlLangsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 558, Short.MAX_VALUE)
        );
        pnlLangsLayout.setVerticalGroup(
            pnlLangsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(pnlLangs);

        jLabel6.setText("Subtitulos");

        javax.swing.GroupLayout pnlSubsLayout = new javax.swing.GroupLayout(pnlSubs);
        pnlSubs.setLayout(pnlSubsLayout);
        pnlSubsLayout.setHorizontalGroup(
            pnlSubsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 558, Short.MAX_VALUE)
        );
        pnlSubsLayout.setVerticalGroup(
            pnlSubsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jScrollPane2.setViewportView(pnlSubs);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        addLang.setText("Agregar");
        addLang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addLangActionPerformed(evt);
            }
        });

        addSub.setText("Agregar");
        addSub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSubActionPerformed(evt);
            }
        });

        jLabel2.setText("Costo de compra:");

        txtCstCompra.setColumns(9);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(btnSave)
                            .addGap(18, 18, 18)
                            .addComponent(btnDel))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jcbFormats, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jckVendible)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txCosto, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel3)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jcbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel4)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(pnlBCode, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCstCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(addLang)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane1)
                                    .addComponent(jScrollPane2))))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addSub)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btnSave)
                                    .addComponent(btnDel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel1)
                                    .addComponent(jcbFormats, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel3)
                                    .addComponent(jcbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel2)
                                    .addComponent(txtCstCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jckVendible)
                                    .addComponent(txCosto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(txBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pnlBCode, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(addLang))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel6)
                                    .addComponent(addSub))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jckVendibleItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jckVendibleItemStateChanged
        txCosto.setEditable(jckVendible.isSelected());
    }//GEN-LAST:event_jckVendibleItemStateChanged

    private void addLangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addLangActionPerformed
        addLangSub(true);
    }//GEN-LAST:event_addLangActionPerformed

    private void addSubActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addSubActionPerformed
        addLangSub(false);
    }//GEN-LAST:event_addSubActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if(validateData()){
            store();
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelActionPerformed
        deleteMe();
    }//GEN-LAST:event_btnDelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addLang;
    private javax.swing.JButton addSub;
    private javax.swing.JButton btnDel;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JComboBox jcbFormats;
    private javax.swing.JComboBox jcbStatus;
    private javax.swing.JCheckBox jckVendible;
    private javax.swing.JPanel pnlBCode;
    private javax.swing.JPanel pnlLangs;
    private javax.swing.JPanel pnlSubs;
    private javax.swing.JTextField txBarcode;
    private javax.swing.JTextField txCosto;
    private javax.swing.JTextField txtCstCompra;
    // End of variables declaration//GEN-END:variables

    @Override
    public void insertUpdate(DocumentEvent e) {
        updateBarcodePan();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        updateBarcodePan();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        updateBarcodePan();
    }
}
