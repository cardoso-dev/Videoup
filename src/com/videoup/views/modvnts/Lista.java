/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.views.modvnts;

import com.videoup.controllers.Controller;
import com.videoup.views.utils.ViewLista;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
        delQry="delete from videoup_ventas WHERE;";
        idn="idvn";
        mainQry="select idvn,vc.cod_cst,concat(applldos,' ',name),vv.cst_env+vv.cst_subtotal+vv.impuesto as costo";
        mainQry+=",onfecha,status From videoup_ventas vv left join videoup_customers vc on vc.idct=vv.idcli";
        orderby=" order by vv.idvn desc";
        countQry="select count(*) from videoup_ventas vv";
        reloadTable(0);
        pnlFilter.setVisible(false);
        pnlNorth.add(pnlFilter,BorderLayout.CENTER);
        hltrs.put("hlGreen","Finalizada");
        hltrs.put("hlOrange","Apartado");
        hltrs.put("hlRed","Cancelada");
        builHighLighters(hltrs);
        showBtnPrint(false);
    }

    @Override
    protected void showList(String query,int pag){
        List<Object[]> lista;
        DefaultTableModel tmodel=((DefaultTableModel)tabla.getModel());
        NumberFormat frmCurr=NumberFormat.getCurrencyInstance();
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
            rowArray=new Object[4];
            ck=new cellKey(row[0],"Venta "+row[0]);
            if(row[5]!=null){
                if( row[5].toString().toLowerCase().indexOf("2")>=0 ){
                    ck.addHighLighter("hlRed",true);
                }else if( row[5].toString().toLowerCase().indexOf("1")>=0 ){
                    ck.addHighLighter("hlGreen",true);
                }else if( row[5].toString().toLowerCase().indexOf("0")>=0 ){
                    ck.addHighLighter("hlOrange",true);
                }
            }
            rowArray[0]=ck;
            rowArray[1]=row[2]+(row[1]!=null?" ["+row[1]+"]":"");
            rowArray[2]=frmCurr.format(row[3]);
            rowArray[3]=asDate(row[4],DateFormat.LONG,DateFormat.SHORT);
            tmodel.addRow(rowArray);
        }
        tabla.setModel(tmodel);
    }
    
    @Override
    protected void buildTableModel(){
        tabla.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {"Venta", "Cliente", "Cargos", "Fecha"}
        ){
            boolean[] canEdit = new boolean [] {false, false, false, false};
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {return canEdit [columnIndex];}
        });
        tabla.setColumnSelectionAllowed(false);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(230);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(205);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(160);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(210);
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
        String soc=txfSocio.getText().trim();
        int state=jcbfState.getSelectedIndex();
        int[] vstates={0,1,0,2};
        Date dte=jxdfDate.getDate();
        Calendar cal=Calendar.getInstance();
        String dtf;
        whr="";
        if(soc.length()>0){
            whr+="("+buildLike4Where(soc,"applldos")+" or "+buildLike4Where(soc,"name")+" or cod_cst='"+soc+"')";
        }if(state>0){
            whr+=(whr.length()>0?fltUnion:"")+"status="+vstates[state];
        }if(dte!=null){
            cal.setTime(dte);
            dtf=""+cal.get(Calendar.YEAR)+"-"+setCoupleDigits(cal.get(Calendar.MONTH)+1)+"-"+setCoupleDigits(cal.get(Calendar.DATE));
            whr+=(whr.length()>0?fltUnion:"")+"onFecha like '%"+dtf+"%'";
        }
        
        if(whr.length()>0){
            whr=" where "+whr;
            countQry="select count(*) from videoup_ventas vv left join videoup_customers vc on vc.idct=vv.idcli"+whr;
        }else{
            countQry="select count(*) from videoup_ventas vv";
        }
        reloadTable(0);
    }
    
    @Override
    protected void orderByColumn(int col, String ort){
        orderby=" order by";
        if(col==0){
            orderby+=" idvn "+ort;
        }else if(col==1){
            orderby+=" vc.applldos "+ort+", vc.name "+ort;
        }else if(col==2){
            orderby+=" costo "+ort;
        }else if(col==3){
            orderby+=" onfecha "+ort;
        }else{
            orderby+=" idvn desc";
        }
        reloadTable(currPage);
    }
    
    private String setCoupleDigits(int vl){
        return (vl<10?"0":"")+vl;
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
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlFilter = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txfSocio = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jcbfState = new javax.swing.JComboBox();
        jxdfDate = new org.jdesktop.swingx.JXDatePicker();
        btnFilter = new javax.swing.JButton();
        btnTgO = new javax.swing.JToggleButton();
        btnTgY = new javax.swing.JToggleButton();

        jLabel1.setText("Cliente:");

        txfSocio.setColumns(13);
        txfSocio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txfSocioKeyPressed(evt);
            }
        });

        jLabel2.setText("Estado:");

        jLabel3.setText("Fecha");

        jcbfState.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Todos", "Finalizado", "Apartado", "Cancelado" }));

        btnFilter.setText("Filtrar");
        btnFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterActionPerformed(evt);
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

        javax.swing.GroupLayout pnlFilterLayout = new javax.swing.GroupLayout(pnlFilter);
        pnlFilter.setLayout(pnlFilterLayout);
        pnlFilterLayout.setHorizontalGroup(
            pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFilterLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcbfState, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txfSocio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jxdfDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 164, Short.MAX_VALUE)
                .addComponent(btnTgO)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnTgY)
                .addGap(18, 18, 18)
                .addComponent(btnFilter)
                .addContainerGap())
        );
        pnlFilterLayout.setVerticalGroup(
            pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFilterLayout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addGroup(pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(jxdfDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(txfSocio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnTgY)
                            .addComponent(btnTgO))
                        .addGroup(pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jcbfState, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnFilter)))))
        );

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    private void txfSocioKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txfSocioKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            applyFilter();
        }
    }//GEN-LAST:event_txfSocioKeyPressed

    private void btnFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterActionPerformed
        applyFilter();
    }//GEN-LAST:event_btnFilterActionPerformed

    private void btnTgOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTgOActionPerformed
        updateWhUnion(false);
    }//GEN-LAST:event_btnTgOActionPerformed

    private void btnTgYActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTgYActionPerformed
        updateWhUnion(true);
    }//GEN-LAST:event_btnTgYActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFilter;
    private javax.swing.JToggleButton btnTgO;
    private javax.swing.JToggleButton btnTgY;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JComboBox jcbfState;
    private org.jdesktop.swingx.JXDatePicker jxdfDate;
    private javax.swing.JPanel pnlFilter;
    private javax.swing.JTextField txfSocio;
    // End of variables declaration//GEN-END:variables
}
