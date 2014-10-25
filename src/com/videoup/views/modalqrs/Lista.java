/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.views.modalqrs;

import com.videoup.controllers.Controller;
import com.videoup.views.utils.ViewLista;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.text.NumberFormat;
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
        delQry="delete from videoup_itemsrnt WHERE; delete from videoup_rentas WHERE;";
        idn="idrt";
        mainQry="select vr.idrt as idrnt, concat(cod_cst,' ',name,' ',applldos) as socio,GROUP_CONCAT(";
        mainQry+="vi.status SEPARATOR ', ') AS sttus, GROUP_CONCAT(vb.barcode SEPARATOR ', ') AS bcodes, ";
        mainQry+="GROUP_CONCAT(mv.titulo SEPARATOR ', ') AS movs, GROUP_CONCAT(gm.titulo SEPARATOR ', ') AS ";
        mainQry+="games, vr.cst_fin+impuesto as coste, min(i_time) as initm, min(f_time) as fintm from ";
        mainQry+="videoup_rentas vr, videoup_itemsrnt vi,  videoup_customers vc,videoup_bcodes vb left join ";
        mainQry+="videoup_bcdmov jmv on vb.idbc=jmv.idbc left join videoup_movies mv on jmv.idcm=mv.idcm left ";
        mainQry+="join videoup_bcdgam jgm on vb.idbc=jgm.idbc left join videoup_games gm on jgm.idca=gm.idca";
        whr=" where vr.idrt=vi.idrt and vc.idct=vr.idcli and vb.idbc=vi.idbc";
        orderby=" group by vr.idrt order by initm desc";
        countQry="select count(vr.idrt) from videoup_rentas vr";
        reloadTable(0);
        pnlFilter.setVisible(false);
        pnlNorth.add(pnlFilter,BorderLayout.CENTER);
        hltrs.put("hlBlue","En curso");
        hltrs.put("hlGreen","Finalizada pagada");
        hltrs.put("hlOrange","Solicitada");
        hltrs.put("hlRed","Cancelada");
        hltrs.put("hlGray","Finalizada sin pagar");
        hltrs.put("hlBlue2","En curso con prepago");
        builHighLighters(hltrs);
        showBtnPrint(false);
    }
    
    @Override
    protected void showList(String query,int pag){
        List<Object[]> lista;
        NumberFormat frmCurr=NumberFormat.getCurrencyInstance();
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
            if(row[2]!=null){
                if( row[2].toString().toLowerCase().indexOf("3")>=0 ){
                    ck.addHighLighter("hlBlue",true);
                }else if( row[2].toString().toLowerCase().indexOf("1")>=0 ){
                    ck.addHighLighter("hlRed",true);
                }else if( row[2].toString().toLowerCase().indexOf("4")>=0 ){
                    ck.addHighLighter("hlGreen",true);
                }else if( row[2].toString().toLowerCase().indexOf("0")>=0 ){
                    ck.addHighLighter("hlOrange",true);
                }else if( row[2].toString().toLowerCase().indexOf("6")>=0 ){
                    ck.addHighLighter("hlGray",true);
                }else if( row[2].toString().toLowerCase().indexOf("7")>=0 ){
                    ck.addHighLighter("hlBlue2",true);
                }
            }
            rowArray[0]=ck;
            rowArray[1]=repStatus(row[2]);
            rowArray[2]=row[3]+" "+(row[4]!=null?row[4]:"")+" "+(row[5]!=null?row[5]:"");
            rowArray[3]=frmCurr.format(row[6]!=null?row[6]:0);
            rowArray[4]=asDate(row[7],DateFormat.LONG,DateFormat.SHORT);
            rowArray[5]=asDate(row[8],DateFormat.LONG,DateFormat.SHORT);
            tmodel.addRow(rowArray);
        }
        tabla.setModel(tmodel);
    }
    
    private String repStatus(Object stt){
        String status="Articulos: ";
        String rep=stt.toString();
        int[] occurrs={0,0,0,0,0,0,0,0};
        String[] nstts={"Solicitado","Cancelado","Finalizado","En curso","Finalizado pagada","Finalizado por pagar","Cambiado","Prepago"};
        for(int h=0;h<nstts.length;h++){
            occurrs[h]=countOccurrs(rep,""+h);
            if(occurrs[h]>0){
                status+=(status.endsWith(": ")?" ":", ")+occurrs[h]+" "+nstts[h];
            }
        }
        return status;
    }
    
    private int countOccurrs(String src, String srch){
        int cnt=0;
        int idx=0;
        int found;
        while(idx>=0){
            found=src.indexOf(srch, idx);
            if(found>=0){
                cnt++;
                idx=found+1;
            }else{
                idx=-1;
            }
        }
        return cnt;
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
    protected void buildTableModel(){
        tabla.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {"Socio", "Estado","Articulos", "Cargos", "Iniciado en", "Finalizado en"}
        ){
            boolean[] canEdit = new boolean [] {false, false, false, false, false};
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {return canEdit [columnIndex];}
        });
        tabla.setColumnSelectionAllowed(false);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(230);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(205);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(330);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(90);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(210);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(210);
    }
    
    @Override
    protected void applyFilter(){
        String soc=txfSocio.getText().trim();
        String code=txProdCode.getText().trim();
        int state=jcbfState.getSelectedIndex();
        int[] vstates={0,3,0,1,4,5,6};
        Date dte=jxdfDate.getDate();
        int tpDte=jcbfKindDte.getSelectedIndex();
        String operDate;
        String whrl="";
        whr=" where vr.idrt=vi.idrt and vc.idct=vr.idcli and vb.idbc=vi.idbc";
        boolean bwrh=false;
        if(soc.length()>0){ bwrh=true;
            whrl+="("+buildLike4Where(soc,"applldos")+" or "+buildLike4Where(soc,"name")+" or cod_cst='"+soc+"')";
        }if(code.length()>0){ bwrh=true;
            whrl+=buildCompare4Where(code,"vb.barcode","=");
        }if(state>0){
            whrl+=(bwrh?fltUnion:"")+"(vi.status="+vstates[state]+(state==1?" or vi.status=7":"")+")";
            bwrh=true;
        }if(dte!=null){
            operDate=(rbtnPrev.isSelected()?"<":(rbtnEqual.isSelected()?"=":">"));
            whrl+=(bwrh?fltUnion:"")+(tpDte==0?"vi.i_time":tpDte==1?"vi.s_time":"vi.f_time")+operDate+"'"+date4Where(dte)+"'";
        }
        whr+=(whrl.length()>0?" and ("+whrl+")":"");
        countQry="select count(vr.idrt) from videoup_rentas vr"+(whrl.length()>=0?", videoup_itemsrnt vi, videoup_customers vc, videoup_bcodes vb"+whr:"");
        reloadTable(0);
    }
    
    @Override
    protected void orderByColumn(int col, String ort){
        orderby=" group by vr.idrt order by ";
        if(col==0){
            orderby+=" socio "+ort;
        }else if(col==1){
            orderby+=" sttus "+ort;
        }else if(col==2){
            orderby+=" bcodes "+ort;
        }else if(col==3){
            orderby+=" coste "+ort;
        }else if(col==4){
            orderby+=" initm "+ort;
        }else if(col==5){
            orderby+=" fintm "+ort;
        }
        reloadTable(currPage);
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
    protected int deleteSelectedRecords(){
        int[] sels=tabla.getSelectedRows();
        int[] ides=new int[sels.length];
        String toDel;
        String toUpdate;
        int selIdxModel;
        int deleted=0;
        cellKey cell;
        String where=" where (";
        String whereUp=" where ";
        DefaultTableModel tmodel=((DefaultTableModel)tabla.getModel());
        int cnt;
        if(delQry==null || idn==null){
            error="DelQry or Idn not setted";
            return -1;
        }
        if(sels.length==0){ return deleted; }
        for(cnt=0;cnt<sels.length;cnt++){
            selIdxModel=tabla.convertRowIndexToModel(sels[cnt]);
            cell=((cellKey)tmodel.getValueAt(selIdxModel,0));
            where+=(cnt==0?"":" or ")+idn+"="+cell.getKey();
            ides[cnt]=cell.getKey();
        } where+=")";
        whereUp="select distinct vb.idbc from videoup_bcodes vb, videoup_itemsrnt vi "+where;
        whereUp+=" and vb.idbc=vi.idbc and (vi.status=3 or vi.status=7) ";
        whereUp=loadIdListUseSQL(whereUp,0,false);
        if(whereUp==null){
            return -1;
        }else if(whereUp.length()>0){
            whereUp=whereUp.replaceAll("(\\d+)","idbc=$1");
            toUpdate="update videoup_bcodes set status='Disponible' where "+whereUp;
            deleted=genDAO.executeSQL(toUpdate);
            if(deleted<0){
                error=genDAO.getError();
                return -1;
            }
        }        
        toDel=delQry.replaceAll("WHERE", where);
        deleted=genDAO.executeSQL(toDel);
        if(deleted<0){
            error=genDAO.getError();
        }else{
            for(int singId: ides){
                ctrl.try2CloseView(singId,true);
                ctrl.goToView(this.getId(),false);
            }
        }
        return deleted;
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
        jcbfKindDte = new javax.swing.JComboBox();
        jxdfDate = new org.jdesktop.swingx.JXDatePicker();
        btnFilter = new javax.swing.JButton();
        rbtnPrev = new javax.swing.JRadioButton();
        rbtnEqual = new javax.swing.JRadioButton();
        rbtnAfter = new javax.swing.JRadioButton();
        btnTgY = new javax.swing.JToggleButton();
        btnTgO = new javax.swing.JToggleButton();
        jLabel4 = new javax.swing.JLabel();
        txProdCode = new javax.swing.JTextField();
        grpDate = new javax.swing.ButtonGroup();

        pnlFilter.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jLabel1.setText("Socio:");

        txfSocio.setColumns(13);
        txfSocio.setToolTipText("Ingrese Nombre, Apellidos o Codigo");
        txfSocio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txfSocioKeyPressed(evt);
            }
        });

        jLabel2.setText("Estado:");

        jLabel3.setText("Fecha");

        jcbfState.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Todos", "En curso", "Solicitada", "Cancelada", "Finalizada pagada", "Finalizada por pagar", "Cambios" }));

        jcbfKindDte.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "De Inicio", "De solicitud", "De cierre" }));

        btnFilter.setText("Filtrar");
        btnFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterActionPerformed(evt);
            }
        });

        grpDate.add(rbtnPrev);
        rbtnPrev.setText("Antes de");

        grpDate.add(rbtnEqual);
        rbtnEqual.setSelected(true);
        rbtnEqual.setText("Exacta");

        grpDate.add(rbtnAfter);
        rbtnAfter.setText("Despues de");

        btnTgY.setText("&");
        btnTgY.setToolTipText("Cumplir todos los criterios");
        btnTgY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTgYActionPerformed(evt);
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

        jLabel4.setText("Codigo de producto:");

        txProdCode.setColumns(15);
        txProdCode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txProdCodeKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout pnlFilterLayout = new javax.swing.GroupLayout(pnlFilter);
        pnlFilter.setLayout(pnlFilterLayout);
        pnlFilterLayout.setHorizontalGroup(
            pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFilterLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFilterLayout.createSequentialGroup()
                        .addComponent(jxdfDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcbfKindDte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rbtnPrev)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rbtnEqual)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rbtnAfter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 337, Short.MAX_VALUE)
                        .addComponent(btnFilter)
                        .addContainerGap())
                    .addGroup(pnlFilterLayout.createSequentialGroup()
                        .addComponent(txfSocio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcbfState, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txProdCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnTgO)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnTgY))))
        );
        pnlFilterLayout.setVerticalGroup(
            pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFilterLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txfSocio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jcbfState, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTgY)
                    .addComponent(btnTgO)
                    .addComponent(jLabel4)
                    .addComponent(txProdCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jxdfDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcbfKindDte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rbtnPrev)
                    .addComponent(rbtnEqual)
                    .addComponent(rbtnAfter)
                    .addComponent(btnFilter)))
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

    private void btnTgYActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTgYActionPerformed
        updateWhUnion(true);
    }//GEN-LAST:event_btnTgYActionPerformed

    private void btnTgOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTgOActionPerformed
        updateWhUnion(false);
    }//GEN-LAST:event_btnTgOActionPerformed

    private void txProdCodeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txProdCodeKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            applyFilter();
        }
    }//GEN-LAST:event_txProdCodeKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFilter;
    private javax.swing.JToggleButton btnTgO;
    private javax.swing.JToggleButton btnTgY;
    private javax.swing.ButtonGroup grpDate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JComboBox jcbfKindDte;
    private javax.swing.JComboBox jcbfState;
    private org.jdesktop.swingx.JXDatePicker jxdfDate;
    private javax.swing.JPanel pnlFilter;
    private javax.swing.JRadioButton rbtnAfter;
    private javax.swing.JRadioButton rbtnEqual;
    private javax.swing.JRadioButton rbtnPrev;
    private javax.swing.JTextField txProdCode;
    private javax.swing.JTextField txfSocio;
    // End of variables declaration//GEN-END:variables
}
