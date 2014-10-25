/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.views.modgames;

import com.videoup.controllers.Controller;
import com.videoup.entities.VideoupCatgs;
import com.videoup.entities.VideoupCtprrentas;
import com.videoup.entities.VideoupFormts;
import com.videoup.utils.DBUtils;
import com.videoup.utils.Globals;
import com.videoup.utils.Validator;
import com.videoup.utils.reps.EntListd;
import com.videoup.views.utils.PrintDiag;
import com.videoup.views.utils.ViewLista;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Pedro
 */
public class Lista extends ViewLista{

    /**
     * Creates new form Lista
     */
    public Lista(Controller ctrl) {
        super(ctrl);
        TreeMap<String,String> hltrs=new TreeMap<String,String>();
        initComponents();
        buildTableModel();
        mainQry="select vg.idca,titulo,procmpy,clasif,vct.catg, GROUP_CONCAT(vf.frmt SEPARATOR ', ') AS formt,";
        mainQry+="GROUP_CONCAT(cpy.barcode SEPARATOR ', ') AS bcode, GROUP_CONCAT(cpy.status SEPARATOR ', ') AS status from videoup_games vg left join ";
        mainQry+="videoup_catgs vct on vg.catg=vct.idcg left join videoup_bcdgam rgc on rgc.idca=vg.idca ";
        mainQry+="left join videoup_bcodes cpy on rgc.idbc=cpy.idbc left join videoup_formts vf on cpy.frmt=vf.idcf";
        orderby=" group by vg.idca order by vg.idca desc";
        countQry="select count(*) from videoup_games vg";
        reloadTable(0);
        loadCatgs();
        loadClasifs();
        loadFormats();
        loadAlqCatgs();
        pnlFilter.setVisible(false);
        pnlListEdit.setVisible(false);
        pnlNorth.add(pnlFilter,BorderLayout.CENTER);
        hltrs.put("hlGreen","Disponible");
        hltrs.put("hlBlue","En alquiler");
        hltrs.put("hlOrange","Apartado");
        hltrs.put("hlGray","Vendida");
        hltrs.put("hlRed","No disponible");
        builHighLighters(hltrs);
    }

    private void loadCatgs(){
        List<VideoupCatgs> lista;
        lista=loadList("From VideoupCatgs",null,0,false);
        jcfCatg.removeAllItems(); jcfCatg.addItem("--");
        jcbGenero.removeAllItems(); jcbGenero.addItem("--");
        if(lista!=null){
            for(VideoupCatgs row: lista){
                jcfCatg.addItem(row);
                jcbGenero.addItem(row);
            }
        }
    }

    private void loadClasifs(){
        List<Object> lista;
        lista=loadList("select distinct clasif From VideoupGames",null,0,false);
        jcfClasif.removeAllItems();
        jcfClasif.addItem("--");
        if(lista!=null){
            for(Object row: lista){
                jcfClasif.addItem(row);
            }
        }
    }

    private void loadFormats(){
        List<VideoupFormts> lista;
        lista=loadList("select distinct(fr) From VideoupFormts fr",null,0,false);
        jcbFormat.removeAllItems(); jcbFormat.addItem("--");
        jcbFormat1.removeAllItems(); jcbFormat1.addItem("--");
        if(lista!=null){
            for(VideoupFormts row: lista){
                jcbFormat.addItem(row);
                jcbFormat1.addItem(row);
            }
        }
    }
    
    private void loadAlqCatgs(){
        List<VideoupCtprrentas> lista;
        lista=loadList("From VideoupCtprrentas",null,0,false);
        jcbCatgAlq.removeAllItems(); jcbCatgAlq.addItem("--");
        jcbCtgPres.removeAllItems(); jcbCtgPres.addItem("--");
        if(lista!=null){
            for(VideoupCtprrentas row: lista){
                jcbCatgAlq.addItem(row);
                jcbCtgPres.addItem(row);
            }
        }
    }
    
    @Override
    protected void showList(String query,int pag){
        List<Object[]> lista;
        DefaultTableModel tmodel=((DefaultTableModel)tabla.getModel());
        Object[] rowArray;
        cellKey ck;
        lista=loadListUseSQL(query+whr+orderby,countQry,pag,true);
        if(lista==null){
            ctrl.showDialogErr("Error al cargar la lista: "+error);
            return;
        }
        for(int itr=tmodel.getRowCount();itr>0;itr--){
            tmodel.removeRow(itr-1);
        }
        for(Object[] row: lista){
            rowArray=new Object[6];
            ck=new cellKey(row[0],row[1]);            
            if(row[7]!=null){
                if( row[7].toString().toLowerCase().contains("disponible") ){
                    ck.addHighLighter("hlGreen",true);
                }else if( row[7].toString().toLowerCase().contains("en alquiler") ){
                    ck.addHighLighter("hlBlue",true);
                }else if( row[7].toString().toLowerCase().indexOf("apartad")>=0 ){
                    ck.addHighLighter("hlOrange",true);
                }else if( row[7].toString().toLowerCase().contains("vendida") ){
                    ck.addHighLighter("hlGray",true);
                }else{ ck.addHighLighter("hlRed",true); }
            }
            rowArray[0]=ck;
            rowArray[1]=row[2];
            rowArray[2]=row[3];
            rowArray[3]=row[4];
            rowArray[4]=row[5];
            rowArray[5]=row[6];
            tmodel.addRow(rowArray);
        }
        tabla.setModel(tmodel);
    }
    
    @Override
    protected void buildTableModel(){
        tabla.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {"Titulo", "Productora", "Clasificacion", "Genero","Formato(s)","Codigo(s)"}
        ){
            boolean[] canEdit = new boolean [] {false, false, false, false, false, false};
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {return canEdit [columnIndex];}
        });
        tabla.setColumnSelectionAllowed(false);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(220);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(150);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(110);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(160);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(160);
    }
    
    @Override
    protected void loadRecord(cellKey ckey){
        ctrl.loadFicha(ckey.getKey());
    }
    
    @Override
    protected void showFilter(){
        pnlFilter.setVisible(!pnlFilter.isVisible());
    }
    
    @Override
    protected int deleteSelectedRecords(){
        int[] sels=tabla.getSelectedRows();
        int[] ides=new int[sels.length];
        int selIdxModel;
        cellKey cell;
        String toDel2="delete from videoup_gimgs WHERE;";
        String toDel3="delete from videoup_games WHERE;";
        String where="";
        DefaultTableModel tmodel=((DefaultTableModel)tabla.getModel());
        int cnt;
        if(sels.length==0){ return 0; }
        for(cnt=0;cnt<sels.length;cnt++){
            selIdxModel=tabla.convertRowIndexToModel(sels[cnt]);
            cell=((cellKey)tmodel.getValueAt(selIdxModel,0));
            where+=(cnt==0?"":" or ")+"IDN="+cell.getKey();
            ides[cnt]=cell.getKey();
        }
        if(!DBUtils.deleteBCodes(where,false)){ error=DBUtils.getError(); return -1; }
        toDel2=toDel2.replaceAll("WHERE"," where "+where.replaceAll("IDN","idrl"));
        toDel3=toDel3.replaceAll("WHERE"," where "+where.replaceAll("IDN","idca"));
        if(genDAO.executeSQL(toDel2)<0){ error=genDAO.getError(); return -1; }
        if(genDAO.executeSQL(toDel3)<0){ error=genDAO.getError(); return -1; }
        for(int singId: ides){
            ctrl.try2CloseView(singId,true);
            ctrl.goToView(this.getId(),false);
        }
        return 0;
    }
    
    @Override
    protected void applyFilter(){
        String tit=txfTitulo.getText().trim();
        String prod=txfProd.getText().trim();
        String cla=jcfClasif.getSelectedItem().toString();
        Object frmt=jcbFormat.getSelectedItem();
        String fltBCode=txfCBar.getText().trim();
        Object catgAlq=jcbCtgPres.getSelectedItem();
        String estado=edoProd.getSelectedItem().toString();
        Object cat=jcfCatg.getSelectedItem();
        whr="";
        if(tit.length()>0){
            whr=buildLike4Where(tit,"titulo");
        }if(prod.length()>0){
            whr+=(whr.length()>0?fltUnion:"")+buildLike4Where(prod,"procmpy");
        }if(!cla.equals("--")){
            whr+=(whr.length()>0?fltUnion:"")+buildLike4Where(cla,"clasif");
        }if(!cat.equals("--")){
            whr+=(whr.length()>0?fltUnion:"")+"vg.catg="+((VideoupCatgs)cat).getIdcg();
        }if(fltBCode.length()>0){
            whr+=(whr.length()>0?fltUnion:"")+buildCompare4Where(fltBCode,"cpy.barcode","=");
        }if(frmt instanceof VideoupFormts){
            whr+=(whr.length()>0?fltUnion:"")+"vf.frmt='"+frmt+"'";
        }if(catgAlq instanceof VideoupCtprrentas){
            whr+=(whr.length()>0?fltUnion:"")+"vg.idcpr="+((VideoupCtprrentas)catgAlq).getIdcpr();
        }if(!estado.equals("Cualquiera")){
            whr+=(whr.length()>0?fltUnion:"")+"cpy.status='"+estado+"'";
        }
        if(whr.length()>0){
            whr=" where "+whr;
            countQry="select count(distinct vg.idca) from videoup_games vg left join videoup_catgs vct on ";
            countQry+="vg.catg=vct.idcg left join videoup_bcdgam rgc on rgc.idca=vg.idca left join videoup_bcodes ";
            countQry+="cpy on rgc.idbc=cpy.idbc left join videoup_formts vf on cpy.frmt=vf.idcf "+whr;
        }else{
            countQry="select count(*) from videoup_games vg";
        }
        reloadTable(0);
    }
    
    @Override
    protected void orderByColumn(int col, String ort){
        orderby=" group by vg.idca order by";
        if(col==0){
            orderby+=" titulo "+ort;
        }else if(col==1){
            orderby+=" procmpy "+ort;
        }else if(col==2){
            orderby+=" clasif "+ort;
        }else if(col==3){
            orderby+=" vct.catg "+ort;
        }else if(col==4){
            orderby+=" formt "+ort;
        }else if(col==5){
            orderby+=" bcode "+ort;
        }else{
            orderby+=" vg.idca desc";
        }
        reloadTable(currPage);
    }
    
    @Override
    public void setAndApplyFilter(String flt){
        txfTitulo.setText(flt);
        txfCBar.setText(flt);
        pnlFilter.setVisible(true);
        applyFilter();
    }
    
    private void showListEdit(boolean sh){
        pnlListEdit.setVisible(sh);
        validate();
    }
    
    private boolean validateEdit(){
        Validator vltor=new Validator();
        int year=Calendar.getInstance().get(Calendar.YEAR);
        if(rbtnIgnrVendbl.isSelected()){
            txtPrecioVen.setText("");
        }
        vltor.addDateField(jxDateLanz,"Fecha de lanzamiento",false,0);
        vltor.addDateField(jxDateEstr,"Fecha de estreno",false,0);
        vltor.addStringField(txClasif, "Clasificacion", false, 1, 5,0);
        vltor.addIntField(txYear, "Año", false, 1925, year, 0);
        vltor.addStringField(txProd, "Compañia productora", false, 5, 155,0);
        vltor.addDoubleField(txtPrecioVen,"Precio de venta",rbtnVendbl.isSelected(),1,9999,0);
        if(!vltor.validate()){
            ctrl.showDialogErr(vltor.getError());
            return false;
        }else if(!vltor.hasAtLeastOneValue() && jcbCatgAlq.getSelectedIndex()==0 && jcbGenero.getSelectedIndex()==0
                 && jcbFormat1.getSelectedIndex()==0 && rbtnIgnrVendbl.isSelected()){
            ctrl.showDialogErr("No ha indicado ningun cambio");
            return false;
        }
        return true;
    }
        
    @Override
    protected boolean updatePage(String ids,String idn){
        VideoupCtprrentas ctAlq;
        VideoupCatgs genero;
        VideoupFormts formato;
        Date lanz=jxDateLanz.getDate();
        Date estr=jxDateEstr.getDate();
        String clasif=txClasif.getText().trim();
        boolean igrnVendbl=rbtnIgnrVendbl.isSelected();
        boolean vendible=rbtnVendbl.isSelected();
        int year;
        String prod=txProd.getText().trim();
        float precio;
        String upd1="update videoup_games vg set";
        String upd2="update videoup_bcodes vb set";
        String ids2;
        try{ ctAlq=((VideoupCtprrentas)jcbCatgAlq.getSelectedItem()); }
        catch(ClassCastException cce){ ctAlq=null; }
        try{ genero=((VideoupCatgs)jcbGenero.getSelectedItem()); }
        catch(ClassCastException cce){ genero=null; }
        try{ formato=((VideoupFormts)jcbFormat1.getSelectedItem()); }
        catch(ClassCastException cce){ formato=null; }
        try{ precio=Float.parseFloat(txtPrecioVen.getText().trim()); }
        catch(NumberFormatException nfb){ precio=0f; }
        try{ year=Integer.parseInt(txYear.getText().trim()); }
        catch(NumberFormatException nfb){ year=0; }
        if(ctAlq!=null){ upd1+=" vg.idcpr="+ctAlq.getIdcpr(); }
        if(genero!=null){ upd1+=(upd1.endsWith("set")?"":", ")+" vg.catg="+genero.getIdcg(); }
        if(lanz!=null){ upd1+=(upd1.endsWith("set")?"":", ")+" vg.ldate='"+dte2SQL(lanz)+"'"; }
        if(estr!=null){ upd1+=(upd1.endsWith("set")?"":", ")+" vg.estreno_until='"+dte2SQL(estr)+"'"; }
        if(clasif.length()>0){ upd1+=(upd1.endsWith("set")?"":", ")+" vg.clasif='"+clasif+"'"; }
        if(year>0){ upd1+=(upd1.endsWith("set")?"":", ")+" vg.anyo="+year; }
        if(prod.length()>0){ upd1+=(upd1.endsWith("set")?"":", ")+" vg.procmpy='"+prod+"'"; }
        if(formato!=null){ upd2+=" vb.frmt="+formato.getIdcf(); }
        if(!igrnVendbl){
            if(precio>0 && vendible){
                upd2+=(upd2.endsWith("set")?"":", ")+" vb.pr_venta="+precio;
            }
            upd2+=(upd2.endsWith("set")?"":", ")+" vb.vendible="+(vendible?1:0);
        }
        ids=ids.replaceAll("(\\d+)",idn+"$1");
        if(!upd2.endsWith("set")){
            ids2=getIdsVb(ids);
            if(ids2==null){
                return false;
            }else if(ids2.length()>0){
                upd2+=" where "+ids2.replaceAll("(\\d+)","idbc=$1");
                if(executeSQL(upd2)<0){
                    return false;
                }
            }
        } if(!upd1.endsWith("set")){
            upd1+=" where "+ids;
            if(executeSQL(upd1)<0){
                return false;
            }
        }
        return true;
    }
    
    private String getIdsVb(String ids){
        String qry="select distinct vb.idbc from videoup_bcodes vb, ";
        qry+="videoup_bcdgam vr, videoup_games vg where vb.idbc=vr.idbc and vr.idca=vg.idca and ("+ids+")";
        return loadIdListUseSQL(qry,0,false);
    }
    
    private void updateWhUnion(boolean isand){
        if(isand){
            btnTgO.setSelected(false);
            fltUnion=" and ";
        }else{
            btnTgY.setSelected(false);
            fltUnion=" or ";
        }
    }
        
    @Override
    protected void send2Print(){
        printRecords(mainQry);
    }
    
    @Override
    protected void printNow(ArrayList fields){
        PrintDiag pDiag=new PrintDiag(getMainWindow(),true,"Video Juegos");
        pDiag.cargaReporte(getParams(), fields,5);
        pDiag.setVisible(true);
    }
    
    private Map<String,Object> getParams(){
        Map<String,Object> parametros = new HashMap<String,Object>();
        parametros.put("vclub",Globals.getConfig("nVclub"));
        parametros.put("dreport","Listado de Video Juegos");
        parametros.put("col1","Codigo");
        parametros.put("col2","Titulo");
        parametros.put("col3","Genero");
        parametros.put("col4","Clasificacion");
        parametros.put("col5","Formato");
        return parametros;
    }
    
    @Override
    protected ArrayList getListCampos(String query){      
        List<Object[]> lista;
        ArrayList listCampos=new ArrayList();
        EntListd campo;
        lista=loadListUseSQL(query+whr+orderby,null,0,false);
        if(lista==null){
            ctrl.showDialogErr("Error al concentrar datos");
            return null;
        }
        for(Object[] row: lista){
            campo=new EntListd( (row[6]!=null?row[6].toString():""),
                    (row[1]!=null?row[1].toString():""),(row[4]!=null?row[4].toString():""),
                    (row[3]!=null?row[3].toString():""),(row[5]!=null?row[5].toString():"") );
            listCampos.add(campo);
        }
        return listCampos;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlFilter = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        txfCBar = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jcbFormat = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txfTitulo = new javax.swing.JTextField();
        txfProd = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btnFilter = new javax.swing.JButton();
        jcfCatg = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jcfClasif = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        btnListEdit = new javax.swing.JToggleButton();
        btnTgO = new javax.swing.JToggleButton();
        btnTgY = new javax.swing.JToggleButton();
        jLabel18 = new javax.swing.JLabel();
        jcbCtgPres = new javax.swing.JComboBox();
        jLabel19 = new javax.swing.JLabel();
        edoProd = new javax.swing.JComboBox();
        pnlListEdit = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        btnEditList = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jcbCatgAlq = new javax.swing.JComboBox();
        jLabel14 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txClasif = new javax.swing.JTextField();
        txYear = new javax.swing.JTextField();
        txProd = new javax.swing.JTextField();
        jxDateLanz = new org.jdesktop.swingx.JXDatePicker();
        txtPrecioVen = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jcbGenero = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        jxDateEstr = new org.jdesktop.swingx.JXDatePicker();
        jcbFormat1 = new javax.swing.JComboBox();
        jLabel17 = new javax.swing.JLabel();
        rbtnVendbl = new javax.swing.JRadioButton();
        rbtnNVendbl = new javax.swing.JRadioButton();
        rbtnIgnrVendbl = new javax.swing.JRadioButton();
        btnGenCred = new javax.swing.JButton();
        btnsVendbl = new javax.swing.ButtonGroup();

        pnlFilter.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        pnlFilter.setLayout(new java.awt.BorderLayout());

        txfCBar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txfCBarKeyPressed(evt);
            }
        });

        jLabel5.setText("Codigo:");

        jLabel6.setText("Formato:");

        jLabel1.setText("Titulo:");

        txfTitulo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txfTituloKeyPressed(evt);
            }
        });

        txfProd.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txfProdKeyPressed(evt);
            }
        });

        jLabel2.setText("Productora");

        btnFilter.setText("Filtrar");
        btnFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterActionPerformed(evt);
            }
        });

        jLabel4.setText("Genero:");

        jLabel3.setText("Clasificacion:");

        btnListEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/videoup/imgs/listedit.png"))); // NOI18N
        btnListEdit.setToolTipText("Edicion grupal de registros");
        btnListEdit.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                btnListEditItemStateChanged(evt);
            }
        });

        btnTgO.setSelected(true);
        btnTgO.setText("O");
        btnTgO.setToolTipText("Cumplir al menos un criterio");
        btnTgO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTgOActionPerformed(evt);
            }
        });

        btnTgY.setText("&");
        btnTgY.setToolTipText("Cumplir todos los criterios");
        btnTgY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTgYActionPerformed(evt);
            }
        });

        jLabel18.setText("Catalogo alquiler:");

        jLabel19.setText("Estado:");

        edoProd.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Cualquiera", "Disponible", "Apartada para alquiler", "Apartada para venta", "En alquiler", "Vendida", "Dañada", "Sustraida", "Perdida" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcfClasif, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcfCatg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcbFormat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcbCtgPres, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnFilter))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txfCBar, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txfTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txfProd, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(edoProd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 124, Short.MAX_VALUE)
                        .addComponent(btnTgO)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnTgY)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnListEdit)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnTgY)
                                .addComponent(btnTgO))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel5)
                                .addComponent(txfCBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel1)
                                .addComponent(txfTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2)
                                .addComponent(txfProd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel19)
                                .addComponent(edoProd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jcfClasif, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)
                            .addComponent(jcfCatg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)
                            .addComponent(jcbFormat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnFilter)
                            .addComponent(jLabel18)
                            .addComponent(jcbCtgPres, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btnListEdit))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlFilter.add(jPanel1, java.awt.BorderLayout.NORTH);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Edicion grupal le permite aplicar valores a todos los registros de la lista actual.");

        btnEditList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/videoup/imgs/save.png"))); // NOI18N
        btnEditList.setToolTipText("Aplicar cambios");
        btnEditList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditListActionPerformed(evt);
            }
        });

        jLabel11.setText("Catalogo de alquiler:");

        jLabel12.setText("Genero:");

        jLabel14.setText("Año:");

        jLabel9.setText("Fecha lanzamiento:");

        jLabel10.setText("Precio venta:");

        jLabel15.setText("Clasificacion:");

        txClasif.setColumns(11);

        txYear.setColumns(11);

        txProd.setColumns(11);

        txtPrecioVen.setColumns(7);

        jLabel8.setText("Productora:");

        jLabel16.setText("Formato:");

        jLabel13.setText("Fecha estreno:");

        jLabel17.setText("En venta:");

        btnsVendbl.add(rbtnVendbl);
        rbtnVendbl.setText("Si");

        btnsVendbl.add(rbtnNVendbl);
        rbtnNVendbl.setText("No");

        btnsVendbl.add(rbtnIgnrVendbl);
        rbtnIgnrVendbl.setSelected(true);
        rbtnIgnrVendbl.setText("Estado actual");

        btnGenCred.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/videoup/imgs/rSocios32.png"))); // NOI18N
        btnGenCred.setToolTipText("Generar credenciales de socios listados");
        btnGenCred.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenCredActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlListEditLayout = new javax.swing.GroupLayout(pnlListEdit);
        pnlListEdit.setLayout(pnlListEditLayout);
        pnlListEditLayout.setHorizontalGroup(
            pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlListEditLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(pnlListEditLayout.createSequentialGroup()
                        .addGroup(pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlListEditLayout.createSequentialGroup()
                                .addGroup(pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jcbCatgAlq, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jcbGenero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txProd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txClasif, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(pnlListEditLayout.createSequentialGroup()
                                        .addGroup(pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtPrecioVen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(pnlListEditLayout.createSequentialGroup()
                                                .addComponent(rbtnVendbl)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(rbtnNVendbl)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(rbtnIgnrVendbl)))
                                        .addGap(0, 303, Short.MAX_VALUE))
                                    .addGroup(pnlListEditLayout.createSequentialGroup()
                                        .addComponent(jcbFormat1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnGenCred))))
                            .addGroup(pnlListEditLayout.createSequentialGroup()
                                .addComponent(jxDateEstr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnEditList))
                            .addGroup(pnlListEditLayout.createSequentialGroup()
                                .addComponent(jxDateLanz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())))
                    .addGroup(pnlListEditLayout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        pnlListEditLayout.setVerticalGroup(
            pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlListEditLayout.createSequentialGroup()
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addGroup(pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlListEditLayout.createSequentialGroup()
                        .addGroup(pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(txClasif, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(txYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(txProd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnlListEditLayout.createSequentialGroup()
                        .addGroup(pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16)
                            .addComponent(jcbFormat1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(rbtnVendbl)
                            .addComponent(rbtnNVendbl)
                            .addComponent(rbtnIgnrVendbl))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(txtPrecioVen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnlListEditLayout.createSequentialGroup()
                        .addGroup(pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(jcbCatgAlq, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(jcbGenero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(jxDateLanz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(jxDateEstr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
            .addGroup(pnlListEditLayout.createSequentialGroup()
                .addComponent(btnGenCred)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnEditList))
        );

        pnlFilter.add(pnlListEdit, java.awt.BorderLayout.CENTER);

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    private void txfTituloKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txfTituloKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            applyFilter();
        }
    }//GEN-LAST:event_txfTituloKeyPressed

    private void btnFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterActionPerformed
        applyFilter();
    }//GEN-LAST:event_btnFilterActionPerformed

    private void txfProdKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txfProdKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            applyFilter();
        }
    }//GEN-LAST:event_txfProdKeyPressed

    private void btnListEditItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_btnListEditItemStateChanged
        showListEdit(btnListEdit.isSelected());
    }//GEN-LAST:event_btnListEditItemStateChanged

    private void btnEditListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditListActionPerformed
        String ask="<html>Se editaran todos los registros de la lista (incluso paginas no visualizadas).";
        ask+="<br />Esta accion no se podra deshacer.<br />¿Desea continuar?</html>";
        String useQry;
        if(validateEdit()){
            if(ctrl.confirm("Editar registros",ask)){
                useQry="select distinct vg.idca from videoup_games vg ";
                if(whr.length()>0){
                    useQry+="left join videoup_catgs vct on vg.catg=vct.idcg left join videoup_bcdgam rgc on rgc.idca=vg.idca ";
                    useQry+="left join videoup_bcodes cpy on rgc.idbc=cpy.idbc left join videoup_formts vf on cpy.frmt=vf.idcf";
                }
                updateRecords(useQry+whr,"vg.idca=");
            }
        }
    }//GEN-LAST:event_btnEditListActionPerformed
    
    private void btnTgOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTgOActionPerformed
        updateWhUnion(false);
    }//GEN-LAST:event_btnTgOActionPerformed

    private void btnTgYActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTgYActionPerformed
        updateWhUnion(true);
    }//GEN-LAST:event_btnTgYActionPerformed

    private void btnGenCredActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenCredActionPerformed
        String useQry="select distinct cpy.barcode from videoup_games vg left join videoup_catgs vct on ";
        useQry+="vg.catg=vct.idcg left join videoup_bcdgam rgc on rgc.idca=vg.idca left join videoup_bcodes ";
        useQry+="cpy on rgc.idbc=cpy.idbc left join videoup_formts vf on cpy.frmt=vf.idcf ";
        String codes=loadIdListUseSQL(useQry+whr,0,false);
        getMainWindow().generateCodesReport(2,codes);
    }//GEN-LAST:event_btnGenCredActionPerformed

    private void txfCBarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txfCBarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            applyFilter();
        }
    }//GEN-LAST:event_txfCBarKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEditList;
    private javax.swing.JButton btnFilter;
    private javax.swing.JButton btnGenCred;
    private javax.swing.JToggleButton btnListEdit;
    private javax.swing.JToggleButton btnTgO;
    private javax.swing.JToggleButton btnTgY;
    private javax.swing.ButtonGroup btnsVendbl;
    private javax.swing.JComboBox edoProd;
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JComboBox jcbCatgAlq;
    private javax.swing.JComboBox jcbCtgPres;
    private javax.swing.JComboBox jcbFormat;
    private javax.swing.JComboBox jcbFormat1;
    private javax.swing.JComboBox jcbGenero;
    private javax.swing.JComboBox jcfCatg;
    private javax.swing.JComboBox jcfClasif;
    private org.jdesktop.swingx.JXDatePicker jxDateEstr;
    private org.jdesktop.swingx.JXDatePicker jxDateLanz;
    private javax.swing.JPanel pnlFilter;
    private javax.swing.JPanel pnlListEdit;
    private javax.swing.JRadioButton rbtnIgnrVendbl;
    private javax.swing.JRadioButton rbtnNVendbl;
    private javax.swing.JRadioButton rbtnVendbl;
    private javax.swing.JTextField txClasif;
    private javax.swing.JTextField txProd;
    private javax.swing.JTextField txYear;
    private javax.swing.JTextField txfCBar;
    private javax.swing.JTextField txfProd;
    private javax.swing.JTextField txfTitulo;
    private javax.swing.JTextField txtPrecioVen;
    // End of variables declaration//GEN-END:variables
}

