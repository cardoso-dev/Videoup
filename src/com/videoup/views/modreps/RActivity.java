/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.views.modreps;

import com.videoup.controllers.Controller;
import com.videoup.utils.Globals;
import com.videoup.utils.reps.EntActv;
import com.videoup.views.utils.PrintDiag;
import com.videoup.views.utils.ViewReport;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Pedro
 */
public class RActivity extends ViewReport{

    private String orderby1;
    private String orderby2;
    private boolean hasRentas;
    private boolean hasVentas;
    private boolean loadRnt;
    private boolean loadVnt;
    
    /**
     * Creates new form RActivity
     */
    public RActivity(Controller ctrl, int id) {
        super(ctrl,id,"Reporte de Actividad");
        JTableHeader header1;
        JTableHeader header2;
        initComponents();
        initComps();
        orderby1=" order by cnt desc";
        orderby2=" order by cnt desc";
        hasRentas=false;
        hasVentas=false;
        loadRnt=true;
        loadVnt=true;
        header1 = tabla1.getTableHeader();
        header1.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent event){
                int columna=tabla1.columnAtPoint(event.getPoint());
                callOrderByColumn(1,columna);
            }
        });
        header2 = tabla2.getTableHeader();
        header2.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent event){
                int columna=tabla1.columnAtPoint(event.getPoint());
                callOrderByColumn(2,columna);
            }
        });
    }
    
    private void initComps(){
        setCtrlPanel(CtrlPan);
        setContentPanel(mainContain);
    }
    
    @Override
    protected boolean validateInput(){
        if(jdpDesde.getDate()==null){
            error="Fecha Desde invalida";
            return false;
        }
        if(jdpHasta.getDate()==null){
            error="Fecha Hasta invalida";
            return false;
        }
        return true;
    }
    
    @Override
    protected void generate(){
        if(loadRnt){ reloadRentas(); }
        if(loadVnt){ reloadVentas(); }
        loadRnt=true; loadVnt=true;
    }
   
    private void reloadRentas(){
        List<Object[]> rentas;
        Date dte1=jdpDesde.getDate();
        Date dte2=jdpHasta.getDate();
        DateFormat dtFt=DateFormat.getDateInstance(DateFormat.SHORT);
        String info=dtFt.format(dte1)+" a "+dtFt.format(dte2);
        String qry="select stringdecode(descr) as dsrc, count(vi.idbc) as cnt, (sum(vi.cst_apli)-bc.pr_compra)";
        qry+=" as benef, bc.pr_compra, sum(vi.cst_apli) as ing from videoup_itemsrnt vi, videoup_bcodes bc ";
        qry+="where vi.f_time>='"+fmtDate(dte1)+"' and vi.f_time<='"+fmtDate(dte2);
        qry+="' and bc.idbc=vi.idbc group by vi.idbc,descr"+orderby1;
        rentas=loadListUseSQL(qry,null,0,false);
        if(rentas==null){
            showError("Error al concentrar alquileres "+getError());
            hasRentas=false;
        }else{
            listRentas(rentas,info);
            hasRentas=!rentas.isEmpty();
        }
        setReady2Print();
    }
    
    private void reloadVentas(){
        List<Object[]> ventas;
        Date dte1=jdpDesde.getDate();
        Date dte2=jdpHasta.getDate();
        DateFormat dtFt=DateFormat.getDateInstance(DateFormat.SHORT);
        String info=dtFt.format(dte1)+" a "+dtFt.format(dte2);
        String qry="select stringdecode(descr) as dscr, count(vi.idbc) as cnt, bc.pr_venta-bc.pr_compra ";
        qry+="as benef, bc.pr_compra, bc.pr_venta from videoup_itemsvnt vi, videoup_bcodes bc, ";
        qry+="videoup_ventas vv where (vi.idvn=vv.idvn and onfecha>='"+fmtDate(dte1)+"' and onfecha<='";
        qry+=fmtDate(dte2)+"' and bc.idbc=vi.idbc) group by vi.idbc,descr"+orderby2;
        ventas=loadListUseSQL(qry,null,0,false);
        if(ventas==null){
            showError("Error al concentrar ventas "+getError());
            hasVentas=false;
        }else{
            listVentas(ventas,info);
            hasVentas=!ventas.isEmpty();
        }
        setReady2Print();
    }
    
    private void listRentas(List<Object[]> rentas, String info){
        DefaultTableModel tmodel=((DefaultTableModel)tabla1.getModel());
        NumberFormat frmCurr=NumberFormat.getCurrencyInstance();
        int tts=0;
        float ingreso;
        float costo;
        Object[] rowArray;
        for(int itr=tmodel.getRowCount();itr>0;itr--){
            tmodel.removeRow(itr-1);
        }
        if(rentas!=null){
            for(int cnt=0;cnt<rentas.size();cnt++){
                Object[] rnt=rentas.get(cnt);
                rowArray=new Object[5];
                rowArray[0]=rnt[0].toString().replaceAll("<br />|null","");
                rowArray[1]=rnt[1];
                ingreso=(rnt[4]==null?0:Float.parseFloat(rnt[4].toString()));
                costo=(rnt[3]==null?0:Float.parseFloat(rnt[3].toString()));
                rowArray[2]=frmCurr.format(ingreso-costo);
                rowArray[3]=frmCurr.format(costo);
                rowArray[4]=frmCurr.format(ingreso);
                tts+=Integer.parseInt(rnt[1].toString());
                tmodel.addRow(rowArray);
            }
            tabla1.setModel(tmodel);
        }
        lblNAlqs.setText("Alquileres registrados "+tts);
        lblGenredOn.setText("Actividad  de "+info);
    }
    
    private void setReady2Print(){
        rdy2Print=(hasRentas && hasVentas);
    }
    
    private void listVentas(List<Object[]> ventas, String info){
        DefaultTableModel tmodel=((DefaultTableModel)tabla2.getModel());
        NumberFormat frmCurr=NumberFormat.getCurrencyInstance();
        int tts=0;
        float ingreso;
        float costo;
        Object[] rowArray;
        for(int itr=tmodel.getRowCount();itr>0;itr--){
            tmodel.removeRow(itr-1);
        }
        tts=0;
        if(ventas!=null){
            for(Object[] vnt: ventas){
                rowArray=new Object[5];
                rowArray[0]=vnt[0].toString().replaceAll("<br />|null","");
                rowArray[1]=vnt[1];
                ingreso=(vnt[4]==null?0:Float.parseFloat(vnt[4].toString()));
                costo=(vnt[3]==null?0:Float.parseFloat(vnt[3].toString()));
                rowArray[2]=frmCurr.format(ingreso-costo);
                rowArray[3]=frmCurr.format(costo);
                rowArray[4]=frmCurr.format(ingreso);
                tts+=Integer.parseInt(vnt[1].toString());
                tmodel.addRow(rowArray);
            }
            tabla2.setModel(tmodel);
        }
        lblNVnts.setText("Ventas registradas "+tts);
        lblGenredOn.setText("Actividad  de "+info);
    }
    
    @Override
    protected void printNow(){
        PrintDiag pDiag=new PrintDiag(getMainWindow(),true,"de Actividad");
        pDiag.cargaReporte(getParams(), getListCampos(),4);
        pDiag.setVisible(true);
    }
    
    private Map<String,Object> getParams(){
        Map<String,Object> parametros = new HashMap<String,Object>();
        parametros.put("vclub",Globals.getConfig("nVclub"));
        parametros.put("dreport",(lblGenredOn.getText()+". "+lblNAlqs.getText()+", "+lblNVnts.getText()));
        return parametros;
    }
    
    private ArrayList getListCampos(){
        DefaultTableModel tmodel=((DefaultTableModel)tabla1.getModel());
        ArrayList listCampos=new ArrayList();
        EntActv campo;
        for(int itr=0;itr<tmodel.getRowCount();itr++){
            campo=new EntActv(
                    tmodel.getValueAt(itr, 0).toString(),
                    "Alquiler",
                    tmodel.getValueAt(itr, 1).toString()
                    );
            listCampos.add(campo);
        }
        tmodel=((DefaultTableModel)tabla2.getModel());
        for(int itr=0;itr<tmodel.getRowCount();itr++){
            campo=new EntActv(
                    tmodel.getValueAt(itr, 0).toString(),
                    "Venta",
                    tmodel.getValueAt(itr, 1).toString()
                    );
            listCampos.add(campo);
        }
        return listCampos;
    }
    
    private void callOrderByColumn(int table, int col){
        TableColumnModel tcm;
        TableColumn tc;
        String tx;
        String txTit;
        String orientation;
        if(table==1){ 
            tcm=tabla1.getTableHeader().getColumnModel();
        }else{
            tcm=tabla2.getTableHeader().getColumnModel();
        }
        tc=tcm.getColumn(col);
        tx=tc.getHeaderValue().toString();
        txTit=tx.split("-")[0];
        clearColumnNames(table);
        if(tx.contains("\u25b2")){
            tc.setHeaderValue(txTit+" - \u25bc");
            orientation="desc";
        }else{
            tc.setHeaderValue(txTit+" - \u25b2");
            orientation="asc";
        }
        orderByColumn(table, col, orientation);
    }
    
    private void orderByColumn(int table, int col, String ort){
        if(table==1){
            orderby1=" order by ";
            if(col==0){
                orderby1+=" dsrc "+ort;
            }else if(col==1){
                orderby1+=" cnt "+ort;
            }else if(col==2){
                orderby1+=" benef "+ort;
            }else if(col==3){
                orderby1+=" pr_compra "+ort;
            }else if(col==4){
                orderby1+=" ing "+ort;
            }
            loadVnt=false;
        }else{
            orderby2=" order by ";
            if(col==0){
                orderby2+=" dscr "+ort;
            }else if(col==1){
                orderby2+=" cnt "+ort;
            }else if(col==2){
                orderby2+=" benef "+ort;
            }else if(col==3){
                orderby2+=" pr_compra "+ort;
            }else if(col==4){
                orderby2+=" pr_venta "+ort;
            }
            loadRnt=false;
        }
        btnGen.doClick();
    }
    
    private void clearColumnNames(int table){
        TableColumnModel tcm;
        TableColumn tc;
        int col;
        String tx;
        String txTit;
        if(table==1){ 
            tcm=tabla1.getTableHeader().getColumnModel();
        }else{
            tcm=tabla2.getTableHeader().getColumnModel();
        }        
        for(col=0;col<tabla1.getColumnCount();col++){
            tc=tcm.getColumn(col);
            tx=tc.getHeaderValue().toString();
            txTit=tx.split("-")[0];
            tc.setHeaderValue(txTit);
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

        mainContain = new javax.swing.JPanel();
        lblGenredOn = new javax.swing.JLabel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabla1 = new javax.swing.JTable();
        lblNAlqs = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabla2 = new javax.swing.JTable();
        lblNVnts = new javax.swing.JLabel();
        CtrlPan = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jdpDesde = new org.jdesktop.swingx.JXDatePicker();
        jLabel2 = new javax.swing.JLabel();
        jdpHasta = new org.jdesktop.swingx.JXDatePicker();

        lblGenredOn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblGenredOn.setText("Generado: DIA-HORA");

        jSplitPane1.setDividerLocation(210);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanel1.setLayout(new java.awt.BorderLayout());

        tabla1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Articulo", "Veces Alquilada", "Beneficio", "Costo", "Ingreso "
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabla1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tabla1);
        if (tabla1.getColumnModel().getColumnCount() > 0) {
            tabla1.getColumnModel().getColumn(0).setPreferredWidth(180);
            tabla1.getColumnModel().getColumn(1).setPreferredWidth(50);
            tabla1.getColumnModel().getColumn(2).setPreferredWidth(36);
            tabla1.getColumnModel().getColumn(3).setPreferredWidth(36);
            tabla1.getColumnModel().getColumn(4).setPreferredWidth(36);
        }

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        lblNAlqs.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblNAlqs.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNAlqs.setText("Alquileres registrados N");
        jPanel1.add(lblNAlqs, java.awt.BorderLayout.PAGE_START);

        jSplitPane1.setTopComponent(jPanel1);

        jPanel2.setLayout(new java.awt.BorderLayout());

        tabla2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Articulo", "Vendida", "Beneficio", "Costo", "Ingreso"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabla2.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tabla2);
        if (tabla2.getColumnModel().getColumnCount() > 0) {
            tabla2.getColumnModel().getColumn(0).setPreferredWidth(180);
            tabla2.getColumnModel().getColumn(1).setPreferredWidth(40);
            tabla2.getColumnModel().getColumn(2).setPreferredWidth(40);
            tabla2.getColumnModel().getColumn(3).setPreferredWidth(40);
            tabla2.getColumnModel().getColumn(4).setPreferredWidth(40);
        }

        jPanel2.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        lblNVnts.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblNVnts.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNVnts.setText("Ventas registradas N");
        jPanel2.add(lblNVnts, java.awt.BorderLayout.PAGE_START);

        jSplitPane1.setBottomComponent(jPanel2);

        javax.swing.GroupLayout mainContainLayout = new javax.swing.GroupLayout(mainContain);
        mainContain.setLayout(mainContainLayout);
        mainContainLayout.setHorizontalGroup(
            mainContainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainContainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainContainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 643, Short.MAX_VALUE)
                    .addComponent(lblGenredOn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        mainContainLayout.setVerticalGroup(
            mainContainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainContainLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblGenredOn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel1.setText("Desde:");

        jLabel2.setText("Hasta:");

        javax.swing.GroupLayout CtrlPanLayout = new javax.swing.GroupLayout(CtrlPan);
        CtrlPan.setLayout(CtrlPanLayout);
        CtrlPanLayout.setHorizontalGroup(
            CtrlPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CtrlPanLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jdpDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jdpHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(442, Short.MAX_VALUE))
        );
        CtrlPanLayout.setVerticalGroup(
            CtrlPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CtrlPanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(CtrlPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jdpDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jdpHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel CtrlPan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private org.jdesktop.swingx.JXDatePicker jdpDesde;
    private org.jdesktop.swingx.JXDatePicker jdpHasta;
    private javax.swing.JLabel lblGenredOn;
    private javax.swing.JLabel lblNAlqs;
    private javax.swing.JLabel lblNVnts;
    private javax.swing.JPanel mainContain;
    private javax.swing.JTable tabla1;
    private javax.swing.JTable tabla2;
    // End of variables declaration//GEN-END:variables
}
