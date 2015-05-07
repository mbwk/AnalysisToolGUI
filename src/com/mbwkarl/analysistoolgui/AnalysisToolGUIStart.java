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
package com.mbwkarl.analysistoolgui;

import com.mbwkarl.analysistoolgui.utils.GUINotifier;
import com.mbwkarl.analysistoolgui.visual.AnalysisToolWindow;
import com.mbwkarl.analysistool.model.DataModel;
import com.mbwkarl.analysistool.model.ModelController;

/**
 *
 * @author Karl Birch
 */
public class AnalysisToolGUIStart {

    private AnalysisToolGUIStart() {
        DataModel model = new DataModel();
        ModelController controller = new ModelController(model);
        AnalysisToolWindow analysisToolGUI = com.mbwkarl.analysistoolgui.visual.AnalysisToolWindow.createMainWindow(controller);
        GUINotifier guinotifier = new GUINotifier(analysisToolGUI);
        model.addNotifier(guinotifier);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AnalysisToolGUIStart start = new AnalysisToolGUIStart();
    }
    
}
