/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.views.modalqrs;

import com.videoup.controllers.CAlquilers;
import com.videoup.controllers.Controller;
import com.videoup.entities.VideoupBaseEntity;
import com.videoup.entities.VideoupBcodes;
import com.videoup.entities.VideoupCustomers;
import com.videoup.entities.VideoupItemsrnt;
import com.videoup.entities.VideoupRentas;
import com.videoup.utils.GenProccess;
import com.videoup.views.utils.ProductBriefData;
import com.videoup.views.utils.ViewFicha;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Pedro
 */
public class CambioTit extends ViewFicha{
    
    private ProductBriefData pbdt;
    private ProductBriefData newp;

    /**
     * Creates new form CambioTit
     */
    public CambioTit(Controller ctrl, int id) {
        super(ctrl,id,false);
        pbdt=null;
        initComponents();
        initComps();
        showMakeChange();
    }
    
    private void initComps(){
        putMainContain(mainContain);
        removeObtnNew();
        changes=false;
    }

    public void setItem2change(String nbcd){
        txSCode2chng.setText(nbcd);
        loadFirstItem();
    }
    
    public void loadFirstItem(){
        VideoupItemsrnt itemr;
        String code=txSCode2chng.getText().trim();
        if(code.equals("")){
            return;
        }
        itemr=(VideoupItemsrnt)getEntity("from VideoupItemsrnt vit left join fetch vit.idbc where barcode='"+code+"' and vit.status=3");
        if(itemr==null){
            showError("No se encontro producto en renta con codigo: "+code);
        }else{
            txSCode2chng.setText("");
            pbdt=new ProductBriefData(itemr,null);
            showItem2change();
        }
    }
    
    public void loadSecondItem(){
        VideoupBcodes copy;
        String code=txNCode.getText().trim();
        if(code.equals("")){
            return;
        }
        copy=(VideoupBcodes)getEntity("from VideoupBcodes where barcode='"+code+"'");
        if(copy==null){
            showError("No se encontro producto con codigo "+code);
        }else{
            txNCode.setText("");
            if(copy.getStatus().startsWith("Apartada")){
                showInfo("No se puede completar la accion","El producto esta apartado");
            }else if(!copy.getStatus().equals("Disponible")){
                showInfo("No se puede completar la accion","El producto no esta disponible para alquiler");
            }else{
                if(copy.getVideoupMovie()!=null){
                    newp=new ProductBriefData(copy.getVideoupMovie(),copy,null,true);
                }else if(copy.getVideoupGame()!=null){
                    newp=new ProductBriefData(copy.getVideoupGame(),copy,null,true);
                }
                showNewItem4change();
            }
        }
    }
    
    private void showItem2change(){
        pnlFirstItem.removeAll();
        if(pbdt!=null){
            pbdt.hideRemovalBtn(); pbdt.showAlqActionButtons(false);
            pnlFirstItem.add(pbdt,BorderLayout.CENTER);
            showMakeChange();
        }
    }
    
    private void showNewItem4change(){
        pnlOptsItems.removeAll();
        if(newp!=null){
            newp.hideRemovalBtn(); newp.showAlqActionButtons(false);
            pnlOptsItems.add(newp,BorderLayout.CENTER);
            showMakeChange();
        }
    }
    
    private void showMakeChange(){
        if(pbdt==null || newp==null){
            btnChangeNow.setEnabled(false);
            pnlMakeChange.setVisible(false);
        }else{
            lblChangeMss.setText("<html>Cambiar <u>"+pbdt.getItemName()+"</u> por <u>"+newp.getItemName()+"</u></html>");
            lblTTchng.setText("<html>Transferir tiempo transcurrido de <u>"+pbdt.getItemName()+"</u> a <u>"+newp.getItemName()+"</u></html>");
            btnChangeNow.setEnabled(true); pnlMakeChange.setVisible(true);
        }
        subContain.validate();
    }
    
    @Override
    protected void store(){
        if(pbdt==null || newp==null){ return; }
        List<VideoupBaseEntity> entities=new ArrayList<VideoupBaseEntity>();
        PerformingTask task=new PerformingTask();
        VideoupItemsrnt itm1=pbdt.getAlqEntity();
        VideoupBcodes copy1=pbdt.getCopy();
        VideoupItemsrnt itm2=newp.getAlqEntity();
        VideoupBcodes copy2=newp.getCopy();
        VideoupRentas renta=itm1.getIdrt();
        VideoupCustomers soc=renta.getIdcli();
        soc.setNumChanges(soc.getNumChanges()+1);
        renta.addVideoupItemsrntList(itm2);
        itm1.setStatus(6);
        itm2.setIdrt(renta);
        itm2.setOnchange(itm1.getIdir());
        if(rbntTTYes.isSelected()){
            itm2.setITime(itm1.getITime());
        }
        copy1.setStatus("Disponible");
        copy2.setStatus("En alquiler");
        entities.add( ((ProductBriefData)pbdt).getProductEntity(3) );
        entities.add( ((ProductBriefData)newp).getProductEntity(1) );
        entities.add(renta); entities.add(soc);
        entities.add(itm1); entities.add(itm2);
        entities.add(copy1); entities.add(copy2);
        setBusy("Guardando registro",true);
        task.setTask(4); task.setListParam(entities);
        validate(); task.execute();
    }
    
    @Override
    protected boolean saveEntities(List<VideoupBaseEntity> entities){
        boolean saveok=super.saveEntities(entities);
        if(saveok){
            VideoupItemsrnt t1=null;
            VideoupItemsrnt t2=null;
            VideoupRentas entRenta=null;
            for(VideoupBaseEntity entt: entities){
                if(entt instanceof VideoupItemsrnt){
                    if(t1==null){ t1=(VideoupItemsrnt)entt; }
                    else if(t2==null){ t2=(VideoupItemsrnt)entt; }
                }else if(entt instanceof VideoupRentas){
                    entRenta=((VideoupRentas)entt);
                }
            }
            if(t1.getOnchange()==0){
                t1.setOnchange(t2.getIdir());
                saveEntity(t1,false);
            }else{
                t2.setOnchange(t1.getIdir());
                saveEntity(t2,false);
            }
            GenProccess.resetApplyOfrts(entRenta,getMainWindow());
            showInfo("Accion realizada", "El cambio de producto se ha realizado");
            pbdt=null; newp=null; showItem2change(); showNewItem4change(); showMakeChange();
            ((CAlquilers)ctrl).reloadFicha(entRenta.getIdrt(),entRenta,null);
        }
        return saveok;
    }
    
    @Override
    public String getTitle(){
        return "Cambio de producto en alquiler";
    }
    
    @Override
    protected void updateIdTitle(){
        lblTitle.setText(getTitle());
        ctrl.changeViewName(this, getTitle());
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainContain = new javax.swing.JScrollPane();
        subContain = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txSCode2chng = new javax.swing.JTextField();
        pnlFirstItem = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txNCode = new javax.swing.JTextField();
        pnlOptsItems = new javax.swing.JPanel();
        pnlMakeChange = new javax.swing.JPanel();
        lblChangeMss = new javax.swing.JLabel();
        btnChangeNow = new javax.swing.JButton();
        lblTTchng = new javax.swing.JLabel();
        rbntTTYes = new javax.swing.JRadioButton();
        rbtnTTNo = new javax.swing.JRadioButton();
        grbtTrnasTime = new javax.swing.ButtonGroup();

        jLabel2.setText("Ingrese el codigo del articulo que desea cambiar:");

        txSCode2chng.setColumns(12);
        txSCode2chng.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txSCode2chngKeyPressed(evt);
            }
        });

        pnlFirstItem.setLayout(new java.awt.BorderLayout());

        jLabel1.setText("Ingrese el codigo del articulo por el cual se hace el cambio:");

        txNCode.setColumns(12);
        txNCode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txNCodeKeyPressed(evt);
            }
        });

        pnlOptsItems.setLayout(new java.awt.BorderLayout());

        pnlMakeChange.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        lblChangeMss.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblChangeMss.setText("Cambiar X por Y");

        btnChangeNow.setText("Cambiar");
        btnChangeNow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangeNowActionPerformed(evt);
            }
        });

        lblTTchng.setText("Tiempo cobro de tiempo transcurrido de X a Y");

        grbtTrnasTime.add(rbntTTYes);
        rbntTTYes.setSelected(true);
        rbntTTYes.setText("Si");

        grbtTrnasTime.add(rbtnTTNo);
        rbtnTTNo.setText("No");

        javax.swing.GroupLayout pnlMakeChangeLayout = new javax.swing.GroupLayout(pnlMakeChange);
        pnlMakeChange.setLayout(pnlMakeChangeLayout);
        pnlMakeChangeLayout.setHorizontalGroup(
            pnlMakeChangeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMakeChangeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlMakeChangeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlMakeChangeLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lblTTchng)
                        .addGap(18, 18, 18)
                        .addComponent(rbntTTYes)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rbtnTTNo)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnlMakeChangeLayout.createSequentialGroup()
                        .addComponent(lblChangeMss, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnChangeNow)
                        .addContainerGap())))
        );
        pnlMakeChangeLayout.setVerticalGroup(
            pnlMakeChangeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMakeChangeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlMakeChangeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblChangeMss)
                    .addComponent(btnChangeNow))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlMakeChangeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTTchng)
                    .addComponent(rbntTTYes)
                    .addComponent(rbtnTTNo))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout subContainLayout = new javax.swing.GroupLayout(subContain);
        subContain.setLayout(subContainLayout);
        subContainLayout.setHorizontalGroup(
            subContainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subContainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(subContainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlFirstItem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlOptsItems, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlMakeChange, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(subContainLayout.createSequentialGroup()
                        .addGroup(subContainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(subContainLayout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txSCode2chng, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(subContainLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txNCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 20, Short.MAX_VALUE)))
                .addContainerGap())
        );
        subContainLayout.setVerticalGroup(
            subContainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subContainLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlMakeChange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(subContainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txSCode2chng, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlFirstItem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(subContainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txNCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlOptsItems, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(157, Short.MAX_VALUE))
        );

        mainContain.setViewportView(subContain);

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    private void txSCode2chngKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txSCode2chngKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            loadFirstItem();
        }
    }//GEN-LAST:event_txSCode2chngKeyPressed

    private void txNCodeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txNCodeKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            loadSecondItem();
        }
    }//GEN-LAST:event_txNCodeKeyPressed

    private void btnChangeNowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeNowActionPerformed
        store();
    }//GEN-LAST:event_btnChangeNowActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChangeNow;
    private javax.swing.ButtonGroup grbtTrnasTime;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel lblChangeMss;
    private javax.swing.JLabel lblTTchng;
    private javax.swing.JScrollPane mainContain;
    private javax.swing.JPanel pnlFirstItem;
    private javax.swing.JPanel pnlMakeChange;
    private javax.swing.JPanel pnlOptsItems;
    private javax.swing.JRadioButton rbntTTYes;
    private javax.swing.JRadioButton rbtnTTNo;
    private javax.swing.JPanel subContain;
    private javax.swing.JTextField txNCode;
    private javax.swing.JTextField txSCode2chng;
    // End of variables declaration//GEN-END:variables
}
