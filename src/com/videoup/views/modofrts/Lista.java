/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.views.modofrts;

import com.videoup.controllers.Controller;
import com.videoup.views.utils.ViewLista;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Pedro
 */
public class Lista extends ViewLista{

    /**
     * Creates new form Lista
     */
    public Lista(Controller ctrl){
        super(ctrl);
        initComponents();
        buildTableModel();
        mainQry="select idcof,namer,tpo,tpsv,priority,aux_n,aux_m,aux_d1,aux_d2,";
        mainQry+="apl_lunes,apl_martes,apl_miercoles,apl_jueves,apl_viernes,apl_sabado";
        mainQry+=",apl_domingo,bypuntos,pr_desc,cst_spc from videoup_ctofrentas vo";
        orderby=" order by vo.idcof";
        countQry="select count(*) From videoup_ctofrentas vo";
        reloadTable(0);
        pnlFilter.setVisible(false);
        pnlNorth.add(pnlFilter,BorderLayout.CENTER);
        showBtnPrint(false);
    }

    @Override
    protected void showList(String query,int pag){
        List<Object[]> lista;
        DefaultTableModel tmodel=((DefaultTableModel)tabla.getModel());
        NumberFormat frmCurr=NumberFormat.getCurrencyInstance();
        Object[] rowArray;
        lista=loadListUseSQL(query+whr+orderby,countQry,pag,true);
        String desc;
        String benef;
        if(lista==null){
            ctrl.showDialogErr("Error al cargar la lista: "+error);
            return;
        }
        for(int itr=tmodel.getRowCount();itr>0;itr--){
            tmodel.removeRow(itr-1);
        }
        for(Object[] row: lista){
            rowArray=new Object[4];
            rowArray[0]=new cellKey(row[0],row[1]);
            if(Integer.parseInt(row[2].toString())==1){
                desc="Para alquileres de "+row[5]+" a "+row[6];
            }else if(Integer.parseInt(row[2].toString())==2){
                desc="Para alquileres entre fechas "+row[7]+" y "+row[8];
            }else if(Integer.parseInt(row[2].toString())==3){
                desc="Page "+row[5]+" y llevese "+row[6];
            }else if(Integer.parseInt(row[2].toString())==4){
                desc="Valida en dias "+(row[15].toString().equals("true")?"Domingo ":"")+(row[9].toString().equals("true")?"Lunes ":"");
                desc+=(row[10].toString().equals("true")?"Martes ":"")+(row[11].toString().equals("true")?"Miercoles ":"");
                desc+=(row[12].toString().equals("true")?"Jueves ":"")+(row[13].toString().equals("true")?"Viernes ":"");
                desc+=(row[14].toString().equals("true")?"Sabado ":"");
            }else{
                desc="Valida por "+row[16]+" puntos";
            }
            if(Integer.parseInt(row[3].toString())==1){
                benef="Un "+row[17]+"% de descuento del total";
            }else if(Integer.parseInt(row[3].toString())==2){
                benef="Costo fijo de "+frmCurr.format(row[18]);
            }else{ benef=desc; }
            rowArray[1]=desc;
            rowArray[2]=benef;
            rowArray[3]=row[4];
            tmodel.addRow(rowArray);
        }
        tabla.setModel(tmodel);
    }
    
    @Override
    protected void buildTableModel(){
        tabla.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {"Nombre oferta","Tipo de oferta","Beneficio","Prioridad"}
        ){
            boolean[] canEdit = new boolean [] {false, false, false,false};
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {return canEdit [columnIndex];}
        });
        tabla.setColumnSelectionAllowed(false);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(160);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(310);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(250);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(130);
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
        String nom=txfNombre.getText().trim();
        int typo=jcbTypo.getSelectedIndex();
        whr="";
        if(nom.length()>0){
            whr+=buildLike4Where(nom,"namer");
        }
        if(typo>0){
            whr+=(whr.length()>0?fltUnion:"")+"tpo="+typo;
        }
        if(whr.length()>0){
            whr=" where "+whr;
            countQry="select count(*) from videoup_ctofrentas vo "+whr;
        }else{
            countQry="select count(*) from videoup_ctofrentas vo";
        }
        reloadTable(0);
    }
    
    @Override
    protected void orderByColumn(int col, String ort){
        orderby=" order by";
        if(col==0){
            orderby+=" namer "+ort;
        }else if(col==1){
            orderby+=" tpo "+ort;
        }else if(col==2){
            orderby+=" tpsv "+ort;
        }else if(col==3){
            orderby+=" priority "+ort;
        }else{
            orderby="";
        }
        reloadTable(currPage);
    }
    
    @Override
    protected int deleteSelectedRecords(){
        int[] sels=tabla.getSelectedRows();
        int[] ides=new int[sels.length];
        String toDel1="delete from videoup_rnts_ctprof WHERE;";
        String toDel_1a="delete from videoup_frmts_ctprof WHERE;";
        String toDel_1b="delete from videoup_catgs_ctprof WHERE;";
        String toDel2="delete from videoup_ctofrentas WHERE;";
        int selIdxModel;
        int deleted=0;
        cellKey cell;
        String where=" where ";
        DefaultTableModel tmodel=((DefaultTableModel)tabla.getModel());
        int cnt;
        if(sels.length==0){ return deleted; }
        for(cnt=0;cnt<sels.length;cnt++){
            selIdxModel=tabla.convertRowIndexToModel(sels[cnt]);
            cell=((cellKey)tmodel.getValueAt(selIdxModel,0));
            where+=(cnt==0?"":" or ")+"IDN="+cell.getKey();
            ides[cnt]=cell.getKey();
        }
        toDel1=toDel1.replaceAll("WHERE", where.replaceAll("IDN","idof"));
        toDel_1a=toDel_1a.replaceAll("WHERE", where.replaceAll("IDN","idof"));
        toDel_1b=toDel_1b.replaceAll("WHERE", where.replaceAll("IDN","idof"));
        toDel2=toDel2.replaceAll("WHERE", where.replaceAll("IDN","idcof"));
        if(genDAO.executeSQL(toDel1)<0){ error=genDAO.getError(); return -1; }
        if(genDAO.executeSQL(toDel_1a)<0){ error=genDAO.getError(); return -1; }
        if(genDAO.executeSQL(toDel_1b)<0){ error=genDAO.getError(); return -1; }
        deleted=genDAO.executeSQL(toDel2);
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
        btnFilter = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txfNombre = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jcbTypo = new javax.swing.JComboBox();
        btnTgO = new javax.swing.JToggleButton();
        btnTgY = new javax.swing.JToggleButton();

        pnlFilter.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        btnFilter.setText("Filtrar");
        btnFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterActionPerformed(evt);
            }
        });

        jLabel1.setText("Nombre:");

        txfNombre.setColumns(9);
        txfNombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txfNombreKeyPressed(evt);
            }
        });

        jLabel2.setText("Tipo de oferta:");

        jcbTypo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "--", "De N a M", "Por fechas", "Pague N x M", "Por dias de la semana", "Por puntos" }));

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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlFilterLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txfNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcbTypo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 95, Short.MAX_VALUE)
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
                        .addComponent(jLabel1)
                        .addComponent(txfNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(jcbTypo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnTgY)
                        .addComponent(btnTgO))
                    .addComponent(btnFilter)))
        );

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    private void btnFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterActionPerformed
        applyFilter();
    }//GEN-LAST:event_btnFilterActionPerformed

    private void txfNombreKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txfNombreKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            applyFilter();
        }
    }//GEN-LAST:event_txfNombreKeyPressed

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
    private javax.swing.JComboBox jcbTypo;
    private javax.swing.JPanel pnlFilter;
    private javax.swing.JTextField txfNombre;
    // End of variables declaration//GEN-END:variables
}
