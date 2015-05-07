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

import com.mbwkarl.analysistool.model.EntryAttribute;
import com.mbwkarl.analysistool.utils.UserInterfaceNotifier;
import com.mbwkarl.analysistoolgui.visual.AnalysisToolWindow;
import com.mbwkarl.analysistoolgui.visual.ControlPanel;
import com.mbwkarl.analysistoolgui.visual.GraphingTab;
import com.mbwkarl.analysistoolgui.visual.InformationTab;
import com.mbwkarl.analysistoolgui.visual.ModellingTab;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author Karl Birch
 */
public class GUINotifier implements UserInterfaceNotifier {
    
    private final AnalysisToolWindow analysisToolGUI;
    private final ControlPanel controlPanel;
    private final InformationTab infoTab;
    private final GraphingTab graphTab;
    private final ModellingTab modelTab;
    
    public GUINotifier(AnalysisToolWindow gui) {
        analysisToolGUI = gui;
        controlPanel = analysisToolGUI.getControlPanel();
        infoTab = analysisToolGUI.getInformationTab();
        graphTab = analysisToolGUI.getGraphingTab();
        modelTab = analysisToolGUI.getModellingTab();
    }

    @Override
    public void loadedFile(String filename) {
        controlPanel.setLoadable();
    }

    @Override
    public void failedLoadFile(String filename) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void savedFile(String filename) {
        JOptionPane.showMessageDialog(analysisToolGUI, "File \"" + filename + "\" converted and saved",
                "Success", JOptionPane.PLAIN_MESSAGE);
    }

    @Override
    public void failedSaveFile(String filename) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mustLoadFile() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void broadcastEntryCount(int entryCount) {
        infoTab.tellEntryCount(entryCount);
    }

    @Override
    public void broadcastFormats(ArrayList<String> typesList) {
        infoTab.tellFormatList(typesList);
    }

    @Override
    public void broadcastPropertyList(String[] proplist) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void broadcastDataStreams(ArrayList<com.mbwkarl.analysistool.model.DataStream> streams) {
        infoTab.tellDatastream(streams);
        controlPanel.tellDataStreams(streams);
    }

    @Override
    public void broadcastCorruptions(int corruptionCount) {
        infoTab.tellCorruptions(corruptionCount);
    }

    @Override
    public void broadcastFormatMap(String formatID, ArrayList<EntryAttribute> xAttributes, ArrayList<EntryAttribute> yAttributes) {
        infoTab.tellFormatMap(formatID, xAttributes, yAttributes);
    }
    
}
