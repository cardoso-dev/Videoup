/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.views.utils;

import com.videoup.entities.VideoupBaseEntity;
import com.videoup.entities.VideoupTagitems;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

/**
 *
 * @author Pedro
 */
public class TagSingle extends JLayeredPane{

    private TreeMap<Integer,ItemTag> items;
    private TreeMap<Integer,JComponent> comps;
    private String error;
    private int wd; // width in mm
    private int hg; // height in mm
    private boolean showOpcs;
    private ViewReport tSheet;

    /**
     * Creates new form TagSingle
     */
    public TagSingle() {
        initComponents();
        items=new TreeMap<Integer,ItemTag>();
        comps=new TreeMap<Integer,JComponent>();
        showOpcs=false;
        pnlOpcs.setVisible(false);
    }
    
    public void showOpcs(boolean so){ showOpcs=so; }
    
    public void setDimMM(int w, int h){ wd=w; hg=h; }
    
    public void settTagSheet(ViewReport ts){ tSheet=ts; }
    
    public int getWidhtMM(){ return wd; }
    public int getHeightMM(){ return hg; }

    public void addNewItem(int k, ItemTag itt){
        addComp(k,itt); items.put(k,itt);
    }

    private void addComp(int k, ItemTag itt){
        JComponent compt;
        InputStream inpstr;
        BufferedImage bfImg;
        if(itt.getType()==2){
            BarcodePan bct=new BarcodePan();
            bct.setCode(itt.getBcValue(), itt.getBcType());
            compt=new JPanel(new java.awt.BorderLayout());
            compt.add(bct,java.awt.BorderLayout.CENTER);
        }else if(itt.getType()==3){
            if(itt.getImage()!=null){
                inpstr=new ByteArrayInputStream(itt.getImage());
                try{
                    bfImg=ImageIO.read(inpstr);
                    compt=new ImagePanel(bfImg,new Dimension(mm2pixels(itt.getDim().width),mm2pixels(itt.getDim().height)));
                }catch (IOException ex){ compt=new JLabel("Error!"); }
            }else{ compt=new JLabel("X"); }
        }else{ compt=new JLabel(itt.getText()); }
        add(compt,1);
        compt.setLocation(mm2pixels(itt.getLocation().x),mm2pixels(itt.getLocation().y));
        compt.setSize( new Dimension(mm2pixels(itt.getDim().width),mm2pixels(itt.getDim().height)) );
        compt.setPreferredSize( new Dimension(mm2pixels(itt.getDim().width),mm2pixels(itt.getDim().height)) );
        compt.setMinimumSize( new Dimension(mm2pixels(itt.getDim().width),mm2pixels(itt.getDim().height)) );
        compt.setMaximumSize( new Dimension(mm2pixels(itt.getDim().width),mm2pixels(itt.getDim().height)) );
        comps.put(k,compt);
    }
        
    public int mm2pixels(int mm){
        int dpi=72; //getToolkit().getScreenResolution();
        double fact=dpi/25.4f;
        return (int)(mm*fact);
    }
    
    public void updateItem(int k, ItemTag tg){  
        ItemTag itmt=items.get(k);
        JComponent comp=comps.get(k);
        itmt.setItemAs(tg);
        items.put(k,itmt);
        remove(comp); comps.remove(k); comp=null;
        updatesViewItem(k); standOut(k);
    }
    
    public void relocateItem(int k, Point newloc){
        ItemTag itmt=items.get(k);
        itmt.setLocation(newloc); updatesViewItem(k); standOut(k);
    }
    
    public void reSizeItem(int k, Dimension ndim){
        ItemTag itmt=items.get(k);
        itmt.setDim(ndim); updatesViewItem(k); standOut(k);
    }
    
    public Point getLocationItem(int k){
        ItemTag itmt=items.get(k);
        return itmt.getLocation();
    }
    
    public ItemTag getItem(int k){
        ItemTag itmt=items.get(k);
        return itmt;
    }
    
    public void standOut(int k){
        Set<Integer> keys=comps.keySet();
        Iterator<Integer> itks=keys.iterator();
        JComponent cmp;
        int kc;
        while(itks.hasNext()){
            kc=itks.next();
            cmp=comps.get(kc);
            if(k==kc){ setLayer(cmp,2);
                cmp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(1,1,195))); 
            }else{ cmp.setBorder(null); setLayer(cmp,1); }
        }
        validate();
    }
    
    private void updatesViewItem(int k){
        ItemTag itm;
        setVisible(false);
        itm=items.get(k);
        JComponent cmp=comps.get(k);
        if(cmp!=null){
            remove(cmp); comps.remove(k); cmp=null;
        }
        addComp(k, itm);
        setVisible(true);
    }
    
    public boolean existsItem(String name){
        Set keys=items.keySet();
        Iterator<Integer> ksIt=keys.iterator();
        while(ksIt.hasNext()){
            ItemTag itg=items.get(ksIt.next());
            if(itg.getTitle().equals(name)){ return true; }
        }
        return false;
    }
    
    public VideoupTagitems getVideoupTagItem(int k){
        if(comps.get(k)!=null){ return items.get(k).getEntity(); }
        return null;
    }
    
    public void removeItem(int k){
        if(comps.get(k)!=null){ remove(comps.get(k)); }
        comps.remove(k); items.remove(k);
    }

    public int getItemCount(){ return items.size(); }
    
    public boolean validateItems(){
        Set keys=items.keySet();
        Iterator<Integer> ksIt=keys.iterator();
        int kb; error=null;
        while(ksIt.hasNext()){
            kb=ksIt.next();
            ItemTag itg=items.get(kb);
            if(!itg.validateInside()){ error=itg.getError()+" (En: "+itg.getTitle()+")"; return false; }
            if(itg.getType()==2){
                BarcodePan bct=((BarcodePan)(comps.get(kb).getComponent(0)));
                if(bct.hasError()!=null){
                    error="Codigo de barras: "+bct.hasError()+" (En: "+itg.getTitle()+")";
                    return false;
                }
            }
        }
        return true;        
    }
    
    public ArrayList<VideoupBaseEntity> getEntities(){
        ArrayList<VideoupBaseEntity> dta=new ArrayList<VideoupBaseEntity>();
        Set keys=items.keySet();
        Iterator<Integer> ksIt=keys.iterator();
        while(ksIt.hasNext()){
            dta.add( items.get(ksIt.next()).getEntity() );
        }
        return dta;
    }
    
    public String getError(){ return error; }
    
    public void setPrinting(boolean prt){
        if(prt){
            setBorder(null); pnlOpcs.setVisible(false);
        }else{
            setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(189, 189, 189)));
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
        java.awt.GridBagConstraints gridBagConstraints;

        pnlOpcs = new javax.swing.JPanel();
        btnDelete = new javax.swing.JButton();
        btnMoveForward = new javax.swing.JButton();

        pnlOpcs.setBackground(new java.awt.Color(255, 123, 122));
        pnlOpcs.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(159, 0, 0), 2, true));
        pnlOpcs.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                pnlOpcsMouseEntered(evt);
            }
        });
        pnlOpcs.setLayout(new java.awt.GridBagLayout());

        btnDelete.setBackground(new java.awt.Color(219, 63, 62));
        btnDelete.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnDelete.setText("X");
        btnDelete.setToolTipText("Quitar esta etiqueta");
        btnDelete.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnDeleteMouseEntered(evt);
            }
        });
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        pnlOpcs.add(btnDelete, new java.awt.GridBagConstraints());

        btnMoveForward.setBackground(new java.awt.Color(219, 63, 62));
        btnMoveForward.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnMoveForward.setText("->");
        btnMoveForward.setToolTipText("Mover a la siguiente posicion");
        btnMoveForward.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnMoveForwardMouseEntered(evt);
            }
        });
        btnMoveForward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveForwardActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        pnlOpcs.add(btnMoveForward, gridBagConstraints);

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                formMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                formMouseExited(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnDeleteMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDeleteMouseEntered
        if(showOpcs){ showOnOps(); }
    }//GEN-LAST:event_btnDeleteMouseEntered

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        if(tSheet!=null){
            Set keys=items.keySet();
            Iterator<Integer> ksIt=keys.iterator();
            String bcode=null;
            while(ksIt.hasNext()){
                ItemTag itg=items.get(ksIt.next());
                if(itg.getType()==2){ bcode=itg.getBcValue(); break; }
            }
            tSheet.insertNullBCode(bcode, true);
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnMoveForwardMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnMoveForwardMouseEntered
        if(showOpcs){ showOnOps(); }
    }//GEN-LAST:event_btnMoveForwardMouseEntered

    private void showOnOps(){
        if(showOpcs){
            int x=(this.getWidth()-pnlOpcs.getWidth())-2;
            int y=(this.getHeight()-pnlOpcs.getHeight())-2;
            if(x<0){ x=0; }
            if(y<0){ y=0; }
            remove(pnlOpcs); add(pnlOpcs,3);
            pnlOpcs.setLocation(x,y);
            pnlOpcs.setSize(pnlOpcs.getPreferredSize());
            pnlOpcs.setVisible(true);
        }
    }
    
    private void btnMoveForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoveForwardActionPerformed
        if(tSheet!=null){
            Set keys=items.keySet();
            Iterator<Integer> ksIt=keys.iterator();
            String bcode=null;
            while(ksIt.hasNext()){
                ItemTag itg=items.get(ksIt.next());
                if(itg.getType()==2){ bcode=itg.getBcValue(); break; }
            }
            tSheet.insertNullBCode(bcode, false);
        }
    }//GEN-LAST:event_btnMoveForwardActionPerformed

    private void pnlOpcsMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlOpcsMouseEntered
        if(showOpcs){ showOnOps(); }
    }//GEN-LAST:event_pnlOpcsMouseEntered

    private void formMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseEntered
        if(showOpcs){ showOnOps(); }
    }//GEN-LAST:event_formMouseEntered

    private void formMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseExited
        if(showOpcs){ pnlOpcs.setVisible(false); }
    }//GEN-LAST:event_formMouseExited

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnMoveForward;
    private javax.swing.JPanel pnlOpcs;
    // End of variables declaration//GEN-END:variables
}
