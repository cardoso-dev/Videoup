/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videoup.views.utils;

import java.awt.Component;
import java.util.List;
import javax.swing.JPanel;
import layout.TableLayout;

/**
 *
 * @author Pedro
 */
public class Utils {
    
    public static void loadAsTableLayout(List<Component> comps, JPanel cont,String empty){
        double[] rows;
        double[][] scells;
        int gridy=1;
        rows=new double[ (comps.size()*2)+2 ];
        rows[0]=4;
        rows[1]=TableLayout.PREFERRED;
        for(int h=2;h<rows.length;h+=2){
            rows[h]=4;
            rows[h+1]=TableLayout.PREFERRED;
        }
        scells=new double[][]{ {11,TableLayout.FILL,9},rows };
        cont.removeAll();
        cont.setLayout(new TableLayout(scells));
        if(comps.isEmpty()){
            cont.add(new javax.swing.JLabel(empty,javax.swing.JLabel.CENTER),"1,1");
        }else{
            for(Component comp: comps){
                cont.add(comp,"1,"+gridy);
                gridy+=2;
            }
        }
    }
    
}
