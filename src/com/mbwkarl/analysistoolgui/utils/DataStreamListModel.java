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

import com.mbwkarl.analysistool.model.DataStream;
import com.mbwkarl.analysistool.utils.StreamDataType;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * An implementation of ListModel specialized for use with a collection
 * of DataStreams.
 * @author Karl Birch
 */
public class DataStreamListModel implements ListModel<DataStream>, Iterable<DataStream> {
    
    private Vector<DataStream> interiorCollection = new Vector<DataStream>();
    private Vector<ListDataListener> listeners = new Vector<>();
    private boolean nullStream = true;
    
    public DataStreamListModel() {
        addNullDataStream();
    }
    
    private void addNullDataStream() {
        DataStream nullDataStream = new DataStream("<EMPTY>", "<X>", "<Y>", StreamDataType.BIGDECIMAL);
        //nullDataStream.addDataPoint(BigDecimal.ONE, BigDecimal.ZERO);
        interiorCollection.add(nullDataStream);
        nullStream = true;
    }
    
    public void addElement(DataStream element) {
        if (nullStream) {
            interiorCollection.remove(0);
        }
        
        int newHead = interiorCollection.size();
        interiorCollection.add(element);
        nullStream = false;
        //listeners.stream().forEach((ldl) -> {
        for (ListDataListener ldl : listeners) {
            ldl.contentsChanged(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, newHead, newHead));
        }
        //});
    }
    
    public void removeElement(DataStream element) {
        int index = interiorCollection.indexOf(element);
        if (index < 0) {
            return;
        }
        interiorCollection.remove(index);
        //listeners.stream().forEach((ldl) -> {
        for (ListDataListener ldl : listeners) {
            ldl.contentsChanged(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index, index - 1));
        }
        //});
        
        if (interiorCollection.isEmpty()) {
            addNullDataStream();
        }
    }

    @Override
    public int getSize() {
        return interiorCollection.size();
    }

    @Override
    public DataStream getElementAt(int index) {
        return interiorCollection.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        listeners.add(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }

    @Override
    public Iterator<DataStream> iterator() {
        return new Iterator<DataStream>() {
            private int index = 0;
            
            @Override
            public boolean hasNext() {
                return index < interiorCollection.size();
            }

            @Override
            public DataStream next() {
                return interiorCollection.get(index++);
            }

            @Override
            public void remove() {
                interiorCollection.remove(index);
            }
        };
    }
    
    public BigDecimal getLowestKey() {
        return DataStream.getCommonLowestTime(interiorCollection.toArray(
                new DataStream[interiorCollection.size()]));
    }
    
    public BigDecimal getHighestKey() {
        return DataStream.getCommonHighestTime(interiorCollection.toArray(
                new DataStream[interiorCollection.size()]));
    }
    
    public boolean isEmpty() {
        return interiorCollection.isEmpty();
    }
    
    public boolean areTimeCompatible() {
        DataStream[] streams = new DataStream[getSize()];
        streams = interiorCollection.toArray(streams);
        
        return DataStream.areStreamsTimeCompatibile(streams);
    }    
}
