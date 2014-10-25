/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.views.modsocios;

import com.videoup.controllers.Controller;
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
    
    public Lista(Controller ctrl) {
        super(ctrl);
        TreeMap<String,String> hltrs=new TreeMap<String,String>();
        initComponents();
        buildTableModel();
        mainQry="select idct,cod_cst,applldos,name,dni,email,tel_home,(f_vigen>=now()) as vig,credito From videoup_customers ";
        orderby=" order by applldos asc, name asc";
        countQry="select count(*) From videoup_customers";
        reloadTable(0);
        pnlFilter.setVisible(false);
        pnlNorth.add(pnlFilter,BorderLayout.CENTER);
        pnlListEdit.setVisible(false);
        hltrs.put("hlGreen","Socio vigente");
        hltrs.put("hlGray","Socio NO vigente");
        hltrs.put("hlRed","Socio con adeudo");
        builHighLighters(hltrs);
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
            if( !((Boolean)row[7]) ){
                ck.addHighLighter("hlGray",true);
            }else if( row[8]!=null && Float.parseFloat(row[8].toString())<0 ){
                ck.addHighLighter("hlRed",true);
            }else{ ck.addHighLighter("hlGreen",true); }
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
            new String [] {"Codigo socio", "Apellidos", "Nombre", "DNI", "Email", "Telefono"}
        ){
            boolean[] canEdit = new boolean [] {false, false, false, false, false, false};
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {return canEdit [columnIndex];}
        });
        tabla.setColumnSelectionAllowed(false);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(220);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(140);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(120);
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
    protected void applyFilter(){
        String cde=txfCode.getText().trim();
        String nom=txfNom.getText().trim();
        String dni=txfDNI.getText().trim();
        int vig=jcbVigen.getSelectedIndex();
        int cred=jcbCredito.getSelectedIndex();
        Calendar cal;
        whr="";
        if(cde.length()>0){
            whr=buildCompare4Where(cde,"cod_cst","=");
        }if(nom.length()>0){
            whr+=(whr.length()>0?fltUnion:"")+buildLike4Where(nom,"name applldos");
        }if(dni.length()>0){
            whr+=(whr.length()>0?fltUnion:"")+buildLike4Where(dni,"dni");
        }if(vig>0){ //Indistinto, Vigente, No vigente, Por vencer
            if(vig<=2){
                whr+=(whr.length()>0?fltUnion:"")+"f_vigen"+(vig==1?">=":"<")+"'"+date4Where(null) +"'";
            }else{
                cal=Calendar.getInstance();
                cal.add(Calendar.DATE,30);
                whr+=(whr.length()>0?fltUnion:"")+"f_vigen<='"+date4Where(cal.getTime())+"'";
            }
        }if(cred>0){ //Indistinto, Cero, Positivo, Adeudo
            whr+=(whr.length()>0?fltUnion:"")+"credito"+(cred==1?"=":(cred==2?">":"<"))+"0";
        }
        if(whr.length()>0){
            whr=" where "+whr;
            countQry="select count(*) From videoup_customers"+whr;
        }else{
            countQry="select count(*) From videoup_customers";
        }
        reloadTable(0);
    }
    
    @Override
    protected void orderByColumn(int col, String ort){
        orderby=" order by ";
        if(col==0){
            orderby+=" cod_cst "+ort;
        }else if(col==1){
            orderby+=" applldos "+ort;
        }else if(col==2){
            orderby+=" name "+ort;
        }else if(col==3){
            orderby+=" dni "+ort;
        }else if(col==4){
            orderby+=" email "+ort;
        }else if(col==5){
            orderby+=" tel_home "+ort;
        }else{
            orderby+=" applldos asc, name asc";
        }
        reloadTable(currPage);
    }
    
    @Override
    public void setAndApplyFilter(String flt){
        txfCode.setText(flt);
        txfNom.setText(flt);
        txfDNI.setText(flt);
        pnlFilter.setVisible(true);
        applyFilter();
    }
    
    @Override
    protected int deleteSelectedRecords(){
        int[] sels=tabla.getSelectedRows();
        int[] ides=new int[sels.length];
        long rnts, vnts;
        int selIdxModel;
        cellKey cell;
        String toDel1="delete from videoup_autrz WHERE;";
        String toDel2="delete from videoup_cstmrnotes WHERE;";
        String toDel3="delete from videoup_puntos WHERE;";
        String toDel4="delete from videoup_customers WHERE;";
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
        // revisar si tiene rentas o ventas
        vnts=genDAO.countExistEntitiesUseSQL("select count(*) from videoup_ventas where "+where.replaceAll("IDN","idcli"));
        rnts=genDAO.countExistEntitiesUseSQL("select count(*) from videoup_rentas where "+where.replaceAll("IDN","idcli"));
        if(vnts<0 || rnts<0){ error=genDAO.getError(); return -1; }
        if(vnts>0 || rnts>0){ error="Existe relacion con registros de alquiler o ventas"; return -1; }
        // borrarlos
        toDel1=toDel1.replaceAll("WHERE"," where "+where.replaceAll("IDN","idct"));
        toDel2=toDel2.replaceAll("WHERE"," where "+where.replaceAll("IDN","idct"));
        toDel3=toDel3.replaceAll("WHERE"," where "+where.replaceAll("IDN","idct"));
        toDel4=toDel4.replaceAll("WHERE"," where "+where.replaceAll("IDN","idct"));
        if(genDAO.executeSQL(toDel1)<0){ error=genDAO.getError(); return -1; }
        if(genDAO.executeSQL(toDel2)<0){ error=genDAO.getError(); return -1; }
        if(genDAO.executeSQL(toDel3)<0){ error=genDAO.getError(); return -1; }
        if(genDAO.executeSQL(toDel4)<0){ error=genDAO.getError(); return -1; }
        for(int singId: ides){
            ctrl.try2CloseView(singId,true);
            ctrl.goToView(this.getId(),false);
        }
        return 0;
    }
    
    private void showListEdit(boolean sh){
        pnlListEdit.setVisible(sh);
        validate();
    }
    
    private boolean validateEdit(){
        Validator vltor=new Validator();
        vltor.addStringField(txCiudad, "Ciudad", false, 3, 55,0);
        vltor.addRegExpField(txCodigop, "Codigo postal", false,((String)Globals.getConfig("pcodeRgexp")),0);
        vltor.addStringField(txProvincia, "Provincia", false, 3, 55,0);
        vltor.addStringField(txPoblacion, "Poblacion", false, 3, 55,0);
        vltor.addDateField(jxDateVigen,"Fecha de vigencia",false,0);
        vltor.addIntField(txtMntCredito,"Credito",false,1,9999,0);
        if(!vltor.validate()){
            ctrl.showDialogErr(vltor.getError());
            return false;
        }else if(!vltor.hasAtLeastOneValue()){
            ctrl.showDialogErr("No ha indicado ningun cambio");
            return false;
        }
        return true;
    }
    
    @Override
    protected boolean updatePage(String ids,String idn){
        String city=txCiudad.getText().trim();
        String cp=txCodigop.getText().trim();
        String prov=txProvincia.getText().trim();
        String pobl=txPoblacion.getText().trim();
        Date Dvig=jxDateVigen.getDate();
        int cred;
        String upd1="update videoup_customers vc set";
        try{ cred=Integer.parseInt(txtMntCredito.getText().trim()); }
        catch(NumberFormatException nfb){ cred=0; }
        if(city.length()>0){ upd1+=" vc.city='"+city+"'"; }
        if(cp.length()>0){ upd1+=(upd1.endsWith("set")?"":", ")+" vc.codp='"+cp+"'"; }
        if(prov.length()>0){ upd1+=(upd1.endsWith("set")?"":", ")+" vc.prov='"+prov+"'"; }
        if(pobl.length()>0){ upd1+=(upd1.endsWith("set")?"":", ")+" vc.pobl='"+pobl+"'"; }
        if(Dvig!=null){ upd1+=(upd1.endsWith("set")?"":", ")+" vc.f_vigen='"+dte2SQL(Dvig)+"'"; }
        if(cred>0){ upd1+=(upd1.endsWith("set")?"":", ")+" vc.credito=vc.credito+"+cred; }
        ids=ids.replaceAll("(\\d+)",idn+"$1");
        if(!upd1.endsWith("set")){
            upd1+=" where "+ids;
            if(executeSQL(upd1)<0){
                return false;
            }
        }
        return true;
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
        PrintDiag pDiag=new PrintDiag(getMainWindow(),true,"Socios");
        pDiag.cargaReporte(getParams(), fields,7);
        pDiag.setVisible(true);
    }
    
    private Map<String,Object> getParams(){
        Map<String,Object> parametros = new HashMap<String,Object>();
        parametros.put("vclub",Globals.getConfig("nVclub"));
        parametros.put("dreport","Listado de Socios");
        parametros.put("col1","Codigo");
        parametros.put("col2","Nombre");
        parametros.put("col3","DNI");
        parametros.put("col4","Correo");
        parametros.put("col5","Telefono");
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
            campo=new EntListd( (row[1]!=null?row[1].toString():""),
                    (row[3]!=null?row[3].toString():"")+" "+(row[2]!=null?row[2].toString():""),
                    (row[4]!=null?row[4].toString():""),(row[5]!=null?row[5].toString():""),
                    (row[6]!=null?row[6].toString():"") );
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
        jLabel1 = new javax.swing.JLabel();
        txfCode = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txfNom = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txfDNI = new javax.swing.JTextField();
        btnFilter = new javax.swing.JButton();
        btnListEdit = new javax.swing.JToggleButton();
        btnTgO = new javax.swing.JToggleButton();
        btnTgY = new javax.swing.JToggleButton();
        jLabel11 = new javax.swing.JLabel();
        jcbVigen = new javax.swing.JComboBox();
        jLabel12 = new javax.swing.JLabel();
        jcbCredito = new javax.swing.JComboBox();
        pnlListEdit = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        btnEditList = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txCiudad = new javax.swing.JTextField();
        txCodigop = new javax.swing.JTextField();
        txProvincia = new javax.swing.JTextField();
        txPoblacion = new javax.swing.JTextField();
        jxDateVigen = new org.jdesktop.swingx.JXDatePicker();
        txtMntCredito = new javax.swing.JTextField();
        btnGenCred = new javax.swing.JButton();

        pnlFilter.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        pnlFilter.setLayout(new java.awt.BorderLayout());

        jLabel1.setText("Codigo de socio:");

        txfCode.setColumns(9);
        txfCode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txfCodeKeyPressed(evt);
            }
        });

        jLabel2.setText("Nombre:");

        txfNom.setColumns(13);
        txfNom.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txfNomKeyPressed(evt);
            }
        });

        jLabel3.setText("DNI:");

        txfDNI.setColumns(9);
        txfDNI.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txfDNIKeyPressed(evt);
            }
        });

        btnFilter.setText("Filtrar");
        btnFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterActionPerformed(evt);
            }
        });

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

        jLabel11.setText("Vigente");

        jcbVigen.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Indistinto", "Vigente", "No vigente", "Por vencer" }));

        jLabel12.setText("Credito:");

        jcbCredito.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Indistinto", "Cero", "Positivo", "Adeudo" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txfCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txfNom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txfDNI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 174, Short.MAX_VALUE)
                        .addComponent(btnTgO)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnTgY))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcbVigen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcbCredito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnFilter)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnListEdit)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnListEdit)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnTgY)
                                .addComponent(btnTgO))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(txfCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2)
                                .addComponent(txfNom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3)
                                .addComponent(txfDNI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(3, 3, 3)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(jcbVigen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12)
                            .addComponent(jcbCredito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnFilter))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlFilter.add(jPanel1, java.awt.BorderLayout.NORTH);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Edicion grupal le permite aplicar valores a todos los registros de la lista actual.");

        btnEditList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/videoup/imgs/save.png"))); // NOI18N
        btnEditList.setToolTipText("Aplicar cambios");
        btnEditList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditListActionPerformed(evt);
            }
        });

        jLabel5.setText("Ciudad:");

        jLabel6.setText("Codigo postal:");

        jLabel7.setText("Provincia:");

        jLabel8.setText("Poblacion:");

        jLabel9.setText("Vigente hasta:");

        jLabel10.setText("Agregar credito:");

        txCiudad.setColumns(11);

        txCodigop.setColumns(11);

        txProvincia.setColumns(11);

        txPoblacion.setColumns(11);

        txtMntCredito.setColumns(7);

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
                .addGroup(pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(pnlListEditLayout.createSequentialGroup()
                        .addGroup(pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlListEditLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txProvincia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txCodigop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtMntCredito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txPoblacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jxDateVigen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(pnlListEditLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE)))
                        .addGap(75, 75, 75)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnGenCred)
                    .addComponent(btnEditList))
                .addGap(4, 4, 4))
        );
        pnlListEditLayout.setVerticalGroup(
            pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlListEditLayout.createSequentialGroup()
                .addGroup(pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlListEditLayout.createSequentialGroup()
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlListEditLayout.createSequentialGroup()
                                .addGroup(pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel5)
                                    .addComponent(txCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel6)
                                    .addComponent(txCodigop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(pnlListEditLayout.createSequentialGroup()
                                .addGroup(pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel8)
                                    .addComponent(txPoblacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel9)
                                    .addComponent(jxDateVigen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel7)
                                .addComponent(txProvincia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlListEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel10)
                                .addComponent(txtMntCredito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(pnlListEditLayout.createSequentialGroup()
                        .addComponent(btnGenCred)
                        .addGap(18, 18, 18)
                        .addComponent(btnEditList)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlFilter.add(pnlListEdit, java.awt.BorderLayout.CENTER);

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    private void btnFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterActionPerformed
        applyFilter();
    }//GEN-LAST:event_btnFilterActionPerformed

    private void txfCodeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txfCodeKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            applyFilter();
        }
    }//GEN-LAST:event_txfCodeKeyPressed

    private void txfNomKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txfNomKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            applyFilter();
        }
    }//GEN-LAST:event_txfNomKeyPressed

    private void txfDNIKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txfDNIKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            applyFilter();
        }
    }//GEN-LAST:event_txfDNIKeyPressed

    private void btnListEditItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_btnListEditItemStateChanged
        showListEdit(btnListEdit.isSelected());
    }//GEN-LAST:event_btnListEditItemStateChanged

    private void btnEditListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditListActionPerformed
        String ask="<html>Se editaran todos los registros de la lista (incluso paginas no visualizadas).";
        ask+="<br />Esta accion no se podra deshacer.<br />¿Desea continuar?</html>";
        String useQry;
        if(validateEdit()){
            if(ctrl.confirm("Editar registros",ask)){
                useQry="select distinct vc.idct from videoup_customers vc ";
                updateRecords(useQry+whr,"vc.idct=");
            }
        }
    }//GEN-LAST:event_btnEditListActionPerformed

    private void btnGenCredActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenCredActionPerformed
        String useQry="select distinct vc.cod_cst from videoup_customers vc ";;
        String codes=loadIdListUseSQL(useQry+whr,0,false);
        getMainWindow().generateCodesReport(1,codes);
    }//GEN-LAST:event_btnGenCredActionPerformed

    private void btnTgOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTgOActionPerformed
        updateWhUnion(false);
    }//GEN-LAST:event_btnTgOActionPerformed

    private void btnTgYActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTgYActionPerformed
        updateWhUnion(true);
    }//GEN-LAST:event_btnTgYActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEditList;
    private javax.swing.JButton btnFilter;
    private javax.swing.JButton btnGenCred;
    private javax.swing.JToggleButton btnListEdit;
    private javax.swing.JToggleButton btnTgO;
    private javax.swing.JToggleButton btnTgY;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
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
    private javax.swing.JComboBox jcbCredito;
    private javax.swing.JComboBox jcbVigen;
    private org.jdesktop.swingx.JXDatePicker jxDateVigen;
    private javax.swing.JPanel pnlFilter;
    private javax.swing.JPanel pnlListEdit;
    private javax.swing.JTextField txCiudad;
    private javax.swing.JTextField txCodigop;
    private javax.swing.JTextField txPoblacion;
    private javax.swing.JTextField txProvincia;
    private javax.swing.JTextField txfCode;
    private javax.swing.JTextField txfDNI;
    private javax.swing.JTextField txfNom;
    private javax.swing.JTextField txtMntCredito;
    // End of variables declaration//GEN-END:variables
}
