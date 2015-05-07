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
import com.mbwkarl.analysistoolgui.utils.DataStreamHolder;
import com.mbwkarl.analysistoolgui.utils.DataStreamHolderListener;
import com.mbwkarl.analysistoolgui.utils.DataStreamTransferHandler;
import com.mbwkarl.analysistool.model.DataStream;
import com.mbwkarl.analysistool.utils.StreamDataType;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Karl Birch
 */
public class ModellingTab extends AnalysisToolTab {

    private final String title = "Model";
    private final ModelScene modelScene = new ModelScene();
    private final DataStreamHolder longitudeStream = new DataStreamHolder(StreamDataType.LATLON),
            latitudeStream = new DataStreamHolder(StreamDataType.LATLON),
            altitudeStream = new DataStreamHolder(StreamDataType.BIGDECIMAL);
    
    public ModellingTab() {
        setLayout(new BorderLayout());
        add(initTopBar(), BorderLayout.NORTH);
        add(modelScene, BorderLayout.CENTER);
//        add(new ModelGL(), BorderLayout.CENTER);
    }
    
    private JPanel initTopBar() {
        JPanel topbar = new JPanel();
        topbar.setLayout(new FlowLayout());
        
        topbar.add(new JLabel("Longitude"));
        topbar.add(longitudeStream);
        longitudeStream.addDataStreamHolderListener(new DataStreamHolderListener() {
            @Override
            public void streamAcquired(DataStream stream) {
                modelScene.setLongitudeStream(stream);
            }

            @Override
            public void streamDiscarded() {
                modelScene.setLongitudeStream(null);
            }
        });
        
        topbar.add(new JLabel("Latitude"));
        topbar.add(latitudeStream);
        latitudeStream.addDataStreamHolderListener(new DataStreamHolderListener() {
            @Override
            public void streamAcquired(DataStream stream) {
                modelScene.setLatitudeStream(stream);
            }

            @Override
            public void streamDiscarded() {
                modelScene.setLatitudeStream(null);
            }
        });
        
        topbar.add(new JLabel("Altitude"));
        topbar.add(altitudeStream);
        altitudeStream.addDataStreamHolderListener(new DataStreamHolderListener() {
            @Override
            public void streamAcquired(DataStream stream) {
                modelScene.setAltitudeStream(stream);
            }

            @Override
            public void streamDiscarded() {
                modelScene.setAltitudeStream(null);
            }
        });
        
        enableDragAndDrop(longitudeStream, latitudeStream, altitudeStream);
        
        return topbar;
    }
    
    private void enableDragAndDrop(DataStreamHolder ... holders) {
        for (DataStreamHolder h : holders) {
        h.setTransferHandler(new DataStreamTransferHandler());
            longitudeStream.setDropTarget(new DropTarget() {
                @Override
                public synchronized void drop(DropTargetDropEvent evt) {
                    Transferable trans = evt.getTransferable();
                    longitudeStream.getTransferHandler().importData(longitudeStream, trans);
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    //listOfSelectedStreams
                }
            });
        }
    }
    
    @Override
    public String getTitle() {
        return title;
    }
    
}
