/*
 * Copyright 2015 Karl Birch.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mbwkarl.analysistoolgui.visual;

import com.mbwkarl.analysistool.model.ModelController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.*;

/**
 *
 * @author Karl Birch
 */
public class AnalysisToolWindow extends JFrame {
    
    // default settings
    public static final int MAXWIDTH = 1700;
    public static final int MAXHEIGHT = 700;
    
    // control panel
    private final ControlPanel cpanel;
    
    // tabs
    private final InformationTab infoTab;
    private final GraphingTab graphTab;
    private final ModellingTab modelTab;
    
    
    /**
     * Open window in appropriate monitor
     * @return 
     */
    private java.awt.Window getFocusedHead() {
        java.awt.Window wind = null;
        
        return wind;
    }
    
    private AnalysisToolWindow(ModelController controller) {
        infoTab = new InformationTab(controller);
        graphTab = new GraphingTab();
        modelTab = new ModellingTab();
        cpanel = new ControlPanel(250, MAXHEIGHT, controller);
        
        initializeSettings();
        setupFrame();
        setVisible(true);
    }
    
    // <editor-fold desc="Initializaton">
    private void initializeSettings() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("AnalysisTool");
        
        //setPreferredSize(new Dimension(1100, 700));
        
        setLocationRelativeTo(getFocusedHead());
    }
    
    private void setupFrame() {
        // setup layout
        javax.swing.JPanel container = new JPanel();
        add(container);
        BorderLayout layout = new BorderLayout();
        container.setLayout(layout);
        
        container.add(cpanel, BorderLayout.WEST);
        container.add(initializeTabbedPane(), BorderLayout.CENTER);
        
        setMinimumSize(new Dimension(1000, 600));
        setMaximizedBounds(new java.awt.Rectangle(MAXWIDTH, MAXHEIGHT));
        pack();
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setBackground(Color.lightGray);
        label.setBorder(BorderFactory.createLineBorder(Color.black));
        label.setOpaque(true);
        label.setHorizontalTextPosition(JLabel.CENTER);
        label.setVerticalTextPosition(JLabel.CENTER);
        return label;
    }
    
    private JTabbedPane initializeTabbedPane() {
        JTabbedPane tabs = new JTabbedPane();
        
        tabs.add(infoTab.getTitle(), infoTab);
        tabs.add(graphTab.getTitle(), graphTab);
        tabs.add(modelTab.getTitle(), modelTab);
        
        return tabs;
    }
    // </editor-fold>
    
    /**
     * This static function is a wrapper around the Constructor of this class
     * that allows us to set the Swing look-and-feel.
     * @param controller
     * @return 
     */
    public static AnalysisToolWindow createMainWindow(ModelController controller) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AnalysisToolWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AnalysisToolWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AnalysisToolWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AnalysisToolWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        return new AnalysisToolWindow(controller);
    }
    
    /**
     * Accessor for the Information Tab
     * @return Reference to the InformationTab object belonging to this AnalysisToolWindow
     */
    public InformationTab getInformationTab() {
        return infoTab;
    }
    
    public GraphingTab getGraphingTab() {
        return graphTab;
    }
    
    public ModellingTab getModellingTab() {
        return modelTab;
    }
    
    public ControlPanel getControlPanel() {
        return cpanel;
    }
    
}
