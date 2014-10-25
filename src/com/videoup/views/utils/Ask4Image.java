/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.views.utils;

import com.videoup.utils.GenProccess;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import uk.co.mmscomputing.device.scanner.Scanner;
import uk.co.mmscomputing.device.scanner.ScannerDevice;
import uk.co.mmscomputing.device.scanner.ScannerIOException;
import uk.co.mmscomputing.device.scanner.ScannerIOMetadata;
import uk.co.mmscomputing.device.scanner.ScannerListener;
import uk.co.mmscomputing.device.twain.TwainIOMetadata;
import uk.co.mmscomputing.device.twain.TwainSource;

/**
 *
 * @author Pedro
 */
public class Ask4Image extends javax.swing.JDialog implements ScannerListener, ActionListener{

    private Scanner scanner=null;
    private List<Component> opts=new ArrayList<Component>();
    private JButton btnFile;
    private ViewFicha ficha;
    private byte[] imgg;
    private int returnKey;
    
    /**
     * Creates new form Ask4Image
     */
    public Ask4Image(java.awt.Frame parent, boolean modal,ViewFicha ficha,int returnKey){
        super(parent, modal);
        this.returnKey=returnKey;
        this.ficha=ficha;
        initComponents();
        imgg=null;
        btnFile=new JButton("Desde archivo");
        btnFile.setActionCommand("from_file");
        btnFile.addActionListener(this);
        opts.add(new JLabel("Desde dispositivo",JLabel.CENTER));
        try {
            scanner=Scanner.getDevice();
            if(scanner!=null){
                scanner.addListener(this);
            }else{
                opts.add(lblError);
            }
            loadDispositives();
        }catch(Exception e){
            //e.printStackTrace();
        }
        pack();
        setLocationRelativeTo(null);
    }

    private void loadDispositives(){
        String[] scanners;
        JButton btn;
        if(scanner!=null){
            try {
                scanners = scanner.getDeviceNames();
                if(scanners.length==0){
                    opts.add(new JLabel("No se encontraron dispositivos",JLabel.CENTER));
                }
                for(String name: scanners){
                    btn=new JButton(name);
                    btn.setActionCommand(name);
                    btn.addActionListener(this);
                    opts.add(btn);
                }
            }catch(ScannerIOException ex){
                lblError.setText("Error: "+ex.getMessage());
                opts.add(lblError);
            }
        }
        opts.add(new JLabel("Desde sistema de archivos",JLabel.CENTER));
        opts.add(btnFile);
        Utils.loadAsTableLayout(opts, pnlCen, "No hay opciones de entrada");
    }
    
    private void getFromFile(){
        imgg=GenProccess.getImageBlob(this,"Elegir imagen");
        if(imgg==null){
            showDialogErr(GenProccess.getError());
        }else if(imgg.length>0){
            ficha.setGettedImage(imgg, returnKey);
            dispose();
        }
    }
    
    private void getFromDispositive(String name){
        try{
            scanner.select(name);
            scanner.acquire();
        }catch (ScannerIOException ex){
            showDialogErr("Error: "+ex.getMessage());
        }
    }
    
    private void showDialogErr(final String err){
        final Ask4Image afi=this;
        EventQueue.invokeLater(new Runnable(){
            @Override
            public void run(){
                JOptionPane.showMessageDialog(afi,err,"Error",JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    protected void setBusy(String mss, boolean busy){
        this.setVisible(false);
        if(busy){
            remove(pnlCen);
            add(jxBusyLbl,BorderLayout.CENTER);
            jxBusyLbl.setText(mss);
            jxBusyLbl.setBusy(true);
        }else{
            jxBusyLbl.setBusy(false);
            remove(jxBusyLbl);
            add(pnlCen,BorderLayout.CENTER);
        }
        this.setVisible(true);
    }
    
    private void setLoadedImage(BufferedImage image){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", baos);
            baos.flush();
            imgg=baos.toByteArray();
            baos.close();
        }catch (IOException ex){
            showDialogErr(ex.getMessage());
            return;
        }
        ficha.setGettedImage(imgg, returnKey);
        dispose();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblError = new javax.swing.JLabel();
        jxBusyLbl = new org.jdesktop.swingx.JXBusyLabel();
        pnlCen = new javax.swing.JPanel();

        lblError.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblError.setForeground(new java.awt.Color(202, 0, 0));
        lblError.setText("¡Error al buscar dispositivos!");

        jxBusyLbl.setText("jXBusyLabel1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cargar Imagen");

        pnlCen.setLayout(new java.awt.BorderLayout());
        getContentPane().add(pnlCen, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXBusyLabel jxBusyLbl;
    private javax.swing.JLabel lblError;
    private javax.swing.JPanel pnlCen;
    // End of variables declaration//GEN-END:variables

    @Override
    public void update(ScannerIOMetadata.Type type, ScannerIOMetadata siom){
        TwainSource ts;
        BufferedImage image;
        boolean busy=false;
        if (type.equals(ScannerIOMetadata.ACQUIRED)){
            image=siom.getImage();
            siom.setImage(null);
            try{
                new uk.co.mmscomputing.concurrent.Semaphore(0, true).tryAcquire(2000, null);
            }catch (InterruptedException e){ showDialogErr(e.getMessage()); }
            setLoadedImage(image);
            busy=false;
        }else if (type.equals(ScannerIOMetadata.NEGOTIATE)) {
            ScannerDevice device = siom.getDevice();
            try{
                device.setShowUserInterface(true);
                busy=true;
            }catch(Exception e){ showDialogErr(e.getMessage()); }
        }else if (type.equals(ScannerIOMetadata.STATECHANGE)) {
            ts = ((TwainIOMetadata)siom).getSource();
            ((TwainIOMetadata)siom).setState(6);
            if ((siom.getLastState() == 3) && (siom.getState() == 4)){
            }
            busy=true;
        }else if (type.equals(ScannerIOMetadata.EXCEPTION)) {
            showDialogErr(siom.getException().toString());
            busy=false;
        }
        //setBusy("Escaneando...",busy);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actCmm=e.getActionCommand();
        if(actCmm.equals("from_file")){
            getFromFile();
        }else{
            getFromDispositive(actCmm);
        }
    }
}
