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

import com.mbwkarl.analysistoolgui.utils.AnalysisToolTab;
import com.mbwkarl.analysistoolgui.utils.RangeSlider;
import com.mbwkarl.analysistoolgui.utils.DataStreamListModel;
import com.mbwkarl.analysistoolgui.utils.DataStreamTransferHandler;
import com.mbwkarl.analysistool.model.DataStream;
import java.awt.BorderLayout;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.DropMode;
import javax.swing.JList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


/**
 *
 * @author Karl Birch
 */
public class GraphingTab extends AnalysisToolTab {

    private final String title = "Graphing";
    private final RangeSlider rslider = new RangeSlider();
    
    private final DataStreamListModel selectedDataStreams = new DataStreamListModel();
    private final JList<DataStream> listOfSelectedStreams = new JList<DataStream>(selectedDataStreams);
    private final Graph2D grapher = new Graph2D(selectedDataStreams, rslider);
    
    public GraphingTab() {
        setLayout(new BorderLayout());
        add(initSelectedDataStreamList(), BorderLayout.LINE_START);
        
        add(grapher, BorderLayout.CENTER);
        
        add(rslider, BorderLayout.SOUTH);
        grapher.init(this);
        
        rslider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                grapher.repaint();
            }
        });
        
    }
    
    private JList initSelectedDataStreamList() {
        listOfSelectedStreams.setFixedCellWidth(120);
        
        listOfSelectedStreams.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                // do nothing
            }

            @Override
            public void keyPressed(KeyEvent e) {
                // do nothing
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 127) { // DEL
                    DataStream remove = listOfSelectedStreams.getSelectedValue();
                    listOfSelectedStreams.setSelectedIndex(0);
                    selectedDataStreams.removeElement(remove);
                }
            }
        });
        
        // listOfSelectedStreams.addListSelectionListener((ListSelectionEvent e) -> {
        listOfSelectedStreams.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    grapher.focusDataStream(listOfSelectedStreams.getSelectedIndex());
                    grapher.repaint();
                }
            }
        });
        // });
        
        listOfSelectedStreams.setTransferHandler(new DataStreamTransferHandler());
        listOfSelectedStreams.setDropMode(DropMode.ON_OR_INSERT);
        listOfSelectedStreams.setDropTarget(new DropTarget() {
            @Override
            public synchronized void drop(DropTargetDropEvent evt) {
                Transferable trans = evt.getTransferable();
                listOfSelectedStreams.getTransferHandler().importData(listOfSelectedStreams, trans);
                evt.acceptDrop(DnDConstants.ACTION_COPY);
                //listOfSelectedStreams
            }
        });
        
        return listOfSelectedStreams;
    }
    
    @Override
    public String getTitle() {
        return title;
    }
    
}
