/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.views.modofrts;

import com.videoup.controllers.Controller;
import com.videoup.entities.VideoupBaseEntity;
import com.videoup.entities.VideoupCatgs;
import com.videoup.entities.VideoupCtofrentas;
import com.videoup.entities.VideoupCtprrentas;
import com.videoup.entities.VideoupFormts;
import com.videoup.views.utils.Utils;
import com.videoup.views.utils.ViewFicha;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

/**
 *
 * @author Pedro
 */
public class Ficha extends ViewFicha{

    private VideoupCtofrentas ent;
    private List<JCheckBox> catPrecs;
    private List<JCheckBox> formts;
    private List<JCheckBox> catgs;
    private TreeMap<Integer,VideoupCtprrentas> mapCatPrcs;
    private TreeMap<Integer,VideoupFormts> mapFormts;
    private TreeMap<Integer,VideoupCatgs> mapCatgs;
    private int typo;

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
        ent=(VideoupCtofrentas)getEntity("from VideoupCtofrentas where idcof="+id);
    }
    
    private void initComps(Object ent){
        Currency currency=Currency.getInstance(Locale.getDefault());
        rbntPrcioEsp.setText("Aplica precio fijo especial "+currency.getSymbol()+":");
        putMainContain(mainContain);
        setUpValidator();
        loadCatgPrecios();
        loadCatgs();
        loadFormts();
        if(ent==null){
            this.ent=new VideoupCtofrentas();
            showType(1);
        }else{
            loadEntity(ent);
        }
        addJText2ListenChanges(txNombre);
        addJText2ListenChanges(txN_n2m);
        addJText2ListenChanges(txM_n2m);
        addJText2ListenChanges(jxdDte1.getEditor());
        addJText2ListenChanges(jxdDte2.getEditor());
        addJText2ListenChanges(txN_nxm);
        addJText2ListenChanges(txM_nxm);
        addJText2ListenChanges(txPuntos);
        addJText2ListenChanges(txPrctOff);
        addJText2ListenChanges(txPrcioEsp);
        addJText2ListenChanges(txPriorty);
        changes=false;
    }
    
    private void loadCatgPrecios(){
        List<VideoupCtprrentas> ctgPrecios=loadList("from VideoupCtprrentas", null, 0, false);
        List<Component> precs=new ArrayList<Component>();
        mapCatPrcs=new TreeMap<Integer,VideoupCtprrentas>();
        catPrecs=new ArrayList<JCheckBox>();
        JCheckBox jchkb;
        precs.add(new JLabel("<html>Aplicar a: <br />Categoria de precios</html>",JLabel.CENTER));
        for(VideoupCtprrentas cgpre: ctgPrecios){
            jchkb=new javax.swing.JCheckBox();
            jchkb.setText(cgpre.getNamec());
            jchkb.setActionCommand(cgpre.getIdcpr().toString());
            jchkb.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent evt) {
                    changes=true;
                }
            });
            precs.add(jchkb);
            catPrecs.add(jchkb);
            mapCatPrcs.put(cgpre.getIdcpr(), cgpre);
        }
        Utils.loadAsTableLayout(precs, pnlCtgPres, "No hay categorias de precios registradas");
    }
    
    private void loadFormts(){
        List<VideoupFormts> formtsLst=loadList("from VideoupFormts", null, 0, false);
        List<Component> precs=new ArrayList<Component>();
        mapFormts=new TreeMap<Integer,VideoupFormts>();
        formts=new ArrayList<JCheckBox>();
        JCheckBox jchkb;
        precs.add(new JLabel("<html>Aplicar a: <br />Formatos</html>",JLabel.CENTER));
        for(VideoupFormts formt: formtsLst){
            jchkb=new javax.swing.JCheckBox();
            jchkb.setText(formt.getFrmt());
            jchkb.setActionCommand(formt.getIdcf().toString());
            jchkb.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent evt) {
                    changes=true;
                }
            });
            precs.add(jchkb);
            formts.add(jchkb);
            mapFormts.put(formt.getIdcf(), formt);
        }
        Utils.loadAsTableLayout(precs, pnlFormts, "No hay formatos registrados");
    }
    
    private void loadCatgs(){
        List<VideoupCatgs> catgsLst=loadList("from VideoupCatgs", null, 0, false);
        List<Component> precs=new ArrayList<Component>();
        mapCatgs=new TreeMap<Integer,VideoupCatgs>();
        catgs=new ArrayList<JCheckBox>();
        JCheckBox jchkb;
        precs.add(new JLabel("<html>Aplicar a: <br />Generos</html>",JLabel.CENTER));
        for(VideoupCatgs catg: catgsLst){
            jchkb=new javax.swing.JCheckBox();
            jchkb.setText(catg.getCatg());
            jchkb.setActionCommand(catg.getIdcg().toString());
            jchkb.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent evt) {
                    changes=true;
                }
            });
            precs.add(jchkb);
            catgs.add(jchkb);
            mapCatgs.put(catg.getIdcg(),catg);
        }
        Utils.loadAsTableLayout(precs, pnlGenrs, "No hay generos registrados");
    }
    
    private void loadCatgsPrecios(boolean toEntity){
        List<VideoupCtprrentas> misCtgPr;
        if(toEntity){
            ent.clearVideoupCtprrentasList();
            for(JCheckBox jchkb: catPrecs){
                if(jchkb.isSelected()){
                    ent.addVideoupCtprrentas(mapCatPrcs.get(Integer.parseInt(jchkb.getActionCommand())));
                }
            }
        }else{
            misCtgPr=ent.getVideoupCtprrentasList();
            if(misCtgPr!=null){
                for(VideoupCtprrentas vctp: misCtgPr){
                    for(JCheckBox jchkb: catPrecs){
                        if( Integer.parseInt(jchkb.getActionCommand())==vctp.getIdcpr() ){
                            jchkb.setSelected(true); break;
                        }
                    }
                }
            }
        }
    }
    
    private void loadFortms2(boolean toEntity){
        List<VideoupFormts> misFormts;
        if(toEntity){
            ent.clearVideoupFormtsList();
            for(JCheckBox jchkb: formts){
                if(jchkb.isSelected()){
                    ent.addVideoupFormts(mapFormts.get(Integer.parseInt(jchkb.getActionCommand())));
                }
            }
        }else{
            misFormts=ent.getVideoupFormtsList();
            if(misFormts!=null){
                for(VideoupFormts vctp: misFormts){
                    for(JCheckBox jchkb: formts){
                        if( Integer.parseInt(jchkb.getActionCommand())==vctp.getIdcf() ){
                            jchkb.setSelected(true); break;
                        }
                    }
                }
            }
        }
    }
    
    private void loadCatgs2(boolean toEntity){
        List<VideoupCatgs> misCatgs;
        if(toEntity){
            ent.clearVideoupCatgsList();
            for(JCheckBox jchkb: catgs){
                if(jchkb.isSelected()){
                    ent.addVideoupCatgs(mapCatgs.get(Integer.parseInt(jchkb.getActionCommand())));
                }
            }
        }else{
            misCatgs=ent.getVideoupCatgsList();
            if(misCatgs!=null){
                for(VideoupCatgs vctp: misCatgs){
                    for(JCheckBox jchkb: catgs){
                        if( Integer.parseInt(jchkb.getActionCommand())==vctp.getIdcg() ){
                            jchkb.setSelected(true); break;
                        }
                    }
                }
            }
        }
    }
    
    @Override
    protected void setUpValidator(){
        super.setUpValidator();
        vltor.addStringField(txNombre, "Nombre de la oferta", true, 6, 21,0);
        vltor.addIntField(txPriorty, "Prioridad de la oferta", true, 1, 99,0);
        vltor.addIntField(txN_n2m, "Numero articulos inicial", true, 1,999,0);
        vltor.addIntField(txM_n2m, "Numero articulos  final", true, 2, 1000,0);
        vltor.addDateField(jxdDte1, "Fecha inicial de vigencia", true, 0);
        vltor.addDateField(jxdDte2, "Fecha final de vigencia", true, 0);
        vltor.addIntField(txN_nxm, "Numero articulos inicial", true, 1,999,0);
        vltor.addIntField(txM_nxm, "Numero articulos  final", true, 2, 1000,0);
        vltor.addIntField(txPuntos, "Numero de puntos", true, 1, 10000,0);
        vltor.addIntField(txPrctOff, "Porcentaje de descuento", true, 1, 100,0);
        vltor.addDoubleField(txPrcioEsp, "Precio fijo especial", true, 0, 10000,0);
        rbntPrctOff.setSelected(true);
        vltor.setIgnoreField(txPrcioEsp,rbntPrctOff.isSelected());
    }
    
    @Override
    protected boolean saveEntity(VideoupBaseEntity ignore,boolean forceAsNew){
        boolean saved;
        int typBenf=(typo==3?3:(rbntPrctOff.isSelected()?1:2));
        ent.setImovie(chkPelis.isSelected());
        ent.setIngame(chkGames.isSelected());
        ent.setPriority(Integer.parseInt(txPriorty.getText()));
        ent.setTpo(typo);
        if(typo==1){
            ent.setAuxN(Integer.parseInt(txN_n2m.getText()));
            ent.setAuxM(Integer.parseInt(txM_n2m.getText()));
        }else if(typo==3){
            ent.setAuxN(Integer.parseInt(txN_nxm.getText()));
            ent.setAuxM(Integer.parseInt(txM_nxm.getText()));
        }else{
            ent.setAuxN(0);
            ent.setAuxM(0);
        }
        ent.setAuxD1(jxdDte1.getDate());
        ent.setAuxD2(jxdDte2.getDate());
        ent.setTpsv(typBenf);
        if(typBenf==1){
            ent.setPrDesc(Integer.parseInt(txPrctOff.getText()));
        }else{
            ent.setPrDesc(0);
        }
        if(typBenf==2){
            ent.setCstSpc(Float.parseFloat(txPrcioEsp.getText()));
        }else{
            ent.setCstSpc(0);
        }
        ent.setNamer(txNombre.getText());
        ent.setAplDomingo(chkDom.isSelected());
        ent.setAplLunes(chkLun.isSelected());
        ent.setAplMartes(chkMar.isSelected());
        ent.setAplMiercoles(chkMie.isSelected());
        ent.setAplJueves(chkJue.isSelected());
        ent.setAplViernes(chkVie.isSelected());
        ent.setAplSabado(chkSab.isSelected());
        ent.setAplydias(jcbAppDias.getSelectedIndex());
        if(typo==5){
            ent.setBypuntos(Integer.parseInt(txPuntos.getText()));
        }else{
            ent.setBypuntos(0);
        }
        loadCatgsPrecios(true);
        loadFortms2(true);
        loadCatgs2(true);
        saved=super.saveEntity(ent,false);
        changes=!saved;
        return saved;
    }
        
    @Override
    public void loadEntity(Object oEnt){
        ent=((VideoupCtofrentas)oEnt);
        int typBenf=ent.getTpsv();
        if(ent.getTpo()==1){
            jcbSelType.setSelectedIndex(0);
            txN_n2m.setText(ent.getAuxN().toString());
            txM_n2m.setText(ent.getAuxM().toString());
        }else if(ent.getTpo()==2){
            jcbSelType.setSelectedIndex(1);
        }else if(ent.getTpo()==3){
            jcbSelType.setSelectedIndex(2);
            txN_nxm.setText(ent.getAuxN().toString());
            txM_nxm.setText(ent.getAuxM().toString());
        }else if(ent.getTpo()==4){
            jcbSelType.setSelectedIndex(3);
        }else if(ent.getTpo()==5){
            jcbSelType.setSelectedIndex(4);
            txPuntos.setText(ent.getBypuntos().toString());
        }
        showType(ent.getTpo());
        chkPelis.setSelected(ent.getImovie());
        chkGames.setSelected(ent.getIngame());
        if(typBenf==1){
            rbntPrctOff.setSelected(true);
            txPrctOff.setText(ent.getPrDesc().toString());
            vltor.setIgnoreField(txPrcioEsp,true);
        }else if(typBenf==2){
            rbntPrcioEsp.setSelected(true);
            txPrcioEsp.setText(""+ent.getCstSpc());
            vltor.setIgnoreField(txPrctOff,true);
        }
        txNombre.setText(ent.getNamer());
        txPriorty.setText(""+ent.getPriority());
        jxdDte1.setDate(ent.getAuxD1());
        jxdDte2.setDate(ent.getAuxD2());
        chkDom.setSelected(ent.getAplDomingo());
        chkLun.setSelected(ent.getAplLunes());
        chkMar.setSelected(ent.getAplMartes());
        chkMie.setSelected(ent.getAplMiercoles());
        chkJue.setSelected(ent.getAplJueves());
        chkVie.setSelected(ent.getAplViernes());
        chkSab.setSelected(ent.getAplSabado());
        jcbAppDias.setSelectedIndex(ent.getAplydias());
        loadCatgsPrecios(false);
        loadFortms2(false);
        loadCatgs2(false);
        lblTitle.setText(getTitle());
        changes=false;
    }
    
    private void updateValidator4Benefittype(){
        vltor.setIgnoreField(txPrctOff,rbntPrcioEsp.isSelected());
        vltor.setIgnoreField(txPrcioEsp,rbntPrctOff.isSelected());
    }
    
    @Override
    protected void updateIdTitle(){
        setId(ent.getIdcof());
        lblTitle.setText(getTitle());
        ctrl.changeViewName(this, getTitle());
    }
    
    @Override
    public String getTitle(){
        return "Ficha de oferta"+(getId()==0?" nueva":(ent==null?" ID: "+getId():": "+ent.getNamer()));
    }
    
    @Override
    protected boolean isDataValid(){
        boolean vltd=super.isDataValid();
        if(!chkDom.isSelected() && !chkLun.isSelected() && !chkMar.isSelected() && !chkMie.isSelected()
                 && !chkJue.isSelected() && !chkVie.isSelected() && !chkSab.isSelected()){
            vltd=false;
            setError("Ningun dia de la semana ha sido marcado, la oferta no se aplicara");
        }
        return vltd;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        grpTipos = new javax.swing.ButtonGroup();
        grpBenef = new javax.swing.ButtonGroup();
        mainContain = new javax.swing.JScrollPane();
        mainContainA = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txNombre = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        chkPelis = new javax.swing.JCheckBox();
        chkGames = new javax.swing.JCheckBox();
        jLabel14 = new javax.swing.JLabel();
        txPriorty = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        pnlOffN2M = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txN_n2m = new javax.swing.JTextField();
        txM_n2m = new javax.swing.JTextField();
        pnlOffDates = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jxdDte1 = new org.jdesktop.swingx.JXDatePicker();
        jxdDte2 = new org.jdesktop.swingx.JXDatePicker();
        pnlOffNxM = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        txN_nxm = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txM_nxm = new javax.swing.JTextField();
        pnlOffPuntos = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        txPuntos = new javax.swing.JTextField();
        pnlOffDias = new javax.swing.JPanel();
        chkDom = new javax.swing.JCheckBox();
        chkLun = new javax.swing.JCheckBox();
        chkMar = new javax.swing.JCheckBox();
        chkMie = new javax.swing.JCheckBox();
        chkJue = new javax.swing.JCheckBox();
        chkVie = new javax.swing.JCheckBox();
        chkSab = new javax.swing.JCheckBox();
        jLabel17 = new javax.swing.JLabel();
        jcbSelType = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        txPrctOff = new javax.swing.JTextField();
        rbntPrcioEsp = new javax.swing.JRadioButton();
        jLabel12 = new javax.swing.JLabel();
        txPrcioEsp = new javax.swing.JTextField();
        rbntPrctOff = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        jcbAppDias = new javax.swing.JComboBox();
        jLabel11 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        pnlCtgPres = new javax.swing.JPanel();
        pnlFormts = new javax.swing.JPanel();
        pnlGenrs = new javax.swing.JPanel();

        jLabel1.setText("Nombre de la oferta:");

        jLabel2.setText("Aplica en articulos:");

        chkPelis.setText("Peliculas");
        chkPelis.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkPelisItemStateChanged(evt);
            }
        });

        chkGames.setText("Videojuegos");
        chkGames.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkGamesItemStateChanged(evt);
            }
        });

        jLabel14.setText("Prioridad de oferta:");

        txPriorty.setColumns(5);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Tipo de oferta"));

        jLabel4.setText("Numero de articulos inicial:");

        jLabel5.setText("Numero de articulos final:");

        txN_n2m.setText("0");

        txM_n2m.setText("0");

        javax.swing.GroupLayout pnlOffN2MLayout = new javax.swing.GroupLayout(pnlOffN2M);
        pnlOffN2M.setLayout(pnlOffN2MLayout);
        pnlOffN2MLayout.setHorizontalGroup(
            pnlOffN2MLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOffN2MLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlOffN2MLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(pnlOffN2MLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txN_n2m, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE)
                    .addComponent(txM_n2m))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlOffN2MLayout.setVerticalGroup(
            pnlOffN2MLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOffN2MLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlOffN2MLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txN_n2m, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlOffN2MLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txM_n2m, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlOffDates.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel6.setText("Vigencias de:");

        jLabel7.setText("a:");

        javax.swing.GroupLayout pnlOffDatesLayout = new javax.swing.GroupLayout(pnlOffDates);
        pnlOffDates.setLayout(pnlOffDatesLayout);
        pnlOffDatesLayout.setHorizontalGroup(
            pnlOffDatesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOffDatesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jxdDte1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jxdDte2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlOffDatesLayout.setVerticalGroup(
            pnlOffDatesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOffDatesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlOffDatesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jxdDte1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jxdDte2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel8.setText("Numero de articulos inicial:");

        txN_nxm.setText("0");

        jLabel9.setText("Numero de articulos final:");

        txM_nxm.setText("0");

        javax.swing.GroupLayout pnlOffNxMLayout = new javax.swing.GroupLayout(pnlOffNxM);
        pnlOffNxM.setLayout(pnlOffNxMLayout);
        pnlOffNxMLayout.setHorizontalGroup(
            pnlOffNxMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOffNxMLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlOffNxMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9))
                .addGap(18, 18, 18)
                .addGroup(pnlOffNxMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txN_nxm, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE)
                    .addComponent(txM_nxm))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlOffNxMLayout.setVerticalGroup(
            pnlOffNxMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOffNxMLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlOffNxMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txN_nxm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlOffNxMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txM_nxm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel10.setText("Numero de puntos requerido:");

        txPuntos.setText("0");

        javax.swing.GroupLayout pnlOffPuntosLayout = new javax.swing.GroupLayout(pnlOffPuntos);
        pnlOffPuntos.setLayout(pnlOffPuntosLayout);
        pnlOffPuntosLayout.setHorizontalGroup(
            pnlOffPuntosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOffPuntosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txPuntos, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlOffPuntosLayout.setVerticalGroup(
            pnlOffPuntosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOffPuntosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlOffPuntosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txPuntos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlOffDias.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        chkDom.setSelected(true);
        chkDom.setText("Domingo");
        chkDom.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkDomItemStateChanged(evt);
            }
        });

        chkLun.setSelected(true);
        chkLun.setText("Lunes");
        chkLun.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkLunItemStateChanged(evt);
            }
        });

        chkMar.setSelected(true);
        chkMar.setText("Martes");
        chkMar.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkMarItemStateChanged(evt);
            }
        });

        chkMie.setSelected(true);
        chkMie.setText("Miercoles");
        chkMie.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkMieItemStateChanged(evt);
            }
        });

        chkJue.setSelected(true);
        chkJue.setText("Jueves");
        chkJue.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkJueItemStateChanged(evt);
            }
        });

        chkVie.setSelected(true);
        chkVie.setText("Viernes");
        chkVie.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkVieItemStateChanged(evt);
            }
        });

        chkSab.setSelected(true);
        chkSab.setText("Sabado");
        chkSab.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkSabItemStateChanged(evt);
            }
        });

        jLabel17.setText("Aplicar solo durante los dias:");

        javax.swing.GroupLayout pnlOffDiasLayout = new javax.swing.GroupLayout(pnlOffDias);
        pnlOffDias.setLayout(pnlOffDiasLayout);
        pnlOffDiasLayout.setHorizontalGroup(
            pnlOffDiasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOffDiasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlOffDiasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlOffDiasLayout.createSequentialGroup()
                        .addGroup(pnlOffDiasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkDom)
                            .addComponent(chkLun)
                            .addComponent(chkMar))
                        .addGap(18, 18, 18)
                        .addGroup(pnlOffDiasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkJue)
                            .addComponent(chkVie)
                            .addComponent(chkSab)))
                    .addComponent(chkMie)
                    .addComponent(jLabel17))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        pnlOffDiasLayout.setVerticalGroup(
            pnlOffDiasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlOffDiasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlOffDiasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkDom)
                    .addComponent(chkJue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlOffDiasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkLun)
                    .addComponent(chkVie))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlOffDiasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkMar)
                    .addComponent(chkSab))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkMie)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jcbSelType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "De N a M articulos", "Aplica entre fechas", "Pague N y lleve M", "Por dias de la semana", "Por puntos" }));
        jcbSelType.setToolTipText("");
        jcbSelType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcbSelTypeItemStateChanged(evt);
            }
        });

        jLabel13.setText("Eliga el tipo de oferta:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pnlOffN2M, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pnlOffNxM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pnlOffPuntos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jcbSelType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pnlOffDates, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pnlOffDias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jcbSelType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlOffN2M, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlOffNxM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlOffPuntos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlOffDates, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlOffDias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Tipo de beneficio"));

        grpBenef.add(rbntPrcioEsp);
        rbntPrcioEsp.setText("Aplica precio fijo especial");
        rbntPrcioEsp.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rbntPrcioEspItemStateChanged(evt);
            }
        });

        jLabel12.setText("%");

        grpBenef.add(rbntPrctOff);
        rbntPrctOff.setText("Aplica porcentaje de descuento");
        rbntPrctOff.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rbntPrctOffItemStateChanged(evt);
            }
        });

        jLabel3.setText("Aplicar beneficio durante:");

        jcbAppDias.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Normal **", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17" }));

        jLabel11.setText("TB*");

        jLabel15.setText("<html>* TB=Tiempo Base, especificado en el catalogo<br />de precios de alquiler que aplique. <br />** Normal es el valor predefinido</htlm>");

        jLabel16.setText("x TB*");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jcbAppDias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rbntPrctOff)
                            .addComponent(rbntPrcioEsp))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txPrctOff)
                            .addComponent(txPrcioEsp, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(jLabel16))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbntPrctOff)
                    .addComponent(txPrctOff, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbntPrcioEsp)
                    .addComponent(txPrcioEsp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jcbAppDias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setLayout(new java.awt.GridLayout(1, 3));

        pnlCtgPres.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        javax.swing.GroupLayout pnlCtgPresLayout = new javax.swing.GroupLayout(pnlCtgPres);
        pnlCtgPres.setLayout(pnlCtgPresLayout);
        pnlCtgPresLayout.setHorizontalGroup(
            pnlCtgPresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 143, Short.MAX_VALUE)
        );
        pnlCtgPresLayout.setVerticalGroup(
            pnlCtgPresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 703, Short.MAX_VALUE)
        );

        jPanel3.add(pnlCtgPres);

        pnlFormts.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        javax.swing.GroupLayout pnlFormtsLayout = new javax.swing.GroupLayout(pnlFormts);
        pnlFormts.setLayout(pnlFormtsLayout);
        pnlFormtsLayout.setHorizontalGroup(
            pnlFormtsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 143, Short.MAX_VALUE)
        );
        pnlFormtsLayout.setVerticalGroup(
            pnlFormtsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 703, Short.MAX_VALUE)
        );

        jPanel3.add(pnlFormts);

        pnlGenrs.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        javax.swing.GroupLayout pnlGenrsLayout = new javax.swing.GroupLayout(pnlGenrs);
        pnlGenrs.setLayout(pnlGenrsLayout);
        pnlGenrsLayout.setHorizontalGroup(
            pnlGenrsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 143, Short.MAX_VALUE)
        );
        pnlGenrsLayout.setVerticalGroup(
            pnlGenrsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 703, Short.MAX_VALUE)
        );

        jPanel3.add(pnlGenrs);

        javax.swing.GroupLayout mainContainALayout = new javax.swing.GroupLayout(mainContainA);
        mainContainA.setLayout(mainContainALayout);
        mainContainALayout.setHorizontalGroup(
            mainContainALayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainContainALayout.createSequentialGroup()
                .addGroup(mainContainALayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainContainALayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(mainContainALayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(mainContainALayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(chkPelis)
                        .addGap(18, 18, 18)
                        .addComponent(chkGames))
                    .addGroup(mainContainALayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(mainContainALayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(mainContainALayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel2)
                            .addGroup(mainContainALayout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txPriorty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        mainContainALayout.setVerticalGroup(
            mainContainALayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainContainALayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainContainALayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainContainALayout.createSequentialGroup()
                        .addGroup(mainContainALayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainContainALayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(txPriorty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(mainContainALayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkPelis)
                            .addComponent(chkGames))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        mainContain.setViewportView(mainContainA);

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    private void chkPelisItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkPelisItemStateChanged
        changes=true;
    }//GEN-LAST:event_chkPelisItemStateChanged

    private void chkGamesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkGamesItemStateChanged
        changes=true;
    }//GEN-LAST:event_chkGamesItemStateChanged

    private void chkDomItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkDomItemStateChanged
        changes=true;
    }//GEN-LAST:event_chkDomItemStateChanged

    private void chkJueItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkJueItemStateChanged
        changes=true;
    }//GEN-LAST:event_chkJueItemStateChanged

    private void chkLunItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkLunItemStateChanged
        changes=true;
    }//GEN-LAST:event_chkLunItemStateChanged

    private void chkVieItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkVieItemStateChanged
        changes=true;
    }//GEN-LAST:event_chkVieItemStateChanged

    private void chkMarItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkMarItemStateChanged
        changes=true;
    }//GEN-LAST:event_chkMarItemStateChanged

    private void chkSabItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkSabItemStateChanged
        changes=true;
    }//GEN-LAST:event_chkSabItemStateChanged

    private void chkMieItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkMieItemStateChanged
        changes=true;
    }//GEN-LAST:event_chkMieItemStateChanged

    private void rbntPrctOffItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rbntPrctOffItemStateChanged
        changes=true;
        updateValidator4Benefittype();
    }//GEN-LAST:event_rbntPrctOffItemStateChanged

    private void rbntPrcioEspItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rbntPrcioEspItemStateChanged
        changes=true;
        updateValidator4Benefittype();
    }//GEN-LAST:event_rbntPrcioEspItemStateChanged

    private void jcbSelTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcbSelTypeItemStateChanged
        int type=jcbSelType.getSelectedIndex();
        showType(type+1);
    }//GEN-LAST:event_jcbSelTypeItemStateChanged

    /**
     * @param type 1=N a M, 2=fechas, 3=NxM, 4=dias, 5=puntos
     */
    private void showType(int type){
        typo=type;
        pnlOffN2M.setVisible(false);
        vltor.setIgnoreField(txN_n2m,true);
        vltor.setIgnoreField(txM_n2m,true);
        vltor.setIgnoreField(jxdDte1,true);
        vltor.setIgnoreField(jxdDte2,true);
        pnlOffNxM.setVisible(false);
        vltor.setIgnoreField(txN_nxm,true);
        vltor.setIgnoreField(txM_nxm,true);
        pnlOffPuntos.setVisible(false);
        vltor.setIgnoreField(txPuntos,true);
        rbntPrctOff.setEnabled(true); rbntPrcioEsp.setEnabled(true);
        vltor.setIgnoreField(txPrctOff,false);
        vltor.setIgnoreField(txPrcioEsp,false);
        txPrctOff.setEditable(true);
        txPrcioEsp.setEditable(true);
        jcbAppDias.setEnabled(true);
        if(type==1){
            pnlOffN2M.setVisible(true);
            vltor.setIgnoreField(txN_n2m,false);
            vltor.setIgnoreField(txM_n2m,false);
            updateValidator4Benefittype();
        }else if(type==2){
            vltor.setIgnoreField(jxdDte1,false);
            vltor.setIgnoreField(jxdDte2,false);
            updateValidator4Benefittype();
        }else if(type==3){
            pnlOffNxM.setVisible(true);
            vltor.setIgnoreField(txN_nxm,false);
            vltor.setIgnoreField(txM_nxm,false);
            rbntPrctOff.setEnabled(false);
            rbntPrcioEsp.setEnabled(false);
            vltor.setIgnoreField(txPrctOff,true);
            vltor.setIgnoreField(txPrcioEsp,true);
            txPrctOff.setEditable(false);
            txPrcioEsp.setEditable(false);
            jcbAppDias.setEnabled(false);
        }else if(type==5){
            pnlOffPuntos.setVisible(true);
            vltor.setIgnoreField(txPuntos,false);
            updateValidator4Benefittype();
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkDom;
    private javax.swing.JCheckBox chkGames;
    private javax.swing.JCheckBox chkJue;
    private javax.swing.JCheckBox chkLun;
    private javax.swing.JCheckBox chkMar;
    private javax.swing.JCheckBox chkMie;
    private javax.swing.JCheckBox chkPelis;
    private javax.swing.JCheckBox chkSab;
    private javax.swing.JCheckBox chkVie;
    private javax.swing.ButtonGroup grpBenef;
    private javax.swing.ButtonGroup grpTipos;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JComboBox jcbAppDias;
    private javax.swing.JComboBox jcbSelType;
    private org.jdesktop.swingx.JXDatePicker jxdDte1;
    private org.jdesktop.swingx.JXDatePicker jxdDte2;
    private javax.swing.JScrollPane mainContain;
    private javax.swing.JPanel mainContainA;
    private javax.swing.JPanel pnlCtgPres;
    private javax.swing.JPanel pnlFormts;
    private javax.swing.JPanel pnlGenrs;
    private javax.swing.JPanel pnlOffDates;
    private javax.swing.JPanel pnlOffDias;
    private javax.swing.JPanel pnlOffN2M;
    private javax.swing.JPanel pnlOffNxM;
    private javax.swing.JPanel pnlOffPuntos;
    private javax.swing.JRadioButton rbntPrcioEsp;
    private javax.swing.JRadioButton rbntPrctOff;
    private javax.swing.JTextField txM_n2m;
    private javax.swing.JTextField txM_nxm;
    private javax.swing.JTextField txN_n2m;
    private javax.swing.JTextField txN_nxm;
    private javax.swing.JTextField txNombre;
    private javax.swing.JTextField txPrcioEsp;
    private javax.swing.JTextField txPrctOff;
    private javax.swing.JTextField txPriorty;
    private javax.swing.JTextField txPuntos;
    // End of variables declaration//GEN-END:variables
}
