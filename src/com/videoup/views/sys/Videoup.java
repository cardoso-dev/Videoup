/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.views.sys;

import com.videoup.controllers.*;
import com.videoup.entities.VideoupCustomers;
import com.videoup.utils.Globals;
import com.videoup.views.utils.ViewsSelector;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TreeMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import org.h2.tools.Server;

/**
 *
 * @author Pedro
 */
public class Videoup extends javax.swing.JFrame implements ActionListener{

    private JButton[] bmMods = null;
    private Component compCent;
    private TreeMap<String,Controller> ctrllrs = null;
    // *********************** chango por congif local
    private int menu; // 0=sidebar, 1=expand
    private MenuExpnd menuExp;
    
    /**
     * Creates new form Videoup
     */
    public Videoup(){
        ConnectingTask task;
        menu=1;
        initComponents();
        setSize(976,683); 
        setTitle("Videoup");
        setIconImage(new javax.swing.ImageIcon(getClass().getResource("/com/videoup/imgs/logo.jpg")).getImage());
        buildPanels();
        ctrllrs=new TreeMap<String,Controller>();
        setLocationRelativeTo(null); setVisible(true); validate();
        task=new ConnectingTask();
        task.execute();
    }
        
    private void buildPanels(){
        if(menu==0){
            buildMenuSideBar();
        }else if(menu==1){
            buildMenuExpand();
        }
    }
    
    private String startServer(){
        try{
            String dbPath=getAppPath()+java.io.File.separator+"h2_db";
            Server.createTcpServer(new String[]{"-tcpAllowOthers","-baseDir",dbPath}).start();
            Globals.reloadConfig();
        }catch(Exception exc){
            //exc.printStackTrace();
            return exc.getMessage();
        }
        return null;
    }
    
    private String getAppPath(){
        String path = Videoup.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String decodedPath = path;
        String absolutePath;
        try {
            decodedPath = java.net.URLDecoder.decode(path, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            //e.printStackTrace();
            return null;
        }
        absolutePath = decodedPath.substring(0, decodedPath.lastIndexOf("/"))+"\\";
        return absolutePath;
    }
    
    private void setServerReady(String msg){
        setVisible(false);
        if(msg!=null){
            lblError.setText("<html><center>Error al conectar al servidor:<br />"+msg+"<br />Verifique que el servidor de base de datos este operando</center></html>");
            mainPane.remove(jxlbBusy);
            jxlbBusy.setBusy(false);
            mainPane.add(lblError,BorderLayout.CENTER);
        }else{
            jxlbBusy.setBusy(false);
            mainPane.remove(jxlbBusy);
            mainPane.remove(lblError);
            if(menu==0){
                for(JButton mdOBtn: bmMods){
                    if(!mdOBtn.getActionCommand().equals("rpts")){
                        mdOBtn.setEnabled(true);
                    }
                }
            }else if(menu==1){
                compCent=menuExp;
                mainPane.add(compCent,BorderLayout.CENTER);
            }
        }
        setVisible(true);
    }
    
    private void addBmMods(JPanel jp){
        String[] imgs={"mMovies.png","mGames.png","mSocios.png","mCatgs.png","mFrmts.png","mAlq.png","mVnts.png","mCAlq.png","mOAlq.png","mReps.png","mConf.png"};
        String[] ttex={"Peliculas","Videojuegos","Socios","Generos","Formatos","Alquileres","Ventas","Precios alquileres","Ofertas alquileres","Reportes","Configuracion"};
        String[] acts={"movies","games","socs","catgs","frmts","alqus","vnts","palqus","oalqus","rpts","conf"};
        bmMods=new JButton[imgs.length];
        java.awt.GridBagConstraints gbc=new java.awt.GridBagConstraints();
        gbc.fill=java.awt.GridBagConstraints.HORIZONTAL; gbc.insets= new java.awt.Insets(4,2,0,2);
        gbc.gridx=0; gbc.gridy=0;
        for(int i=0;i<imgs.length;i++) {
            bmMods[i]=new JButton();
            bmMods[i].setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/videoup/imgs/"+imgs[i])));
            bmMods[i].setToolTipText(ttex[i]);
            bmMods[i].addActionListener(this);
            if(i%2==0){ gbc.gridy=gbc.gridy+1; gbc.gridx=0; }
            else{ gbc.gridx=1; }
            ((GridBagLayout)jp.getLayout()).setConstraints(bmMods[i],gbc);
            bmMods[i].setActionCommand(acts[i]);
            bmMods[i].setEnabled(false);
            jp.add(bmMods[i]);
        }
    }
    
    private void buildMenuSideBar(){
        addBmMods(westPane);
    }
    
    private void buildMenuExpand(){
        mainPane.remove(westPane);
        westPane=null;
        menuExp=new MenuExpnd(this);
    }
    
    public void replaceView(Component vw,boolean isSelector){ 
        mainPane.setVisible(false);
        if(compCent!=null){ mainPane.remove(compCent); }
        if(isSelector && menu==1){
            compCent=menuExp;
        }else{
            compCent=vw;
        }
        mainPane.add(compCent,BorderLayout.CENTER);
        mainPane.setVisible(true);
    }
    
    public void removeView(Component vw){
        if(compCent.equals(vw)){
            mainPane.setVisible(false); mainPane.remove(vw);
            compCent=null; mainPane.setVisible(true);
        }
    }
        
    public String askForString(String mssg, String title){
        String getted=JOptionPane.showInputDialog(this, mssg, title, JOptionPane.QUESTION_MESSAGE);
        return getted;
    }   
    
    /**
     * @param type 0=socios cred, 1=movies tag, 2=juegos tag
     * @param codes 
     */
    public void generateCodesReport(int type,String codes){
        Controller ctrler=ctrllrs.get("rpts");
        ViewsSelector vsl=null;
        if(ctrler==null){
            if(menuExp!=null){ vsl=menuExp.getVsel(); }
            ctrler=new CReps(this,vsl,"Reportes","/com/videoup/imgs/mReps.png");
            ctrllrs.put("rpts",ctrler);
        }
        if(type==0){
            ((CReps)ctrler).generateSociosCred(codes);
        }else if(type==1){
            ((CReps)ctrler).generateMoviesTag(codes);
        }else if(type==2){
            ((CReps)ctrler).generateGamesTag(codes);
        }
    }
    
    public void startSociosAlq(VideoupCustomers soc){
        Controller ctrler=ctrllrs.get("alqus");
        ViewsSelector vsl=null;
        if(ctrler==null){
            if(menuExp!=null){ vsl=menuExp.getVsel(); }
            ctrler=new CAlquilers(this,vsl,"Alquileres","/com/videoup/imgs/mAlq.png");
            ctrllrs.put("alqus",ctrler);
        }
        ((CAlquilers)ctrler).startAlqr(soc);
    }
    
    public void startSociosVenta(VideoupCustomers soc){
        Controller ctrler=ctrllrs.get("vnts");
        ViewsSelector vsl=null;
        if(ctrler==null){
            if(menuExp!=null){ vsl=menuExp.getVsel(); }
            ctrler=new CVentas(this,vsl,"Ventas","/com/videoup/imgs/mVnts.png");
            ctrllrs.put("vnts",ctrler);
        }
        ((CVentas)ctrler).startVenta(soc);
    }
    
    public boolean confirm(String tit, String msg){
        int res=JOptionPane.showConfirmDialog(this,msg,tit,JOptionPane.YES_NO_CANCEL_OPTION);
        if(res==JOptionPane.YES_OPTION){ return true; }
        return false;
    }   
    
    public void showDialogErr(final String err){
        final Videoup vup=this;
        EventQueue.invokeLater(new Runnable(){
            @Override
            public void run(){
                JOptionPane.showMessageDialog(vup,err,"Error",JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    public void showDialogInf(final String tit, final String inf){
        final Videoup vup=this;
        EventQueue.invokeLater(new Runnable(){
            @Override
            public void run(){
                JOptionPane.showMessageDialog(vup, inf, tit, JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }
    
    private void closeApp(){
        /*String[] acts={"movies","games","socs","catgs","frmts","alqus","vnts","palqus","oalqus","rpts","conf"};
        boolean hasChanges=false; */
        boolean flag=true; 
        /* B_Ctrl ctrler;
        for(int h=0;h<acts.length;h++){
            ctrler=ctrllrs.get(acts[h]);
            if(ctrler!=null && ctrler.hasChanges()){ hasChanges=true; break; }
        }
        if(hasChanges){
            flag=confirm("Videoup", "Hay modulos con registros sin guardar, ¿Desea salir y perder los cambios?");
        }*/
        if(flag){
            this.setVisible(false);
            System.exit(0);
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

        lblError = new javax.swing.JLabel();
        mainPane = new javax.swing.JPanel();
        westPane = new com.videoup.views.utils.JRoundPanel();
        jxlbBusy = new org.jdesktop.swingx.JXBusyLabel();

        lblError.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblError.setForeground(new java.awt.Color(212, 0, 0));
        lblError.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblError.setText("<html><center>Error al conectar al servidor:<br />error no se puedo establecer la conexion</center></html>");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        mainPane.setLayout(new java.awt.BorderLayout());

        westPane.setBackground(new java.awt.Color(127, 203, 255));
        westPane.setRadio1(35);
        westPane.setRadio2(35);
        westPane.setLayout(new java.awt.GridBagLayout());
        mainPane.add(westPane, java.awt.BorderLayout.WEST);

        jxlbBusy.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jxlbBusy.setText("Conectando a la base de datos");
        jxlbBusy.setBusy(true);
        jxlbBusy.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        mainPane.add(jxlbBusy, java.awt.BorderLayout.CENTER);

        getContentPane().add(mainPane, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        Videoup vup;
        /* Create and display the form */
        try{
          javax.swing.UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");
            // if you want decorations for frames and dialogs you can put this two lines
          JFrame.setDefaultLookAndFeelDecorated(true);	// to decorate frames 
          JDialog.setDefaultLookAndFeelDecorated(true);	// to decorate dialogs 
	    // or put this one line
            //com.birosoft.liquid.LiquidLookAndFeel.setLiquidDecorations(true);
            // or if you want to use Apple's Panther window decoration
            // com.birosoft.liquid.LiquidLookAndFeel.setLiquidDecorations(true, "panther");
        }catch (Exception e){}
        vup=new Videoup();
        Thread.setDefaultUncaughtExceptionHandler(new FailCatcher(vup));
        vup.setVisible(true);
        /*java.awt.EventQueue.invokeLater(new Runnable() {
            public void run(){}
        });*/
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXBusyLabel jxlbBusy;
    private javax.swing.JLabel lblError;
    private javax.swing.JPanel mainPane;
    private com.videoup.views.utils.JRoundPanel westPane;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent e) {
        String[] act=e.getActionCommand().split("_");
        Controller ctrler;
        ViewsSelector vsl=null;
        Component view;
        if(act[1].equals("search")){
            act[0]=(menuExp.getSearchType()==0?"socs":(menuExp.getSearchType()==1?"movies":"games"));
        }
        ctrler=ctrllrs.get(act[0]);
        if(ctrler==null){
            if(menuExp!=null){ vsl=menuExp.getVsel(); }
            if(act[0].equals("movies")){
                ctrler=new CMovies(this,vsl,"Peliculas","/com/videoup/imgs/mMovies.png");
                ctrllrs.put(act[0],ctrler);
            }else if(act[0].equals("games")){
                ctrler=new CGames(this,vsl,"Videojuegos","/com/videoup/imgs/mGames.png");
                ctrllrs.put(act[0],ctrler);
            }else if(act[0].equals("catgs")){
                ctrler=new CCatgs(this,vsl,"Generos","/com/videoup/imgs/mCatgs.png");
                ctrllrs.put(act[0],ctrler);
            }else if(act[0].equals("frmts")){
                ctrler=new CFormts(this,vsl,"Formatos","/com/videoup/imgs/mFrmts.png");
                ctrllrs.put(act[0],ctrler);
            }else if(act[0].equals("socs")){
                ctrler=new CSocios(this,vsl,"Socios","/com/videoup/imgs/mSocios.png");
                ctrllrs.put(act[0],ctrler);
            }else if(act[0].equals("alqus")){
                ctrler=new CAlquilers(this,vsl,"Alquileres","/com/videoup/imgs/mAlq.png");
                ctrllrs.put(act[0],ctrler);
            }else if(act[0].equals("vnts")){
                ctrler=new CVentas(this,vsl,"Ventas","/com/videoup/imgs/mVnts.png");
                ctrllrs.put(act[0],ctrler);
            }else if(act[0].equals("palqus")){
                ctrler=new CPreAlqs(this,vsl,"Precios alquileres","/com/videoup/imgs/mCAlq.png");
                ctrllrs.put(act[0],ctrler);
            }else if(act[0].equals("oalqus")){
                ctrler=new COrfts(this,vsl,"Ofertas alquileres","/com/videoup/imgs/mOAlq.png");
                ctrllrs.put(act[0],ctrler);
            }else if(act[0].equals("conf")){
                ctrler=new CConf(this,vsl,"Configuracion","/com/videoup/imgs/mConf.png");
                ctrllrs.put(act[0],ctrler);
            }else if(act[0].equals("rpts")){
                ctrler=new CReps(this,vsl,"Reportes","/com/videoup/imgs/mReps.png");
                ctrllrs.put(act[0],ctrler);
            }else if(act[0].equals("bonos")){
                ctrler=new CBonos(this,vsl,"Bonos","/com/videoup/imgs/mBonos.png");
                ctrllrs.put(act[0],ctrler);
            }
        }
        if(act[1].equals("search")){
            quickSearch(ctrler,menuExp.getSearchString());
        }else if(act.length==2){
            ctrler.goToNamedView(act[1]);
        }else{
            view=ctrler.getCurrentView();
            mainPane.setVisible(false);
            if(compCent!=null){ mainPane.remove(compCent); }
            mainPane.add(view,BorderLayout.CENTER);
            compCent=view;
            mainPane.setVisible(true);
        }
    }
    
    private void quickSearch(Controller ctl, String val){
        if(val.trim().length()>0){
            ctl.loadFilteredList(val);
            ctl.goToNamedView("lst");
        }
    }
    
    public int usingMenu(){
        return menu;
    }
    
    public void addVNameGlobalList(Controller ctl, String name, int key, boolean closable){
        if(menuExp!=null){
            menuExp.addViewName(ctl, name, key, closable);
        }
    }
    
    public class ConnectingTask extends SwingWorker<Boolean, Void>{
        String getted;
        @Override
        protected Boolean doInBackground() throws Exception{
            boolean result=true;
            getted=startServer();
            if(getted==null){
                result=false;
            }
            return result;
        }
        @Override
        protected void done(){
            setServerReady(getted);
        }
    }
}
