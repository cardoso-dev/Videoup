/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.views.utils;

import com.videoup.entities.VideoupBaseEntity;
import com.videoup.entities.VideoupBcodes;
import com.videoup.entities.VideoupCtprrentas;
import com.videoup.entities.VideoupGames;
import com.videoup.entities.VideoupGimgs;
import com.videoup.entities.VideoupItemsrnt;
import com.videoup.entities.VideoupItemsvnt;
import com.videoup.entities.VideoupMimgs;
import com.videoup.entities.VideoupMovies;
import com.videoup.utils.GenProccess;
import com.videoup.utils.GeneralDAO;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.Date;
import javax.imageio.ImageIO;

/**
 *
 * @author Pedro
 */
public class ProductBriefData extends javax.swing.JPanel {
    
    private VideoupMovies mov;
    private VideoupGames gam;
    private VideoupMimgs mImg;
    private VideoupGimgs gImg;
    private VideoupBcodes copy;
    private ImagePanel imgp;
    private ViewFicha aFich;
    private String descr;
    private VideoupItemsrnt itmr;
    private VideoupItemsvnt itmv;
    private float cargos;
    
    public ProductBriefData(VideoupMovies m, VideoupBcodes copy, ViewFicha fc, boolean isAlq){
        initComponents();
        GeneralDAO genDao;
        this.copy=copy;
        aFich=fc;
        mov=m;
        if(m.getIdcpr().getIdcpr()==5){
            if(!GenProccess.productApplyEstreno(m)){
                if( !GenProccess.switchCatgprNormal(m) ){
                    lblData.setText("<html>Error cargando datos del producto</html>");
                    return;
                }
            }
        }
        genDao=new GeneralDAO();
        mImg=(VideoupMimgs)genDao.getEntity("from VideoupMimgs where idrl="+mov.getId());
        if(mImg!=null){ setImage(mImg.getImg()); }
        gam=null;
        showAlqActionButtons(false);
        if(isAlq){
            showAlqData();
        }else{
            showVentData();
        }
    }
    
    public ProductBriefData(VideoupGames g, VideoupBcodes copy, ViewFicha fc, boolean isAlq){
        GeneralDAO genDao;
        initComponents();
        this.copy=copy;
        aFich=fc;
        gam=g;
        if(g.getIdcpr().getIdcpr()==5){
            if(!GenProccess.productApplyEstreno(g)){
                if( !GenProccess.switchCatgprNormal(g) ){
                    lblData.setText("<html>Error cargando datos del producto</html>");
                    return;
                }
            }
        }
        mov=null;
        genDao=new GeneralDAO();
        gImg=(VideoupGimgs)genDao.getEntity("from VideoupGimgs where idrl="+gam.getId());
        if(gImg!=null){ setImage(gImg.getImg()); }
        showAlqActionButtons(false);
        if(isAlq){
            showAlqData();
        }else{
            showVentData();
        }
    }
    
    public ProductBriefData(VideoupItemsrnt itmr, ViewFicha fc){
        GeneralDAO genDao;
        initComponents();
        this.itmr=itmr;
        copy=itmr.getIdbc();
        aFich=fc;
        if(itmr.getIsmov()){
            genDao=new GeneralDAO();
            mov=copy.getVideoupMovie();
            mImg=(VideoupMimgs)genDao.getEntity("from VideoupMimgs where idrl="+mov.getId());
            if(mImg!=null){ setImage(mImg.getImg()); }
            gam=null;
        }else{
            genDao=new GeneralDAO();
            gam=copy.getVideoupGame();
            gImg=(VideoupGimgs)genDao.getEntity("from VideoupGimgs where idrl="+gam.getId());
            if(gImg!=null){ setImage(gImg.getImg()); }
            mov=null;
        }
        showAlqData();
        hideRemovalBtn();
    }
    
    public ProductBriefData(VideoupItemsvnt itmv, ViewFicha fc){
        GeneralDAO genDao;
        initComponents();
        this.itmv=itmv;
        copy=itmv.getIdbc();
        aFich=fc;
        if(itmv.getIsmov()){
            genDao=new GeneralDAO();
            mov=copy.getVideoupMovie();
            mImg=(VideoupMimgs)genDao.getEntity("from VideoupMimgs where idrl="+mov.getId());
            if(mImg!=null){ setImage(mImg.getImg()); }
            gam=null;
        }else{
            genDao=new GeneralDAO();
            gam=copy.getVideoupGame();
            gImg=(VideoupGimgs)genDao.getEntity("from VideoupGimgs where idrl="+gam.getId());
            if(gImg!=null){ setImage(gImg.getImg()); }
            mov=null;
        }
        showVentData();
        showAlqActionButtons(false);
        hideRemovalBtn();
    }
    
    private void setImage(byte[] bites){
        InputStream inpstr;
        BufferedImage bfImg;
        if(bites==null){
            pnlPortada.remove(imgp);
            return;
        }
        inpstr=new ByteArrayInputStream(bites);
        try {
            bfImg = ImageIO.read(inpstr);
        }catch (IOException ex) {
            pnlPortada.remove(imgp);
            return;
        }
        if(imgp!=null){
            pnlPortada.remove(imgp);
        }
        imgp=new ImagePanel(bfImg, new Dimension(143,189) );
        pnlPortada.add(imgp,BorderLayout.CENTER);
        validate();
    }
    
    private void showAlqData(){
        NumberFormat frmCurr=NumberFormat.getCurrencyInstance();
        String label="<html>";
        String states[]={ "<font color=\"#0000FF\">Solicitada</font>",
            "<font color=\"#FF0000\">Cancelada</font>",
            "<font color=\"#000000\">Finalizada</font>",
            "<font color=\"#0000FF\">En curso</font>",
            "<font color=\"#000000\">Terminada pagada</font>",
            "<font color=\"#990000\">Terminada por pagar</font>",
            "<font color=\"#8F008F\">Cambiada</font>",
            "<font color=\"#0000FF\">En curso con prepago</font>"
        };
        VideoupCtprrentas ctgp;
        String nomcatpr;
        float cosotou;
        int btime;
        String nomUnddTime;
        if(mov!=null){
            ctgp=mov.getIdcpr();
            descr="Pelicula: "+mov.getTitulo()+(mov.getCatg()!=null?"<br /> "+mov.getCatg():"")+", "+mov.getClasif();
        }else{
            ctgp=gam.getIdcpr();
            descr="Videojuego: "+gam.getTitulo()+(gam.getCatg()!=null?"<br /> "+gam.getCatg():"")+", "+gam.getClasif();
        }
        if(itmr!=null){
            nomcatpr=itmr.getNmCtrprrent();
            cosotou=itmr.getCstUt();
            btime=itmr.getBTime();
            nomUnddTime=(itmr.getUTime()==60?"Hora":"Dia");
        }else{
            nomcatpr=ctgp.getNamec();
            cosotou=ctgp.getCostou();
            btime=ctgp.getUnsBase();
            nomUnddTime=ctgp.getUnidadNm();
        }
        label+=descr+", Catalogo: "+nomcatpr+"<br />Ejemplar: "+copy.getBarcode();
        label+="<br /><br />Alquiler: "+frmCurr.format(cosotou)+" x "+btime+" "+nomUnddTime;
        if(itmr!=null){
            label+="<br /> * Estado: "+states[itmr.getStatus()];
            if( (itmr.getStatus()>1 && itmr.getStatus()<6) || itmr.getStatus()==7 ){
                cargos=GenProccess.getCosto(itmr,0);
                if(itmr.getStatus()==7){
                    cargos-=itmr.getCstApli();
                }
                label+="<br /> * Tiempo transcurrido: ";
                label+=GenProccess.getSpentTime(itmr)+"<br /> * Cargos generados: "+frmCurr.format(cargos);
            }
            showAlqActionButtons(itmr.getStatus()==3 || itmr.getStatus()==7);
        }
        lblData.setText(label+"</html>");
    }
    
    private void showVentData(){
        NumberFormat frmCurr=NumberFormat.getCurrencyInstance();
        String label="<html>";
        VideoupCtprrentas ctgp;
        float cosotov=copy.getPrVenta();
        if(mov!=null){
            ctgp=mov.getIdcpr();
            descr="Pelicula: "+mov.getTitulo()+"<br /> "+mov.getCatg()+", "+mov.getClasif();
        }else{
            ctgp=gam.getIdcpr();
            descr="Videojuego: "+gam.getTitulo()+"<br /> "+gam.getCatg()+", "+gam.getClasif();
        }
        label+=descr+", Catalogo: "+ctgp.getNamec()+"<br />Ejemplar: "+copy.getBarcode();
        label+="<br /><br />Costo de venta: "+frmCurr.format(cosotov);
        lblData.setText(label+"</html>");
    }
    
    public float getCargoVenta(){
        return copy.getPrVenta();
    }
    
    public String getItemName(){
        if(mov!=null){
            return "Pelicula: "+mov.getTitulo();
        }else{
            return "Videojuego: "+gam.getTitulo();
        }
    }
     
    /**
     * 
     * @param increase 0=no-increase, 1=increase num_alqs, 2=increase num_solds, 3=num_changes
     * @return 
     */
    public VideoupBaseEntity getProductEntity(int increase){
        if(mov!=null){
            if(increase==1){
                mov.setNumAlqs(mov.getNumAlqs()+1);
            }else if(increase==2){
                mov.setNumSolds(mov.getNumSolds()+1);
            }else if(increase==3){
                mov.setNumChanges(mov.getNumChanges()+1);
            }
            return mov;
        }else{
            if(increase==1){
                gam.setNumAlqs(gam.getNumAlqs()+1);
            }else if(increase==2){
                gam.setNumSolds(gam.getNumSolds()+1);
            }else if(increase==3){
                gam.setNumChanges(gam.getNumChanges()+1);
            }
            return gam;
        }
    }
    
    public float getAlqCargos(){
        return cargos;
    }
    
    public VideoupItemsrnt getAlqEntity(){
        if(itmr!=null){
            return itmr;
        }
        itmr=new VideoupItemsrnt();
        VideoupCtprrentas ctgp=null;
        if(mov!=null){
            ctgp=mov.getIdcpr();
            itmr.setIsmov(true);
        }else if(gam!=null){
            ctgp=gam.getIdcpr();
            itmr.setIsmov(false);
        }
        itmr.setDescr(descr);
        itmr.setIdbc(copy);
        itmr.setCstUt(ctgp.getCostou());
        itmr.setCstUtx(ctgp.getCstuXtra());
        itmr.setBTime(ctgp.getUnsBase());
        itmr.setUTime(ctgp.getUnidadMins());
        itmr.setCstFin(0f);
        itmr.setCstXt(0f);
        itmr.setCstApli(0f);
        itmr.setNmCtrprrent(ctgp.getNamec());
        itmr.setOfrtOff(0);
        itmr.setOfrtCfin(0f);
        itmr.setSTime(null);
        itmr.setITime(new Date());
        itmr.setFTime(null);
        itmr.setStatus(3);
        itmr.setOnchange(0);
        return itmr;
    }
    
    public VideoupItemsvnt getVentEntity(){
        VideoupItemsvnt itmv=new VideoupItemsvnt();
        itmv.setIdbc(copy);
        itmv.setDescr(getItemName());
        return itmv;
    }
    
    public boolean isBCode(String bc){
        return copy.getBarcode().equals(bc);
    }
    
    public void markAsRentedBefore(boolean rented){
        lblTit.setText("<html>Articulo"+(rented?"<span style=\"color:red;text-decoration:underline;\"> *Visto </span>":"")+"</html>");
    }
    
    public boolean equals(ProductBriefData pbd){
        return pbd.isBCode(copy.getBarcode());
    }
    
    public VideoupBcodes getCopy(){
        return copy;
    }
    
    public void hideRemovalBtn(){
        btnRemove.setEnabled(false);
        btnRemove.setVisible(false);
    }
    
    public void showAlqActionButtons(boolean sh){
        pnlButtons.setVisible(sh);
    }
    
    public String getBCode(){
        return copy.getBarcode();
    }
    
    public boolean isAlqMark2finalize(){
        return jckDevol.isSelected();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlPortada = new javax.swing.JPanel();
        lblData = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        btnRemove = new javax.swing.JButton();
        lblTit = new javax.swing.JLabel();
        pnlButtons = new javax.swing.JPanel();
        jckDevol = new javax.swing.JCheckBox();
        bntCancel = new javax.swing.JButton();
        btnChange = new javax.swing.JButton();

        pnlPortada.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        pnlPortada.setLayout(new java.awt.BorderLayout());

        lblData.setBackground(new java.awt.Color(255, 255, 255));
        lblData.setText("Datos de producto");
        lblData.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lblData.setOpaque(true);

        btnRemove.setText("x");
        btnRemove.setToolTipText("Descartar articulo");
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });

        lblTit.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblTit.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTit.setText("Articulo");

        pnlButtons.setLayout(new javax.swing.BoxLayout(pnlButtons, javax.swing.BoxLayout.PAGE_AXIS));

        jckDevol.setText("Devolucion");
        jckDevol.setToolTipText("Marcar para devolucion");
        pnlButtons.add(jckDevol);

        bntCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/videoup/imgs/close.png"))); // NOI18N
        bntCancel.setToolTipText("Cancelar articulo");
        bntCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntCancelActionPerformed(evt);
            }
        });
        pnlButtons.add(bntCancel);

        btnChange.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/videoup/imgs/change.png"))); // NOI18N
        btnChange.setToolTipText("Cambiar articulo");
        btnChange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangeActionPerformed(evt);
            }
        });
        pnlButtons.add(btnChange);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlPortada, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(lblTit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemove))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator1)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblData)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(pnlButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlPortada, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRemove)
                    .addComponent(lblTit))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlButtons, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(46, 46, 46))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        if(aFich!=null){
            if(aFich instanceof com.videoup.views.modalqrs.Ficha){
                ((com.videoup.views.modalqrs.Ficha)aFich).removeItem(this);
            }else if(aFich instanceof com.videoup.views.modvnts.Ficha){
                ((com.videoup.views.modvnts.Ficha)aFich).removeItem(this);
            }
        }
    }//GEN-LAST:event_btnRemoveActionPerformed

    private void btnChangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeActionPerformed
        if(aFich!=null){
            if(aFich instanceof com.videoup.views.modalqrs.Ficha){
                ((com.videoup.views.modalqrs.Ficha)aFich).sent2change(copy.getBarcode());
            }
        }
    }//GEN-LAST:event_btnChangeActionPerformed

    private void bntCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntCancelActionPerformed
        if(aFich!=null){
            if(aFich instanceof com.videoup.views.modalqrs.Ficha){
                ((com.videoup.views.modalqrs.Ficha)aFich).cancelItem(this,itmr,copy);
            }
        }
    }//GEN-LAST:event_bntCancelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bntCancel;
    private javax.swing.JButton btnChange;
    private javax.swing.JButton btnRemove;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JCheckBox jckDevol;
    private javax.swing.JLabel lblData;
    private javax.swing.JLabel lblTit;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlPortada;
    // End of variables declaration//GEN-END:variables
}
