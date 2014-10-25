/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.views.modalqrs;

import com.videoup.controllers.CAlquilers;
import com.videoup.controllers.Controller;
import com.videoup.entities.VideoupBaseEntity;
import com.videoup.entities.VideoupItemsrnt;
import com.videoup.entities.VideoupSoldBonos;
import com.videoup.views.utils.Utils;
import com.videoup.views.utils.ViewFicha;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author Pedro
 */
public class Devolucion extends ViewFicha{

    private List<Component> resumes;
    private DevResume drs2end;
    
    /**
     * Creates new form Devolucion
     */
    public Devolucion(Controller ctrl, int id) {
        super(ctrl,id,false);
        resumes=new ArrayList<Component>();
        drs2end=null;
        initComponents();
        initComps();
    }
    
    public CAlquilers getAlqsCtrl(){
        return ((CAlquilers)ctrl);
    }
    
    private void initComps(){
        putMainContain(mainContain);
        removeObtnNew();
        removeObtnSave();
        changes=false;
    }

    public void addItems2Resume(ArrayList<String> bcs, int h){
        for(String bcd: bcs){
            txtDCode.setText(bcd);
            addItem2resume(null,h);
        }
    }
    
    public void addItem2Resume(String bcd){
        txtDCode.setText(bcd);
        addItem2resume(null,0);
    }
    
    public void addAllItemsRent(int idrent){
        DevResume tmpDr;
        for(Component comp: resumes){
            tmpDr=(DevResume)comp;
            if(tmpDr.getIdRent()==idrent){
                tmpDr.addAllItems();
                refreshResumes();
                return;
            }
        }
    }
    
    public void addItem2resume(VideoupItemsrnt itm, int h){
        String code=txtDCode.getText().trim();
        if(code.equals("") && itm==null){
            return;
        }
        if(itm==null){
            itm=(VideoupItemsrnt)getEntity("from VideoupItemsrnt vit left join fetch vit.idbc where barcode='"+code+"' and (vit.status=3 or vit.status=7)");
        }
        if(itm==null){
            showError("No se encontro producto en renta con codigo: "+code);
        }else{
            txtDCode.setText("");
            addToResumes(itm,h);
        }
    }
    
    public void removeDevResume(DevResume dvrs){
        for(Component comp: resumes){
            if( ((DevResume)comp).getIdRent()==dvrs.getIdRent() ){
                resumes.remove(dvrs); break;
            }
        }
        refreshResumes();
    }
    
    private void addToResumes(VideoupItemsrnt itm, int h){
        boolean added=false;
        DevResume newDr;
        DevResume tmpDr;
        for(Component comp: resumes){
            tmpDr=(DevResume)comp;
            if(tmpDr.getIdRent()==itm.getIdrt().getIdrt()){
                if(!tmpDr.contains(itm)){
                    tmpDr.addItem(itm,h);
                }
                added=true;
                break;
            }
        }
        if(!added){
            newDr=new DevResume(this);
            newDr.addItem(itm,h);
            resumes.add(newDr);
        }
        refreshResumes();
    }
    
    private void refreshResumes(){
        pnlResums.setVisible(false);
        Utils.loadAsTableLayout(resumes, pnlResums, "No hay articulos agregados");
        pnlResums.setVisible(true);
    }
    
    public void finalizeDevRs(DevResume drs){
        List<VideoupBaseEntity> entities;
        PerformingTask task;
        if(drs.hasErrors()){
            showError("Error en los cargos, corrija los errores para poder realizar la accion");
            return;
        }else if(drs.hasBeenEdited()){
            if(!ctrl.confirm("Atencion","Desea registrar los cargos editados y diferentes de lo calculado")){
                return;
            }
        }
        task=new PerformingTask();
        entities=drs.getItems2Save();
        setBusy("Guardando registro",true);
        task.setTask(4); task.setListParam(entities);
        drs2end=drs;
        validate(); task.execute();
    }
    
    @Override
    protected boolean saveEntities(List<VideoupBaseEntity> entities){
        boolean saveok=super.saveEntities(entities);
        if(saveok){
            drs2end.setFinalize();
            showInfo("Accion realizada", "Se ha registrado el cierre del alquiler de los articulos");
            drs2end=null;
        }
        return saveok;
    }
    
    public List<VideoupSoldBonos> getCliBonos(int idcli){
        Calendar cal=Calendar.getInstance();
        String dtf=""+cal.get(Calendar.YEAR)+"-"+setCoupleDigits(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DATE);
        String qry="from VideoupSoldBonos where bonos>used and '"+dtf+"'<=hasta and idcli="+idcli;
        List<VideoupSoldBonos> bonos=loadList(qry, null, 0, false);
        return bonos;
    }
        
    private String setCoupleDigits(int vl){
        return (vl<10?"0":"")+vl;
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
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtDCode = new javax.swing.JTextField();
        pnlResums = new javax.swing.JPanel();

        subContain.setLayout(new java.awt.BorderLayout());

        jLabel2.setText("Ingrese el codigo del articulo a devolver:");

        txtDCode.setColumns(12);
        txtDCode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtDCodeKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(136, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtDCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        subContain.add(jPanel1, java.awt.BorderLayout.NORTH);

        javax.swing.GroupLayout pnlResumsLayout = new javax.swing.GroupLayout(pnlResums);
        pnlResums.setLayout(pnlResumsLayout);
        pnlResumsLayout.setHorizontalGroup(
            pnlResumsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 447, Short.MAX_VALUE)
        );
        pnlResumsLayout.setVerticalGroup(
            pnlResumsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 197, Short.MAX_VALUE)
        );

        subContain.add(pnlResums, java.awt.BorderLayout.CENTER);

        mainContain.setViewportView(subContain);

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    private void txtDCodeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDCodeKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            addItem2resume(null,0);
        }
    }//GEN-LAST:event_txtDCodeKeyPressed

    @Override
    public String getTitle(){
        return "Devolucion de productos en alquiler";
    }
    
    @Override
    protected void updateIdTitle(){
        lblTitle.setText(getTitle());
        ctrl.changeViewName(this, getTitle());
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane mainContain;
    private javax.swing.JPanel pnlResums;
    private javax.swing.JPanel subContain;
    private javax.swing.JTextField txtDCode;
    // End of variables declaration//GEN-END:variables
}
