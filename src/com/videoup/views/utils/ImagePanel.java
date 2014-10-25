/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.views.utils;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 *
 * @author Pedro
 */
public class ImagePanel extends javax.swing.JPanel {

    private BufferedImage img;
    private Dimension dim;
    private boolean repeatH;
    private boolean repeatV;
    
    /**
     * Creates new form ImagePanel
     */
    public ImagePanel(BufferedImage img){
        this.img=img;
        dim=null;
        setPreferredSize(new Dimension(img.getWidth(),img.getHeight()));
        //initComponents();
    }  
    
    /**
     * Creates new form ImagePanel
     */
    public ImagePanel(BufferedImage img, Dimension dim){
        this.img=img;
        this.dim=dim;
        setPreferredSize(dim);
        //initComponents();
    }  
    
    @Override
    public void paintComponent(Graphics g){
            super.paintComponent(g);
            int wd=img.getWidth();
            int hg=img.getHeight();
            int x=0;
            int y=0;
            double prop;
            if(dim!=null){
                if(wd>dim.width || hg>dim.height){
                    if(wd>hg){
                        prop= ((double)hg/(double)wd);
                        wd=dim.width;
                        hg=(int)Math.floor(prop*wd);
                    }else{
                        prop= ((double)wd/(double)hg);
                        hg=dim.height;
                        wd=(int)Math.floor(prop*hg);
                    }
                }
            }
            if(wd<getWidth() && !repeatH){
                x=(int) Math.floor( (getWidth()-wd)/2 );
            }
            if(hg<getHeight() && !repeatV){
                y=(int) Math.floor( (getHeight()-hg)/2 );
            }
            g.drawImage(img,x,y,wd,hg,null);
            if(repeatH){
                while( (x+wd)<getWidth() ){
                    x+=wd;
                    g.drawImage(img,x,y,wd,hg,null);
                    if(repeatV){ 
                        drawRepeatV(g,wd,hg,x,0);
                    }
                }
            }else if(repeatV){
                drawRepeatV(g,wd,hg,x,0);
            }
    }
    
    private void drawRepeatV(Graphics g, int wd, int hg, int x, int y){
        while( (y+hg)<getHeight() ){
            y+=hg;
            g.drawImage(img,x,y,wd,hg,null);
        }
    }

    public void setRepeatH(boolean repeatH) {
        this.repeatH = repeatH;
    }

    public void setRepeatV(boolean repeatV) {
        this.repeatV = repeatV;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}