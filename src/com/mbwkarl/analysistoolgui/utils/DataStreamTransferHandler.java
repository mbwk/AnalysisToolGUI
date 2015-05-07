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
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

/**
 *
 * @author Karl Birch
 */
public class DataStreamTransferHandler extends TransferHandler {
    
    //protected abstract void cleanup(JComponent c, boolean remove);
    private DataFlavor myDataFlavor = new DataFlavor(DataStream.class, DataStream.class.getSimpleName());
    
    @Override
    protected Transferable createTransferable(JComponent c) {
        JList<DataStream> jl = (JList<DataStream>) c;
        return new TransferableDataStream(jl.getSelectedValue());
    }
    
    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }
    
    private void importDataStreamToList(JList<DataStream> c, DataStream ds) {
        JList<DataStream> list = c;
        DataStreamListModel dslm = (DataStreamListModel) list.getModel();
        dslm.addElement(ds);
    }
    
    private void importDataStreamToHolder(DataStreamHolder c, DataStream ds) {
        c.holdDataStream(ds);
    }
    
    @Override
    public boolean importData(JComponent c, Transferable t) {
        if (canImport(c, t.getTransferDataFlavors())) {
            try {
                DataStream ds = (DataStream)t.getTransferData(myDataFlavor);
                Class classType = c.getClass();
                if (classType == JList.class) {
                    importDataStreamToList((JList<DataStream>) c, ds);
                } else if (classType == DataStreamHolder.class) {
                    importDataStreamToHolder((DataStreamHolder) c, ds);
                }
                return true;
            } catch (UnsupportedFlavorException ufe) {
            } catch (IOException ioe) {
            }
        }

        return false;
    }
    
    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
        // cleanup(c, action == MOVE);
    }
    
    @Override
    public boolean canImport(JComponent c, DataFlavor[] flavors) {
        for (DataFlavor flavor : flavors) {
            if (myDataFlavor.equals(flavor)) {
                return true;
            }
        }
        return false;
    }
    
}
