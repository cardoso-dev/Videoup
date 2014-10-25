/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.views.modreps;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.swing.RepaintManager;

/**
 *
 * @author Pedro
 */
public class PrintTSheet implements Printable{
    
    private Component[] comp2print;
    private int wd_mm;
    private int hg_mm;
    private String err;
    
    public PrintTSheet(){ }
    
    public void printComponent(Component[] c, int w_mm, int h_mm) {
        comp2print=c; wd_mm=w_mm; hg_mm=h_mm;
        print();
    }
    
    private double mm2measure(int src){
        double calcMsr=src*0.0393700787;
        int INCH=72;
        return calcMsr*INCH;
    }
  
  public boolean print(){
    PrinterJob printJob = PrinterJob.getPrinterJob();
    PageFormat pagf=printJob.defaultPage();
    Paper ppr=pagf.getPaper();
    Book pBook = new Book();
    if(printJob.printDialog()){
        HashPrintRequestAttributeSet attrm = new HashPrintRequestAttributeSet();             
        attrm.add(new MediaPrintableArea(0f,0f,wd_mm,hg_mm,MediaPrintableArea.MM)); 
        ppr.setSize(mm2measure(wd_mm),mm2measure(hg_mm));
        ppr.setImageableArea(0,0,ppr.getWidth(),ppr.getHeight());
        pagf.setPaper(ppr);
        pagf=printJob.pageDialog(pagf);
        pBook.append(this, pagf,comp2print.length);
        printJob.setPageable(pBook);
          try {
            printJob.print(attrm);
          } catch(PrinterException pe) {
              err=pe.getMessage(); return false; 
          }
    }else{ err="Impresora invalida"; return false; }
    return true;
  }

    @Override
  public int print(Graphics g, PageFormat pageFormat, int pageIdx){
    if(pageIdx>comp2print.length){
      return (NO_SUCH_PAGE);
    }else{
      Graphics2D g2d = (Graphics2D)g;
      g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
      disableDoubleBuffering(comp2print[pageIdx]);
      comp2print[pageIdx].paint(g2d);
      enableDoubleBuffering(comp2print[pageIdx]);
      return (PAGE_EXISTS);
    }
  }

  /** The speed and quality of printing suffers dramatically if
   *  any of the containers have double buffering turned on.
   *  So this turns if off globally.
   *  @see enableDoubleBuffering
   */
  public static void disableDoubleBuffering(Component c) {
    RepaintManager currentManager = RepaintManager.currentManager(c);
    currentManager.setDoubleBufferingEnabled(false);
  }

  /** Re-enables double buffering globally. */ 
  public static void enableDoubleBuffering(Component c) {
    RepaintManager currentManager = RepaintManager.currentManager(c);
    currentManager.setDoubleBufferingEnabled(true);
  }
    
  public String getError(){ return err; }
    
}
