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
package com.mbwkarl.analysistoolgui.utils;

import com.mbwkarl.analysistoolgui.utils.DataStreamHolderListener;
import com.mbwkarl.analysistool.model.DataStream;
import com.mbwkarl.analysistool.utils.StreamDataType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;

/**
 *
 * @author Karl Birch
 */
public class DataStreamHolder extends JButton {
    
    private DataStream heldDataStream = null;
    private final StreamDataType streamType;
    private ArrayList<DataStreamHolderListener> listeners = new ArrayList<>();
    
    public DataStreamHolder(StreamDataType type) {
        streamType = type;
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearDataStream();
            }
        });
        clearDataStream();
    }
    
    public void holdDataStream(DataStream stream) {
        if (streamType == stream.getType()) {
            heldDataStream = stream;
            setEnabled(true);
            setText("ACTIVE");
            //listeners.stream().forEach((dshl) -> {
            for (DataStreamHolderListener dshl : listeners) {
                dshl.streamAcquired(heldDataStream);
            }
            //});
        }
    }
    
    public void clearDataStream() {
        heldDataStream = null;
        setEnabled(false);
        setText("EMPTY");
        //listeners.stream().forEach((dshl) -> {
        for (DataStreamHolderListener dshl : listeners) {
            dshl.streamDiscarded();
        }
        //});
    }
    
    public void addDataStreamHolderListener(DataStreamHolderListener dshl) {
        listeners.add(dshl);
    }
    
}
