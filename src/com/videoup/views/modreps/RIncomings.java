/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.views.modreps;

import com.videoup.controllers.Controller;
import com.videoup.utils.Globals;
import com.videoup.utils.reps.EntIngrs;
import com.videoup.views.utils.PrintDiag;
import com.videoup.views.utils.ViewReport;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Pedro
 */
public class RIncomings extends ViewReport{

    /**
     * Creates new form RIncomings
     */
    public RIncomings(Controller ctrl, int id) {
        super(ctrl,id,"Reporte de Ingresos");
        initComponents();
        initComps();
    }
    
    private void initComps(){
        setCtrlPanel(CtrlPan);
        setContentPanel(mainContain);
    }
    
    @Override
    protected void generate(){
        List<Object[]> rentas;
        List<Object[]> ventas;
        List<Object[]> credito;
        List<Object[]> bonos;
        Date dte1=jdpDesde.getDate();
        Date dte2=jdpHasta.getDate();
        DateFormat dtFt=DateFormat.getDateInstance(DateFormat.SHORT);
        String info=dtFt.format(dte1)+" a "+dtFt.format(dte2)+", concepto";
        String qry="Select vr.idrt, vr.impuesto, vi.f_time, stringdecode(vi.descr), vi.cst_apli,vr.fromCredito From videoup_rentas vr,";
        qry+=" videoup_itemsrnt vi where vr.idrt=vi.idrt and vi.f_time>='"+fmtDate(dte1);
        qry+="' and vi.f_time<='"+fmtDate(dte2)+"' and vi.status=4 order by vi.f_time desc, vi.idir desc";
        if(jcbFilter.getSelectedIndex()<=1){
            rentas=loadListUseSQL(qry,null,0,false);
            if(rentas==null){ showError("Error al concentrar alquileres "+getError()); }
            else{ info+=" alquileres"; }
        }else{ rentas=new ArrayList<Object[]>(); }
        qry="select idvn, onfecha, cst_env+impuesto+cst_subtotal,fromCredito as costo from videoup_ventas vv where onFecha>='";
        qry+=fmtDate(dte1)+"' and onFecha<='"+fmtDate(dte2)+"' and status=1 order by onfecha desc";
        if(jcbFilter.getSelectedIndex()==0||jcbFilter.getSelectedIndex()==2){
            ventas=loadListUseSQL(qry,null,0,false);
            if(ventas==null){ showError("Error al concentrar ventas "+getError()); }
            else{ info+=(info.endsWith("alquileres")?", ":"")+" ventas"; }
        }else{ ventas=new ArrayList<Object[]>(); }
        qry="select idhc, asig_on, monto, cod_cst, name, applldos as socio from videoup_customers vc, ";
        qry+="videoup_histcredito vh where asig_on>='"+fmtDate(dte1)+"' and asig_on<='"+fmtDate(dte2)+"' and ";
        qry+=" vc.idct=vh.idct and monto>0 order by asig_on desc";
        if(jcbFilter.getSelectedIndex()==0||jcbFilter.getSelectedIndex()==3){
            credito=loadListUseSQL(qry,null,0,false);
            if(credito==null){ showError("Error al concentrar venats de Credito "+getError()); }
            else{ info+=(info.endsWith("s")?", ":"")+" creditos"; }
        }else{ credito=new ArrayList<Object[]>(); }
        qry="select idsb, inicia, pagado, idcli, name, applldos  from videoup_customers vc, ";
        qry+="videoup_soldbonos vs where inicia>='"+fmtDate(dte1)+"' and inicia<='"+fmtDate(dte2)+"' and ";
        qry+=" vc.idct=vs.idcli order by inicia desc";
        if(jcbFilter.getSelectedIndex()==0||jcbFilter.getSelectedIndex()==4){
            bonos=loadListUseSQL(qry,null,0,false);
            if(bonos==null){ showError("Error al concentrar ventas de Bonos "+getError()); }
            else{ info+=(info.endsWith("s")?", ":"")+" bonos"; }
        }else{ bonos=new ArrayList<Object[]>(); }
        listData(rentas,ventas,credito,bonos,info);
        rdy2Print=(!rentas.isEmpty() || !ventas.isEmpty() || !credito.isEmpty());
    }
    
    private void listData(List<Object[]> rentas, List<Object[]> ventas, List<Object[]> credito, List<Object[]> bonos, String info){
        DefaultTableModel tmodel=((DefaultTableModel)tabla.getModel());
        DateFormat dtFt;
        NumberFormat frmCurr=NumberFormat.getCurrencyInstance();
        Calendar today=Calendar.getInstance();
        float sTotal=0;
        float tax4Item=0;
        float cred4Item=0;
        float tmpCst;
        int cnt2, nItems;
        Object[] rowArray;
        for(int itr=tmodel.getRowCount();itr>0;itr--){
            tmodel.removeRow(itr-1);
        }
        if(rentas!=null){
            for(int cnt=0;cnt<rentas.size();cnt++){
                Object[] rnt=rentas.get(cnt);
                if( (cnt>0 && !rentas.get(cnt-1)[0].equals(rentas.get(cnt)[0])) || cnt==0 ){
                    nItems=0; cnt2=cnt;
                    while( cnt2<rentas.size() && rentas.get(cnt2)[0].equals(rentas.get(cnt)[0]) ){
                        cnt2++; nItems++;
                    }
                    tax4Item=(Float.parseFloat(rnt[1].toString())/(float)nItems);
                    if(rnt[5]!=null){
                        cred4Item=(Float.parseFloat(rnt[5].toString())/(float)nItems);
                    }else{ cred4Item=0; }
                }
                tmpCst=Float.parseFloat(rnt[4].toString())+tax4Item-cred4Item;
                rowArray=new Object[3];
                rowArray[0]=asDate(rnt[2],DateFormat.LONG,0);
                rowArray[1]="Alquiler de "+rnt[3].toString().replaceAll("<br />|null","");
                rowArray[2]=frmCurr.format(tmpCst);
                sTotal+=(tmpCst+tax4Item-cred4Item);
                tmodel.addRow(rowArray);
            }
        }
        if(ventas!=null){
            for(Object[] vnt: ventas){
                tmpCst=Float.parseFloat(vnt[2].toString());
                if(vnt[3]!=null){
                    tmpCst-=Float.parseFloat(vnt[3].toString());
                }
                rowArray=new Object[3];
                rowArray[0]=asDate(vnt[1],DateFormat.LONG,0);
                rowArray[1]="Venta numero "+vnt[0];
                rowArray[2]=frmCurr.format(tmpCst);
                sTotal+=(tmpCst);
                tmodel.addRow(rowArray);
            }
        }
        if(credito!=null){
            for(Object[] crd: credito){
                tmpCst=Float.parseFloat(crd[2].toString());
                rowArray=new Object[3];
                rowArray[0]=asDate(crd[1],DateFormat.LONG,0);
                rowArray[1]="Carga de credito a socio ["+crd[3]+"] "+crd[4]+" "+crd[5];
                rowArray[2]=frmCurr.format(tmpCst);
                sTotal+=(tmpCst);
                tmodel.addRow(rowArray);
            }
        }
        if(bonos!=null){
            for(Object[] bono: bonos){
                tmpCst=Float.parseFloat(bono[2].toString());
                rowArray=new Object[3];
                rowArray[0]=asDate(bono[1],DateFormat.LONG,0);
                rowArray[1]="Venta de bonos a socio ["+bono[3]+"] "+bono[4]+" "+bono[5];
                rowArray[2]=frmCurr.format(tmpCst);
                sTotal+=(tmpCst);
                tmodel.addRow(rowArray);
            }
        }
        txtSTotal.setText(frmCurr.format(sTotal));
        dtFt=DateFormat.getDateInstance(DateFormat.FULL);
        lblGenredOn.setText("Ingresos de "+info+". Generado en: "+dtFt.format(today.getTime()));
        tabla.setModel(tmodel);
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
    protected void printNow(){
        PrintDiag pDiag=new PrintDiag(getMainWindow(),true,"de Ingresos");
        pDiag.cargaReporte(getParams(), getListCampos(),1);
        pDiag.setVisible(true);
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
        txtSTotal = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabla = new javax.swing.JTable();
        CtrlPan = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jdpDesde = new org.jdesktop.swingx.JXDatePicker();
        jLabel2 = new javax.swing.JLabel();
        jdpHasta = new org.jdesktop.swingx.JXDatePicker();
        jLabel3 = new javax.swing.JLabel();
        jcbFilter = new javax.swing.JComboBox();

        lblGenredOn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblGenredOn.setText("Generado: DIA-HORA");

        txtSTotal.setEditable(false);
        txtSTotal.setColumns(7);
        txtSTotal.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
        jLabel5.setText("Total de ingresos:");

        tabla.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Fecha", "Operacion", "Ingreso"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabla.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tabla);
        tabla.getColumnModel().getColumn(0).setMinWidth(80);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(80);
        tabla.getColumnModel().getColumn(1).setMinWidth(190);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(190);
        tabla.getColumnModel().getColumn(2).setMinWidth(90);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(90);

        javax.swing.GroupLayout mainContainLayout = new javax.swing.GroupLayout(mainContain);
        mainContain.setLayout(mainContainLayout);
        mainContainLayout.setHorizontalGroup(
            mainContainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainContainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainContainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 531, Short.MAX_VALUE)
                    .addGroup(mainContainLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblGenredOn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        mainContainLayout.setVerticalGroup(
            mainContainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainContainLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblGenredOn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainContainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addContainerGap())
        );

        jLabel1.setText("Desde:");

        jLabel2.setText("Hasta:");

        jLabel3.setText("Incluir ingresos:");

        jcbFilter.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Todos", "Alquileres", "Ventas", "Venta de Credito", "Venta de Bonos" }));

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
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcbFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(238, Short.MAX_VALUE))
        );
        CtrlPanLayout.setVerticalGroup(
            CtrlPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CtrlPanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(CtrlPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jdpDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jdpHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jcbFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    private Map<String,Object> getParams(){
        Map<String,Object> parametros = new HashMap<String,Object>();
        parametros.put("vclub",Globals.getConfig("nVclub"));
        parametros.put("dreport",lblGenredOn.getText());
        parametros.put("total",txtSTotal.getText());
        return parametros;
    }
    
    private ArrayList getListCampos(){
        DefaultTableModel tmodel=((DefaultTableModel)tabla.getModel());
        ArrayList listCampos=new ArrayList();
        EntIngrs campo;
        for(int itr=0;itr<tmodel.getRowCount();itr++){
            campo=new EntIngrs(
                    tmodel.getValueAt(itr, 0).toString(),
                    tmodel.getValueAt(itr, 1).toString(),
                    tmodel.getValueAt(itr, 2).toString()
                    );
            listCampos.add(campo);
        }
        return listCampos;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel CtrlPan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox jcbFilter;
    private org.jdesktop.swingx.JXDatePicker jdpDesde;
    private org.jdesktop.swingx.JXDatePicker jdpHasta;
    private javax.swing.JLabel lblGenredOn;
    private javax.swing.JPanel mainContain;
    private javax.swing.JTable tabla;
    private javax.swing.JTextField txtSTotal;
    // End of variables declaration//GEN-END:variables
}
