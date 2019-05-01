package modnlp.gui;


import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import modnlp.tec.client.ConcordanceBrowser;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author shane
 */
public class GraphicalNamedSubcorpusLoader extends JFrame {
    
    private ConcordanceBrowser parent = null;
    
    public GraphicalNamedSubcorpusLoader(ConcordanceBrowser p) {
        super("Load Saved Sub-corpus");
        parent = p;
 
        
    }
    


    public void activate() {
        JPanel pa = new JPanel();
        String[] petStrings = { "Bird", "Cat", "Dog", "Rabbit", "Pig" };
        JButton load = new JButton("Load");
        JComboBox petList = new JComboBox(petStrings);
        petList.setSelectedIndex(0);   
        pa.add(petList, BorderLayout.PAGE_START);
        pa.add(load);
        pa.setBorder(BorderFactory.createEmptyBorder(50,50,50,50));
        this.add(pa, BorderLayout.CENTER);
        this.pack();
        this.setVisible(true);
        
    }

  
}


