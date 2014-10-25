/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.views.modalqrs;

import com.videoup.controllers.CAlquilers;
import com.videoup.controllers.Controller;
import com.videoup.entities.VideoupBaseEntity;
import com.videoup.entities.VideoupBcodes;
import com.videoup.entities.VideoupCustomers;
import com.videoup.entities.VideoupGames;
import com.videoup.entities.VideoupItemsrnt;
import com.videoup.entities.VideoupMovies;
import com.videoup.entities.VideoupRentas;
import com.videoup.utils.GenProccess;
import com.videoup.views.modsocios.MiniFicha;
import com.videoup.views.utils.ProductBriefData;
import com.videoup.views.utils.Utils;
import com.videoup.views.utils.ViewFicha;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Pedro
 */
public class Ficha extends ViewFicha{
    
    private VideoupRentas ent;
    private MiniFicha mfich;
    private List<Component> prods;
    private VideoupCustomers soc;
    private boolean saved;
    private boolean searchSocByCode;
    private NumberFormat frmCurr;
    private ProductBriefData pbdCancel;
    private boolean justSaved;
    
    /**
     * Creates new form Ficha
     */
    public Ficha(Controller ctrl, int id) {
        super(ctrl,id,true);
        initComponents();
        initComps(ent);
    }
    
    public Ficha(Controller ctrl, Object ent, int id) {
        super(ctrl,id,false);
        initComponents();
        initComps(ent);
    }
    
    @Override
    protected void loadEntity(int id){
        ent=(VideoupRentas)getEntity("from VideoupRentas where idrt="+id);
    }
    
    private void initComps(Object ent){
        putMainContain(mainContain);
        justSaved=false;
        pbdCancel=null;
        frmCurr=NumberFormat.getCurrencyInstance();
        btnFinalize.setEnabled(false); btnFinalize.setVisible(false);
        lblOfrts.setVisible(false);
        prods=new ArrayList<Component>();
        soc=null; saved=false; searchSocByCode=true;
        if(ent==null){
            this.ent=new VideoupRentas();
        }else{
            loadEntity(ent);
        }
    }
    
    private void addItem(){
        List<VideoupBcodes> copies;
        String code=txCodeBar.getText().trim();
        if(code.equals("")){
            return;
        }
        copies=loadList("from VideoupBcodes where barcode='"+code+"'",null,0,false);
        if(copies==null){
            showError("Error al consultar: "+getError());
        }else if(copies.isEmpty()){
            showError("No se encontraron productos con codigo "+code);
        }else{
            txCodeBar.setText("");
            for(VideoupBcodes copy: copies){
                if(copy.getStatus().startsWith("Apartada")){
                    showInfo("No se puede completar la accion","El producto esta apartado");
                }else if(!copy.getStatus().equals("Disponible")){
                    showInfo("No se puede completar la accion","El producto no esta disponible para alquiler");
                }else if(copy.getVideoupMoviesList()!=null && !copy.getVideoupMoviesList().isEmpty()){
                    for(VideoupMovies mov: copy.getVideoupMoviesList()){
                        if(!hasCopy(copy)){
                            prods.add(new ProductBriefData(mov,copy,this,true));
                        }
                    }
                    checkRentedBefore();
                }else if(copy.getVideoupGamesList()!=null && !copy.getVideoupGamesList().isEmpty()){
                    for(VideoupGames gam: copy.getVideoupGamesList()){
                        if(!hasCopy(copy)){
                            prods.add(new ProductBriefData(gam,copy,this,true));                            
                        }
                    }
                    checkRentedBefore();
                }
            }
            if(prods.isEmpty()){
                showError("No se encontraron registros del producto");
            }else{
                refreshItems();
            }
        }
    }
    
    private void checkRentedBefore(){
        List<VideoupRentas> rentas;
        List<VideoupItemsrnt> items;
        for(Component cmp: prods){
            if( cmp instanceof ProductBriefData ){
                ((ProductBriefData)cmp).markAsRentedBefore(false);
            }
        }
        if(soc==null || prods.isEmpty()){
            return;
        }
        rentas=soc.getVideoupRentasList();
        if(rentas!=null){
            for(VideoupRentas rnt: rentas){
                items=rnt.getVideoupItemsrntList();
                for(VideoupItemsrnt itm: items){
                    for(Component cmp: prods){
                        if( cmp instanceof ProductBriefData &&
                                ((ProductBriefData)cmp).isBCode(itm.getIdbc().getBarcode()) ){
                            ((ProductBriefData)cmp).markAsRentedBefore(true);
                        }
                    }
                }
            }
        }
    }
    
    private boolean hasCopy(VideoupBcodes copy){
        boolean hasIt=false;
        for(Component sItem: prods){
            if(sItem instanceof ProductBriefData){
                hasIt=((ProductBriefData)sItem).isBCode(copy.getBarcode());
                if(hasIt){ break; }
            }
        }
        return hasIt;
    }
    
    private void refreshItems(){
        pnlCenter.setVisible(false);
        Utils.loadAsTableLayout(prods, pnlCenter, "No hay productos agregados");
        pnlCenter.setVisible(true);
    }
    
    public void removeItem(ProductBriefData pbd){
        if(saved){
            return;
        }
        for(Component sItem: prods){
            if(sItem instanceof ProductBriefData){
                if( pbd.equals( (ProductBriefData)sItem ) ){
                    prods.remove(sItem);
                    break;
                }
            }
        }
        refreshItems();
    }
    
    public void setCustomr(VideoupCustomers sc){
        soc=sc;
        pnlSelSoc.removeAll();
        pnlEast.remove(pnlLstPers);
        setSocio();
    }
    
    public int getSocioCode(){
        if(soc!=null){
            return soc.getIdct();
        }
       return 0;
    }
    
    private void setSocio(){
        String code=txSCode.getText().trim();
        Date today=Calendar.getInstance().getTime();
        if(code.equals("") && soc==null){
            return;
        }
        if(soc==null && searchSocByCode){
            soc=(VideoupCustomers)getEntity("from VideoupCustomers where upper(codCst)='"+code.toUpperCase()+"'");
        }else if(soc==null && !searchSocByCode){
            search4Name(code); return;
        }
        pnlEast.setVisible(false);
        pnlEast.remove(lblSocioErr);
        pnlEast.remove(pnlLstPers);
        if(mfich!=null){
            pnlEast.remove(mfich);
        }
        if(soc==null){
            lblSocioErr.setText("<html>Error!<br /> verifica el <br />codigo <br />ingresado</html>");
            pnlEast.add(lblSocioErr,BorderLayout.CENTER);
        }else{
            if(today.before(soc.getFAlta()) || today.after(soc.getFVigen())){
                code="<html>El socio indicado<br />("+soc.getCodCst()+"<br />"+soc.getApplldos()+"<br />";
                code+=soc.getName()+")<br />no tiene<br />suscripcion<br />vigente</html>";
                lblSocioErr.setText(code);
                pnlEast.add(lblSocioErr,BorderLayout.CENTER);
                soc=null;
            }else{
                mfich=new MiniFicha(soc,this);
                pnlEast.add(mfich,BorderLayout.CENTER);
                checkRentedBefore();
            }
        }
        pnlEast.setVisible(true);
    }
    
    private void search4Name(String name){
        List<VideoupCustomers> socs;
        List<Component> opts;
        CustmrOption cOption;
        String qry="select DISTINCT vc From VideoupCustomers vc left join fetch vc.videoupAutrzList va where ";
        qry+=buildLike4Where(name,"va.pname")+" or ";
        qry+=buildLike4Where(name,"name")+" or "+buildLike4Where(name,"applldos");
        socs=loadList(qry,null,0, false);
        pnlEast.setVisible(false);
        pnlEast.remove(lblSocioErr); pnlEast.remove(pnlLstPers);
        if(mfich!=null){
            pnlEast.remove(mfich);
        }
        if(socs==null){
            showError("Error: "+this.getError());
            pnlEast.remove(lblSocioErr); pnlEast.validate();
        }else if(socs.isEmpty()){
            lblSocioErr.setText("<html>Vacio!<br /> no se encontraron <br />datos con<br />el nombre ingresado</html>");
            pnlEast.add(lblSocioErr,BorderLayout.CENTER);
        }else{
            opts=new ArrayList<Component>();
            opts.add(new javax.swing.JLabel(socs.size()+" encontrados"));
            for(VideoupCustomers cstmr: socs){
                cOption=new CustmrOption(cstmr,this);
                opts.add(cOption);
            }
            pnlSelSoc.removeAll();
            Utils.loadAsTableLayout(opts, pnlSelSoc, "No se encontraron personas");
            pnlEast.add(pnlLstPers,BorderLayout.CENTER);
        }
        pnlEast.setVisible(true); mainContain.validate();
    }
    
    @Override
    public void loadEntity(Object oEnt){
        ProductBriefData tmpp;
        List<String> lsOfrts;
        int enCurso=0;
        int pagados=0;
        int porpagar=0;
        float gcargos=0;
        float pcargos=0;
        float taxes=0;
        float dcargos=0;
        String sResume;
        ent=((VideoupRentas)oEnt);
        soc=ent.getIdcli();
        setSocio(); prods.clear();
        prods.add(pnlResume);
        for(VideoupItemsrnt itemrn: ent.getVideoupItemsrntList()){
            tmpp=new ProductBriefData(itemrn,this);
            prods.add(tmpp);
            if(itemrn.getStatus()==3){
                enCurso++; gcargos+=tmpp.getAlqCargos();
            }else if(itemrn.getStatus()==4){
                pagados++; pcargos+=tmpp.getAlqCargos();
            }else if(itemrn.getStatus()==5){
                porpagar++; dcargos+=tmpp.getAlqCargos();
            }else if(itemrn.getStatus()==7){
                enCurso++; dcargos+=tmpp.getAlqCargos();
            }
        }
        if(ent.getImpuesto()!=null){ taxes=ent.getImpuesto(); }else{ taxes=0; }
        sResume="<html>"+enCurso+" articulos en curso. Cargos generados "+frmCurr.format(gcargos);
        if(pagados>0){
            sResume+="<br />"+pagados+" articulos finalizados. Cargos pagados "+frmCurr.format(pcargos+taxes);
            sResume+="<br /> -- (Cargos + Impuestos : "+frmCurr.format(pcargos)+"+"+frmCurr.format(taxes)+" respectivamente)";
        }
        if(porpagar>0){ sResume+="<br />"+porpagar+" articulos finalizados. Cargos por cobrar "+frmCurr.format(dcargos); }
        lsOfrts=GenProccess.getOfrtsNames(ent);
        if(lsOfrts!=null && !lsOfrts.isEmpty()){
            sResume+="<br />Se aplican las siguientes ofertas:";
            for(String oOfrt: lsOfrts){
                sResume+="<br /> * "+oOfrt;
            }
        }
        lblResume.setText(sResume+"</html>");
        refreshItems();
        lblAskItems.setVisible(false); lblAskCustmr.setVisible(false);
        txSCode.setEditable(false); txSCode.setVisible(false);
        soBCode.setVisible(false); soBName.setVisible(false);
        txCodeBar.setEditable(false); txCodeBar.setVisible(false);
        updateIdTitle();
        btnFinalize.setEnabled(enCurso>0); btnFinalize.setVisible(enCurso>0);
        removeObtnSave();
        saved=true;
    }
    
    @Override
    public String getTitle(){
        return "Ficha de alquiler "+(getId()==0?"nuevo":getId()+(soc!=null?" socio: "+soc.getCodCst():""));
    }
    
    @Override
    protected void store(){
        PerformingTask task=new PerformingTask();
        setBusy("Guardando registro",true);
        task.setTask(1); task.setParam(null,false);
        task.setShowDialog(false); validate(); task.execute();
    }
    
    @Override
    protected void updateIdTitle(){
        setId(ent.getIdrt());
        lblTitle.setText(getTitle());
        ctrl.changeViewName(this, getTitle(),!justSaved);
        if(justSaved){
            if(ctrl.confirm("Prepago","El registro se ha guardado. ¿Desea registrar un prepago?")){
                getPrepayment();
            }
        }
        justSaved=false;
    }
    
    private void getPrepayment(){
        Prepago prepago=new Prepago(getMainWindow(),true,this);
        prepago.setLocationRelativeTo(null);
        prepago.setVisible(true);
    }

    public void savePrepaid(int hh){
        sent2Finalize(hh);
    }
    
    @Override
    protected boolean isDataValid(){
        boolean valid=!prods.isEmpty();
        valid=(valid && (soc!=null));
        setError("Debe indicar socio y al menos un articulo ");
        return valid;
    }
    
    @Override
    protected boolean saveEntity(VideoupBaseEntity ignore, boolean forceAsNew){
        List<VideoupBaseEntity> entities=new ArrayList<VideoupBaseEntity>();
        VideoupRentas renta=new VideoupRentas();
        int enCurso=0;
        int aplyRnts;
        String ofrts=null;
        List<String> lsOfrts;
        soc.setNumAlqs(soc.getNumAlqs()+1);
        renta.setIdcli(soc);
        renta.setCstFin(0f);
        renta.setOtcstFin(0f);
        renta.setFactura(false);
        entities.add(renta);
        entities.add(soc);
        for(Component sItem: prods){
            if(sItem instanceof ProductBriefData){
                VideoupItemsrnt item=((ProductBriefData)sItem).getAlqEntity();
                VideoupBcodes cp=((ProductBriefData)sItem).getCopy();
                cp.setStatus("En alquiler");
                entities.add( ((ProductBriefData)sItem).getProductEntity(1) );
                entities.add(item);
                entities.add(cp);
                item.setIdrt(renta);
                renta.addVideoupItemsrntList(item);
                enCurso++;
            }
        }
        saved=super.saveEntities(entities);
        if(saved){
            ent=renta;
            lblAskItems.setVisible(false); lblAskCustmr.setVisible(false);
            txSCode.setEditable(false); txSCode.setVisible(false);
            soBCode.setVisible(false); soBName.setVisible(false);
            txCodeBar.setEditable(false); txCodeBar.setVisible(false);
            aplyRnts=GenProccess.evalAndApplyOfrts(renta,getMainWindow());
            if(aplyRnts==-1){
                ofrts="Hubo un error al buscar ofertas aplicables";
                lblOfrts.setVisible(true);
                //System.out.println(" * * * "+GenProccess.getError());
            }else if(aplyRnts==1){
                lsOfrts=GenProccess.getOfrtsNames(renta);
                ofrts="Se aplican las siguientes ofertas:";
                if(lsOfrts!=null && !lsOfrts.isEmpty()){
                    for(String oOfrt: lsOfrts){
                        ofrts+="<br /> * "+oOfrt;
                    }
                }
            }
            for(Component sItem: prods){
                if(sItem instanceof ProductBriefData){
                    ((ProductBriefData)sItem).hideRemovalBtn();
                    ((ProductBriefData)sItem).showAlqActionButtons(true);
                }
            }
            justSaved=true;
            //updateIdTitle();
            removeObtnSave();
            btnFinalize.setEnabled(true); btnFinalize.setVisible(true);
            lblResume.setText("<html>"+enCurso+" articulos en curso."+(ofrts==null?"":"<br />"+ofrts)+"</html>");
            prods.add(0,pnlResume); refreshItems();
        }
        return saved;
    }
        
    @Override
    public boolean hasChanges(){
        if(saved){
            return false;
        }
        return (!prods.isEmpty() || (soc!=null));
    }
    
    public void sent2change(String code){
        ((CAlquilers)ctrl).sent2change(code);
    }
    
    private void sent2Finalize(int hh){
        ArrayList<String> codes=new ArrayList<String>();
        for(Component sItem: prods){
            if(sItem instanceof ProductBriefData){
                if( ((ProductBriefData)sItem).isAlqMark2finalize() || hh>0 ){
                    codes.add( ((ProductBriefData)sItem).getBCode() );
                }
            }
        }
        if(!codes.isEmpty()){
            ((CAlquilers)ctrl).sent2finalize(codes,hh);
        }
    }
    
    public void cancelItem(ProductBriefData pbd, VideoupItemsrnt itmre, VideoupBcodes cpy){
        PerformingTask task;
        pbdCancel=pbd;
        List<VideoupBaseEntity> entities;
        if(!ctrl.confirm("Confirme accion", "Desea cancelar el alquiler del producto, el producto se marcara como devuelto pero no se generara ningun cargo.")){
            return;
        }
        task=new PerformingTask();
        entities=new ArrayList<VideoupBaseEntity>();
        setBusy("Guardando registro",true);
        itmre.setStatus(1);
        cpy.setStatus("Disponible");
        entities.add(itmre);
        entities.add(cpy);
        task.setTask(4); task.setListParam(entities);
        validate(); task.execute();
    }
    
    @Override
    protected boolean saveEntities(List<VideoupBaseEntity> entities){
        boolean csaved=false;
        csaved=super.saveEntities(entities);
        if(csaved){
            GenProccess.resetApplyOfrts(ent,getMainWindow());
            loadEntity(ent);
            pbdCancel.showAlqActionButtons(false);
            showInfo("Accion realizada", "La cancelacion del producto se ha realizado");
        }
        return csaved;
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
        pnlNorth = new javax.swing.JPanel();
        lblAskItems = new javax.swing.JLabel();
        txCodeBar = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        pnlCenter = new javax.swing.JPanel();
        pnlEast = new javax.swing.JPanel();
        pnlAskSocio = new javax.swing.JPanel();
        lblAskCustmr = new javax.swing.JLabel();
        txSCode = new javax.swing.JTextField();
        soBCode = new javax.swing.JLabel();
        soBName = new javax.swing.JLabel();
        lblSocioErr = new javax.swing.JLabel();
        pnlResume = new javax.swing.JPanel();
        btnFinalize = new javax.swing.JButton();
        lblResume = new javax.swing.JLabel();
        lblOfrts = new javax.swing.JLabel();
        pnlLstPers = new javax.swing.JScrollPane();
        pnlSelSoc = new javax.swing.JPanel();

        mainContain.setLayout(new java.awt.BorderLayout());

        lblAskItems.setText("Ingrese codigo de barras de articulo:");

        txCodeBar.setColumns(15);
        txCodeBar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txCodeBarKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout pnlNorthLayout = new javax.swing.GroupLayout(pnlNorth);
        pnlNorth.setLayout(pnlNorthLayout);
        pnlNorthLayout.setHorizontalGroup(
            pnlNorthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNorthLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblAskItems)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txCodeBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(227, Short.MAX_VALUE))
        );
        pnlNorthLayout.setVerticalGroup(
            pnlNorthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNorthLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlNorthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAskItems)
                    .addComponent(txCodeBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mainContain.add(pnlNorth, java.awt.BorderLayout.NORTH);

        pnlCenter.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout pnlCenterLayout = new javax.swing.GroupLayout(pnlCenter);
        pnlCenter.setLayout(pnlCenterLayout);
        pnlCenterLayout.setHorizontalGroup(
            pnlCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 701, Short.MAX_VALUE)
        );
        pnlCenterLayout.setVerticalGroup(
            pnlCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 343, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(pnlCenter);

        mainContain.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        pnlEast.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlEast.setLayout(new java.awt.BorderLayout());

        lblAskCustmr.setText("Socio que realiza el alquiler:");

        txSCode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txSCodeKeyPressed(evt);
            }
        });

        soBCode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/videoup/imgs/codebar16.png"))); // NOI18N
        soBCode.setToolTipText("Buscar por codigo de socio");
        soBCode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(23, 33, 36), 2));
        soBCode.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                soBCodeMouseClicked(evt);
            }
        });

        soBName.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/videoup/imgs/miniSoc.png"))); // NOI18N
        soBName.setToolTipText("Buscar por nombre");
        soBName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                soBNameMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnlAskSocioLayout = new javax.swing.GroupLayout(pnlAskSocio);
        pnlAskSocio.setLayout(pnlAskSocioLayout);
        pnlAskSocioLayout.setHorizontalGroup(
            pnlAskSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAskSocioLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlAskSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblAskCustmr)
                    .addGroup(pnlAskSocioLayout.createSequentialGroup()
                        .addComponent(txSCode)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(soBCode)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(soBName)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlAskSocioLayout.setVerticalGroup(
            pnlAskSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAskSocioLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblAskCustmr)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlAskSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(soBCode)
                    .addComponent(soBName)
                    .addComponent(txSCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlEast.add(pnlAskSocio, java.awt.BorderLayout.NORTH);

        mainContain.add(pnlEast, java.awt.BorderLayout.EAST);

        lblSocioErr.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblSocioErr.setForeground(new java.awt.Color(172, 0, 0));
        lblSocioErr.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSocioErr.setText("<html><center>Error!<br /> verifica el <br />codigo <br />ingresado</center></html>");

        pnlResume.setBackground(new java.awt.Color(255, 255, 255));
        pnlResume.setLayout(new java.awt.BorderLayout());

        btnFinalize.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/videoup/imgs/finalize.png"))); // NOI18N
        btnFinalize.setToolTipText("Finalizar la renta de los articulos marcados para devolucion");
        btnFinalize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinalizeActionPerformed(evt);
            }
        });
        pnlResume.add(btnFinalize, java.awt.BorderLayout.EAST);

        lblResume.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        lblResume.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblResume.setText("N articulos en curso. Cargos generados X.XX");
        pnlResume.add(lblResume, java.awt.BorderLayout.CENTER);

        lblOfrts.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblOfrts.setForeground(new java.awt.Color(0, 0, 205));
        lblOfrts.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblOfrts.setText("<html><u>Reintentar buscar y aplicar ofertas</u></html>");
        pnlResume.add(lblOfrts, java.awt.BorderLayout.SOUTH);

        pnlSelSoc.setLayout(null);
        pnlLstPers.setViewportView(pnlSelSoc);

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    private void txCodeBarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txCodeBarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            addItem();
        }
    }//GEN-LAST:event_txCodeBarKeyPressed

    private void txSCodeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txSCodeKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            soc=null; setSocio();
        }
    }//GEN-LAST:event_txSCodeKeyPressed

    private void btnFinalizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinalizeActionPerformed
        sent2Finalize(0);
    }//GEN-LAST:event_btnFinalizeActionPerformed

    private void soBNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_soBNameMouseClicked
        soBCode.setBorder(null); searchSocByCode=false;
        soBName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(23, 33, 36), 2));
    }//GEN-LAST:event_soBNameMouseClicked

    private void soBCodeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_soBCodeMouseClicked
        soBName.setBorder(null); searchSocByCode=true;
        soBCode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(23, 33, 36), 2));
    }//GEN-LAST:event_soBCodeMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFinalize;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblAskCustmr;
    private javax.swing.JLabel lblAskItems;
    private javax.swing.JLabel lblOfrts;
    private javax.swing.JLabel lblResume;
    private javax.swing.JLabel lblSocioErr;
    private javax.swing.JPanel mainContain;
    private javax.swing.JPanel pnlAskSocio;
    private javax.swing.JPanel pnlCenter;
    private javax.swing.JPanel pnlEast;
    private javax.swing.JScrollPane pnlLstPers;
    private javax.swing.JPanel pnlNorth;
    private javax.swing.JPanel pnlResume;
    private javax.swing.JPanel pnlSelSoc;
    private javax.swing.JLabel soBCode;
    private javax.swing.JLabel soBName;
    private javax.swing.JTextField txCodeBar;
    private javax.swing.JTextField txSCode;
    // End of variables declaration//GEN-END:variables
}

