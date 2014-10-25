/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.views.modconf;

import com.videoup.controllers.Controller;
import com.videoup.entities.VideoupBaseEntity;
import com.videoup.entities.VideoupCatgs;
import com.videoup.entities.VideoupCtprrentas;
import com.videoup.entities.VideoupFormts;
import com.videoup.views.utils.ViewFicha;
import java.util.ArrayList;

/**
 *
 * @author Pedro
 */
public class DataBase extends ViewFicha{

    /**
     * Creates new form DataBase
     */
    public DataBase(Controller ctrl, int id) {
        super(ctrl,id,false);
        initComponents();
        initComps();
    }
    
    private void initComps(){
        putMainContain(mainContain);
        removeObtnNew();
        changes=false;
    }

    @Override
    protected void updateIdTitle(){
        lblTitle.setText(getTitle());
        ctrl.changeViewName(this, getTitle());
    }
    
    @Override
    public String getTitle(){
        return "Motor de base de datos";
    }
    
    public boolean saveRecords(ArrayList<VideoupBaseEntity> lste){
        boolean saved=saveEntities(lste);
        return saved;
    }
    
    public boolean existsDni(String Dni){
        String q="select count(*) from VideoupCustomers where dni='"+Dni+"'";
        long n=countExistEntities(q);
        if(n==0){ return false; }
        return true;
    }
    
    public boolean existsCodeSoc(String CdSoc){
        String q="select count(*) from VideoupCustomers where codCst='"+CdSoc+"'";
        long n=countExistEntities(q);
        if(n==0){ return false; }
        return true;
    }
    
    public boolean existsMCodeBar(String code){
        String q="select count(*) from VideoupBcodes where barcode='"+code+"'";
        long n=countExistEntities(q);
        if(n==0){ return false; }
        return true;
    }
    
    public VideoupCatgs getCatg(String name){
        String q;
        VideoupCatgs catg;
        if(name.trim().length()==0){
            return null;
        }
        q="from VideoupCatgs where catg='"+name+"'";
        catg=(VideoupCatgs)getEntity(q);
        if(catg==null){
            catg=new VideoupCatgs(name);
            if(!saveEntity(catg,false)){
                return null;
            }
        }
        return catg;
    }
    
    public VideoupFormts getFormt(String name){
        String q="from VideoupFormts where frmt='"+name+"'";
        VideoupFormts frt=(VideoupFormts)getEntity(q);
        if(frt==null){
            frt=new VideoupFormts(name);
            if(!saveEntity(frt,false)){
                return null;
            }
        }
        return frt;
    }
    
    /**
     * @param normal false=estrenos
     * @return 
     */
    public VideoupCtprrentas getCtprr(boolean normal){
        String q="from VideoupCtprrentas where idcpr="+(normal?1:5);
        VideoupCtprrentas ctprr=(VideoupCtprrentas)getEntity(q);
        return ctprr;
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
        jLabel1 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        btnImport = new javax.swing.JButton();
        serverOpt = new javax.swing.ButtonGroup();

        jLabel1.setText("Conexion a servidor de base de datos:");

        serverOpt.add(jRadioButton1);
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("Usa H2 engine");
        jRadioButton1.setEnabled(false);

        serverOpt.add(jRadioButton2);
        jRadioButton2.setText("Usar MySQL");
        jRadioButton2.setEnabled(false);

        btnImport.setText("Importar datos CSV");
        btnImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mainContainLayout = new javax.swing.GroupLayout(mainContain);
        mainContain.setLayout(mainContainLayout);
        mainContainLayout.setHorizontalGroup(
            mainContainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainContainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainContainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(mainContainLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(mainContainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRadioButton1)
                            .addComponent(jRadioButton2)))
                    .addComponent(btnImport))
                .addContainerGap(121, Short.MAX_VALUE))
        );
        mainContainLayout.setVerticalGroup(
            mainContainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainContainLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButton2)
                .addGap(18, 18, 18)
                .addComponent(btnImport)
                .addContainerGap(172, Short.MAX_VALUE))
        );

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    private void btnImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportActionPerformed
        JDImportCSV importer=new JDImportCSV(getMainWindow(),true,this);
        importer.setVisible(true);
    }//GEN-LAST:event_btnImportActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnImport;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JPanel mainContain;
    private javax.swing.ButtonGroup serverOpt;
    // End of variables declaration//GEN-END:variables
}