/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.views.modalqrs;

import com.videoup.entities.VideoupCtofrentas;
import com.videoup.entities.VideoupItemsrnt;
import com.videoup.utils.GenProccess;
import java.awt.Color;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Pedro
 */
public class ItemResume extends javax.swing.JPanel implements DocumentListener{

    private VideoupItemsrnt itmr;
    private String arti;
    private float clc_cargo;
    private float cargo;
    private float[] costos;
    private DevResume dvRsm;
    private NumberFormat frmCurr;
    private int usBonos;
    private int ignoreSecs;
    private boolean blokcRiser;
    private int prep_hh;
    
    /**
     * Creates new form ItemResume
     */
    ItemResume(DevResume dvRsm, VideoupItemsrnt itmr, int hh){
        String oName=null;
        VideoupCtofrentas cofRnts;
        this.itmr=itmr;
        this.dvRsm=dvRsm;
        prep_hh=hh;
        blokcRiser=false;
        usBonos=0;
        ignoreSecs=0;
        frmCurr=NumberFormat.getCurrencyInstance();
        arti="<html>["+itmr.getIdbc().getBarcode()+"] "+GenProccess.getItemName(itmr)+"<br />";
        arti+=" Cargo por alquiler de : ";
        if(prep_hh>0){
            arti+=GenProccess.stringSpentTime( prep_hh*3600 );
        }else{
            arti+=GenProccess.getSpentTime(itmr);
        }
        cofRnts=itmr.getVideoupCtofrentas();
        if(cofRnts!=null){
            oName=cofRnts.getNamer();
        }
        if(oName!=null){
            arti+="<br /><span style=\"color:rgb(219,64,43);\"> ** Aplica oferta: "+oName+"</span>";
        } arti+="</html>";
        initComponents();
        txtCargoApli.getDocument().addDocumentListener(this);
        showData();
    }
    
    private void riseChange(){
        float newCargo;
        boolean empty=false;
        if(blokcRiser){ return; }
        try{
            empty=(txtCargoApli.getText().trim().length()==0);
            newCargo=Float.parseFloat(txtCargoApli.getText());
        }catch(NumberFormatException nfe){
            newCargo=(empty?0:-1);
            txtCargoApli.setBackground(new Color(255,107,97));
            txtCargoApli.setToolTipText(null);
        }
        cargo=newCargo;
        if(cargo>=0){
            txtCargoApli.setBackground(new Color(216,227,255));
            txtCargoApli.setToolTipText("Cargo editado");
        }
        dvRsm.updateTotals();
    }
    
    public int useBonos(int bonos, int hours,boolean reset){
        long myhours;
        int lBonos=0;
        if(reset){
            usBonos=0;
            ignoreSecs=0;
        }
        myhours=(hours>0?hours:GenProccess.getHoursSpent(itmr,ignoreSecs));
        while(lBonos<bonos && myhours>0){
            myhours-=hours;
            myhours=(myhours<0?0:myhours);
            lBonos++;
        }
        usBonos+=lBonos;
        ignoreSecs+=(lBonos*hours*3600);
        showData();
        return lBonos;
    }
    
    public boolean canUseBonos(){
        long myhours=(prep_hh>0?prep_hh:GenProccess.getHoursSpent(itmr,ignoreSecs));
        return (myhours>0);
    }
    
    private void showData(){
        final DecimalFormat dff=new DecimalFormat("#.##");
        DecimalFormat dfi=new DecimalFormat("#");
        float prepmny=(itmr.getStatus()==7?itmr.getCstApli():0);
        costos=GenProccess.getCostos(itmr,ignoreSecs,prep_hh);
        cargo=costos[4];
        if(prepmny>0){
            txtPrepago.setText( frmCurr.format(prepmny) );
            cargo-=prepmny;
            if(cargo<0){ cargo=0; }
        }else{
            lblPrepago.setVisible(false); txtPrepago.setVisible(false);
        }
        clc_cargo=cargo;
        lblArticulo.setText(arti);
        txCFin.setText( frmCurr.format(costos[0]) );
        if(usBonos>0){
            lblBonos.setVisible(true);
            lblBonos.setText("-"+usBonos+" bonos");
        }else{ lblBonos.setVisible(false); }
        if(costos[1]==0){
            lbCOfrt.setVisible(false); txCOfrt.setVisible(false);
        }else{
            strikeCargo();
        }
        if(costos[2]==0){
            lbDscOfrt.setVisible(false); txDscOfrt.setVisible(false);
        }else{
            txDscOfrt.setText( dfi.format(costos[2])+" %" );
        }
        if(costos[3]==0){
            lbCXtra.setVisible(false); txCXtra.setVisible(false);
        }else{
            txCXtra.setText( frmCurr.format(costos[3]) );
        }
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                blokcRiser=true;
                txtCargoApli.setText( dff.format(cargo) );
                blokcRiser=false;
            }
        });
    }
    
    private void strikeCargo(){
        Map attributes = (new Font("Tahoma", Font.PLAIN, 11)).getAttributes();
        attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
        txCOfrt.setText( frmCurr.format(costos[1]) );
        txCFin.setFont(new Font(attributes));
    }

    private void resetCargos(){
        final DecimalFormat dff=new DecimalFormat("#.##");
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                txtCargoApli.setText( dff.format(clc_cargo) );
            }
        });
        txtCargoApli.setBackground(new Color(255,255,255));
        txtCargoApli.setToolTipText(null);
    }
    
    public int getIdIt() {
        return itmr.getIdir();
    }

    public int getStatus(){
        return itmr.getStatus();
    }
    
    public float getCargo() {
        return cargo;
    }

    public VideoupItemsrnt getItemr(boolean applyCostos){
        float cargo2apply=cargo;
        if(applyCostos){
            if(itmr.getStatus()==7){
                cargo2apply+=itmr.getCstApli();
            }
            itmr.setCstFin(costos[0]);
            itmr.setOfrtCfin(costos[1]);
            itmr.setOfrtOff( (int)costos[2] );
            itmr.setCstXt(costos[3]);
            itmr.setCstApli(cargo2apply);
        }
        return itmr;
    }
    
    public boolean isCargoEdited(){
        return (cargo!=clc_cargo);
    }
    
    public void setFinalized(){
        txtCargoApli.setEditable(false); lblReset.setVisible(false);
    }
    
    public boolean isPrepaid(){
        return (prep_hh>0);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblArticulo = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lbCFin = new javax.swing.JLabel();
        lbCOfrt = new javax.swing.JLabel();
        txCOfrt = new javax.swing.JTextField();
        lbDscOfrt = new javax.swing.JLabel();
        txDscOfrt = new javax.swing.JTextField();
        lbCXtra = new javax.swing.JLabel();
        txCXtra = new javax.swing.JTextField();
        lbCApli = new javax.swing.JLabel();
        pnlCApli = new javax.swing.JPanel();
        txtCargoApli = new javax.swing.JTextField();
        lblReset = new javax.swing.JLabel();
        txCFin = new javax.swing.JFormattedTextField();
        lblBonos = new javax.swing.JLabel();
        lblPrepago = new javax.swing.JLabel();
        txtPrepago = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();

        setLayout(new java.awt.BorderLayout());

        lblArticulo.setText("Articulo");
        add(lblArticulo, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        lbCFin.setText("Cargo por Alquiler:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 3;
        gridBagConstraints.ipady = 1;
        jPanel1.add(lbCFin, gridBagConstraints);

        lbCOfrt.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbCOfrt.setForeground(new java.awt.Color(219, 64, 43));
        lbCOfrt.setText("Cargo por oferta:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 3;
        gridBagConstraints.ipady = 1;
        jPanel1.add(lbCOfrt, gridBagConstraints);

        txCOfrt.setEditable(false);
        txCOfrt.setColumns(6);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 3;
        gridBagConstraints.ipady = 1;
        jPanel1.add(txCOfrt, gridBagConstraints);

        lbDscOfrt.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbDscOfrt.setForeground(new java.awt.Color(219, 64, 43));
        lbDscOfrt.setText("Descuento por oferta:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 3;
        gridBagConstraints.ipady = 1;
        jPanel1.add(lbDscOfrt, gridBagConstraints);

        txDscOfrt.setEditable(false);
        txDscOfrt.setColumns(6);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 3;
        gridBagConstraints.ipady = 1;
        jPanel1.add(txDscOfrt, gridBagConstraints);

        lbCXtra.setText("Cargo tiempo extra:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 3;
        gridBagConstraints.ipady = 1;
        jPanel1.add(lbCXtra, gridBagConstraints);

        txCXtra.setEditable(false);
        txCXtra.setColumns(6);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 3;
        gridBagConstraints.ipady = 1;
        jPanel1.add(txCXtra, gridBagConstraints);

        lbCApli.setText("Cargo a aplicar:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 3;
        gridBagConstraints.ipady = 1;
        jPanel1.add(lbCApli, gridBagConstraints);

        pnlCApli.setLayout(new java.awt.BorderLayout());

        txtCargoApli.setColumns(6);
        pnlCApli.add(txtCargoApli, java.awt.BorderLayout.CENTER);

        lblReset.setBackground(new java.awt.Color(255, 255, 255));
        lblReset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/videoup/imgs/reset.png"))); // NOI18N
        lblReset.setToolTipText("Reset cargo original calculado");
        lblReset.setOpaque(true);
        lblReset.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblResetMouseClicked(evt);
            }
        });
        pnlCApli.add(lblReset, java.awt.BorderLayout.EAST);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 3;
        gridBagConstraints.ipady = 1;
        jPanel1.add(pnlCApli, gridBagConstraints);

        txCFin.setEditable(false);
        txCFin.setColumns(6);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 3;
        gridBagConstraints.ipady = 1;
        jPanel1.add(txCFin, gridBagConstraints);

        lblBonos.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblBonos.setForeground(new java.awt.Color(0, 0, 153));
        lblBonos.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblBonos.setText("Usando N bonos");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(lblBonos, gridBagConstraints);

        lblPrepago.setText("Prepago:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 3;
        gridBagConstraints.ipady = 1;
        jPanel1.add(lblPrepago, gridBagConstraints);

        txtPrepago.setEditable(false);
        txtPrepago.setBackground(new java.awt.Color(255, 210, 91));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 3;
        gridBagConstraints.ipady = 1;
        jPanel1.add(txtPrepago, gridBagConstraints);

        add(jPanel1, java.awt.BorderLayout.EAST);

        jSeparator1.setForeground(new java.awt.Color(54, 139, 255));
        add(jSeparator1, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void lblResetMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblResetMouseClicked
        resetCargos();
    }//GEN-LAST:event_lblResetMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lbCApli;
    private javax.swing.JLabel lbCFin;
    private javax.swing.JLabel lbCOfrt;
    private javax.swing.JLabel lbCXtra;
    private javax.swing.JLabel lbDscOfrt;
    private javax.swing.JLabel lblArticulo;
    private javax.swing.JLabel lblBonos;
    private javax.swing.JLabel lblPrepago;
    private javax.swing.JLabel lblReset;
    private javax.swing.JPanel pnlCApli;
    private javax.swing.JFormattedTextField txCFin;
    private javax.swing.JTextField txCOfrt;
    private javax.swing.JTextField txCXtra;
    private javax.swing.JTextField txDscOfrt;
    private javax.swing.JTextField txtCargoApli;
    private javax.swing.JTextField txtPrepago;
    // End of variables declaration//GEN-END:variables

    @Override
    public void insertUpdate(DocumentEvent e) {
        riseChange();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        riseChange();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        riseChange();
    }
}
