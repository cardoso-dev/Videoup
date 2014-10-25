/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.views.sys;

import com.vendors.utils.WrapLayout;
import com.videoup.controllers.Controller;
import com.videoup.views.utils.ViewsSelector;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;

/**
 *
 * @author Pedro
 */
public class MenuExpnd extends javax.swing.JPanel {

    private Videoup vUp;
    private OpenViewsMenuExpnd lstViews;
    private SearchMnuExpnd quickSearch;
    
    /**
     * Creates new form MenuExpnd
     */
    public MenuExpnd(Videoup vUp){
        lstViews=new OpenViewsMenuExpnd();
        this.vUp=vUp;
        initComponents();
        pnlMenu=new javax.swing.JPanel();
        pnlMenu.setLayout(new WrapLayout(java.awt.FlowLayout.CENTER,17,19));
        jScrollPane1 = new javax.swing.JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setViewportView(pnlMenu);
        add(jScrollPane1, java.awt.BorderLayout.CENTER);
        vUp.add(lstViews, java.awt.BorderLayout.EAST);
        buildMenues();
        
    }
    
    private void buildMenues(){
        String dir="/com/videoup/imgs/";
        quickSearch=new SearchMnuExpnd(vUp);
        pnlMenu.add(quickSearch);
        Object[][] bts={
            {new ImageIcon(getClass().getResource(dir+"nuevo.png")),"Registrar alquiler","alqus_new"},
            {new ImageIcon(getClass().getResource(dir+"devol.png")),"Devolucion de articulos","alqus_dev"},
            {new ImageIcon(getClass().getResource(dir+"change.png")),"Cambio de articulos","alqus_chn"},
            {new ImageIcon(getClass().getResource(dir+"list.png")),"Listado de alquileres","alqus_lst"}
        };
        pnlMenu.add(new ItemMnuExpnd(vUp,"Alquileres","/com/videoup/imgs/mAlq.png",bts));
        bts=new Object[][]{
            {new ImageIcon(getClass().getResource(dir+"nuevo.png")),"Registrar socio","socs_new"},
            {new ImageIcon(getClass().getResource(dir+"list.png")),"Listado de socios","socs_lst"}
        };
        pnlMenu.add(new ItemMnuExpnd(vUp,"Socios","/com/videoup/imgs/mSocios.png",bts));
        bts=new Object[][]{
            {new ImageIcon(getClass().getResource(dir+"nuevo.png")),"Registrar pelicula","movies_new"},
            {new ImageIcon(getClass().getResource(dir+"list.png")),"Listado de Peliculas","movies_lst"}
        };
        pnlMenu.add(new ItemMnuExpnd(vUp,"Peliculas","/com/videoup/imgs/mMovies.png",bts));
        bts=new Object[][]{
            {new ImageIcon(getClass().getResource(dir+"nuevo.png")),"Registrar juego","games_new"},
            {new ImageIcon(getClass().getResource(dir+"list.png")),"Listado de juegos","games_lst"}
        };
        pnlMenu.add(new ItemMnuExpnd(vUp,"Juegos","/com/videoup/imgs/mGames.png",bts));
        bts=new Object[][]{
            {new ImageIcon(getClass().getResource(dir+"nuevo.png")),"Registrar venta","vnts_new"},
            {new ImageIcon(getClass().getResource(dir+"list.png")),"Listado de ventas","vnts_lst"}
        };
        pnlMenu.add(new ItemMnuExpnd(vUp,"Ventas","/com/videoup/imgs/mVnts.png",bts));
        bts=new Object[][]{
            {new ImageIcon(getClass().getResource(dir+"nuevo.png")),"Registrar oferta alquiler","oalqus_new"},
            {new ImageIcon(getClass().getResource(dir+"list.png")),"Listado de ofertas alquiler","oalqus_lst"}
        };
        pnlMenu.add(new ItemMnuExpnd(vUp,"Ofertas","/com/videoup/imgs/mOAlq.png",bts));
        bts=new Object[][]{
            {new ImageIcon(getClass().getResource(dir+"nuevo.png")),"Registrar bono","bonos_new"},
            {new ImageIcon(getClass().getResource(dir+"list.png")),"Listado de bonos","bonos_lst"}
        };
        pnlMenu.add(new ItemMnuExpnd(vUp,"Bonos","/com/videoup/imgs/mBonos.png",bts));
        bts=new Object[][]{
            {new ImageIcon(getClass().getResource(dir+"nuevo.png")),"Registrar precios alquiler","palqus_new"},
            {new ImageIcon(getClass().getResource(dir+"list.png")),"Listado de precios alquiler","palqus_lst"}
        }; 
        pnlMenu.add(new ItemMnuExpnd(vUp,"<html>Precios<br />Alquileres</html>","/com/videoup/imgs/mCAlq.png",bts));
        bts=new Object[][]{
            {new ImageIcon(getClass().getResource(dir+"rIngs.png")),"Reporte de ingresos","rpts_ings"},
            {new ImageIcon(getClass().getResource(dir+"rActivity.png")),"Reporte de actividad","rpts_activ"},
            {new ImageIcon(getClass().getResource(dir+"rSocios.png")),"Credencial de socios","rpts_socs"},
            {new ImageIcon(getClass().getResource(dir+"rMovs.png")),"Etiquetas de peliculas","rpts_movs"},
            {new ImageIcon(getClass().getResource(dir+"rGames.png")),"Etiquetas de videojuegos","rpts_games"}
        };
        pnlMenu.add(new ItemMnuExpnd(vUp,"Reportes","/com/videoup/imgs/mReps.png",bts));
        bts=new Object[][]{
            {new ImageIcon(getClass().getResource(dir+"nuevo.png")),"Registrar generos","catgs_new"},
            {new ImageIcon(getClass().getResource(dir+"list.png")),"Listado de generos","catgs_lst"}
        };
        pnlMenu.add(new ItemMnuExpnd(vUp,"Generos","/com/videoup/imgs/mCatgs.png",bts));
        bts=new Object[][]{
            {new ImageIcon(getClass().getResource(dir+"nuevo.png")),"Registrar formato","frmts_new"},
            {new ImageIcon(getClass().getResource(dir+"list.png")),"Listado de formatos","frmts_lst"}
        };
        pnlMenu.add(new ItemMnuExpnd(vUp,"Formatos","/com/videoup/imgs/mFrmts.png",bts));
        bts=new Object[][]{
            {new ImageIcon(getClass().getResource(dir+"sys.png")),"Sistema","conf_sys"},
            {new ImageIcon(getClass().getResource(dir+"dbs.png")),"Base de datos","conf_dbc"},
            {new ImageIcon(getClass().getResource(dir+"codebar.png")),"Codigos de barras","conf_cdb"},
            {new ImageIcon(getClass().getResource(dir+"langs.png")),"Idiomas en productos","conf_idm"},
            {new ImageIcon(getClass().getResource(dir+"taxes.png")),"Aplicacion de impuestos","conf_txs"},
            {new ImageIcon(getClass().getResource(dir+"web.png")),"Avisos web","conf_web"}
        };
        pnlMenu.add(new ItemMnuExpnd(vUp,"Configuraciones generales","/com/videoup/imgs/mConf.png",bts));
    }

    public void addViewName(Controller ctl, String name, int key, boolean closable){
        lstViews.addOpenViewName(ctl, name, key, closable);
    }
    
    public ViewsSelector getVsel() {
        return lstViews.getVsel();
    }
    
    public int getSearchType(){
        return quickSearch.getTypo();
    }
    
    public String getSearchString(){
        return quickSearch.getVal2Search();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        pnlMenu = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        pnlMenu.setLayout(null);
        jScrollPane1.setViewportView(pnlMenu);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnlMenu;
    // End of variables declaration//GEN-END:variables
}
