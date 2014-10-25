/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.utils;

import com.videoup.entities.VideoupBaseEntity;
import com.videoup.entities.VideoupBcodes;
import com.videoup.entities.VideoupCtofrentas;
import com.videoup.entities.VideoupCtprrentas;
import com.videoup.entities.VideoupCustomers;
import com.videoup.entities.VideoupGames;
import com.videoup.entities.VideoupItemsrnt;
import com.videoup.entities.VideoupMovies;
import com.videoup.entities.VideoupRentas;
import com.videoup.views.sys.Videoup;
import java.awt.Component;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Pedro
 */
public class GenProccess {
    
    private static String error;

    public static String getError() {
        return error;
    }
    
    // itemsrnt utils
    
    public static String getSpentTime(VideoupItemsrnt itmr){
        long secs=getSeconds(itmr);
        return stringSpentTime(secs);
    }
    
    public static String stringSpentTime(long secs){
        long mins=secs/60;
        long hours=mins/60;
        long days=hours/24;
        secs-=(mins*60);
        mins-=(hours*60);
        hours-=(days*24);
        return (days>0?days+" dias, ":"")+(hours>0?hours+" horas, ":"")+(mins>0?mins+" minutos ":"")+(secs>0?secs+" segundos ":"");
    }
    
    public static long getHoursSpent(VideoupItemsrnt itmr,int ignoresecs){
        long secs=getSeconds(itmr)-ignoresecs;
        long mins=secs/60;
        long hours=mins/60;
        hours+=( (float)hours<(((float)secs/(float)60)/(float)60)?1:0 );
        return hours;
    }
    
    public static float getCosto(VideoupItemsrnt itmr, int h){
        float[] costos=GenProccess.getCostos(itmr,0,h);
        return costos[4];
    }
    
    /** 
     *  
     * @param itmr
     * @param ignoresecs
     * @param hours
     * @return segun indice 0=final, 1=ofrt, 2=% desc ofrt, 3=xtra, 4=apli
     */
    public static float[] getCostos(VideoupItemsrnt itmr,int ignoresecs,int hours){
        float costos[]=new float[5];
        long secs=(hours>0?(hours*3600):getSeconds(itmr))-ignoresecs;
        secs=(secs<0?0:secs);
        long mins=secs/60;
        int fracExtra=Globals.getIntConfig("fracMinExtra");
        long minsx;
        int unsx;
        int TBs=0;
        int regularTime;
        int min_sx;
        float costo_minsx;
        if(itmr.getStatus()==0||itmr.getStatus()==1||itmr.getStatus()==6){
            costos[0]=costos[1]=costos[2]=costos[3]=costos[4]=0;
        }else if(itmr.getStatus()!=3 && itmr.getStatus()!=7){
            costos[0]=itmr.getCstFin();
            costos[1]=itmr.getOfrtCfin();
            costos[2]=itmr.getOfrtOff();
            costos[3]=itmr.getCstXt();
            costos[4]=itmr.getCstApli();
        }else{
            // revisar que se aplique costo por tiempo base
            if(secs>0){
                costos[0]=itmr.getCstUt()*itmr.getBTime();
            }else{ costos[0]=0; }
            costos[1]=itmr.getOfrtCfin();
            costos[2]=itmr.getOfrtOff();
            if( (costos[1]>0||costos[2]>0) && itmr.getVideoupCtofrentas().getAplydias()>0 ){
                // costo de oferta aplicar por TBs
                TBs=itmr.getVideoupCtofrentas().getAplydias();
                costos[1]=costos[1]*TBs;
            }
            regularTime=( itmr.getBTime()*itmr.getUTime()*(TBs>0?TBs:1) );
            if(mins>regularTime){ // tiene tiempo extra
                costos[0]=costos[0]*(TBs>0?TBs:1);
                minsx=mins-regularTime;
                if(fracExtra>0){
                    min_sx=(int)Math.floor((double)minsx/(double)fracExtra);
                    min_sx*=fracExtra;
                    costo_minsx=(itmr.getCstUtx()/(itmr.getBTime()*itmr.getUTime()));
                    costos[3]=min_sx*costo_minsx;
                }else{
                    unsx=(int)Math.ceil( (double)minsx/(double)itmr.getUTime() );
                    costos[3]=itmr.getCstUtx()*unsx;
                }
            }
            if(costos[1]!=0){ costos[4]=costos[1]+costos[3];
            }else if(costos[2]!=0){
                costos[4]= (costos[0]*(1-(costos[2]/100f))) + costos[3];
            }else{ costos[4]=costos[0]+costos[3]; }
        }
        return costos;
    }
    
    public static String getItemName(VideoupItemsrnt itmr){
        VideoupBcodes copy=itmr.getIdbc();
        if(itmr.getIsmov()){
            return "Pelicula: "+copy.getVideoupMovie().getTitulo();
        }else{
            return "Videojuego: "+copy.getVideoupGame().getTitulo();
        }
    }
    
    private static long getSeconds(VideoupItemsrnt itmr){
        Date dnow;
        long milisecs;
        if(itmr.getFTime()==null){
            dnow=Calendar.getInstance().getTime();
        }else{
            dnow=itmr.getFTime();
        }
        milisecs=dnow.getTime()-itmr.getITime().getTime();
        return (long)milisecs/1000;
    }
    
    // ofrts utils
    /**
     * @param vrt
     * @return  -1 error, 0=no applies, 1=applied as needed
     */
    public static int evalAndApplyOfrts(VideoupRentas vrt, Videoup vup){
        GeneralDAO gDao=new GeneralDAO();
        RentMetaData rntMDt=new RentMetaData(vrt);
        VideoupCustomers cstmr=vrt.getIdcli();
        List<VideoupBaseEntity> ents;
        List<VideoupItemsrnt> items;
        int maxoffrts=Globals.getIntConfig("maxoffrts");
        boolean applied=false;
        boolean updCustmr=false;
        List<VideoupCtofrentas> ofrtas=gDao.getListEntities("from VideoupCtofrentas order by priority desc",0,(maxoffrts>0?maxoffrts:5));
        if(ofrtas==null){
            error=gDao.getError(); return -1;
        }else if(ofrtas.isEmpty()){
            return 0;
        }
        for(VideoupCtofrentas ofrt: ofrtas){
            applied=applied||applyOfrt(ofrt,rntMDt,cstmr,vup);
            updCustmr=(updCustmr||ofrt.getTpo()==5);
        }
        if(applied){
            items=vrt.getVideoupItemsrntList();
            ents=new ArrayList<VideoupBaseEntity>();
            for(VideoupItemsrnt itmr: items){
                ents.add(itmr);
            }
            if(updCustmr){ ents.add(cstmr); }
            if(gDao.saveEntities(ents)){
                 return 1;
            }else{
                error=gDao.getError(); return -1;
            }
        }else{
            return 0;
        }
    }
    
    public static int resetApplyOfrts(VideoupRentas vrt, Videoup vup){
        List<VideoupItemsrnt> items=vrt.getVideoupItemsrntList();
        if(items!=null && !items.isEmpty()){
            for(VideoupItemsrnt itmr: items){
                if(itmr.hasOfrts()){
                    itmr.setOfrtCfin(0f); itmr.setOfrtOff(0);
                    itmr.clearVideoupCtofrentasList();
                }
            }
        }
        return evalAndApplyOfrts(vrt,vup);
    }
    
    private static boolean applyOfrt(VideoupCtofrentas ofrt,RentMetaData rMDt,VideoupCustomers cstmr, Videoup vup){
        boolean applied=false;
        boolean[] days;
        String ask;
        List<VideoupItemsrnt> applying;
        if(ofrt.getTpo()==1){
            applying=rMDt.getItems(ofrt.getImovie(),ofrt.getIngame(),ofrt.getVideoupCtprrentasList(),
                    ofrt.getVideoupFormtsList(),ofrt.getVideoupCatgsList(),false,ofrt);
            if(applying.size()>=ofrt.getAuxN() && applying.size()<=ofrt.getAuxM()){
                applyOfrt(ofrt,applying);
                applied=true;
            }
        }else if(ofrt.getTpo()==2){
            applying=rMDt.getItems(ofrt.getAuxD1(),ofrt.getAuxD2(),ofrt.getImovie(),ofrt.getIngame(),
                    ofrt.getVideoupCtprrentasList(),ofrt.getVideoupFormtsList(),ofrt.getVideoupCatgsList(),ofrt);
            if(!applying.isEmpty()){
                applyOfrt(ofrt,applying);
                applied=true;
            }
        }else if(ofrt.getTpo()==3){
            applying=rMDt.getItems(ofrt.getImovie(),ofrt.getIngame(),ofrt.getVideoupCtprrentasList(),
                    ofrt.getVideoupFormtsList(),ofrt.getVideoupCatgsList(),true,ofrt);
            if(applying.size()==ofrt.getAuxM()){
                applyOfrt(ofrt,applying);
                applied=true;
            }
        }else if(ofrt.getTpo()==4){
            days=new boolean[7]; days[0]=ofrt.getAplDomingo();
            days[1]=ofrt.getAplLunes(); days[2]=ofrt.getAplMartes(); days[3]=ofrt.getAplMiercoles();
            days[4]=ofrt.getAplJueves(); days[5]=ofrt.getAplViernes(); days[6]=ofrt.getAplSabado();
            applying=rMDt.getItems(days,ofrt.getImovie(),ofrt.getIngame(),ofrt.getVideoupCtprrentasList(),
                    ofrt.getVideoupFormtsList(),ofrt.getVideoupCatgsList(),ofrt);
            if(!applying.isEmpty()){
                applyOfrt(ofrt,applying);
                applied=true;
            }
        }else if(ofrt.getTpo()==5){
            applying=rMDt.getItems(ofrt.getBypuntos(), ofrt.getImovie(),ofrt.getIngame(),ofrt.getVideoupCtprrentasList(),
                    ofrt.getVideoupFormtsList(),ofrt.getVideoupCatgsList(),ofrt);
            if(!applying.isEmpty()){
                ask="<html>En este alquiler puede aplicar la oferta: <u>"+ofrt.getNamer()+"</u> ";
                if(ofrt.getTpsv()==1){
                    ask+="<br />(Un "+ofrt.getPrDesc()+"% de descuento del total)";
                }else if(ofrt.getTpsv()==2){
                    ask+="<br />(Costo fijo de "+NumberFormat.getCurrencyInstance().format(ofrt.getCstSpc())+")";
                }
                ask+="<br />Por "+ofrt.getBypuntos()+" puntos de la cuenta del socio. ¿Desea aplicarla?</html>";
                if( vup.confirm("Confirme accion",ask) ){
                    cstmr.subtractVideoupPuntos(ofrt.getBypuntos());
                    applyOfrt(ofrt,applying);
                    applied=true;
                }
            }
        }
        return applied;
    }
    
    private static void applyOfrt(VideoupCtofrentas ofrt,List<VideoupItemsrnt> applying){
        float cstsp=(ofrt.getCstSpc()/applying.size());
        for(VideoupItemsrnt itmr: applying){
            itmr.addVideoupCtofrentas(ofrt);
            if(ofrt.getTpo()==3){
                itmr.setOfrtCfin( ((itmr.getCstUt()*ofrt.getAuxN())/ofrt.getAuxM()) );
            }else if(ofrt.getTpsv()==1){
                itmr.setOfrtOff(ofrt.getPrDesc());
            }else{
                itmr.setOfrtCfin(cstsp);
            }
        }
    }
    
    public static List<String> getOfrtsNames(VideoupRentas vrt){
        List<String> lsOfrts=new ArrayList<String>();
        List<VideoupItemsrnt> items=vrt.getVideoupItemsrntList();
        String aName;
        if(items!=null && !items.isEmpty()){
            for(VideoupItemsrnt itmr: items){
                if(itmr.hasOfrts()){
                    aName=itmr.getVideoupCtofrentas().getNamer();
                    if(!lsOfrts.contains(aName)){
                        lsOfrts.add(aName);
                    }
                }
            }
        }
        return lsOfrts;
    }
    
    /** Indicates if a given date is between a period of two dates
     * @param base The base date to evaluate
     * @param first the first date of period
     * @param last the last date of period
     * @return 
     */
    public static boolean isDateBetween(Date base, Date first, Date last){
        return (base.equals(first)||base.after(first)) && (base.equals(last)||base.before(last));
    }
    
    // movies games
    
    public static byte[] getImageBlob(Component parent,String message){
        JFileChooser selFile=new JFileChooser();
        String[] suffices=ImageIO.getReaderFileSuffixes();
        File imgfile;
        Image img;
        boolean isImage;
        byte[] blob;
        FileInputStream fileInpStr;
        int slFile;
        for(int i=0;i<suffices.length;i++){
            selFile.addChoosableFileFilter(new FileNameExtensionFilter(suffices[i]+" archivos",suffices[i]));
        }
        slFile=selFile.showDialog(parent,message);
        if(slFile==JFileChooser.APPROVE_OPTION){
            imgfile=selFile.getSelectedFile();
            isImage=true;
            try{
                img=ImageIO.read(imgfile);
                if(img==null){ isImage=false; }
            }catch(IOException ex){ isImage=false; }
            if(!isImage){
                error="El archivo debe ser una imagen";
                return null;
            }
            blob=new byte[(int)imgfile.length()];
            try {
                fileInpStr=new FileInputStream(imgfile);
                fileInpStr.read(blob);
            }catch (FileNotFoundException ex){
                error="Error: "+ex.getMessage();
                return null;
            }catch (IOException ex){
                error="Error: "+ex.getMessage();
                return null;
            }
            return blob;
        }
        blob=new byte[0];
        return blob;
    }    
    
    public static boolean productApplyEstreno(VideoupBaseEntity ent){
        Date today=Calendar.getInstance().getTime();
        if(ent instanceof VideoupGames && ((VideoupGames)ent).getEstrenoUntil()!=null){
            return !today.after( ((VideoupGames)ent).getEstrenoUntil() );
        }else if(ent instanceof VideoupMovies && ((VideoupMovies)ent).getEstrenoUntil()!=null){
            return !today.after( ((VideoupMovies)ent).getEstrenoUntil() );
        }
        return false;
    }
    
    public static boolean switchCatgprNormal(VideoupBaseEntity ent){
        GeneralDAO gDao=new GeneralDAO();
        boolean saved;
        VideoupCtprrentas ctNormalg;
        if(!Globals.getBooleanConfig("changeFinEstreno")){
            return true;
        }
        ctNormalg=(VideoupCtprrentas)gDao.getEntity("from VideoupCtprrentas where idcpr=1");
        if(ctNormalg==null){
            error=gDao.getError();
            return false;
        }
        if(ent instanceof VideoupGames){
            ((VideoupGames)ent).setIdcpr(ctNormalg);
        }else if(ent instanceof VideoupMovies){
            ((VideoupMovies)ent).setIdcpr(ctNormalg);
        }else{
            error="Instancia invalida";
            return false;
        }
        saved=gDao.saveEntity(ent,false);
        if(!saved){
            error=gDao.getError();
        }
        return saved;
    }
}
