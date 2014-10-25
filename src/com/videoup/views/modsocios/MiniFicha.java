/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.views.modsocios;

import com.videoup.entities.VideoupAutrz;
import com.videoup.entities.VideoupCustomers;
import com.videoup.entities.VideoupItemsrnt;
import com.videoup.entities.VideoupRentas;
import com.videoup.utils.GeneralDAO;
import com.videoup.utils.Globals;
import com.videoup.views.utils.ImagePanel;
import com.videoup.views.utils.ViewFicha;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import javax.swing.ImageIcon;

/**
 *
 * @author Pedro
 */
public class MiniFicha extends javax.swing.JPanel {

    private ViewFicha parent;
    private VideoupCustomers socio;
    private String[] infos;
      // 0=neutral, 1=negativa, 2=advertencia, 3=positiva, 4=articles has the socio
    
    /**
     * Creates new form MiniFicha
     */
    public MiniFicha(VideoupCustomers socio, ViewFicha parent) {
        this.socio=socio;
        this.parent=parent;
        infos=new String[]{"","","","",""};
        initComponents();
        createInfoBar();
        showData();
    }
    
    private void createInfoBar(){
        Image imRed=(new ImageIcon(getClass().getResource("/com/videoup/imgs/ovalCntRed.png"))).getImage();
        Image imBlue=(new ImageIcon(getClass().getResource("/com/videoup/imgs/ovalCntBlue.png"))).getImage();
        Image imRed2=(new ImageIcon(getClass().getResource("/com/videoup/imgs/ovalCntRed.png"))).getImage();
        Image imOran=(new ImageIcon(getClass().getResource("/com/videoup/imgs/ovalCntOran.png"))).getImage();
        Image imNeg=(new ImageIcon(getClass().getResource("/com/videoup/imgs/ovalCntNeg.png"))).getImage();
        Image imVrd=(new ImageIcon(getClass().getResource("/com/videoup/imgs/ovalCntVrd.png"))).getImage();
        Image imAma=(new ImageIcon(getClass().getResource("/com/videoup/imgs/ovalCntYellow.png"))).getImage();
        ImagePanel pnlRed=new ImagePanel( imageToBufferedImage(imRed) );
        ImagePanel pnlBlue=new ImagePanel( imageToBufferedImage(imBlue) );
        ImagePanel pnlRed2=new ImagePanel( imageToBufferedImage(imRed2) );
        ImagePanel pnlOran=new ImagePanel( imageToBufferedImage(imOran) );
        ImagePanel pnlNeg=new ImagePanel( imageToBufferedImage(imNeg) );
        ImagePanel pnlVrd=new ImagePanel( imageToBufferedImage(imVrd) );
        ImagePanel pnlAma=new ImagePanel( imageToBufferedImage(imAma) );
        pnlRed.setLayout(new BorderLayout());
        pnlRed.add(lblAdeudo,BorderLayout.CENTER);
        pnlBlue.setLayout(new BorderLayout());
        pnlBlue.add(lblArticulos,BorderLayout.CENTER);
        pnlRed2.setLayout(new BorderLayout());
        pnlRed2.add(lblNegativo,BorderLayout.CENTER);
        pnlOran.setLayout(new BorderLayout());
        pnlOran.add(lblAdvertencia,BorderLayout.CENTER);
        pnlNeg.setLayout(new BorderLayout());
        pnlNeg.add(lblNeutral,BorderLayout.CENTER);
        pnlVrd.setLayout(new BorderLayout());
        pnlVrd.add(lblPositivo,BorderLayout.CENTER);
        pnlAma.setLayout(new BorderLayout());
        pnlAma.add(lblBonos,BorderLayout.CENTER);
        pnlInfo.removeAll();
        pnlInfo.add(pnlAma);
        pnlInfo.add(pnlVrd);
        pnlInfo.add(pnlNeg);
        pnlInfo.add(pnlOran);
        pnlInfo.add(pnlRed2);
        pnlInfo.add(pnlBlue);
        pnlInfo.add(pnlRed);
    }
    
    public static BufferedImage imageToBufferedImage(Image im) {
        BufferedImage bi=new BufferedImage(im.getWidth(null),im.getHeight(null),BufferedImage.TYPE_INT_ARGB);
        Graphics bg = bi.getGraphics();
        bg.drawImage(im, 0, 0, null);
        bg.dispose();
        return bi;
    }
    
    private void showData(){
        List<VideoupRentas> alqs;
        List<VideoupItemsrnt> prods;
        NumberFormat nmf=NumberFormat.getCurrencyInstance();
        int movsOn=0, gamesOn=0, bonos=0;
        String contacto="<html>"+(socio.getEmail()==null?"":"Mail: "+socio.getEmail()+"<br />")+"Tel: "+socio.getTelHome()+"<br />";
        contacto+=(socio.getTelMovil()!=null&&!socio.getTelMovil().equals("")?"Movil: "+socio.getTelMovil():"")+"</html>";
        String direccion="<html>Direccion: "+socio.getAddr()+"<br />"+(socio.getPobl()==null?"":socio.getPobl()+"<br />");
        direccion+=(socio.getCity()==null?"":socio.getCity()+"<br />")+(socio.getCodp()==null?"":"CP: "+socio.getCodp()+"<br />");
        direccion+=(socio.getProv()==null?"":socio.getProv())+"</html>";
        List<VideoupAutrz> pautz=socio.getVideoupAutrzList();
        String psAut="<html>";
        bonos=countBonos();
        lblNombre.setText(socio.getName()+" "+socio.getApplldos());
        lblDni.setText((String)Globals.getConfig("dniDef")+": "+socio.getDni());
        lblCodeSoc.setText( (socio.getCodCst()!=null?socio.getCodCst():" --- ") );
        lblContacto.setText(contacto);
        lblDireccion.setText(direccion);
        alqs=socio.getVideoupRentasList();
        if(alqs!=null){
            for(VideoupRentas alq: alqs){
                prods=alq.getVideoupItemsrntList();
                for(VideoupItemsrnt prod: prods){
                    if(prod.getFTime()==null && prod.getStatus()==3){
                        infos[4]+=prod.getDescr()+"<br />";
                        movsOn+=(prod.getIsmov()?1:0);
                        gamesOn+=(prod.getIsmov()?0:1);
                    }
                }
            }
        }
        lblArticulos.setText( (movsOn>0||gamesOn>0?""+(movsOn+gamesOn):"0") );
        lblArticulos.setToolTipText( movsOn>0||gamesOn>0?
                "Tiene "+(movsOn>0?movsOn+" peliculas, ":"")+(gamesOn>0?gamesOn+" juegos":"")+" en su poder":
                "No tiene articulos en su poder" );
        lblAdeudo.setText((socio.getCredito()<0?""+Math.abs(socio.getCredito()):"0"));
        lblAdeudo.setToolTipText((socio.getCredito()<0?"Adeuda: "+nmf.format(Math.abs(socio.getCredito())):"Sin adeudo"));
        lblNegativo.setText(""+socio.countNotesByType(1));
        lblNegativo.setToolTipText( socio.countNotesByType(1)>0?socio.countNotesByType(1)+" notas negativas":"Sin notas negativas" );
        lblAdvertencia.setText(""+socio.countNotesByType(2));
        lblAdvertencia.setToolTipText( socio.countNotesByType(2)>0?socio.countNotesByType(2)+" advertencias":"Sin advertencias" );
        lblNeutral.setText(""+socio.countNotesByType(0));
        lblNeutral.setToolTipText( socio.countNotesByType(0)>0?socio.countNotesByType(0)+" notas":"Sin notas" );
        lblPositivo.setText(""+socio.countNotesByType(3));
        lblPositivo.setToolTipText( socio.countNotesByType(3)>0?socio.countNotesByType(3)+" notas positivas":"Sin notas positivas" );
        lblBonos.setText(""+(bonos<0?"e!":bonos));
        lblBonos.setToolTipText( ""+(bonos<0?"Error al consultar bonos!":"Cuenta con "+bonos+" bonos") );
        loadNotes(0); loadNotes(1); loadNotes(2); loadNotes(3); loadNotes(4);
        if(pautz==null || pautz.isEmpty()){
            lblPAutrz.setText("No hay registros");
        }else{
            for(VideoupAutrz per: pautz){
                psAut+=per.getPname()+"<br />";
            }
            lblPAutrz.setText(psAut+"</html>");
        }
    }

    private int countBonos(){
        GeneralDAO gDao=new GeneralDAO();
        int bonos;
        Calendar cal=Calendar.getInstance();
        String dtf=""+cal.get(Calendar.YEAR)+"-"+setCoupleDigits(cal.get(Calendar.MONTH)+1);
        dtf+="-"+setCoupleDigits(cal.get(Calendar.DATE));
        String qry="select sum(bonos-used) from videoup_soldbonos where bonos>used and '"+dtf+"'<=hasta and idcli="+socio.getIdct();
        bonos=gDao.countExistEntitiesUseSQL(qry);
        return bonos;
    }
        
    private String setCoupleDigits(int vl){
        return (vl<10?"0":"")+vl;
    }
    
    private void loadNotes(int tp){
        String[] notes;
        if(socio.countNotesByType(tp)>0){
            notes=socio.getNotesByType(tp);
            for(String nt: notes){
                infos[tp]+="* "+nt+"<br />";
            }
        }
    }
    
    /**
     * 
     * @param inf 0=neutral, 1=negativa, 2=advertencia, 3=positiva, 4=articles has the socio
     */
    private void showInfo(int inf){
        String[] tts=new String[]{"Notas neutrales","Notas negativas","Advertencias","Notas positivas","Articulos"};
        if(infos[inf].length()>0){
            parent.showInfo(tts[inf],"<html>"+infos[inf]+"</html>");
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

        lblNombre = new javax.swing.JLabel();
        lblDni = new javax.swing.JLabel();
        lblCodeSoc = new javax.swing.JLabel();
        lblContacto = new javax.swing.JLabel();
        lblDireccion = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblPAutrz = new javax.swing.JLabel();
        pnlInfo = new javax.swing.JPanel();
        lblBonos = new javax.swing.JLabel();
        lblPositivo = new javax.swing.JLabel();
        lblNeutral = new javax.swing.JLabel();
        lblAdvertencia = new javax.swing.JLabel();
        lblNegativo = new javax.swing.JLabel();
        lblArticulos = new javax.swing.JLabel();
        lblAdeudo = new javax.swing.JLabel();

        lblNombre.setText("Nombre");

        lblDni.setText("lblDni");

        lblCodeSoc.setText("lblCodeSoc");

        lblContacto.setText("Datos contacto");

        lblDireccion.setText("Direccion");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("<html><u>Pueden usar la cuenta</u></html>");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("<html><u>Datos del socio<U></html>");

        lblPAutrz.setText("Personas autorizadas");

        pnlInfo.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 113), 1, true));
        pnlInfo.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 4, 2));

        lblBonos.setFont(new java.awt.Font("Tahoma", 1, 19)); // NOI18N
        lblBonos.setForeground(new java.awt.Color(0, 95, 0));
        lblBonos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBonos.setText("99");
        pnlInfo.add(lblBonos);

        lblPositivo.setFont(new java.awt.Font("Tahoma", 1, 19)); // NOI18N
        lblPositivo.setForeground(new java.awt.Color(0, 59, 0));
        lblPositivo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPositivo.setText("99");
        lblPositivo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblPositivoMouseClicked(evt);
            }
        });
        pnlInfo.add(lblPositivo);

        lblNeutral.setFont(new java.awt.Font("Tahoma", 1, 19)); // NOI18N
        lblNeutral.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNeutral.setText("99");
        lblNeutral.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblNeutralMouseClicked(evt);
            }
        });
        pnlInfo.add(lblNeutral);

        lblAdvertencia.setFont(new java.awt.Font("Tahoma", 1, 19)); // NOI18N
        lblAdvertencia.setForeground(new java.awt.Color(185, 69, 0));
        lblAdvertencia.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAdvertencia.setText("99");
        lblAdvertencia.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblAdvertenciaMouseClicked(evt);
            }
        });
        pnlInfo.add(lblAdvertencia);

        lblNegativo.setFont(new java.awt.Font("Tahoma", 1, 19)); // NOI18N
        lblNegativo.setForeground(new java.awt.Color(185, 0, 0));
        lblNegativo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNegativo.setText("99");
        lblNegativo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblNegativoMouseClicked(evt);
            }
        });
        pnlInfo.add(lblNegativo);

        lblArticulos.setFont(new java.awt.Font("Tahoma", 1, 19)); // NOI18N
        lblArticulos.setForeground(new java.awt.Color(27, 33, 216));
        lblArticulos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblArticulos.setText("99");
        lblArticulos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblArticulosMouseClicked(evt);
            }
        });
        pnlInfo.add(lblArticulos);

        lblAdeudo.setFont(new java.awt.Font("Tahoma", 1, 19)); // NOI18N
        lblAdeudo.setForeground(new java.awt.Color(133, 0, 0));
        lblAdeudo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAdeudo.setText("99");
        pnlInfo.add(lblAdeudo);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(lblDni, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblCodeSoc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblContacto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblDireccion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                    .addComponent(lblNombre, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblPAutrz, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblNombre)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblDni)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCodeSoc)
                .addGap(18, 18, 18)
                .addComponent(lblContacto)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblDireccion)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblPAutrz)
                .addContainerGap(106, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void lblNegativoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblNegativoMouseClicked
        showInfo(1);
    }//GEN-LAST:event_lblNegativoMouseClicked

    private void lblAdvertenciaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAdvertenciaMouseClicked
        showInfo(2);
    }//GEN-LAST:event_lblAdvertenciaMouseClicked

    private void lblArticulosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblArticulosMouseClicked
        showInfo(4);
    }//GEN-LAST:event_lblArticulosMouseClicked

    private void lblNeutralMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblNeutralMouseClicked
        showInfo(0);
    }//GEN-LAST:event_lblNeutralMouseClicked

    private void lblPositivoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblPositivoMouseClicked
        showInfo(3);
    }//GEN-LAST:event_lblPositivoMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel lblAdeudo;
    private javax.swing.JLabel lblAdvertencia;
    private javax.swing.JLabel lblArticulos;
    private javax.swing.JLabel lblBonos;
    private javax.swing.JLabel lblCodeSoc;
    private javax.swing.JLabel lblContacto;
    private javax.swing.JLabel lblDireccion;
    private javax.swing.JLabel lblDni;
    private javax.swing.JLabel lblNegativo;
    private javax.swing.JLabel lblNeutral;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblPAutrz;
    private javax.swing.JLabel lblPositivo;
    private javax.swing.JPanel pnlInfo;
    // End of variables declaration//GEN-END:variables
}

