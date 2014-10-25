/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.views.modconf;

import com.csvreader.CsvReader;
import com.videoup.entities.VideoupAutrz;
import com.videoup.entities.VideoupBaseEntity;
import com.videoup.entities.VideoupBcodes;
import com.videoup.entities.VideoupCatgs;
import com.videoup.entities.VideoupCimgs;
import com.videoup.entities.VideoupCtprrentas;
import com.videoup.entities.VideoupCustomers;
import com.videoup.entities.VideoupDocs;
import com.videoup.entities.VideoupFormts;
import com.videoup.entities.VideoupGames;
import com.videoup.entities.VideoupGimgs;
import com.videoup.entities.VideoupMimgs;
import com.videoup.entities.VideoupMovies;
import com.videoup.entities.VideoupPuntos;
import com.videoup.utils.Globals;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Pedro
 */
public class JDImportCSV extends javax.swing.JDialog implements ItemListener{
    
    private int destiny; // 1=socios, 2=movies, 3=games
    private DataBase prWin;
    private CsvReader csvRead;
    private String path2file;
    private char sepr;
    private Date today;
    private Date dateNextMonth;
    private ArrayList<Object[]> outing;
    private float credPred;
    private int puntosPred;
    private int imported;
    private ImporterImageField socIIFfoto;
    private ImporterImageField movIIFport;
    private ImporterImageField gamIIFport;
    private ArrayList<ImporterImageField> socIIFdocs;

    /**
     * Creates new form JDImportCSV
     */
    public JDImportCSV(java.awt.Frame parent, boolean modal,DataBase prWin) {
        super(parent, modal);
        DateFormat dFormt=DateFormat.getDateInstance(DateFormat.SHORT);
        today=Calendar.getInstance().getTime();
        dateNextMonth=new Date(today.getTime()+2592000000l);
        socIIFfoto=new ImporterImageField("Foto",false);
        movIIFport=new ImporterImageField("Portada",false);
        gamIIFport=new ImporterImageField("Portada",false);
        this.prWin=prWin;
        initComponents();
        getContentPane().setLayout(new BorderLayout());
        lblDni.setText("*"+(String)Globals.getConfig("dniDef"));
        setJCombosListener();
        destiny=0;
        lblAltaPred.setText("Predeterminado: "+dFormt.format(today));
        lblVigenPred.setText("Predeterminado: "+dFormt.format(dateNextMonth));
        lblMLanzPred.setText("Predeterminado: "+dFormt.format(today));
        lblMEstrPred.setText("Predeterminado: "+dFormt.format(dateNextMonth));
        pnlFoto.add(socIIFfoto,BorderLayout.CENTER);
        pnlMPortada.add(movIIFport,BorderLayout.CENTER);
        pnlGPortada.add(gamIIFport,BorderLayout.CENTER);
        replacePanel(1);
    }
    
    private void cancelAndClose(){
        dispose();
    }
    
    /**
     * 
     * @param pnl 1=chooser, 2=pick file, 3=sociosA, 4=moviesA, 5=gamesA, 6=processing, 7=results
     */
    private void replacePanel(int pnl){
        Component cmp=null;
        Dimension dim;
        getContentPane().removeAll();
        if(pnl==1){ cmp=pnlChoose; }
        else if(pnl==2){ cmp=pnlPickFile; }
        else if(pnl==3){ cmp=pnlSociosA; }
        else if(pnl==4){ cmp=pnlMoviesA; }
        else if(pnl==5){ cmp=pnlGamesA; }
        else if(pnl==6){ cmp=pnlProcessing; }
        else if(pnl==7){ cmp=pnlResults; }
        getContentPane().add(cmp,BorderLayout.CENTER);
        if(pnl>=3 && pnl<6){ getContentPane().add(jscPVTable,BorderLayout.SOUTH); }
        pack();
        dim=getSize();
        if(dim.getHeight()<=349){ dim.height=351; }
        if(dim.getWidth()<=239){ dim.width=267; }
        setSize(dim);
        setLocationRelativeTo(null);
        validate();
    }
    
    private void pickAndLoadFile(){
        JFileChooser selFile=new JFileChooser();
        csvRead=null;
        int slFile;
        sepr=txtSeparador.getText().charAt(0);
        String[] headers;
        String[][] firstsValues;
        boolean atLeastOne=false;
        selFile.addChoosableFileFilter(new FileNameExtensionFilter("Archivos TXT","txt"));
        selFile.addChoosableFileFilter(new FileNameExtensionFilter("Archivos CSV","csv"));
        slFile=selFile.showDialog(this,"Seleccione origen de datos");
        if(slFile==JFileChooser.APPROVE_OPTION){
            try{
                path2file=selFile.getSelectedFile().getAbsolutePath();
                csvRead=new CsvReader(path2file,sepr);
                headers=csvRead.getHeaders();
                firstsValues=new String[7][csvRead.getColumnCount()];
                for(int g=0;g<firstsValues.length;g++){
                    if(csvRead.readRecord()){
                        firstsValues[g]=csvRead.getValues();
                        atLeastOne=true;
                    }
                }
                if(!atLeastOne){
                    prWin.showError("El archivo parece estar vacio, formato incorrecto o separador mal especificado");
                }else{
                    configImport(firstsValues,headers);
                }
            }catch(IOException ex){
                prWin.showError("Error: "+ex.getMessage());
            }finally{
                if(csvRead!=null){ csvRead.close(); }
            }
        }else{
            prWin.showError("Debe elegir un archivo CSV");
        }
    }
    
    private void configImport(String[][] firstsLines,String[] headers){
        buildTableModel(headers,firstsLines[0].length);
        loadShowList(firstsLines);
        if(destiny==1){
            replacePanel(3);
        }else if(destiny==2){
            replacePanel(4);
        }else if(destiny==3){
            replacePanel(5);
        }
    }
    
    private void buildTableModel(String[] headers, int nCols){
        if(headers==null){
            headers=new String[nCols];
            for(int h=1;h<=nCols;h++){
                headers[h-1]="Columna "+h;
            }
        }
        previewTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {}, headers){
                public boolean isCellEditable(int rowIndex, int columnIndex) {return false;}
            });
        previewTable.setColumnSelectionAllowed(true);
        previewTable.getTableHeader().setReorderingAllowed(false);
        previewTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        previewTable.setColumnSelectionAllowed(true);
        previewTable.setRowSelectionAllowed(false);
        setItemsCols(headers);
        for(int h=0;h<nCols;h++){
            previewTable.getColumnModel().getColumn(h).setMinWidth(97);
        }
    }
    
    private void loadShowList(String[][] data){
        DefaultTableModel tmodel=((DefaultTableModel)previewTable.getModel());
        for(int itr=tmodel.getRowCount();itr>0;itr--){
            tmodel.removeRow(itr-1);
        }
        for(int itr=0;itr<data.length;itr++){
            tmodel.addRow(data[itr]);
        }
        previewTable.setModel(tmodel);
    }
    
    private void setItemsCols(String[] hds){
        addJCombosItem("---");
        for(int i=0;i<hds.length;i++){
            addJCombosItem(hds[i]);
        }
    }
    
    private void addJCombosItem(String item){
        if(destiny==1){
                jcbSocNom.addItem(item);
                jcbSocApell.addItem(item);
                jcbSocDni.addItem(item);
                jcbSocCode.addItem(item);
                jcbSocMail.addItem(item);
                jcbSocDir.addItem(item);
                jcbSocPostal.addItem(item);
                jcbSocCity.addItem(item);
                jcbSocProvin.addItem(item);
                jcbSocPobla.addItem(item);
                jcbSocTelCasa.addItem(item);
                jcbSocTelMovil.addItem(item);
                jcbSocFechaAlt.addItem(item);
                jcbSocFechaVig.addItem(item);
                socIIFfoto.addFieldsItem(item);
                jcbSocCred.addItem(item);
                jcbSocPuntos.addItem(item);
                jcbSocRepre1.addItem(item);
                jcbSocRepre2.addItem(item);
        }else if(destiny==2){
            jcbMTit.addItem(item);
            jcbMDir.addItem(item);
            jcbMUrlTr.addItem(item);
            jcbMProta.addItem(item);
            jcbMProdu.addItem(item);
            jcbMDuraMin.addItem(item);
            jcbMSinop.addItem(item);
            jcbMClasif.addItem(item);
            jcbMAnyo.addItem(item);
            jcbMCatg.addItem(item);
            movIIFport.addFieldsItem(item);
            jcbMFLanz.addItem(item);
            jcbMFEstr.addItem(item);
            jcbMCodeb.addItem(item);
            jcbMFormt.addItem(item);
        }else if(destiny==3){
            jcbGTit.addItem(item);
            jcbGUrlTr.addItem(item);
            jcbGProdu.addItem(item);
            jcbGCatg.addItem(item);
            jcbGCodeb.addItem(item);
            jcbGAnyo.addItem(item);
            gamIIFport.addFieldsItem(item);
            jcbGSinop.addItem(item);
            jcbGClasif.addItem(item);
            jcbGFLanz.addItem(item);
            jcbGFEstr.addItem(item);
            jcbGFormt.addItem(item);
        }
    }
    
    private void setJCombosListener(){
        jcbSocNom.addItemListener(this);
        jcbSocApell.addItemListener(this);
        jcbSocDni.addItemListener(this);
        jcbSocCode.addItemListener(this);
        jcbSocMail.addItemListener(this);
        jcbSocDir.addItemListener(this);
        jcbSocPostal.addItemListener(this);
        jcbSocCity.addItemListener(this);
        jcbSocProvin.addItemListener(this);
        jcbSocPobla.addItemListener(this);
        jcbSocTelCasa.addItemListener(this);
        jcbSocTelMovil.addItemListener(this);
        jcbSocFechaAlt.addItemListener(this);
        jcbSocFechaVig.addItemListener(this);
        socIIFfoto.addJcombListener(this);
        jcbSocCred.addItemListener(this);
        jcbSocPuntos.addItemListener(this);
        jcbSocRepre1.addItemListener(this);
        jcbSocRepre2.addItemListener(this);
        jcbMTit.addItemListener(this);
        jcbMDir.addItemListener(this);
        jcbMUrlTr.addItemListener(this);
        jcbMProta.addItemListener(this);
        jcbMProdu.addItemListener(this);
        jcbMDuraMin.addItemListener(this);
        jcbMSinop.addItemListener(this);
        jcbMClasif.addItemListener(this);
        jcbMAnyo.addItemListener(this);
        jcbMCatg.addItemListener(this);
        movIIFport.addJcombListener(this);
        jcbMFLanz.addItemListener(this);
        jcbMFEstr.addItemListener(this);
        jcbMCodeb.addItemListener(this);
        jcbMFormt.addItemListener(this);
        jcbGTit.addItemListener(this);
        jcbGUrlTr.addItemListener(this);
        jcbGProdu.addItemListener(this);
        jcbGCatg.addItemListener(this);
        jcbGCodeb.addItemListener(this);
        gamIIFport.addJcombListener(this);
        jcbGAnyo.addItemListener(this);
        jcbGSinop.addItemListener(this);
        jcbGClasif.addItemListener(this);
        jcbGFLanz.addItemListener(this);
        jcbGFEstr.addItemListener(this);
        jcbGFormt.addItemListener(this);
    }
    
    private boolean isValidSocios(){
        try{
            credPred=Float.parseFloat(txtCredPred.getText());
        }catch(NumberFormatException nfe){
            credPred=-1;
        }
        if(credPred<0){
            prWin.showError("El valor de credito predeterminado es invalido");
        }        
        try{
            puntosPred=Integer.parseInt(txtPuntosPred.getText());
        }catch(NumberFormatException nfe){
            puntosPred=-1;
        }
        if(puntosPred<0){
            prWin.showError("El valor de puntos predeterminado es invalido");
        }        
        if(jcbSocNom.getSelectedIndex()==0 || jcbSocApell.getSelectedIndex()==0   || jcbSocDni.getSelectedIndex()==0
                || jcbSocCode.getSelectedIndex()==0 || jcbSocDir.getSelectedIndex()==0){
            prWin.showError("Debe especificar los valores obligatorios");
            return false;
        }
        if(socIIFdocs!=null){
            for(ImporterImageField imprdoc: socIIFdocs){
                if(!imprdoc.isNameValid()){
                    prWin.showError("Debe especificar los nombres de los documentos (7 a 155 caracteres)");
                    return false;
                }
            }
        }
        return true;
    }
    
    private boolean isValidMovies(){
        if(jcbMTit.getSelectedIndex()==0 || jcbMSinop.getSelectedIndex()==0
                || jcbMClasif.getSelectedIndex()==0){
            prWin.showError("Debe especificar los valores obligatorios");
            return false;
        }
        return true;
    }
    
    private boolean isValidGames(){
        if(jcbGTit.getSelectedIndex()==0 || jcbGSinop.getSelectedIndex()==0 || jcbGClasif.getSelectedIndex()==0){
            prWin.showError("Debe especificar los valores obligatorios");
            return false;
        }
        return true;
    }
    
    private void launchImport(){
        PerformingTask hilo=new PerformingTask();
        lblImporting.setBusy(true);
        replacePanel(6);
        setEnabled(false);
        /*if(destiny==1){
            stopImport(importSocios());
        }else if(destiny==2){
            stopImport(importMovies());
        }else if(destiny==3){
            stopImport(importGames());
        }*/
        hilo.execute();
    }
    
    private void stopImport(int success){
        lblImporting.setBusy(false);
        showResults(success);
        replacePanel(7);
        setEnabled(true);
    }
        
    private int importSocios(){
        VideoupCustomers newCstmr;
        ArrayList<VideoupBaseEntity> lstEnts;
        VideoupPuntos pnts;
        VideoupAutrz agre1;
        VideoupAutrz agre2;
        VideoupCimgs foto;
        VideoupDocs aDoc;
        String[] data;
        String warnn;
        byte[] img;
        String photo;
        String entVal;
        String[] entValidate;
        float tCred;
        int apliPuntos, counter;
        Date fecha;
        outing=new ArrayList<Object[]>(); // 1=ok, -1 error, 0=ignore, 2=warning
        lstEnts=new ArrayList<VideoupBaseEntity>();
        try{
            csvRead=new CsvReader(path2file,sepr);
            imported=0; counter=0;
            while(csvRead.readRecord()){
                pnts=null;
                agre1=null;
                agre2=null;
                foto=null;
                warnn="";
                data=csvRead.getValues();
                counter++;
                try{
                    if(data[jcbSocDni.getSelectedIndex()-1].trim().equals("")){
                        warnn+="Registro de socio ["+data[jcbSocNom.getSelectedIndex()-1]+" "+data[jcbSocApell.getSelectedIndex()-1];
                        warnn+="] tiene "+(String)Globals.getConfig("dniDef")+" vacio ";
                    }
                    if(prWin.existsDni(data[jcbSocDni.getSelectedIndex()-1])){
                        outing.add(new Object[]{0,"Ignorando registro, ya existe DNI: ["+data[jcbSocDni.getSelectedIndex()-1]+"]"});
                        continue;
                    }
                    if(data[jcbSocNom.getSelectedIndex()-1].trim().equals("")){
                        outing.add(new Object[]{-1,"Error, registro con Codigo de socio vacio"});
                        continue;
                    }else if(prWin.existsCodeSoc(data[jcbSocCode.getSelectedIndex()-1])){
                        outing.add(new Object[]{0,"Ignorando registro, ya existe Codigo de socio: ["+data[jcbSocCode.getSelectedIndex()-1]+"]"});
                        continue;
                    }
                    apliPuntos=0;
                    newCstmr=new VideoupCustomers();
                    newCstmr.setName(data[jcbSocNom.getSelectedIndex()-1]);
                    newCstmr.setApplldos(data[jcbSocApell.getSelectedIndex()-1]);
                    newCstmr.setDni(data[jcbSocDni.getSelectedIndex()-1]);
                    newCstmr.setCodCst(data[jcbSocCode.getSelectedIndex()-1]);
                    if(jcbSocMail.getSelectedIndex()>0){
                        newCstmr.setEmail(data[jcbSocMail.getSelectedIndex()-1]);
                    }
                    newCstmr.setAddr(data[jcbSocDir.getSelectedIndex()-1]);
                    if(jcbSocPostal.getSelectedIndex()>0){
                        newCstmr.setCodp(data[jcbSocPostal.getSelectedIndex()-1]);
                    }
                    if(jcbSocCity.getSelectedIndex()>0){
                        newCstmr.setCity(data[jcbSocCity.getSelectedIndex()-1]);
                    }
                    if(jcbSocProvin.getSelectedIndex()>0){
                        newCstmr.setCity(data[jcbSocProvin.getSelectedIndex()-1]);
                    }
                    if(jcbSocPobla.getSelectedIndex()>0){
                        newCstmr.setCity(data[jcbSocPobla.getSelectedIndex()-1]);
                    }
                    if(jcbSocTelCasa.getSelectedIndex()>0){
                        newCstmr.setTelHome(data[jcbSocTelCasa.getSelectedIndex()-1]);
                    }
                    if(jcbSocTelMovil.getSelectedIndex()>0){
                        newCstmr.setTelMovil(data[jcbSocTelMovil.getSelectedIndex()-1]);
                    }
                    if(jcbSocCred.getSelectedIndex()>0){
                        try{
                            tCred=Float.parseFloat(data[jcbSocCred.getSelectedIndex()-1]);
                        }catch(NumberFormatException nfe){ tCred=-1; }
                        if(tCred<0){
                            warnn+="Valor de credito invalido: ["+data[jcbSocCred.getSelectedIndex()-1]+"] se cambiara a 0.00 ";
                            tCred=0f;
                        }
                        newCstmr.setCredito(tCred);
                    }else{ newCstmr.setCredito(credPred); }
                    if(jcbSocPuntos.getSelectedIndex()>0){
                        try{
                            apliPuntos=Integer.parseInt(data[jcbSocPuntos.getSelectedIndex()-1]);
                        }catch(NumberFormatException nfe){ apliPuntos=-1; }
                        if(apliPuntos<0){
                            outing.add(new Object[]{-1,"Valor de puntos invalido: ["+data[jcbSocCred.getSelectedIndex()-1]+"]"});
                            continue;
                        }
                    }else if(puntosPred>0){ apliPuntos=puntosPred; }
                    if(apliPuntos>0){
                        pnts=new VideoupPuntos(apliPuntos,today,dateNextMonth,null,newCstmr);
                        newCstmr.addVideoupPuntos(pnts);
                    }
                    if(jcbSocRepre1.getSelectedIndex()>0){
                        agre1=new VideoupAutrz(data[jcbSocRepre1.getSelectedIndex()-1]);
                        agre1.setIdct(newCstmr);
                        newCstmr.addVideoupAutrz(agre1);
                    }
                    if(jcbSocRepre2.getSelectedIndex()>0){
                        agre2=new VideoupAutrz(data[jcbSocRepre2.getSelectedIndex()-1]);
                        agre2.setIdct(newCstmr);
                        newCstmr.addVideoupAutrz(agre2);
                    }
                    if(jcbSocFechaAlt.getSelectedIndex()>0){
                        fecha=getDate(data[jcbSocFechaAlt.getSelectedIndex()-1]);
                        if(fecha==null){
                            fecha=today;
                            warnn+="No se pudo interpretar fecha: ["+data[jcbSocFechaAlt.getSelectedIndex()-1]+"] usando predeterminada ";
                        }
                    }else if(jxdAltaStatic.getDate()!=null){
                        fecha=jxdAltaStatic.getDate();
                    }else{ fecha=today; }
                    newCstmr.setFAlta(fecha);
                    if(jcbSocFechaVig.getSelectedIndex()>0){
                        fecha=getDate(data[jcbSocFechaVig.getSelectedIndex()-1]);
                        if(fecha==null){
                            fecha=dateNextMonth;
                            warnn+="No se pudo interpretar fecha: ["+data[jcbSocFechaVig.getSelectedIndex()-1]+"] usando predeterminada ";
                        }
                    }else if(jxdVigenStatic.getDate()!=null){
                        fecha=jxdVigenStatic.getDate();
                    }else{ fecha=dateNextMonth; }
                    newCstmr.setFVigen(fecha);
                }catch(ArrayIndexOutOfBoundsException aie){
                    outing.add(new Object[]{-1,"Registro mal formado, ignorando registro numero: "+counter+
                            (data.length>0?" ["+data[0]+((data.length>1?" , "+data[1]:""))+"]":"")+" no se encuentran todos los campos requeridos"});
                    continue;
                }
                entVal=newCstmr.validate2Import();
                if(entVal!=null){
                    entValidate=entVal.split("_");
                    if(entValidate[0].equals("-1")){
                        outing.add(new Object[]{-1,"Datos invalidos, ignorando registro numero: "+counter+
                            (data.length>0?" ["+data[0]+((data.length>1?" , "+data[1]:""))+"]":"")+entValidate[1]});
                        continue;
                    }else if(entValidate[0].equals("0")){
                        warnn+=entValidate[1];
                    }
                }
                lstEnts.clear();
                lstEnts.add(newCstmr);
                if(pnts!=null){ lstEnts.add(pnts); }
                if(agre1!=null){
                    entVal=agre1.validate2Import();
                    if(entVal!=null){
                        entValidate=entVal.split("_");
                        if(entValidate[0].equals("-1")){
                            outing.add(new Object[]{-1,"Datos invalidos, ignorando registro numero: "+counter+
                                    (data.length>0?" ["+data[0]+((data.length>1?" , "+data[1]:""))+"]":"")+entValidate[1]});
                            continue;
                        }else if(entValidate[0].equals("0")){
                            warnn+=entValidate[1];
                        }
                    }
                    lstEnts.add(agre1);
                }
                if(agre2!=null){
                    entVal=agre2.validate2Import();
                    if(entVal!=null){
                        entValidate=entVal.split("_");
                        if(entValidate[0].equals("-1")){
                            outing.add(new Object[]{-1,"Datos invalidos, ignorando registro numero: "+counter+
                                    (data.length>0?" ["+data[0]+((data.length>1?" , "+data[1]:""))+"]":"")+entValidate[1]});
                            continue;
                        }else if(entValidate[0].equals("0")){
                            warnn+=entValidate[1];
                        }
                    }
                    lstEnts.add(agre2);
                }
                if(prWin.saveRecords(lstEnts)){
                    // if photo save it
                    if(socIIFfoto.isReady2Use()){
                        lstEnts.clear();
                        foto=new VideoupCimgs();
                        photo=data[socIIFfoto.getIndexColumn()-1];
                        if(socIIFfoto.getType()==1){
                            foto.setFoto(photo.getBytes());
                        }else{
                            if(socIIFfoto.getType()==3){
                                photo=socIIFfoto.getFFPath(photo);
                            }
                            img=getImageFromFile(photo);
                            if(img==null){
                                warnn+="No se pudo obtener archivo: ["+photo+"] ";
                                foto=null;
                            }else{ foto.setFoto(img); }
                        }
                        if(foto!=null){
                            foto.setIdrl(newCstmr.getId());
                            lstEnts.add(foto);
                            if(!prWin.saveRecords(lstEnts)){
                                warnn+="No se pudo guardar la foto ";
                            }
                        }
                    }
                    if(socIIFdocs!=null){
                        lstEnts.clear();
                        for(ImporterImageField doc: socIIFdocs){
                            if(doc.isReady2Use()){
                                aDoc=new VideoupDocs();
                                photo=data[socIIFfoto.getIndexColumn()-1];
                                if(doc.getType()==1){
                                    aDoc.setDocimg(photo.getBytes());
                                }else{
                                    if(doc.getType()==3){
                                        photo=doc.getFFPath(photo);
                                    }
                                    img=getImageFromFile(photo);
                                    if(img==null){
                                        warnn+="No se pudo obtener archivo: ["+photo+"] ";
                                        aDoc=null;
                                    }else{ aDoc.setDocimg(img); }
                                }
                                if(aDoc!=null){
                                    aDoc.setIdct(newCstmr.getId());
                                    aDoc.setDocname(doc.genInputName());
                                    lstEnts.add(aDoc);
                                }
                            }
                        }
                        if(!lstEnts.isEmpty() && !prWin.saveRecords(lstEnts)){
                            warnn+="No se pudo guardar documento(s)";
                        }
                    }
                    // inform result
                    if(warnn.length()>0){
                        outing.add(new Object[]{2,warnn});
                    }else{
                        imported++;
                    }
                }else{
                    outing.add(new Object[]{-1,"Error: ["+prWin.getError()+"]"});
                }
            }
        }catch(IOException ex){
            prWin.showError("Error: "+ex.getMessage());
        }finally{
            if(csvRead!=null){ csvRead.close(); }
        }
        return imported;
    }
    
    private int importMovies(){
        VideoupMovies newMovie;
        ArrayList<VideoupBaseEntity> lstEnts;
        VideoupFormts formt;
        VideoupBcodes bcode;
        VideoupCatgs cate;
        VideoupMimgs caratula;
        VideoupCtprrentas ctgPreNomr=prWin.getCtprr(true);
        VideoupCtprrentas ctgPreEstr=prWin.getCtprr(false);
        String[] data;
        String warnn;
        String entVal;
        String[] entValidate;
        byte[] img;
        String portada;
        Date fecha;
        int duramin, counter, anyo;
        if(ctgPreNomr==null || ctgPreEstr==null){
            prWin.showError("Error al cargar catalogos de costos");
        }
        outing=new ArrayList<Object[]>(); // 1=ok, -1 error, 0=ignore, 2=warning
        lstEnts=new ArrayList<VideoupBaseEntity>();
        try{
            csvRead=new CsvReader(path2file,sepr);
            imported=0; counter=0;
            while(csvRead.readRecord()){
                warnn="";
                formt=null;
                bcode=null;
                cate=null;
                caratula=null;
                data=csvRead.getValues();
                counter++;
                try{
                    try{
                        if(jcbMDuraMin.getSelectedIndex()>0){
                            duramin=Integer.parseInt(data[jcbMDuraMin.getSelectedIndex()-1]);
                        }else{ duramin=0; }
                    }catch(NumberFormatException nfe){
                        warnn+="Campo duracion invalido (0), corregir manualmente para: ["+data[jcbMTit.getSelectedIndex()-1]+"] ";
                        duramin=0;
                    }
                    if(jcbMCodeb.getSelectedIndex()>0 && prWin.existsMCodeBar(data[jcbMCodeb.getSelectedIndex()-1])){
                        outing.add(new Object[]{0,"Ignorando registro, ya existe Codigo de barras: ["+data[jcbMCodeb.getSelectedIndex()-1]+"]"});
                        continue;
                    }
                    if(jcbMFormt.getSelectedIndex()>0 && 
                            txtExcludesFormts.getText().toLowerCase().indexOf(data[jcbMFormt.getSelectedIndex()-1].toLowerCase())>=0){
                        outing.add(new Object[]{0,"Ignorando registro con formato: ["+data[jcbMFormt.getSelectedIndex()-1]+"]"});
                        continue;
                    }
                    if(jcbMFormt.getSelectedIndex()>0 && 
                            txtExcludesFormts.getText().toLowerCase().indexOf(data[jcbMFormt.getSelectedIndex()-1].toLowerCase())>=0){
                        outing.add(new Object[]{0,"Ignorando registro con formato: ["+data[jcbMFormt.getSelectedIndex()-1]+"]"});
                        continue;
                    }
                    newMovie=new VideoupMovies();
                    newMovie.setTitulo(data[jcbMTit.getSelectedIndex()-1]);
                    newMovie.setSinopsis(data[jcbMSinop.getSelectedIndex()-1]);
                    newMovie.setClasif(data[jcbMClasif.getSelectedIndex()-1]);
                    newMovie.setDmin(duramin);
                    if(jcbMCatg.getSelectedIndex()>0){
                        cate=prWin.getCatg(data[jcbMCatg.getSelectedIndex()-1]);
                    }
                    if(cate!=null){ newMovie.setCatg(cate); }
                    if(jcbMDir.getSelectedIndex()>0){
                        newMovie.setDirector(data[jcbMDir.getSelectedIndex()-1]);
                    }
                    if(jcbMUrlTr.getSelectedIndex()>0){
                        newMovie.setTrailerUrl(data[jcbMUrlTr.getSelectedIndex()-1]);
                    }
                    if(jcbMProta.getSelectedIndex()>0){
                        newMovie.setPrgistas(data[jcbMProta.getSelectedIndex()-1]);
                    }
                    if(jcbMProdu.getSelectedIndex()>0){
                        newMovie.setProcmpy(data[jcbMProdu.getSelectedIndex()-1]);
                    }
                    if(jcbMAnyo.getSelectedIndex()>0){
                        try{
                            anyo=Integer.parseInt(data[jcbMAnyo.getSelectedIndex()-1]);
                            newMovie.setAnyo(anyo);
                        }catch(NumberFormatException nfe){
                            warnn+="Año invalido (0), corregir manualmente para: ["+data[jcbMTit.getSelectedIndex()-1]+"] ";
                        }
                    }
                    if(jcbMCodeb.getSelectedIndex()>0){
                        bcode=new VideoupBcodes(data[jcbMCodeb.getSelectedIndex()-1],false,0,"Disponible");
                        if(jcbMFormt.getSelectedIndex()>0){
                            formt=prWin.getFormt(data[jcbMFormt.getSelectedIndex()-1]);
                            if(formt==null){
                                warnn+="No se pudo obtener formato ["+data[jcbMFormt.getSelectedIndex()-1]+"] para: ["+data[jcbMTit.getSelectedIndex()-1]+"] ";
                            }else{ bcode.setFrmt(formt); }
                        }
                        newMovie.addVideoupBcodes(bcode);
                        bcode.addVideoupMovies(newMovie);
                    }
                    if(jcbMFLanz.getSelectedIndex()>0){
                        fecha=getDate(data[jcbMFLanz.getSelectedIndex()-1]);
                        if(fecha==null){
                            fecha=today;
                            warnn+="No se pudo interpretar fecha: ["+data[jcbMFLanz.getSelectedIndex()-1]+"] usando predeterminada ";
                        }
                    }else if(jdtMLanz.getDate()!=null){
                        fecha=jdtMLanz.getDate();
                    }else{ fecha=today; }
                    newMovie.setLdate(fecha);
                    if(jcbMFEstr.getSelectedIndex()>0){
                        fecha=getDate(data[jcbMFEstr.getSelectedIndex()-1]);
                        if(fecha==null){
                            fecha=dateNextMonth;
                            warnn+="No se pudo interpretar fecha: ["+data[jcbMFEstr.getSelectedIndex()-1]+"] usando predeterminada ";
                        }
                    }else if(jdtMEstr.getDate()!=null){
                        fecha=jdtMEstr.getDate();
                    }else{ fecha=dateNextMonth; }
                    newMovie.setEstrenoUntil(fecha);
                    if(fecha.after(today)){
                        newMovie.setIdcpr(ctgPreNomr);
                    }else{
                        newMovie.setIdcpr(ctgPreEstr);
                    }
                }catch(ArrayIndexOutOfBoundsException aie){
                    outing.add(new Object[]{-1,"Registro mal formado, ignorando registro numero: "+counter+
                            (data.length>0?" ["+data[0]+((data.length>1?" , "+data[1]:""))+"]":"")+" no se encuentran todos los campos requeridos"});
                    continue;
                }
                entVal=newMovie.validate2Import();
                if(entVal!=null){
                    entValidate=entVal.split("_");
                    if(entValidate[0].equals("-1")){
                        outing.add(new Object[]{-1,"Datos invalidos, ignorando registro numero: "+counter+
                            (data.length>0?" ["+data[0]+((data.length>1?" , "+data[1]:""))+"]":"")+entValidate[1]});
                        continue;
                    }else if(entValidate[0].equals("0")){
                        warnn+=entValidate[1];
                    }
                }
                lstEnts.clear();
                lstEnts.add(newMovie);
                if(formt!=null){
                    entVal=formt.validate2Import();
                    if(entVal!=null){
                        entValidate=entVal.split("_");
                        if(entValidate[0].equals("-1")){
                            outing.add(new Object[]{-1,"Datos invalidos, ignorando registro numero: "+counter+
                                    (data.length>0?" ["+data[0]+((data.length>1?" , "+data[1]:""))+"]":"")+entValidate[1]});
                            continue;
                        }else if(entValidate[0].equals("0")){
                            warnn+=entValidate[1];
                        }
                    }
                    lstEnts.add(formt);
                }
                if(cate!=null){
                    entVal=cate.validate2Import();
                    if(entVal!=null){
                        entValidate=entVal.split("_");
                        if(entValidate[0].equals("-1")){
                            outing.add(new Object[]{-1,"Datos invalidos, ignorando registro numero: "+counter+
                                    (data.length>0?" ["+data[0]+((data.length>1?" , "+data[1]:""))+"]":"")+entValidate[1]});
                            continue;
                        }else if(entValidate[0].equals("0")){
                            warnn+=entValidate[1];
                        }
                    } 
                    lstEnts.add(cate);
                }
                if(bcode!=null){
                    entVal=bcode.validate2Import();
                    if(entVal!=null){
                        entValidate=entVal.split("_");
                        if(entValidate[0].equals("-1")){
                            outing.add(new Object[]{-1,"Datos invalidos, ignorando registro numero: "+counter+
                                    (data.length>0?" ["+data[0]+((data.length>1?" , "+data[1]:""))+"]":"")+entValidate[1]});
                            continue;
                        }else if(entValidate[0].equals("0")){
                            warnn+=entValidate[1];
                        }
                    } 
                    lstEnts.add(bcode);
                }
                if(prWin.saveRecords(lstEnts)){
                    if(movIIFport.isReady2Use()){
                        lstEnts.clear();
                        caratula=new VideoupMimgs();
                        portada=data[movIIFport.getIndexColumn()-1];
                        if(movIIFport.getType()==1){
                            caratula.setImg(portada.getBytes());
                        }else{
                            if(movIIFport.getType()==3){
                                portada=movIIFport.getFFPath(portada);
                            }
                            img=getImageFromFile(portada);
                            if(img==null){
                                warnn+="No se pudo obtener archivo: ["+portada+"] ";
                                caratula=null;
                            }else{ caratula.setImg(img); }
                        }
                        if(caratula!=null){
                            caratula.setIdrl(newMovie.getId());
                            lstEnts.add(caratula);
                            if(!prWin.saveRecords(lstEnts)){
                                warnn+="No se pudo guardar la imagen de portada ";
                            }
                        }
                    }
                    if(warnn.length()>0){
                        outing.add(new Object[]{2,warnn});
                    }else{
                        imported++;
                    }
                }else{
                    outing.add(new Object[]{-1,"Error: ["+prWin.getError()+"]"});
                }
            }
        }catch(IOException ex){
            prWin.showError("Error: "+ex.getMessage());
        }finally{
            if(csvRead!=null){ csvRead.close(); }
        }
        return imported;
    }
    
    private int importGames(){
        VideoupGames newGame;
        ArrayList<VideoupBaseEntity> lstEnts;
        VideoupFormts formt;
        VideoupBcodes bcode;
        VideoupCatgs cate;
        VideoupGimgs caratula;
        VideoupCtprrentas ctgPreNomr=prWin.getCtprr(true);
        VideoupCtprrentas ctgPreEstr=prWin.getCtprr(false);
        String[] data;
        String entVal;
        String[] entValidate;
        String warnn;
        byte[] img;
        String portada;
        Date fecha;
        int counter, anyo;
        if(ctgPreNomr==null || ctgPreEstr==null){
            prWin.showError("Error al cargar catalogos de costos");
        }
        outing=new ArrayList<Object[]>(); // 1=ok, -1 error, 0=ignore, 2=warning
        lstEnts=new ArrayList<VideoupBaseEntity>();
        try{
            csvRead=new CsvReader(path2file,sepr);
            imported=0; counter=0;
            while(csvRead.readRecord()){
                warnn="";
                formt=null;
                bcode=null;
                cate=null;
                data=csvRead.getValues();
                counter++;
                try{
                    if(jcbGCodeb.getSelectedIndex()>0 && prWin.existsMCodeBar(data[jcbGCodeb.getSelectedIndex()-1])){
                        outing.add(new Object[]{0,"Ignorando registro, ya existe Codigo de barras: ["+data[jcbGCodeb.getSelectedIndex()-1]+"]"});
                        continue;
                    }
                    if(jcbGFormt.getSelectedIndex()>0 && 
                            txtGExcludesFormts.getText().toLowerCase().indexOf(data[jcbGFormt.getSelectedIndex()-1].toLowerCase())>=0){
                        outing.add(new Object[]{0,"Ignorando registro con formato: ["+data[jcbGFormt.getSelectedIndex()-1]+"]"});
                        continue;
                    }
                    if(jcbGCatg.getSelectedIndex()>0){
                        cate=prWin.getCatg(data[jcbGCatg.getSelectedIndex()-1]);
                    }
                    newGame=new VideoupGames();
                    newGame.setTitulo(data[jcbGTit.getSelectedIndex()-1]);
                    newGame.setSinopsis(data[jcbGSinop.getSelectedIndex()-1]);
                    newGame.setClasif(data[jcbGClasif.getSelectedIndex()-1]);
                    if(cate!=null){ newGame.setCatg(cate); }
                    if(jcbGUrlTr.getSelectedIndex()>0){
                        newGame.setTrailerUrl(data[jcbGUrlTr.getSelectedIndex()-1]);
                    }
                    if(jcbGProdu.getSelectedIndex()>0){
                        newGame.setProcmpy(data[jcbGProdu.getSelectedIndex()-1]);
                    }
                    if(jcbGAnyo.getSelectedIndex()>0){
                        try{
                            anyo=Integer.parseInt(data[jcbGAnyo.getSelectedIndex()-1]);
                            newGame.setAnyo(anyo);
                        }catch(NumberFormatException nfe){
                            warnn+="Año invalido (0), corregir manualmente para: ["+data[jcbMTit.getSelectedIndex()-1]+"] ";
                        }
                    }
                    if(jcbGCodeb.getSelectedIndex()>0){
                        bcode=new VideoupBcodes(data[jcbGCodeb.getSelectedIndex()-1],false,0,"Disponible");
                        if(jcbGFormt.getSelectedIndex()>0){
                            formt=prWin.getFormt(data[jcbGFormt.getSelectedIndex()-1]);
                            if(formt==null){
                                warnn+="No se pudo obtener formato ["+data[jcbGFormt.getSelectedIndex()-1]+"] para: ["+data[jcbGTit.getSelectedIndex()-1]+"] ";
                            }else{ bcode.setFrmt(formt); }
                        }
                        newGame.addVideoupBcodes(bcode);
                        bcode.addVideoupGames(newGame);
                    }
                    if(jcbGFLanz.getSelectedIndex()>0){
                        fecha=getDate(data[jcbGFLanz.getSelectedIndex()-1]);
                        if(fecha==null){
                            fecha=today;
                            warnn+="No se pudo interpretar fecha: ["+data[jcbGFLanz.getSelectedIndex()-1]+"] usando predeterminada ";
                        }
                    }else if(jdtGLanz.getDate()!=null){
                        fecha=jdtGLanz.getDate();
                    }else{ fecha=today; }
                    newGame.setLdate(fecha);
                    if(jcbGFEstr.getSelectedIndex()>0){
                        fecha=getDate(data[jcbGFEstr.getSelectedIndex()-1]);
                        if(fecha==null){
                            fecha=dateNextMonth;
                            warnn+="No se pudo interpretar fecha: ["+data[jcbGFEstr.getSelectedIndex()-1]+"] usando predeterminada ";
                        }
                    }else if(jdtGEstr.getDate()!=null){
                        fecha=jdtGEstr.getDate();
                    }else{ fecha=dateNextMonth; }
                    newGame.setEstrenoUntil(fecha);
                    if(fecha.after(today)){
                        newGame.setIdcpr(ctgPreNomr);
                    }else{
                        newGame.setIdcpr(ctgPreEstr); 
                    }
                }catch(ArrayIndexOutOfBoundsException aie){
                    outing.add(new Object[]{-1,"Registro mal formado, ignorando registro numero: "+counter+
                            (data.length>0?" ["+data[0]+((data.length>1?" , "+data[1]:""))+"]":"")+" no se encuentran todos los campos requeridos"});
                    continue;
                }
                entVal=newGame.validate2Import();
                if(entVal!=null){
                    entValidate=entVal.split("_");
                    if(entValidate[0].equals("-1")){
                        outing.add(new Object[]{-1,"Datos invalidos, ignorando registro numero: "+counter+
                            (data.length>0?" ["+data[0]+((data.length>1?" , "+data[1]:""))+"]":"")+entValidate[1]});
                        continue;
                    }else if(entValidate[0].equals("0")){
                        warnn+=entValidate[1];
                    }
                }
                lstEnts.clear();
                lstEnts.add(newGame);
                if(formt!=null){
                    entVal=formt.validate2Import();
                    if(entVal!=null){
                        entValidate=entVal.split("_");
                        if(entValidate[0].equals("-1")){
                            outing.add(new Object[]{-1,"Datos invalidos, ignorando registro numero: "+counter+
                                    (data.length>0?" ["+data[0]+((data.length>1?" , "+data[1]:""))+"]":"")+entValidate[1]});
                            continue;
                        }else if(entValidate[0].equals("0")){
                            warnn+=entValidate[1];
                        }
                    }
                    lstEnts.add(formt);
                }
                if(cate!=null){
                    entVal=cate.validate2Import();
                    if(entVal!=null){
                        entValidate=entVal.split("_");
                        if(entValidate[0].equals("-1")){
                            outing.add(new Object[]{-1,"Datos invalidos, ignorando registro numero: "+counter+
                                    (data.length>0?" ["+data[0]+((data.length>1?" , "+data[1]:""))+"]":"")+entValidate[1]});
                            continue;
                        }else if(entValidate[0].equals("0")){
                            warnn+=entValidate[1];
                        }
                    } 
                    lstEnts.add(cate);
                }
                if(bcode!=null){
                    entVal=bcode.validate2Import();
                    if(entVal!=null){
                        entValidate=entVal.split("_");
                        if(entValidate[0].equals("-1")){
                            outing.add(new Object[]{-1,"Datos invalidos, ignorando registro numero: "+counter+
                                    (data.length>0?" ["+data[0]+((data.length>1?" , "+data[1]:""))+"]":"")+entValidate[1]});
                            continue;
                        }else if(entValidate[0].equals("0")){
                            warnn+=entValidate[1];
                        }
                    } 
                    lstEnts.add(bcode);
                }
                if(prWin.saveRecords(lstEnts)){
                    if(gamIIFport.isReady2Use()){
                        lstEnts.clear();
                        caratula=new VideoupGimgs();
                        portada=data[gamIIFport.getIndexColumn()-1];
                        if(gamIIFport.getType()==1){
                            caratula.setImg(portada.getBytes());
                        }else{
                            if(gamIIFport.getType()==3){
                                portada=gamIIFport.getFFPath(portada);
                            }
                            img=getImageFromFile(portada);
                            if(img==null){
                                warnn+="No se pudo obtener archivo: ["+portada+"] ";
                                caratula=null;
                            }else{ caratula.setImg(img); }
                        }
                        if(caratula!=null){
                            caratula.setIdrl(newGame.getId());
                            lstEnts.add(caratula);
                            if(!prWin.saveRecords(lstEnts)){
                                warnn+="No se pudo guardar la imagen de portada ";
                            }
                        }
                    }
                    if(warnn.length()>0){
                        outing.add(new Object[]{2,warnn});
                    }else{
                        imported++;
                    }
                }else{
                    outing.add(new Object[]{-1,"Error: ["+prWin.getError()+"]"});
                }
            }
        }catch(IOException ex){
            prWin.showError("Error: "+ex.getMessage());
        }finally{
            if(csvRead!=null){ csvRead.close(); }
        }
        return imported;
    }
    
    private Date getDate(String sDate){
        Calendar caler=Calendar.getInstance();
        String[] tks;
        int day;
        int month;
        int year;
        sDate=sDate.split(" ")[0];
        tks=sDate.split( (sDate.indexOf("/")>=0?"/":"-") );
        if(tks.length!=3){
            return null;
        }
        try{
            if(tks[0].length()>2){
                year=Integer.parseInt(tks[0]);
                day=Integer.parseInt(tks[2]);
            }else{
                year=Integer.parseInt(tks[2]);
                day=Integer.parseInt(tks[0]);
            }
            month=Integer.parseInt(tks[1]);
        }catch(NumberFormatException nfe){
            return null;
        }
        caler.set(year,month,day);
        return caler.getTime();
    }
    
    private byte[] getImageFromFile(String fpath){
        File imgfile=new File(fpath);
        Image img;
        boolean isImage=true;
        byte[] blob;
        FileInputStream fileInpStr;
        try{
            img=ImageIO.read(imgfile);
            if(img==null){ isImage=false; }
        }catch(IOException ex){ isImage=false; }
        if(!isImage){
            return null;
        }
        blob=new byte[(int)imgfile.length()];
        try{
            fileInpStr=new FileInputStream(imgfile);
            fileInpStr.read(blob);
        }catch (FileNotFoundException ex){
            return null;
        }catch (IOException ex){
            return null;
        }
        return blob;
    }
    
    private void showResults(int successfully){
        //outing  1=ok, -1 error, 0=ignore, 2=warning
        int warns=0;
        int errs=0;
        int ignrs=0;
        Object[] obj;
        lblImportSuccess.setText(""+successfully);
        for(int h=0;h<outing.size();h++){
            obj=outing.get(h);
            if(Integer.parseInt(obj[0].toString())<0){
                pnlErrorList.add(new JLabel(obj[1].toString()));
                errs++;
            }else if(Integer.parseInt(obj[0].toString())==0){
                pnlIgnores.add(new JLabel(obj[1].toString()));
                ignrs++;
            }else{
                pnlWarningList.add(new JLabel(obj[1].toString()));
                warns++;
            }
        }
        lblNErrs.setText(""+errs);
        lblIgnores.setText(""+ignrs);
        lblNWrans.setText(""+warns);
    }
    
    @Override
    public void itemStateChanged(ItemEvent e) {
        int sel=((JComboBox)e.getSource()).getSelectedIndex()-1;
        if(sel>=0){
            previewTable.setColumnSelectionInterval(sel, sel);
            previewTable.scrollRectToVisible(previewTable.getCellRect(0,sel,true));
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

        pnlSociosA = new javax.swing.JPanel();
        jcbSocDni = new javax.swing.JComboBox();
        lblDni = new javax.swing.JLabel();
        jcbSocApell = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jcbSocNom = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jcbSocCode = new javax.swing.JComboBox();
        jcbSocMail = new javax.swing.JComboBox();
        jcbSocDir = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jcbSocPostal = new javax.swing.JComboBox();
        jcbSocCity = new javax.swing.JComboBox();
        jcbSocProvin = new javax.swing.JComboBox();
        jcbSocPobla = new javax.swing.JComboBox();
        jcbSocTelCasa = new javax.swing.JComboBox();
        jcbSocTelMovil = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jcbSocFechaAlt = new javax.swing.JComboBox();
        jcbSocFechaVig = new javax.swing.JComboBox();
        jLabel19 = new javax.swing.JLabel();
        jcbSocCred = new javax.swing.JComboBox();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jcbSocPuntos = new javax.swing.JComboBox();
        jcbSocRepre1 = new javax.swing.JComboBox();
        jcbSocRepre2 = new javax.swing.JComboBox();
        btn2ImportS = new javax.swing.JButton();
        btnCancel3 = new javax.swing.JButton();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        txtCredPred = new javax.swing.JTextField();
        txtPuntosPred = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        lblAltaPred = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jxdAltaStatic = new org.jdesktop.swingx.JXDatePicker();
        lblVigenPred = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jxdVigenStatic = new org.jdesktop.swingx.JXDatePicker();
        pnlFoto = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        pnlDocs = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        btnAddDoc = new javax.swing.JButton();
        pnlMoviesA = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jcbMTit = new javax.swing.JComboBox();
        jcbMDir = new javax.swing.JComboBox();
        jcbMUrlTr = new javax.swing.JComboBox();
        jcbMProta = new javax.swing.JComboBox();
        jcbMProdu = new javax.swing.JComboBox();
        jcbMDuraMin = new javax.swing.JComboBox();
        jcbMSinop = new javax.swing.JComboBox();
        jcbMClasif = new javax.swing.JComboBox();
        jcbMCatg = new javax.swing.JComboBox();
        jdtMLanz = new org.jdesktop.swingx.JXDatePicker();
        jdtMEstr = new org.jdesktop.swingx.JXDatePicker();
        btnCancel4 = new javax.swing.JButton();
        btnImportMovies = new javax.swing.JButton();
        jLabel45 = new javax.swing.JLabel();
        lblMLanzPred = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        lblMEstrPred = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jcbMFLanz = new javax.swing.JComboBox();
        jcbMFEstr = new javax.swing.JComboBox();
        jLabel46 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jcbMCodeb = new javax.swing.JComboBox();
        jcbMFormt = new javax.swing.JComboBox();
        jLabel55 = new javax.swing.JLabel();
        txtExcludesFormts = new javax.swing.JTextField();
        jLabel59 = new javax.swing.JLabel();
        jcbMAnyo = new javax.swing.JComboBox();
        pnlMPortada = new javax.swing.JPanel();
        pnlGamesA = new javax.swing.JPanel();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jcbGTit = new javax.swing.JComboBox();
        jcbGUrlTr = new javax.swing.JComboBox();
        jcbGProdu = new javax.swing.JComboBox();
        jcbGClasif = new javax.swing.JComboBox();
        jcbGCatg = new javax.swing.JComboBox();
        jdtGLanz = new org.jdesktop.swingx.JXDatePicker();
        jdtGEstr = new org.jdesktop.swingx.JXDatePicker();
        btnCancel5 = new javax.swing.JButton();
        btnImportGames = new javax.swing.JButton();
        jLabel63 = new javax.swing.JLabel();
        lblGLanzPred = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        lblGEstrPred = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        jcbGFLanz = new javax.swing.JComboBox();
        jcbGFEstr = new javax.swing.JComboBox();
        jLabel66 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jcbGCodeb = new javax.swing.JComboBox();
        jcbGFormt = new javax.swing.JComboBox();
        jLabel52 = new javax.swing.JLabel();
        jcbGSinop = new javax.swing.JComboBox();
        jLabel56 = new javax.swing.JLabel();
        txtGExcludesFormts = new javax.swing.JTextField();
        jLabel68 = new javax.swing.JLabel();
        jcbGAnyo = new javax.swing.JComboBox();
        pnlGPortada = new javax.swing.JPanel();
        pnlPickFile = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        btnPickFile = new javax.swing.JButton();
        btnCancel2 = new javax.swing.JButton();
        txtSeparador = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jscPVTable = new javax.swing.JScrollPane();
        previewTable = new javax.swing.JTable();
        pnlProcessing = new javax.swing.JPanel();
        lblImporting = new org.jdesktop.swingx.JXBusyLabel();
        pnlResults = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        lblImportSuccess = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        lblNErrs = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        lblNWrans = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        pnlErrorList = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        pnlWarningList = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        lblIgnores = new javax.swing.JLabel();
        label = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        pnlIgnores = new javax.swing.JPanel();
        pnlChoose = new javax.swing.JPanel();
        btnSocs = new javax.swing.JButton();
        btnMovs = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        btnGames = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        pnlSociosA.setLayout(new java.awt.GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jcbSocDni, gridBagConstraints);

        lblDni.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDni.setText("* DNI");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(lblDni, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jcbSocApell, gridBagConstraints);

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("* Apellidos");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jLabel6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jcbSocNom, gridBagConstraints);

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("* Nombre");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jLabel5, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Elija correspondencia entre columnas (tabla inferior) y campos de socio:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jLabel4, gridBagConstraints);

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("* Codigo de socio");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jLabel7, gridBagConstraints);

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Email");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jLabel8, gridBagConstraints);

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("* Direccion");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jLabel9, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jcbSocCode, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jcbSocMail, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jcbSocDir, gridBagConstraints);

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Codigo postal");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jLabel10, gridBagConstraints);

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Ciudad");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jLabel11, gridBagConstraints);

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("Provincia");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jLabel12, gridBagConstraints);

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("Poblacion");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jLabel13, gridBagConstraints);

        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel14.setText("Telefono casa");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jLabel14, gridBagConstraints);

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText("Telefono movil");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jLabel15, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jcbSocPostal, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jcbSocCity, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jcbSocProvin, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jcbSocPobla, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jcbSocTelCasa, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jcbSocTelMovil, gridBagConstraints);

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("* Fecha alta");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jLabel16, gridBagConstraints);

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel17.setText("* Fecha vigencia");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jLabel17, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jcbSocFechaAlt, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jcbSocFechaVig, gridBagConstraints);

        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText("Credito");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jLabel19, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jcbSocCred, gridBagConstraints);

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel20.setText("Puntos");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jLabel20, gridBagConstraints);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel21.setText("Representante 1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jLabel21, gridBagConstraints);

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel22.setText("Representante 2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jLabel22, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jcbSocPuntos, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jcbSocRepre1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jcbSocRepre2, gridBagConstraints);

        btn2ImportS.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btn2ImportS.setText("Siguiente");
        btn2ImportS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn2ImportSActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(btn2ImportS, gridBagConstraints);

        btnCancel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnCancel3.setText("Cancelar");
        btnCancel3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancel3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(btnCancel3, gridBagConstraints);

        jLabel23.setText("* Datos obligatorios (en fechas puede usar predeterminado o valor especifico)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jLabel23, gridBagConstraints);

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel24.setText("Predeterminado");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jLabel24, gridBagConstraints);

        txtCredPred.setColumns(3);
        txtCredPred.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtCredPred.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(txtCredPred, gridBagConstraints);

        txtPuntosPred.setColumns(3);
        txtPuntosPred.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPuntosPred.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(txtPuntosPred, gridBagConstraints);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel25.setText("Predeterminado:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jLabel25, gridBagConstraints);

        lblAltaPred.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblAltaPred.setText("Prederminado:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(lblAltaPred, gridBagConstraints);

        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel26.setText("Usar valor especifico:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jLabel26, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jxdAltaStatic, gridBagConstraints);

        lblVigenPred.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblVigenPred.setText("Predeterminado:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(lblVigenPred, gridBagConstraints);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel27.setText("Usar valor especifico:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jLabel27, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlSociosA.add(jxdVigenStatic, gridBagConstraints);

        pnlFoto.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pnlSociosA.add(pnlFoto, gridBagConstraints);

        pnlDocs.setLayout(new javax.swing.BoxLayout(pnlDocs, javax.swing.BoxLayout.PAGE_AXIS));

        jLabel18.setText("Imagenes de documentos");
        jPanel1.add(jLabel18);

        btnAddDoc.setText("Agregar ");
        btnAddDoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddDocActionPerformed(evt);
            }
        });
        jPanel1.add(btnAddDoc);

        pnlDocs.add(jPanel1);

        jScrollPane4.setViewportView(pnlDocs);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pnlSociosA.add(jScrollPane4, gridBagConstraints);

        pnlMoviesA.setLayout(new java.awt.GridBagLayout());

        jLabel29.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setText("Eliga correspondencia entre columnas (tabla inferior) y campos de pelicula");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlMoviesA.add(jLabel29, gridBagConstraints);

        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel32.setText("*Titulo");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlMoviesA.add(jLabel32, gridBagConstraints);

        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel34.setText("Director");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlMoviesA.add(jLabel34, gridBagConstraints);

        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel36.setText("Url trailer");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlMoviesA.add(jLabel36, gridBagConstraints);

        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel37.setText("Protagonistas");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlMoviesA.add(jLabel37, gridBagConstraints);

        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel38.setText("*Sinopsis");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlMoviesA.add(jLabel38, gridBagConstraints);

        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel39.setText("*Fecha lanzamiento");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlMoviesA.add(jLabel39, gridBagConstraints);

        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel40.setText("Productora");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlMoviesA.add(jLabel40, gridBagConstraints);

        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel41.setText("Duracion en minutos");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlMoviesA.add(jLabel41, gridBagConstraints);

        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel42.setText("*Clasificacion");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlMoviesA.add(jLabel42, gridBagConstraints);

        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel43.setText("Genero");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlMoviesA.add(jLabel43, gridBagConstraints);

        jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel44.setText("Estreno hasta");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlMoviesA.add(jLabel44, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlMoviesA.add(jcbMTit, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlMoviesA.add(jcbMDir, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlMoviesA.add(jcbMUrlTr, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlMoviesA.add(jcbMProta, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlMoviesA.add(jcbMProdu, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlMoviesA.add(jcbMDuraMin, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlMoviesA.add(jcbMSinop, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlMoviesA.add(jcbMClasif, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlMoviesA.add(jcbMCatg, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlMoviesA.add(jdtMLanz, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlMoviesA.add(jdtMEstr, gridBagConstraints);

        btnCancel4.setText("Cancelar");
        btnCancel4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancel4ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlMoviesA.add(btnCancel4, gridBagConstraints);

        btnImportMovies.setText("Siguiente");
        btnImportMovies.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportMoviesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlMoviesA.add(btnImportMovies, gridBagConstraints);

        jLabel45.setText("* Datos obligatorios (en fechas puede usar predeterminado o valor especifico)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlMoviesA.add(jLabel45, gridBagConstraints);

        lblMLanzPred.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblMLanzPred.setText("Predeterminado");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pnlMoviesA.add(lblMLanzPred, gridBagConstraints);

        jLabel47.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel47.setText("Usar valor especifico");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pnlMoviesA.add(jLabel47, gridBagConstraints);

        lblMEstrPred.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblMEstrPred.setText("Predeterminado");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pnlMoviesA.add(lblMEstrPred, gridBagConstraints);

        jLabel49.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel49.setText("Usar valor especifico");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pnlMoviesA.add(jLabel49, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlMoviesA.add(jcbMFLanz, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlMoviesA.add(jcbMFEstr, gridBagConstraints);

        jLabel46.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel46.setText("Codigo de barras");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlMoviesA.add(jLabel46, gridBagConstraints);

        jLabel48.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel48.setText("Formato");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlMoviesA.add(jLabel48, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlMoviesA.add(jcbMCodeb, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlMoviesA.add(jcbMFormt, gridBagConstraints);

        jLabel55.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel55.setText("Excluir");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlMoviesA.add(jLabel55, gridBagConstraints);

        txtExcludesFormts.setText("psp, ps2, ps3, wii, xbox");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlMoviesA.add(txtExcludesFormts, gridBagConstraints);

        jLabel59.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel59.setText("Año");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlMoviesA.add(jLabel59, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlMoviesA.add(jcbMAnyo, gridBagConstraints);

        pnlMPortada.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pnlMoviesA.add(pnlMPortada, gridBagConstraints);

        pnlGamesA.setLayout(new java.awt.GridBagLayout());

        jLabel50.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel50.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel50.setText("Eliga correspondencia entre columnas (tabla inferior) y campos de videojuego");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlGamesA.add(jLabel50, gridBagConstraints);

        jLabel51.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel51.setText("*Titulo");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlGamesA.add(jLabel51, gridBagConstraints);

        jLabel54.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel54.setText("Url trailer");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlGamesA.add(jLabel54, gridBagConstraints);

        jLabel57.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel57.setText("*Fecha lanzamiento");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlGamesA.add(jLabel57, gridBagConstraints);

        jLabel58.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel58.setText("Productora");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlGamesA.add(jLabel58, gridBagConstraints);

        jLabel60.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel60.setText("*Clasificacion");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlGamesA.add(jLabel60, gridBagConstraints);

        jLabel61.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel61.setText("Genero");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlGamesA.add(jLabel61, gridBagConstraints);

        jLabel62.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel62.setText("Estreno hasta");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlGamesA.add(jLabel62, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlGamesA.add(jcbGTit, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlGamesA.add(jcbGUrlTr, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlGamesA.add(jcbGProdu, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlGamesA.add(jcbGClasif, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlGamesA.add(jcbGCatg, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlGamesA.add(jdtGLanz, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlGamesA.add(jdtGEstr, gridBagConstraints);

        btnCancel5.setText("Cancelar");
        btnCancel5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancel5ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlGamesA.add(btnCancel5, gridBagConstraints);

        btnImportGames.setText("Siguiente");
        btnImportGames.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportGamesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlGamesA.add(btnImportGames, gridBagConstraints);

        jLabel63.setText("* Datos obligatorios (en fechas puede usar predeterminado o valor especifico)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlGamesA.add(jLabel63, gridBagConstraints);

        lblGLanzPred.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblGLanzPred.setText("Predeterminado");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pnlGamesA.add(lblGLanzPred, gridBagConstraints);

        jLabel64.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel64.setText("Usar valor especifico");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pnlGamesA.add(jLabel64, gridBagConstraints);

        lblGEstrPred.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblGEstrPred.setText("Predeterminado");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pnlGamesA.add(lblGEstrPred, gridBagConstraints);

        jLabel65.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel65.setText("Usar valor especifico");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pnlGamesA.add(jLabel65, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlGamesA.add(jcbGFLanz, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlGamesA.add(jcbGFEstr, gridBagConstraints);

        jLabel66.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel66.setText("Codigo de barras");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlGamesA.add(jLabel66, gridBagConstraints);

        jLabel67.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel67.setText("Formato");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlGamesA.add(jLabel67, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlGamesA.add(jcbGCodeb, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlGamesA.add(jcbGFormt, gridBagConstraints);

        jLabel52.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel52.setText("*Sinopsis");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlGamesA.add(jLabel52, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlGamesA.add(jcbGSinop, gridBagConstraints);

        jLabel56.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel56.setText("Excluir");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlGamesA.add(jLabel56, gridBagConstraints);

        txtGExcludesFormts.setText("dvd, blueray, blue ray");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlGamesA.add(txtGExcludesFormts, gridBagConstraints);

        jLabel68.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel68.setText("Año");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlGamesA.add(jLabel68, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        pnlGamesA.add(jcbGAnyo, gridBagConstraints);

        pnlGPortada.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pnlGamesA.add(pnlGPortada, gridBagConstraints);

        pnlPickFile.setLayout(new java.awt.GridBagLayout());

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Eliga archivo origen de los datos:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 4;
        pnlPickFile.add(jLabel2, gridBagConstraints);

        btnPickFile.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnPickFile.setText("Buscar...");
        btnPickFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPickFileActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 4;
        pnlPickFile.add(btnPickFile, gridBagConstraints);

        btnCancel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnCancel2.setText("Cancelar");
        btnCancel2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancel2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 4;
        pnlPickFile.add(btnCancel2, gridBagConstraints);

        txtSeparador.setColumns(5);
        txtSeparador.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSeparador.setText(",");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlPickFile.add(txtSeparador, gridBagConstraints);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Separador:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.ipady = 2;
        pnlPickFile.add(jLabel3, gridBagConstraints);

        jscPVTable.setOpaque(false);
        jscPVTable.setPreferredSize(new java.awt.Dimension(200, 151));

        previewTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        previewTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        previewTable.setAutoscrolls(false);
        previewTable.setColumnSelectionAllowed(true);
        previewTable.setOpaque(false);
        jscPVTable.setViewportView(previewTable);

        pnlProcessing.setLayout(new java.awt.BorderLayout());

        lblImporting.setText("Importando datos");
        pnlProcessing.add(lblImporting, java.awt.BorderLayout.CENTER);

        pnlResults.setLayout(new java.awt.GridBagLayout());

        jLabel28.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setText("Resultados de la operacion:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 5);
        pnlResults.add(jLabel28, gridBagConstraints);

        lblImportSuccess.setBackground(new java.awt.Color(0, 142, 0));
        lblImportSuccess.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblImportSuccess.setForeground(new java.awt.Color(243, 243, 243));
        lblImportSuccess.setText("N");
        lblImportSuccess.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 5);
        pnlResults.add(lblImportSuccess, gridBagConstraints);

        jLabel30.setBackground(new java.awt.Color(0, 142, 0));
        jLabel30.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(243, 243, 243));
        jLabel30.setText("Registros importados con exito");
        jLabel30.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 5);
        pnlResults.add(jLabel30, gridBagConstraints);

        lblNErrs.setBackground(new java.awt.Color(235, 24, 0));
        lblNErrs.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblNErrs.setForeground(new java.awt.Color(236, 236, 236));
        lblNErrs.setText("N");
        lblNErrs.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 5);
        pnlResults.add(lblNErrs, gridBagConstraints);

        jLabel31.setBackground(new java.awt.Color(235, 24, 0));
        jLabel31.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(236, 236, 236));
        jLabel31.setText("Errores al tratar de importar");
        jLabel31.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 5);
        pnlResults.add(jLabel31, gridBagConstraints);

        lblNWrans.setBackground(new java.awt.Color(255, 179, 60));
        lblNWrans.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblNWrans.setText("N");
        lblNWrans.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 5);
        pnlResults.add(lblNWrans, gridBagConstraints);

        jLabel33.setBackground(new java.awt.Color(255, 179, 60));
        jLabel33.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel33.setText("Registros se importaron con alerta");
        jLabel33.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 5);
        pnlResults.add(jLabel33, gridBagConstraints);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(417, 153));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(799, 166));

        pnlErrorList.setLayout(new javax.swing.BoxLayout(pnlErrorList, javax.swing.BoxLayout.PAGE_AXIS));
        jScrollPane1.setViewportView(pnlErrorList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 5);
        pnlResults.add(jScrollPane1, gridBagConstraints);

        jScrollPane2.setMinimumSize(new java.awt.Dimension(417, 153));
        jScrollPane2.setPreferredSize(new java.awt.Dimension(799, 166));

        pnlWarningList.setLayout(new javax.swing.BoxLayout(pnlWarningList, javax.swing.BoxLayout.PAGE_AXIS));
        jScrollPane2.setViewportView(pnlWarningList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 5);
        pnlResults.add(jScrollPane2, gridBagConstraints);

        btnClose.setText("Cerrar");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 0);
        pnlResults.add(btnClose, gridBagConstraints);

        lblIgnores.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblIgnores.setText("N");
        lblIgnores.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 5);
        pnlResults.add(lblIgnores, gridBagConstraints);

        label.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        label.setText("Registros ignorados al importar");
        label.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 5);
        pnlResults.add(label, gridBagConstraints);

        jScrollPane3.setMinimumSize(new java.awt.Dimension(417, 156));
        jScrollPane3.setPreferredSize(new java.awt.Dimension(799, 166));

        pnlIgnores.setLayout(new javax.swing.BoxLayout(pnlIgnores, javax.swing.BoxLayout.PAGE_AXIS));
        jScrollPane3.setViewportView(pnlIgnores);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 5);
        pnlResults.add(jScrollPane3, gridBagConstraints);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Importar datos CSV");

        pnlChoose.setLayout(new java.awt.GridBagLayout());

        btnSocs.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnSocs.setText("Socios >");
        btnSocs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSocsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 4;
        pnlChoose.add(btnSocs, gridBagConstraints);

        btnMovs.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnMovs.setText("Peliculas >");
        btnMovs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMovsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 4;
        pnlChoose.add(btnMovs, gridBagConstraints);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Importar datos de:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 4;
        pnlChoose.add(jLabel1, gridBagConstraints);

        btnGames.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnGames.setText("Videojuegos >");
        btnGames.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGamesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 4;
        pnlChoose.add(btnGames, gridBagConstraints);

        btnCancel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnCancel.setText("Cancelar ");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 4;
        pnlChoose.add(btnCancel, gridBagConstraints);

        getContentPane().add(pnlChoose, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        cancelAndClose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnSocsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSocsActionPerformed
        replacePanel(2);
        destiny=1;
    }//GEN-LAST:event_btnSocsActionPerformed

    private void btnMovsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMovsActionPerformed
        replacePanel(2);
        destiny=2;
    }//GEN-LAST:event_btnMovsActionPerformed

    private void btnGamesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGamesActionPerformed
        replacePanel(2);
        destiny=3;
    }//GEN-LAST:event_btnGamesActionPerformed

    private void btnCancel2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancel2ActionPerformed
        cancelAndClose();
    }//GEN-LAST:event_btnCancel2ActionPerformed

    private void btnPickFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPickFileActionPerformed
        pickAndLoadFile();
    }//GEN-LAST:event_btnPickFileActionPerformed

    private void btnCancel3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancel3ActionPerformed
        cancelAndClose();
    }//GEN-LAST:event_btnCancel3ActionPerformed

    private void btn2ImportSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn2ImportSActionPerformed
        if(isValidSocios()){
            launchImport();
        }
    }//GEN-LAST:event_btn2ImportSActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        cancelAndClose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnCancel4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancel4ActionPerformed
        cancelAndClose();
    }//GEN-LAST:event_btnCancel4ActionPerformed

    private void btnImportMoviesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportMoviesActionPerformed
        if(isValidMovies()){
            launchImport();
        }
    }//GEN-LAST:event_btnImportMoviesActionPerformed

    private void btnCancel5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancel5ActionPerformed
        cancelAndClose();
    }//GEN-LAST:event_btnCancel5ActionPerformed

    private void btnImportGamesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportGamesActionPerformed
        if(isValidGames()){
            launchImport();
        }
    }//GEN-LAST:event_btnImportGamesActionPerformed

    private void btnAddDocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddDocActionPerformed
        ImporterImageField newDoc;
        if(socIIFdocs==null){
            socIIFdocs=new ArrayList<ImporterImageField>();
        }
        newDoc=new ImporterImageField("Documento "+socIIFdocs.size(),true);
        newDoc.copyFielsItems(jcbSocNom);
        socIIFdocs.add(newDoc);
        pnlDocs.add(newDoc); jScrollPane4.validate();
    }//GEN-LAST:event_btnAddDocActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn2ImportS;
    private javax.swing.JButton btnAddDoc;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnCancel2;
    private javax.swing.JButton btnCancel3;
    private javax.swing.JButton btnCancel4;
    private javax.swing.JButton btnCancel5;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnGames;
    private javax.swing.JButton btnImportGames;
    private javax.swing.JButton btnImportMovies;
    private javax.swing.JButton btnMovs;
    private javax.swing.JButton btnPickFile;
    private javax.swing.JButton btnSocs;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JComboBox jcbGAnyo;
    private javax.swing.JComboBox jcbGCatg;
    private javax.swing.JComboBox jcbGClasif;
    private javax.swing.JComboBox jcbGCodeb;
    private javax.swing.JComboBox jcbGFEstr;
    private javax.swing.JComboBox jcbGFLanz;
    private javax.swing.JComboBox jcbGFormt;
    private javax.swing.JComboBox jcbGProdu;
    private javax.swing.JComboBox jcbGSinop;
    private javax.swing.JComboBox jcbGTit;
    private javax.swing.JComboBox jcbGUrlTr;
    private javax.swing.JComboBox jcbMAnyo;
    private javax.swing.JComboBox jcbMCatg;
    private javax.swing.JComboBox jcbMClasif;
    private javax.swing.JComboBox jcbMCodeb;
    private javax.swing.JComboBox jcbMDir;
    private javax.swing.JComboBox jcbMDuraMin;
    private javax.swing.JComboBox jcbMFEstr;
    private javax.swing.JComboBox jcbMFLanz;
    private javax.swing.JComboBox jcbMFormt;
    private javax.swing.JComboBox jcbMProdu;
    private javax.swing.JComboBox jcbMProta;
    private javax.swing.JComboBox jcbMSinop;
    private javax.swing.JComboBox jcbMTit;
    private javax.swing.JComboBox jcbMUrlTr;
    private javax.swing.JComboBox jcbSocApell;
    private javax.swing.JComboBox jcbSocCity;
    private javax.swing.JComboBox jcbSocCode;
    private javax.swing.JComboBox jcbSocCred;
    private javax.swing.JComboBox jcbSocDir;
    private javax.swing.JComboBox jcbSocDni;
    private javax.swing.JComboBox jcbSocFechaAlt;
    private javax.swing.JComboBox jcbSocFechaVig;
    private javax.swing.JComboBox jcbSocMail;
    private javax.swing.JComboBox jcbSocNom;
    private javax.swing.JComboBox jcbSocPobla;
    private javax.swing.JComboBox jcbSocPostal;
    private javax.swing.JComboBox jcbSocProvin;
    private javax.swing.JComboBox jcbSocPuntos;
    private javax.swing.JComboBox jcbSocRepre1;
    private javax.swing.JComboBox jcbSocRepre2;
    private javax.swing.JComboBox jcbSocTelCasa;
    private javax.swing.JComboBox jcbSocTelMovil;
    private org.jdesktop.swingx.JXDatePicker jdtGEstr;
    private org.jdesktop.swingx.JXDatePicker jdtGLanz;
    private org.jdesktop.swingx.JXDatePicker jdtMEstr;
    private org.jdesktop.swingx.JXDatePicker jdtMLanz;
    private javax.swing.JScrollPane jscPVTable;
    private org.jdesktop.swingx.JXDatePicker jxdAltaStatic;
    private org.jdesktop.swingx.JXDatePicker jxdVigenStatic;
    private javax.swing.JLabel label;
    private javax.swing.JLabel lblAltaPred;
    private javax.swing.JLabel lblDni;
    private javax.swing.JLabel lblGEstrPred;
    private javax.swing.JLabel lblGLanzPred;
    private javax.swing.JLabel lblIgnores;
    private javax.swing.JLabel lblImportSuccess;
    private org.jdesktop.swingx.JXBusyLabel lblImporting;
    private javax.swing.JLabel lblMEstrPred;
    private javax.swing.JLabel lblMLanzPred;
    private javax.swing.JLabel lblNErrs;
    private javax.swing.JLabel lblNWrans;
    private javax.swing.JLabel lblVigenPred;
    private javax.swing.JPanel pnlChoose;
    private javax.swing.JPanel pnlDocs;
    private javax.swing.JPanel pnlErrorList;
    private javax.swing.JPanel pnlFoto;
    private javax.swing.JPanel pnlGPortada;
    private javax.swing.JPanel pnlGamesA;
    private javax.swing.JPanel pnlIgnores;
    private javax.swing.JPanel pnlMPortada;
    private javax.swing.JPanel pnlMoviesA;
    private javax.swing.JPanel pnlPickFile;
    private javax.swing.JPanel pnlProcessing;
    private javax.swing.JPanel pnlResults;
    private javax.swing.JPanel pnlSociosA;
    private javax.swing.JPanel pnlWarningList;
    private javax.swing.JTable previewTable;
    private javax.swing.JTextField txtCredPred;
    private javax.swing.JTextField txtExcludesFormts;
    private javax.swing.JTextField txtGExcludesFormts;
    private javax.swing.JTextField txtPuntosPred;
    private javax.swing.JTextField txtSeparador;
    // End of variables declaration//GEN-END:variables

    public class PerformingTask extends SwingWorker<Boolean, Void>{
        private int success;
        @Override
        protected Boolean doInBackground() throws Exception{
            boolean result=true;
            if(destiny==1){
                success=importSocios();
            }else if(destiny==2){
                success=importMovies();
            }else if(destiny==3){
                success=importGames();
            }
            return result;
        }
        @Override
        protected void done(){
            stopImport(success);
        }
    }
}

