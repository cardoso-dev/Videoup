/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.views.modreps;

import com.videoup.controllers.Controller;
import com.videoup.entities.VideoupTagitems;
import com.videoup.entities.VideoupTags;
import com.videoup.views.utils.ItemTag;
import com.videoup.views.utils.TagSingle;
import com.videoup.views.utils.ViewReport;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JPanel;
import org.netbeans.lib.awtextra.AbsoluteConstraints;

/**
 *
 * @author Pedro
 */
public class RTagProds extends ViewReport{

    private JPanel[] sheets;
    private boolean reloadBCodes;
    private List<Object[]> codeLst;
    private  VideoupTags vtags;
    private String table;
    private String tRela;
    private String idName;
    private boolean isMov;

    /**
     * Creates new form RTagProds
     */
    public RTagProds(Controller ctrl, int id, String prod,boolean movs) {
        super(ctrl,id,"Etiquetas de "+prod);
        initComponents();
        initComps();
        isMov=movs;
        table=(movs?"videoup_movies":"videoup_games");
        tRela=(movs?"videoup_bcdmov":"videoup_bcdgam");
        idName=(movs?"idcm":"idca");
        jLabel7.setText("Codigo(s) de "+prod+" para imprimir:");
        reloadBCodes=true;
        lblInfPreview.setText("");
    }

    private void initComps(){
        setCtrlPanel(CtrlPan);
        setContentPanel(mainContain);
    }
    
    @Override
    protected void generate(){
        ArrayList<TagSingle> tagList;
        int ttlTags;
        int rowsXSheet;
        int totalRows;
        int hSDisp;
        int nSheets;
        int gasto;
        boolean flag=true;
        tagList=getTags();
        if(tagList==null){ return; }
        ttlTags=tagList.size();
        int wSDisp=Integer.parseInt(jspnWd.getValue().toString())-
                ( Integer.parseInt(jspnSMiz.getValue().toString())+Integer.parseInt(jspnSMder.getValue().toString()) );
        // calculate cols that can be used
        int cols=0;
        while(flag){
            gasto=((cols+1)*tagList.get(0).getWidhtMM())+(cols*Integer.parseInt(jspnMTleft.getValue().toString()));
            if(gasto<=wSDisp){ cols++; }else{ flag=false; }
        }
        if(cols<=0){
            lblInfPreview.setText("<html><p style=\"color:red;\">Espacio insuficiente modifique margenes o tamaño de la hoja</p></html>");
            showError("Espacio insuficiente modifique margenes o tamaño de la hoja");
            return;
        }// calculate rows in a sheet and rtows to be needed
        hSDisp=Integer.parseInt(jspnHg.getValue().toString())-
                ( Integer.parseInt(jspnSMtop.getValue().toString())+Integer.parseInt(jspnSMbtt.getValue().toString()) );
        rowsXSheet=0; flag=true;
        while(flag){
            gasto=((rowsXSheet+1)*tagList.get(0).getHeightMM())+(rowsXSheet*Integer.parseInt(jspnMTtop.getValue().toString()));
            if(gasto<=hSDisp){ rowsXSheet++; }else{ flag=false; }
        }
        totalRows=(int)Math.ceil((float)ttlTags/(float)cols);
        // calculate sheets to use
        nSheets=(int)Math.ceil((float)totalRows/(float)rowsXSheet);
        loadSheets(nSheets); showTags(tagList,cols,rowsXSheet);
        rdy2Print=(ttlTags>0);
    }
    
    private void loadSheets(int num){
        int wd=Integer.parseInt(jspnWd.getValue().toString());
        int hg=Integer.parseInt(jspnHg.getValue().toString());
        Dimension dim=new Dimension(mm2pixels(wd),mm2pixels(hg));
        GridBagConstraints gbc=new GridBagConstraints();
        gbc.gridx=0; gbc.insets=new java.awt.Insets(3,2,3,2); pnlSheets.removeAll();
        sheets=new JPanel[num];
        for(int k=0;k<num;k++){
            sheets[k]=new JPanel(); sheets[k].setPreferredSize(dim);
            sheets[k].setMaximumSize(dim); sheets[k].setMinimumSize(dim);
            sheets[k].setBackground(new java.awt.Color(255, 255, 255));
            sheets[k].setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(27, 27, 27)));
            sheets[k].setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
            gbc.gridy=k; pnlSheets.add(sheets[k],gbc);
        } validate();
    }
    
    private void showTags(ArrayList<TagSingle> allTags, int cols, int rowsSheet){
        int tt=allTags.size();
        int cSheet=0;
        TagSingle tgs;
        int mSTop=mm2pixels(Integer.parseInt(jspnSMtop.getValue().toString()));
        int mSLeft=mm2pixels(Integer.parseInt(jspnSMiz.getValue().toString()));
        int col=0;
        int mTgLeft=mm2pixels(Integer.parseInt(jspnMTleft.getValue().toString()));
        int row=0;
        int mTgTop=mm2pixels(Integer.parseInt(jspnMTtop.getValue().toString()));
        int cx;
        int cy;
        for(int g=0;g<tt;g++){
            tgs=allTags.get(g);
            if(col>0){
                cx=(col*(tgs.getPreferredSize().width+mTgLeft))+mTgLeft+mSLeft;
            }else{ cx=mSLeft; }
            if(row>0){
                cy=(row*(tgs.getPreferredSize().height+mTgTop))+mTgTop+mSTop;
            }else{ cy=mSTop; }
            tgs.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(189, 189, 189)));
            sheets[cSheet].add(tgs,new AbsoluteConstraints(cx,cy));
            col++;
            if(col==cols){ col=0; row++;
                if(row==rowsSheet){ row=0; cSheet++; }
            }
        }
        lblInfPreview.setText("Vista previa generada ("+sheets.length+" paginas, "+(cols*rowsSheet)+" etiquetas por pagina)");
        validate();
    }
    
    private ArrayList<TagSingle> getTags(){
        ArrayList<TagSingle> tags;
        List<VideoupTagitems> items;
        VideoupTagitems itm;
        TagSingle aTag;
        ItemTag c_item;
        Dimension dim;
        if(reloadBCodes){ updateBcodes(); }
        if(codeLst==null){ return null; }
        tags=new ArrayList<TagSingle>();
        items=getTagsInfo();
        if(items==null){ return null; }
        for(int h=0;h<codeLst.size();h++){
            aTag=new TagSingle(); aTag.settTagSheet(this);
            for(int i=0;i<items.size();i++){
                itm=items.get(i);
                if(codeLst.get(h)!=null){
                    c_item=new ItemTag();
                    c_item.cloneVideoupTagitems(itm);
                    if(itm.getItemtype()==2){
                        c_item.setBcValue(codeLst.get(h)[4].toString());
                    }else if(itm.getItemtype()==4){
                        c_item.setText(codeLst.get(h)[0].toString());
                    }else if(itm.getItemtype()==5){
                        c_item.setText( codeLst.get(h)[1]==null?"":codeLst.get(h)[1].toString() );
                    }else if(itm.getItemtype()==6){
                        c_item.setText( codeLst.get(h)[2]==null?"":codeLst.get(h)[2].toString() );
                    }else if(itm.getItemtype()==7){
                        c_item.setText( codeLst.get(h)[3]==null?"":codeLst.get(h)[3].toString() );
                    }
                    aTag.addNewItem(i, c_item); aTag.showOpcs(true);
                }
            }
            dim=new Dimension(mm2pixels(vtags.getWidth()),mm2pixels(vtags.getHeight()));
            aTag.setPreferredSize(dim); aTag.setMinimumSize(dim); aTag.setMaximumSize(dim);
            aTag.setDimMM(vtags.getWidth(),vtags.getHeight());
            tags.add(aTag); 
        }
        reloadBCodes=true;
        return tags;
    }
    
    private void updateBcodes(){
        String cons;
        String counts;
        String where;
        codeLst=new ArrayList<Object[]>();
        counts="select count(distinct vm."+idName+") from "+table+" vm,videoup_bcodes vb,"+tRela+" vr ";
        counts+=" where vb.idbc=vr.idbc and vr."+idName+"=vm."+idName+" and ";
        cons="select vm.titulo,vm.clasif,vc.catg,vf.frmt,vb.barcode from "+table+" vm ";
        cons+="left join videoup_catgs vc on vm.catg=vc.idcg join "+tRela+" vr on vr."+idName+"=vm."+idName+" ";
        cons+="join videoup_bcodes vb on vb.idbc=vr.idbc left join videoup_formts vf on vf.idcf=vb.frmt";
        cons+=" where ";
        where=getWhereByCode("vb.barcode");
        if(where==null){ codeLst=null; return; }
        codeLst=loadListUseSQL(cons+where,counts+where,0,false);
        if(codeLst==null){
            lblInfPreview.setText("<html><p style=\"color:red;\">Error: "+getError()+"</p></html>"); 
            showError("Error: "+getError());
        }
        if(codeLst.isEmpty()){ 
            lblInfPreview.setText("<html><p style=\"color:red;\">No se encontraron coincidencias con los parametros proporcionados</p></html>");
            showError("No se encontraron coincidencias con los parametros proporcionados");
            codeLst=null; 
        }
    }
    
    private String getWhereByCode(String fName){
        String where=null;
        StringTokenizer tks;
        if(rbtnID.isSelected() && !jtxID.getText().trim().equals("")){
            where="upper("+fName+")='"+jtxID.getText().trim().toUpperCase()+"'";
        }else if(rbtnID.isSelected() && jtxID.getText().trim().equals("")){
            lblInfPreview.setText("<html><p style=\"color:red;\">Codigo unico no especificado</p></html>");
            showError("Codigo unico no especificado");
        }else if(rbtnIdMul.isSelected() && jtxIdMulti.getText().trim().equals("")){
            lblInfPreview.setText("<html><p style=\"color:red;\">Codigos especificos debe indicar varios codigos separados por una coma o espacio</p></html>");
            showError("Codigos especificos debe indicar varios codigos separados por una coma o espacio");
        }else if(rbtnIdMul.isSelected() && !jtxIdMulti.getText().trim().equals("")){
            tks=new StringTokenizer(jtxIdMulti.getText().trim(),", ");
            where="";
            while(tks.hasMoreTokens()){
                where+=(where.equals("")?"":" or ")+"upper("+fName+")='"+tks.nextToken().toUpperCase()+"'";
            }
        }
        return (where==null?null:"("+where+")");
    }
    
    private List<VideoupTagitems> getTagsInfo(){
        vtags=(VideoupTags)getEntity("from VideoupTags where idtg="+(isMov?"1":"2"));
        if(vtags==null){
            showError("Error al obtener informacion de etiquetas");
            return null;
        }
        return vtags.getVideoupTagitemsList();
    }
    
    @Override
    public void insertNullBCode(String bcode, boolean replace){
        int tt;
        if(codeLst!=null && bcode!=null){
            tt=codeLst.size();
            for(int h=0;h<tt;h++){
                if(codeLst.get(h)==null){ continue; }
                if(codeLst.get(h)[4].toString().equals(bcode) && replace){
                    codeLst.set(h, null); break;
                }else if(codeLst.get(h)[4].toString().equals(bcode) && !replace){
                    codeLst.add(h, null); break;
                }
            }
            reloadBCodes=false;
            btnGen.doClick();
        }
    }
    
    @Override
    protected boolean validateInput(){
        if(rbtnID.isSelected() && jtxID.getText().trim().equals("")){
            error="Codigo unico no especificado";
            return false;
        }
        if(rbtnIdMul.isSelected() && jtxIdMulti.getText().trim().equals("")){
            error="Codigos no especificados";
            return false;
        }
        return true;
    }
    
    @Override
    protected void printNow(){
        PrintTSheet printr=new PrintTSheet();
        int num=sheets.length;
        for(int k=0;k<num;k++){ sheets[k].setBorder(null); setPrintingTags(sheets[k],true); }
        printr.printComponent(sheets,
                pixels2mm(sheets[0].getPreferredSize().width),
                pixels2mm(sheets[0].getPreferredSize().height) );
        for(int k=0;k<num;k++){
            setPrintingTags(sheets[k],false);
            sheets[k].setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(27, 27, 27)));
        }
    }
    
    private void setPrintingTags(JPanel sheet, boolean print){
        Component[] comps=sheet.getComponents();
        for(int b=0;b<comps.length;b++){
            if(comps[b] instanceof TagSingle){
                ((TagSingle)comps[b]).setPrinting(print);
            }
        }
    }
    
    public int pixels2mm(int pxl){
        int dpi=72; // getToolkit().getScreenResolution();
        double fact=dpi/25.4f;
        return (int)(pxl/fact);
    }
    
    public void builFromCodes(String codes){
        String[] singles;
        codes=codes.replaceAll("or","");
        singles=codes.split(" ");
        if(singles.length==1){
            jtxID.setText(codes);
            rbtnID.setSelected(true);
        }else{
            jtxIdMulti.setText(codes);
            rbtnIdMul.setSelected(true);
        }
        btnGen.doClick();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        CtrlPan = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        rbtnID = new javax.swing.JRadioButton();
        rbtnIdMul = new javax.swing.JRadioButton();
        jtxID = new javax.swing.JTextField();
        jtxIdMulti = new javax.swing.JTextField();
        lblInfPreview = new javax.swing.JLabel();
        grpRbtnTpCode = new javax.swing.ButtonGroup();
        mainContain = new javax.swing.JPanel();
        scroller = new javax.swing.JScrollPane();
        contain = new javax.swing.JPanel();
        pnlSheets = new javax.swing.JPanel();
        pnlMetrics = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jspnMTtop = new javax.swing.JSpinner();
        jLabel13 = new javax.swing.JLabel();
        jspnMTleft = new javax.swing.JSpinner();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jspnHg = new javax.swing.JSpinner();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jspnWd = new javax.swing.JSpinner();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jspnSMiz = new javax.swing.JSpinner();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jspnSMder = new javax.swing.JSpinner();
        jLabel18 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jspnSMtop = new javax.swing.JSpinner();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jspnSMbtt = new javax.swing.JSpinner();
        jLabel9 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();

        jLabel7.setText("Codigo(s) de pelicula para imprimir:");

        grpRbtnTpCode.add(rbtnID);
        rbtnID.setSelected(true);
        rbtnID.setText("Codigo unico:");

        grpRbtnTpCode.add(rbtnIdMul);
        rbtnIdMul.setText("Especificos:");

        jtxID.setColumns(11);
        jtxID.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxIDFocusGained(evt);
            }
        });

        jtxIdMulti.setColumns(32);
        jtxIdMulti.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxIdMultiFocusGained(evt);
            }
        });

        lblInfPreview.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        lblInfPreview.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblInfPreview.setText("jLabel1");

        javax.swing.GroupLayout CtrlPanLayout = new javax.swing.GroupLayout(CtrlPan);
        CtrlPan.setLayout(CtrlPanLayout);
        CtrlPanLayout.setHorizontalGroup(
            CtrlPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CtrlPanLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel7)
                .addContainerGap(371, Short.MAX_VALUE))
            .addGroup(CtrlPanLayout.createSequentialGroup()
                .addGroup(CtrlPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(CtrlPanLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(CtrlPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rbtnID)
                            .addComponent(rbtnIdMul))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(CtrlPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxIdMulti)))
                    .addGroup(CtrlPanLayout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(lblInfPreview, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        CtrlPanLayout.setVerticalGroup(
            CtrlPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CtrlPanLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(CtrlPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbtnID)
                    .addComponent(jtxID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(CtrlPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbtnIdMul)
                    .addComponent(jtxIdMulti, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblInfPreview)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mainContain.setLayout(new java.awt.BorderLayout());

        contain.setLayout(new java.awt.BorderLayout());

        pnlSheets.setBorder(javax.swing.BorderFactory.createTitledBorder("Vista previa"));
        pnlSheets.setLayout(new java.awt.GridBagLayout());
        contain.add(pnlSheets, java.awt.BorderLayout.CENTER);

        pnlMetrics.setBorder(javax.swing.BorderFactory.createTitledBorder("Metricas"));

        jLabel21.setText("Izq:");

        jLabel20.setText("Sup:");

        jspnMTtop.setModel(new javax.swing.SpinnerNumberModel(10, 0, 100, 1));

        jLabel13.setText("Margenes entre etiquetas:");

        jspnMTleft.setModel(new javax.swing.SpinnerNumberModel(10, 0, 100, 1));

        jLabel14.setText("mm");

        jLabel15.setText("mm");

        jLabel3.setText("Alto:");

        jspnHg.setModel(new javax.swing.SpinnerNumberModel(275, 0, 550, 1));
        jspnHg.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jspnHgStateChanged(evt);
            }
        });

        jLabel5.setText("mm");

        jLabel6.setText("mm");

        jspnWd.setModel(new javax.swing.SpinnerNumberModel(210, 0, 403, 1));
        jspnWd.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jspnWdStateChanged(evt);
            }
        });

        jLabel4.setText("Ancho:");

        jLabel2.setText("Medidas de hoja:");

        jspnSMiz.setModel(new javax.swing.SpinnerNumberModel(10, 0, 100, 1));

        jLabel16.setText("Sup:");

        jLabel17.setText("Izq:");

        jLabel19.setText("mm");

        jspnSMder.setModel(new javax.swing.SpinnerNumberModel(10, 0, 100, 1));

        jLabel18.setText("mm");

        jLabel10.setText("Margenes internos hoja:");

        jspnSMtop.setModel(new javax.swing.SpinnerNumberModel(10, 0, 100, 1));

        jLabel11.setText("mm");

        jLabel12.setText("mm");

        jspnSMbtt.setModel(new javax.swing.SpinnerNumberModel(10, 0, 100, 1));

        jLabel9.setText("Inf:");

        jLabel22.setText("Der:");

        javax.swing.GroupLayout pnlMetricsLayout = new javax.swing.GroupLayout(pnlMetrics);
        pnlMetrics.setLayout(pnlMetricsLayout);
        pnlMetricsLayout.setHorizontalGroup(
            pnlMetricsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMetricsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlMetricsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16)
                    .addComponent(jLabel17)
                    .addComponent(jLabel9)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlMetricsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jspnSMder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jspnSMbtt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jspnSMiz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jspnSMtop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlMetricsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(jLabel18)
                    .addComponent(jLabel12)
                    .addComponent(jLabel19)))
            .addGroup(pnlMetricsLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel10))
            .addGroup(pnlMetricsLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel2))
            .addGroup(pnlMetricsLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(pnlMetricsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlMetricsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jspnHg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jspnWd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlMetricsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel5)))
            .addGroup(pnlMetricsLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel13))
            .addGroup(pnlMetricsLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(pnlMetricsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20)
                    .addComponent(jLabel21))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlMetricsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jspnMTtop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jspnMTleft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlMetricsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14)
                    .addComponent(jLabel15)))
        );
        pnlMetricsLayout.setVerticalGroup(
            pnlMetricsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlMetricsLayout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlMetricsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jspnHg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlMetricsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jspnWd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlMetricsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jspnSMtop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlMetricsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jspnSMiz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlMetricsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jspnSMbtt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlMetricsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(jspnSMder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlMetricsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(jspnMTtop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlMetricsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlMetricsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jspnMTleft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel15))
                    .addComponent(jLabel21))
                .addContainerGap(195, Short.MAX_VALUE))
        );

        contain.add(pnlMetrics, java.awt.BorderLayout.EAST);

        scroller.setViewportView(contain);

        mainContain.add(scroller, java.awt.BorderLayout.CENTER);

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    private void jtxIDFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxIDFocusGained
        rbtnID.setSelected(true);
    }//GEN-LAST:event_jtxIDFocusGained

    private void jtxIdMultiFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxIdMultiFocusGained
        rbtnIdMul.setSelected(true);
    }//GEN-LAST:event_jtxIdMultiFocusGained

    private void jspnHgStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jspnHgStateChanged
        resizeSheet();
    }//GEN-LAST:event_jspnHgStateChanged

    private void resizeSheet(){
        int wd=Integer.parseInt(jspnWd.getValue().toString());
        int hg=Integer.parseInt(jspnHg.getValue().toString());
        Dimension dim=new Dimension(mm2pixels(wd),mm2pixels(hg));
        if(sheets!=null){
            int num=sheets.length;
            for(int k=0;k<num;k++){
                sheets[k].setPreferredSize(dim); sheets[k].setMaximumSize(dim); sheets[k].setMinimumSize(dim);
            } validate();            
        }
    }
    
    public int mm2pixels(int mm){
        int dpi=72; //getToolkit().getScreenResolution();
        double fact=dpi/25.4f;
        return (int)(mm*fact);
    }
    
    private void jspnWdStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jspnWdStateChanged
        resizeSheet();
    }//GEN-LAST:event_jspnWdStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel CtrlPan;
    private javax.swing.JPanel contain;
    private javax.swing.ButtonGroup grpRbtnTpCode;
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSpinner jspnHg;
    private javax.swing.JSpinner jspnMTleft;
    private javax.swing.JSpinner jspnMTtop;
    private javax.swing.JSpinner jspnSMbtt;
    private javax.swing.JSpinner jspnSMder;
    private javax.swing.JSpinner jspnSMiz;
    private javax.swing.JSpinner jspnSMtop;
    private javax.swing.JSpinner jspnWd;
    private javax.swing.JTextField jtxID;
    private javax.swing.JTextField jtxIdMulti;
    private javax.swing.JLabel lblInfPreview;
    private javax.swing.JPanel mainContain;
    private javax.swing.JPanel pnlMetrics;
    private javax.swing.JPanel pnlSheets;
    private javax.swing.JRadioButton rbtnID;
    private javax.swing.JRadioButton rbtnIdMul;
    private javax.swing.JScrollPane scroller;
    // End of variables declaration//GEN-END:variables
}
