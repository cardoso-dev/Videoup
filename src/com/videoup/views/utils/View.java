/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.views.utils;

import com.videoup.controllers.Controller;
import com.videoup.entities.VideoupBaseEntity;
import com.videoup.utils.DBUtils;
import com.videoup.utils.GeneralDAO;
import com.videoup.views.sys.Videoup;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Pedro
 */
public class View extends javax.swing.JPanel {
    
    protected Controller ctrl;
    private int id; // -2 viewselector, -1 list, 0=new non saved, >0 id record 
    protected boolean changes;
    protected GeneralDAO genDAO;
    protected String error;
    
    public View(Controller ctrl, int id) {
        this.ctrl=ctrl;
        initComponents();
        this.id=id;
        changes=false;
        genDAO=new GeneralDAO();
    }    

    public int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }
    
    public boolean hasChanges(){
        return changes;
    }
    
    public void closeMe(){
        boolean cont=true;
        if(hasChanges()){
            cont=ctrl.confirm("Cerrar modulo","Hay datos si guardar ¿desea cerrar y perder cambios?");
        }
        if(cont && ctrl!=null){
            ctrl.closeView(this);
        }
    }
    
    public String getTitle(){
        return "View id: "+getId();
    }
    
    public List loadList(String fquery, String cntQry, int pag, boolean limit){
        List lista=genDAO.getListEntities(fquery, cntQry, pag, limit);
        if(lista==null){
            error=genDAO.getError();
        }
        return lista;
    }
    
    public List loadListUseSQL(String fquery, String cntQry, int pag, boolean limit){
        List lista=genDAO.getListEntitiesUseSQL(fquery, cntQry, pag, limit);
        if(lista==null){
            error=genDAO.getError();
        }
        return lista;
    }
    
    public String loadIdListUseSQL(String fquery, int pag, boolean limit){
        String sr=DBUtils.loadIdListUseSQL(fquery, pag, limit);
        if(sr==null){
            error=DBUtils.getError();
        }
        return sr;
    }
    
    public int executeSQL(String qry){
        int res=genDAO.executeSQL(qry);
        if(res<0){
            error=genDAO.getError();
        }
        return res;
    }
    
    protected boolean saveEntities(List<VideoupBaseEntity> entities){
        boolean saveok=genDAO.saveEntities(entities);
        if(!saveok){
            error=genDAO.getError();
        }else{
            error=null;
        }
        return saveok;
    }
    
    /**
     * 
     * @param ops Operators separated by a blank space
     * @param tables Tables separated by a blank space
     * @return 
     */
    protected String buildLike4Where(String opers,String tables){
        String[] ops=opers.split(" ");
        String[] tbs=tables.split(" ");
        String builded="";
        int gh;
        int ji;
        for(gh=0;gh<ops.length;gh++){
            for(ji=0;ji<tbs.length;ji++){
                builded+=(builded.length()>0?" or ":"")+"upper("+tbs[ji]+") like '%"+ops[gh].toUpperCase()+"%'";
            }
        }
        if(ops.length>0 && tbs.length>0){
            builded="("+builded+")";
        }
        return builded;
    }
    
    /**
     * 
     * @param ops Operators separated by a blank space
     * @param tables Tables separated by a blank space
     * @return 
     */
    protected String buildCompare4Where(String opers,String tables,String oper){
        String[] ops=opers.split(" ");
        String[] tbs=tables.split(" ");
        String builded="";
        int gh;
        int ji;
        for(gh=0;gh<ops.length;gh++){
            for(ji=0;ji<tbs.length;ji++){
                builded+=(builded.length()>0?" or ":"")+"upper("+tbs[ji]+")"+oper+"'"+ops[gh].toUpperCase()+"'";
            }
        }
        if(ops.length>0 && tbs.length>0){
            builded="("+builded+")";
        }
        return builded;
    }
    
    public long countExistEntities(String countQry){
        long totals=genDAO.countExistEntities(countQry);
        if(totals==-1){
            error=genDAO.getError();
        }
        return totals;
    }
    
    
    public long countExistEntitiesUseSQL(String countQry){
        long totals=genDAO.countExistEntitiesUseSQL(countQry);
        if(totals==-1){
            error=genDAO.getError();
        }
        return totals;
    }
    
    public VideoupBaseEntity getEntity(String query){
        GeneralDAO gDao=new GeneralDAO();
        VideoupBaseEntity ent;
        ent=gDao.getEntity(query);
        if(ent==null){
            error=gDao.getError();
        }
        return ent;
    }
    
    public Videoup getMainWindow(){
        return ctrl.getMainWindow();
    }
    
    public void showError(String someErr){
        ctrl.showDialogErr(someErr);
    }
    
    public void showInfo(String tit, String someInfo){
        ctrl.showDialogInf(tit,someInfo);
    }
    
    protected void setError(String er){
        error=er;
    }
    
    public String getError(){
        return error;
    }
    
    protected String dte2SQL(Date dte){
        SimpleDateFormat dteFrmt=new SimpleDateFormat("yyyy-MM-dd");
        return dteFrmt.format(dte);
    }
    
    protected String asDate(Object dte, int dteFormat, int timeFormat){
        DateFormat dteFrmt;
        Calendar cal;
        String jd;
        String[] tks;
        if(dte==null){
            return "";
        }
        if(timeFormat==0){
            dteFrmt=DateFormat.getDateInstance(dteFormat);
        }else{
            dteFrmt=DateFormat.getDateTimeInstance(dteFormat,timeFormat);
        }
        cal=Calendar.getInstance();
        if(dte instanceof Date){
            cal.setTime( (Date)dte );
        }else{
            jd=dte.toString().split(" ")[0];
            tks=jd.split("-");
            cal.set(Calendar.YEAR, Integer.parseInt(tks[0]));
            cal.set(Calendar.MONTH, Integer.parseInt(tks[1]));
            cal.set(Calendar.DATE, Integer.parseInt(tks[2]));
        }
        return dteFrmt.format(cal.getTime());
    }
    
    protected String date4Where(Date dte){
        Calendar cal=Calendar.getInstance();
        String dtf;
        if(dte!=null){
            cal.setTime(dte);
        }
        dtf=""+cal.get(Calendar.YEAR)+"-"+setCoupleDigits(cal.get(Calendar.MONTH)+1);
        dtf+="-"+setCoupleDigits(cal.get(Calendar.DATE));
        return dtf;
    }
        
    private String setCoupleDigits(int vl){
        return (vl<10?"0":"")+vl;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
