/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.views.utils;

import com.videoup.controllers.Controller;
import com.videoup.utils.GeneralDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlighterFactory;

/**
 *
 * @author Pedro
 */
public class ViewLista extends View{
    
    protected String mainQry;
    protected String countQry;
    protected int currPage;
    private int maxPage;
    protected int tPages;
    protected String orderby;
    protected String whr;
    protected String delQry;
    protected String idn;
    protected String fltUnion;
    
    /**
     * Creates new form ViewLista
     */
    public ViewLista(Controller ctrl) {
        super(ctrl,-1);
        initComponents();
        lblHighLtr.setVisible(false);
        genDAO=new GeneralDAO();
        countQry=null;
        delQry=null;
        idn=null;
        orderby="";
        whr="";
        fltUnion=" or ";
        lblTitle.setText(getTitle());
        JTableHeader header = tabla.getTableHeader();
        header.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent event){
                int columna=tabla.columnAtPoint(event.getPoint());
                callOrderByColumn(columna);
            }
        });
    }

    protected void builHighLighters(TreeMap<String,String> names){
        String names2Show="<html>Resaltado:<br />";
        HighlightPredicate hlGray;
        ColorHighlighter hlColGray;
        HighlightPredicate hlRed;
        ColorHighlighter hlColRed;
        HighlightPredicate hlGreen;
        ColorHighlighter hlColGreen;
        HighlightPredicate hlBlue;
        ColorHighlighter hlColBlue;
        HighlightPredicate hlBlue2;
        ColorHighlighter hlColBlue2;
        HighlightPredicate hlOrange;
        ColorHighlighter hlColOrange;
        hlGray=new HighlightPredicate(){
            @Override
            public boolean isHighlighted(Component renderer, ComponentAdapter adapter){
                cellKey ck=((cellKey)adapter.getValueAt(adapter.row,0));
                return ck.isHighlight("hlGray");
            }
        };
        hlRed=new HighlightPredicate(){
            @Override
            public boolean isHighlighted(Component renderer, ComponentAdapter adapter){
                cellKey ck=((cellKey)adapter.getValueAt(adapter.row,0));
                return ck.isHighlight("hlRed");
            }
        };
        hlGreen=new HighlightPredicate(){
            @Override
            public boolean isHighlighted(Component renderer, ComponentAdapter adapter){
                cellKey ck=((cellKey)adapter.getValueAt(adapter.row,0));
                return ck.isHighlight("hlGreen");
            }
        };
        hlBlue=new HighlightPredicate(){
            @Override
            public boolean isHighlighted(Component renderer, ComponentAdapter adapter){
                cellKey ck=((cellKey)adapter.getValueAt(adapter.row,0));
                return ck.isHighlight("hlBlue");
            }
        };
        hlBlue2=new HighlightPredicate(){
            @Override
            public boolean isHighlighted(Component renderer, ComponentAdapter adapter){
                cellKey ck=((cellKey)adapter.getValueAt(adapter.row,0));
                return ck.isHighlight("hlBlue2");
            }
        };
        hlOrange=new HighlightPredicate(){
            @Override
            public boolean isHighlighted(Component renderer, ComponentAdapter adapter){
                cellKey ck=((cellKey)adapter.getValueAt(adapter.row,0));
                return ck.isHighlight("hlOrange");
            }
        };
        hlColGray=new ColorHighlighter(hlGray,new Color(255,255,185),new Color(89,79,99));
        hlColRed=new ColorHighlighter(hlRed,new Color(205,55,15),new Color(245,235,242));
        hlColGreen=new ColorHighlighter(hlGreen,new Color(217,255,199),new Color(0,37,0));
        hlColBlue=new ColorHighlighter(hlBlue,new Color(190,215,255),new Color(0,0,99));
        hlColBlue2=new ColorHighlighter(hlBlue2,new Color(120,135,255),new Color(250,250,249));
        hlColOrange=new ColorHighlighter(hlOrange,new Color(255,180,90),new Color(87,9,0));
        tabla.addHighlighter(HighlighterFactory.createSimpleStriping(HighlighterFactory.LEDGER));
        tabla.addHighlighter(hlColGreen);
        if(names.get("hlGreen")!=null){
            names2Show+="<span style=\"background:rgb(217,255,199);color:rgb(0,37,0);\">"+names.get("hlGreen")+"</span><br />";
        }
        tabla.addHighlighter(hlColBlue);
        if(names.get("hlBlue")!=null){
            names2Show+="<span style=\"background:rgb(190,215,255);color:rgb(0,0,99);\">"+names.get("hlBlue")+"</span><br />";
        }
        tabla.addHighlighter(hlColBlue2);
        if(names.get("hlBlue2")!=null){
            names2Show+="<span style=\"background:rgb(120,135,255);color:rgb(250,250,249);\">"+names.get("hlBlue2")+"</span><br />";
        }
        tabla.addHighlighter(hlColGray);
        if(names.get("hlGray")!=null){
            names2Show+="<span style=\"background:rgb(255,255,185);color:rgb(89,79,99);\">"+names.get("hlGray")+"</span><br />";
        }
        tabla.addHighlighter(hlColRed);
        if(names.get("hlRed")!=null){
            names2Show+="<span style=\"background:rgb(205,55,15);color:rgb(245,235,242);\">"+names.get("hlRed")+"</span><br />";
        }
        tabla.addHighlighter(hlColOrange);
        if(names.get("hlOrange")!=null){
            names2Show+="<span style=\"background:rgb(255,180,90);color:rgb(87,9,0);\">"+names.get("hlOrange")+"</span><br />";
        }
        lblHighLtr.setToolTipText(names2Show+"</html>");
        lblHighLtr.setText("<html><span style=\"color:rgb(255,0,0);\">H</span><span style=\"color:rgb(0,135,0);\">L</span><span style=\"color:rgb(0,0,255);\">T</span></html>");
        lblHighLtr.setVisible(true);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jxbLbl = new org.jdesktop.swingx.JXBusyLabel();
        pnlNorth = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        lblHighLtr = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        btnVSel = new javax.swing.JButton();
        btnDel = new javax.swing.JButton();
        btnFiltra = new javax.swing.JButton();
        btnNew = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        jspTabla = new javax.swing.JScrollPane();
        tabla = new org.jdesktop.swingx.JXTable();
        pnlSouth = new javax.swing.JPanel();
        btnFirstPg = new javax.swing.JButton();
        btnPrevPg = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        btnCurrPg = new javax.swing.JButton();
        lblPages = new javax.swing.JLabel();
        btnNextPg = new javax.swing.JButton();
        btnLastPg = new javax.swing.JButton();

        jxbLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jxbLbl.setText("Cargando lista");
        jxbLbl.setFont(new java.awt.Font("Tahoma", 0, 23)); // NOI18N

        setLayout(new java.awt.BorderLayout());

        pnlNorth.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED, java.awt.Color.darkGray, java.awt.Color.lightGray));
        pnlNorth.setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.BorderLayout());

        lblHighLtr.setFont(new java.awt.Font("Tahoma", 1, 17)); // NOI18N
        lblHighLtr.setText("HGL");
        jPanel2.add(lblHighLtr, java.awt.BorderLayout.WEST);

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 6, 3));

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTitle.setText("Title");
        jPanel1.add(lblTitle);

        btnVSel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/videoup/imgs/menu.png"))); // NOI18N
        btnVSel.setToolTipText("Vistas abiertas");
        btnVSel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVSelActionPerformed(evt);
            }
        });
        jPanel1.add(btnVSel);

        btnDel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/videoup/imgs/trash.png"))); // NOI18N
        btnDel.setToolTipText("Eliminar registros seleccionados");
        btnDel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelActionPerformed(evt);
            }
        });
        jPanel1.add(btnDel);

        btnFiltra.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/videoup/imgs/buscar.png"))); // NOI18N
        btnFiltra.setToolTipText("Filtrar registros");
        btnFiltra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltraActionPerformed(evt);
            }
        });
        jPanel1.add(btnFiltra);

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/videoup/imgs/nuevo.png"))); // NOI18N
        btnNew.setToolTipText("Nuevo registro");
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        jPanel1.add(btnNew);

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/videoup/imgs/print.png"))); // NOI18N
        btnPrint.setToolTipText("Imprimir lista");
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        jPanel1.add(btnPrint);

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/videoup/imgs/close.png"))); // NOI18N
        btnClose.setToolTipText("Cerrar listado");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jPanel1.add(btnClose);

        jPanel2.add(jPanel1, java.awt.BorderLayout.CENTER);

        pnlNorth.add(jPanel2, java.awt.BorderLayout.NORTH);

        add(pnlNorth, java.awt.BorderLayout.NORTH);

        tabla.setAutoCreateRowSorter(false);
        tabla.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tabla.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tabla.setColumnSelectionAllowed(true);
        tabla.setRowSorter(null);
        tabla.setSelectionBackground(new java.awt.Color(21, 21, 17));
        tabla.setSelectionForeground(new java.awt.Color(254, 254, 254));
        tabla.setSortable(false);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaMouseClicked(evt);
            }
        });
        jspTabla.setViewportView(tabla);
        tabla.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        add(jspTabla, java.awt.BorderLayout.CENTER);

        pnlSouth.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED, java.awt.Color.darkGray, java.awt.Color.lightGray));

        btnFirstPg.setText("|<");
        btnFirstPg.setToolTipText("Primer pagina");
        btnFirstPg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFirstPgActionPerformed(evt);
            }
        });
        pnlSouth.add(btnFirstPg);

        btnPrevPg.setText("<");
        btnPrevPg.setToolTipText("Pagina anterior");
        btnPrevPg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrevPgActionPerformed(evt);
            }
        });
        pnlSouth.add(btnPrevPg);

        jLabel1.setText("pagina");
        pnlSouth.add(jLabel1);

        btnCurrPg.setText("X");
        btnCurrPg.setToolTipText("Recargar pagina actual");
        btnCurrPg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCurrPgActionPerformed(evt);
            }
        });
        pnlSouth.add(btnCurrPg);

        lblPages.setText("X de Y");
        pnlSouth.add(lblPages);

        btnNextPg.setText(">");
        btnNextPg.setToolTipText("Siguiente pagina");
        btnNextPg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextPgActionPerformed(evt);
            }
        });
        pnlSouth.add(btnNextPg);

        btnLastPg.setText(">|");
        btnLastPg.setToolTipText("Ultima pagina");
        btnLastPg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLastPgActionPerformed(evt);
            }
        });
        pnlSouth.add(btnLastPg);

        add(pnlSouth, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void tablaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaMouseClicked
        int fila=tabla.rowAtPoint(evt.getPoint());
        int columna=tabla.columnAtPoint(evt.getPoint());
        if(fila<0 || columna<0){
            return;
        }
        fila=tabla.convertRowIndexToModel(fila);
        Object firstCell;
        DefaultTableModel tmodel=((DefaultTableModel)tabla.getModel());
        if (evt.getClickCount()==2 && (fila > -1) && (columna > -1)){
            firstCell=tmodel.getValueAt(fila,0);
            loadRecord( (cellKey)firstCell );
        }
    }//GEN-LAST:event_tablaMouseClicked
    
    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        closeMe();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        ctrl.loadNewFicha();
    }//GEN-LAST:event_btnNewActionPerformed

    private void btnFiltraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltraActionPerformed
        showFilter();
    }//GEN-LAST:event_btnFiltraActionPerformed

    private void btnDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelActionPerformed
        if(ctrl.confirm("Va a eliminar los registros seleccionados", "Esta accion NO se podra deshacer, ¿desea continuar?")){
            try {
                LoadingTask task=new LoadingTask();
                setBusy(true,"Eliminando registros");
                task.setTask(2); task.execute();
                task.get();
            }catch (InterruptedException ex){
                //ex.printStackTrace();
            }catch (ExecutionException ex){
                //ex.printStackTrace();
            }finally{ reloadTable(0); }
        }
    }//GEN-LAST:event_btnDelActionPerformed

    private void btnVSelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVSelActionPerformed
        ctrl.goToViewsSel();
    }//GEN-LAST:event_btnVSelActionPerformed

    private void btnLastPgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLastPgActionPerformed
        loadPage(3);
    }//GEN-LAST:event_btnLastPgActionPerformed

    private void btnNextPgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextPgActionPerformed
        loadPage(2);
    }//GEN-LAST:event_btnNextPgActionPerformed

    private void btnCurrPgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCurrPgActionPerformed
        loadPage(4);
    }//GEN-LAST:event_btnCurrPgActionPerformed

    private void btnPrevPgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrevPgActionPerformed
        loadPage(1);
    }//GEN-LAST:event_btnPrevPgActionPerformed

    private void btnFirstPgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFirstPgActionPerformed
        loadPage(0);
    }//GEN-LAST:event_btnFirstPgActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        send2Print();
    }//GEN-LAST:event_btnPrintActionPerformed

    protected void printNow(ArrayList fields){
        System.out.println("Codificar printNow");
    }
    
    protected void showBtnPrint(boolean print){
        btnPrint.setEnabled(print);
        btnPrint.setVisible(print);
    }
    
    protected void loadRecord(cellKey ckey){
    }
    
    protected void showFilter(){
    }
    
    protected void applyFilter(){
    }
    
    protected int deleteSelectedRecords(){
        int[] sels=tabla.getSelectedRows();
        int[] ides=new int[sels.length];
        String toDel;
        int selIdxModel;
        int deleted=0;
        cellKey cell;
        String where=" where ";
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
    
    protected void reloadTable(int pag){
        LoadingTask task=new LoadingTask();
        setBusy(true,"Cargando lista");
        task.setQuery(mainQry); task.setTask(1);
        task.setPage(pag); task.execute();
    }
    
    protected void showList(String query,int pag){}
    
    protected void showPags(int page){
        tPages=genDAO.getLastTotalPages();
        currPage=page;
        maxPage=tPages;
        lblPages.setText(" de "+tPages);
        lblPages.setToolTipText("Total "+genDAO.getLastTotalrecs()+" registros");
        btnCurrPg.setText(""+(currPage+(tPages>0?1:0)));
        btnCurrPg.setToolTipText("Registros por pagina "+genDAO.getPageSize());
        btnFirstPg.setEnabled(page>0);
        btnPrevPg.setEnabled(page>1);
        btnNextPg.setEnabled(tPages>(page+1));
        btnLastPg.setEnabled(tPages>(page+1));
    }
    
    public void setAndApplyFilter(String flt){
    }
    
    protected void updateRecords(String query, String idn){
        LoadingTask task=new LoadingTask();
        setBusy(true,"Actualizando registros");
        task.setTask(3); task.setQuery(query); 
        task.setIdn(idn); task.execute();
    }
    
    protected boolean updateList(String query, String idn){
        String upds;
        int cnt;
        for(cnt=0;cnt<tPages;cnt++){
            upds=loadIdListUseSQL(query,cnt,true);
            if(upds==null){
                ctrl.showDialogErr("<html>Error: "+error+"</html>");
                return false;
            }
            if(!updatePage(upds,idn)){
                ctrl.showDialogErr("<html>Error: "+error+"</html>");
                return false;
            }
        }
        return true;
    }
    
    protected boolean updatePage(String ids,String idn){
        error="UpdatePage no implemented";
        return false;
    }
    
    protected void send2Print(){
    }
    
    protected void printRecords(String query){
        LoadingTask task=new LoadingTask();
        setBusy(true,"Concentrando informacion");
        task.setTask(4); task.setQuery(query); task.execute();
    }
    
    protected boolean printList(String qry){
        ArrayList fields=getListCampos(qry);
        if(fields==null){
            ctrl.showDialogErr("<html>Error: "+error+"</html>");
            return false;
        }
        printNow(fields);
        return true;
    }
    
    protected ArrayList getListCampos(String query){
        error="Not implemented getListCampos";
        return null;
    }
    
    /**
     * 
     * @param numpg where 0=first, 1=prev, 2=next, 3=last, 4=current
     */
    protected void loadPage(int num){
        int pg2load=0;
        if(num==1){ pg2load=currPage-1; }
        else if(num==2){ pg2load=currPage+1; }
        else if(num==3){ pg2load=(maxPage-1); }
        else if(num==4){ pg2load=currPage; }
        reloadTable(pg2load);
    }
    
    protected void buildTableModel(){}
    
    private void callOrderByColumn(int col){
        TableColumnModel tcm=tabla.getTableHeader().getColumnModel();
        TableColumn tc=tcm.getColumn(col);
        String tx=tc.getHeaderValue().toString();
        String txTit=tx.split("-")[0];
        String orientation;
        clearColumnNames();
        if(tx.contains("\u25b2")){
            tc.setHeaderValue(txTit+" - \u25bc");
            orientation="desc";
        }else{
            tc.setHeaderValue(txTit+" - \u25b2");
            orientation="asc";
        }
        orderByColumn(col, orientation);
    }
    
    protected void orderByColumn(int col, String ort){
        System.out.println(" Sort column: "+col+" "+ort);
    }
    
    protected void setBusy(boolean busy, String message){
        this.setVisible(false);
        jxbLbl.setText(message);
        if(busy){
            remove(jspTabla);
            remove(pnlNorth);
            remove(pnlSouth);
            add(jxbLbl,BorderLayout.CENTER);
            jxbLbl.setBusy(true);
        }else{
            jxbLbl.setBusy(false);
            remove(jxbLbl);
            add(jspTabla,BorderLayout.CENTER);
            add(pnlNorth,BorderLayout.NORTH);
            add(pnlSouth,BorderLayout.SOUTH);
        }
        this.setVisible(true);
    }
    
    @Override
    public String getTitle(){
        return "Listado de "+ctrl.getTitulo();
    }
    
    protected void clearColumnNames(){
        TableColumnModel tcm=tabla.getTableHeader().getColumnModel();
        TableColumn tc;
        int col;
        String tx;
        String txTit;
        for(col=0;col<tabla.getColumnCount();col++){
            tc=tcm.getColumn(col);
            tx=tc.getHeaderValue().toString();
            txTit=tx.split("-")[0];
            tc.setHeaderValue(txTit);
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnCurrPg;
    private javax.swing.JButton btnDel;
    private javax.swing.JButton btnFiltra;
    private javax.swing.JButton btnFirstPg;
    private javax.swing.JButton btnLastPg;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnNextPg;
    private javax.swing.JButton btnPrevPg;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnVSel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jspTabla;
    private org.jdesktop.swingx.JXBusyLabel jxbLbl;
    private javax.swing.JLabel lblHighLtr;
    private javax.swing.JLabel lblPages;
    private javax.swing.JLabel lblTitle;
    protected javax.swing.JPanel pnlNorth;
    private javax.swing.JPanel pnlSouth;
    protected org.jdesktop.swingx.JXTable tabla;
    // End of variables declaration//GEN-END:variables

    private class LoadingTask extends SwingWorker<Void, Void>{
        private int task; // 1=showList, 2=deleteSelectedRecords, 3=updateList, 4=printList
        private int pag;
        private String qry=null;
        private String idn=null;
        public void setTask(int t){ task=t; }
        public void setPage(int p){ pag=p; }
        public void setQuery(String q){ qry=q; }
        public void setIdn(String idn) { this.idn = idn; }
        @Override
        protected Void doInBackground() throws Exception{
            if(task==1){
                showList(qry,pag);
                showPags(pag);
            }else if(task==2){
                if(deleteSelectedRecords()<0){
                    showError(error);
                }
            }else if(task==3){
                if(!updateList(qry,idn)){
                    showError(error);
                }else{
                    loadPage(4);
                    showInfo("Accion realizada","Todos los registros se han actualizado");
                }
            }else if(task==4){
                if(!printList(qry)){
                    showError(error);
                }
            }
            return null;
        }
        @Override
        protected void done() { setBusy(false,"....."); }
    }

    protected class cellKey{
        private String rep;
        private int key;
        private TreeMap<String,Boolean> highLtrs;

        public cellKey(Object key, Object rep) {
            this.rep = rep.toString();
            this.key = Integer.parseInt(key.toString());
            highLtrs=null;
        }
        
        @Override
        public String toString() {
            return rep;
        }

        public int getKey() {
            return key;
        }
        
        public void addHighLighter(String key, boolean val){
            if(highLtrs==null){
                highLtrs=new TreeMap<String,Boolean>();
            }
            highLtrs.put(key,val);
        }
        
        public boolean isHighlight(String k){
            if(highLtrs==null){
                return false;
            }
            if(highLtrs.get(k)!=null){
                return highLtrs.get(k);
            }
            return false;
        }
        
    }
        
}
