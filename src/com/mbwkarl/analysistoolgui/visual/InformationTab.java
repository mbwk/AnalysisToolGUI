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
import com.mbwkarl.analysistool.model.DataStream;
import com.mbwkarl.analysistool.model.EntryAttribute;
import com.mbwkarl.analysistool.model.ModelController;
import com.mbwkarl.analysistool.utils.StreamDataType;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Vector; // for JList and JComboBox
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Karl Birch
 */
public class InformationTab extends AnalysisToolTab {
   
    private final int ROWS = 4, COLUMNS = 4, HORI_GAP = 5, VERT_GAP = 2;
    
    private final String title = "Information";
    
    private final BorderLayout overalllayout = new BorderLayout();
    
    /* INFORMATION */
    java.awt.Font headerFont = new java.awt.Font("Terminus", java.awt.Font.BOLD, 18);
    
    // stored info
    private final String DEFAULT_VALUE = "0";
    private final String EMPTY_STRING = "<EMPTY>";
    private final String UNSELECTED_VAL = "[NONE SELECTED]";
    private Vector<String> attrXNames = new Vector<>();
    private Vector<String> attrXTypes = new Vector<>();
    private Vector<String> attrYNames = new Vector<>();
    private Vector<String> attrYTypes = new Vector<>();
    
    // info links
    private ArrayList<DataStream> streams = new ArrayList<>();
    private final ModelController modelController;
    
    // file info panel
    private final BorderLayout fileInfoPanelLayout = new BorderLayout();
    private final GridLayout fileInfoFieldsLayout = new GridLayout(ROWS, COLUMNS, HORI_GAP, VERT_GAP);
    private final JTextField entryCountField = new JTextField(DEFAULT_VALUE);
    private final JTextField datastreamCountField = new JTextField(DEFAULT_VALUE);
    private final JTextField formatCountField = new JTextField(DEFAULT_VALUE);
    private final JTextField corruptionCountField = new JTextField(DEFAULT_VALUE);
    
    // formats list/browser
    private final BorderLayout formatsLayout = new BorderLayout();
    private final BorderLayout formatsListLayout = new BorderLayout();
    private final JList<String> formatsList = new JList<>();
    private final JPanel formatInfoPanel = new JPanel();
    private final JTextField nameField = new JTextField(EMPTY_STRING);
    private final JTextField attrXType = new JTextField(EMPTY_STRING);
    private final JTextField attrYType = new JTextField(EMPTY_STRING);
    private final JTextField attrXuserDefinedName = new JTextField();
    private final JTextField attrYuserDefinedName = new JTextField();
    private final JComboBox<String> attrXSelection = new JComboBox<>(attrXNames);
    private final JComboBox<String> attrYSelection = new JComboBox<>(attrYNames);
    
    
    public InformationTab(ModelController controller) {
        modelController = controller;
        formatsList.setFixedCellWidth(80);
        
        String[] initialListData = new String[1];
        initialListData[0] = EMPTY_STRING;
        
        formatsList.setListData(initialListData);
        
        overalllayout.setHgap(5);
        overalllayout.setVgap(10);
        this.setLayout(overalllayout);
        
        attrXType.setEditable(false);
        attrYType.setEditable(false);
        
        initComponents();
    }
    
    // <editor-fold defaultstate="collapsed" desc="Initialization and Layout">
    private void initComponents() {
        JPanel fileInformationPanel = initFileInfoPanel();
        add(fileInformationPanel, BorderLayout.NORTH);
        
        JPanel formatsPanel = initFormatsPanel();
        add(formatsPanel, BorderLayout.CENTER);
    }
    
    private JPanel initFileInfoPanel() {
        JPanel fip = new JPanel();
        fip.setLayout(fileInfoPanelLayout);
        
        JPanel fifp = new JPanel();
        fifp.setLayout(fileInfoFieldsLayout);
        
        JLabel fiplabel = new JLabel("Logfile Information:");
        fiplabel.setFont(headerFont);
        fip.add(fiplabel, BorderLayout.NORTH);
        fip.add(fifp, BorderLayout.CENTER);
        
        JLabel entryCountLabel = new JLabel("Entries: ");
        entryCountLabel.setHorizontalAlignment(JLabel.RIGHT);
        fifp.add(entryCountLabel);
        entryCountField.setEditable(false);
        fifp.add(entryCountField);
        
        JLabel datastreamCountLabel = new JLabel("Datastreams: ");
        datastreamCountLabel.setHorizontalAlignment(JLabel.RIGHT);
        fifp.add(datastreamCountLabel);
        datastreamCountField.setEditable(false);
        fifp.add(datastreamCountField);
        
        JLabel corruptionsLabel = new JLabel("Corruptions: ");
        corruptionsLabel.setHorizontalAlignment(JLabel.RIGHT);
        fifp.add(corruptionsLabel);
        corruptionCountField.setEditable(false);
        fifp.add(corruptionCountField);
        
        JLabel formatsLabel = new JLabel("Formats: ");
        formatsLabel.setHorizontalAlignment(JLabel.RIGHT);
        fifp.add(formatsLabel);
        formatCountField.setEditable(false);
        fifp.add(formatCountField);
        
        return fip;
    }
    
    private JPanel initFormatsPanel() {
        JPanel formatsPanel = new JPanel();
        formatsPanel.setLayout(formatsLayout);
        JLabel formatsLabel = new JLabel("Formats: ");
        formatsLabel.setFont(headerFont);
        formatsPanel.add(formatsLabel, BorderLayout.NORTH);
        
        JPanel formatsListPanel = new JPanel();
        formatsPanel.add(formatsListPanel, BorderLayout.CENTER);
        formatsListPanel.setLayout(formatsListLayout);
        
        formatsList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (formatsList.getSelectedValue() == null) {
                        return;
                    }
                    String format = formatsList.getSelectedValue().toString();
                    modelController.requestFormatAttributes(format);
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(
                formatsList,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        formatsListPanel.add(scrollPane, BorderLayout.WEST);
        
        formatsListPanel.add(formatInfoPanel, BorderLayout.CENTER);
        ArrayList<EntryAttribute> initial = new ArrayList<>();
        initial.add(new EntryAttribute(EMPTY_STRING, StreamDataType.STRING));
        formatsList.setSelectedIndex(0);
        
        initFormatInfoPanel();
        //updateFormatInfoPanel(EMPTY_STRING, initial);
        
        return formatsPanel;
    }
    
    private void initFormatInfoPanel() {
        attrXSelection.setPrototypeDisplayValue(UNSELECTED_VAL);
        //attrXSelection.addItemListener((ItemEvent e) -> {
        attrXSelection.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    updateAttrFields('x');
                    updateStreamName();
                }
            }
        });
        //});
        
        attrYSelection.setPrototypeDisplayValue(UNSELECTED_VAL);
        //attrYSelection.addItemListener((ItemEvent e) -> {
        attrYSelection.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    updateAttrFields('y');
                    updateStreamName();
                }
            }
        });
        //});
        
        GridBagLayout gridBagLayout = new GridBagLayout();
        formatInfoPanel.setLayout(gridBagLayout);
        GridBagConstraints c = new GridBagConstraints();
        c.ipadx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.LINE_START;
        
        c.ipady = 0;
        c.weightx = 0;
        c.weighty = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.insets = new Insets(1, 1, 1, 1);
        
        /* labels */
        String typeLabel = "Attribute Type: ";
        String nameLabel = "User Defined Name: ";
        
        c.gridx = 1;
        c.gridy = 0;
        JLabel streamNameLabel = new JLabel("Datastream Name: ");
        streamNameLabel.setHorizontalAlignment(JLabel.RIGHT);
        formatInfoPanel.add(streamNameLabel, c);
        
        c.gridy = 1;
        c.gridwidth = 2;
        formatInfoPanel.add(new JSeparator(SwingConstants.HORIZONTAL), c);
        c.gridwidth = 1;
        
        // x selection
        c.gridy = 2;
        JLabel xAttr = new JLabel("X Axis Attribute: ");
        xAttr.setHorizontalAlignment(JLabel.RIGHT);
        formatInfoPanel.add(xAttr, c);
        
        // x type
        c.gridy = 3;
        JLabel xTypeLabel = new JLabel(typeLabel);
        xTypeLabel.setHorizontalAlignment(JLabel.RIGHT);
        formatInfoPanel.add(xTypeLabel, c);
        
        // x name
        c.gridy = 4;
        JLabel xNameLabel = new JLabel(nameLabel);
        xNameLabel.setHorizontalAlignment(JLabel.RIGHT);
        formatInfoPanel.add(xNameLabel, c);
        
        // seperator
        c.gridy = 5;
        c.gridwidth = 2;
        formatInfoPanel.add(new JSeparator(SwingConstants.HORIZONTAL), c);
        c.gridwidth = 1;
        
        // y selection
        c.gridy = 6;
        JLabel yAttrLabel = new JLabel("Y Axis Attribute: ");
        yAttrLabel.setHorizontalAlignment(JLabel.RIGHT);
        formatInfoPanel.add(yAttrLabel, c);
        
        // y type
        c.gridy = 7;
        JLabel yTypeLabel = new JLabel(typeLabel);
        yTypeLabel.setHorizontalAlignment(JLabel.RIGHT);
        formatInfoPanel.add(yTypeLabel, c);
        
        // y name
        c.gridy = 8;
        JLabel yNameLabel = new JLabel(nameLabel);
        yNameLabel.setHorizontalAlignment(JLabel.RIGHT);
        formatInfoPanel.add(yNameLabel, c);
        
        /* data */
        c.gridx = 2;
        
        c.gridy = 0;
        formatInfoPanel.add(nameField, c);
        
        c.gridy = 2;
        formatInfoPanel.add(attrXSelection, c);
        
        c.gridy = 3;
        formatInfoPanel.add(attrXType, c);
        
        c.gridy = 4;
        formatInfoPanel.add(attrXuserDefinedName, c);
        
        c.gridy = 6;
        formatInfoPanel.add(attrYSelection, c);
        
        c.gridy = 7;
        formatInfoPanel.add(attrYType, c);
        
        c.gridy = 8;
        formatInfoPanel.add(attrYuserDefinedName, c);
        
        c.gridx = 1;
        c.gridwidth = 2;
        c.gridy = 9;
        formatInfoPanel.add(new JSeparator(SwingConstants.HORIZONTAL), c);
        
        c.gridy = 10;
        JButton extractStreamButton = new JButton("Extract Stream");
        formatInfoPanel.add(extractStreamButton, c);
        
        // extractStreamButton.addActionListener((ActionEvent e) -> {
        extractStreamButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String streamName, xAxisName, yAxisName, formatType, keyAttr, valAttr;
                streamName = nameField.getText();

                xAxisName = attrXuserDefinedName.getText();
                if (xAxisName.isEmpty()) {
                    xAxisName = attrYNames.get(attrXSelection.getSelectedIndex());
                }

                yAxisName = attrYuserDefinedName.getText();
                if (yAxisName.isEmpty()) {
                    yAxisName = attrYNames.get(attrYSelection.getSelectedIndex());
                }

                formatType = formatsList.getSelectedValue().toString();
                keyAttr = attrXNames.get(attrXSelection.getSelectedIndex());
                valAttr = attrYNames.get(attrYSelection.getSelectedIndex());
                modelController.requestDataStreamExtraction(streamName, xAxisName,
                                           yAxisName, formatType, keyAttr, valAttr);
            }
        });

        // });
    }
    // </editor-fold>
    
    private void updateAttrFields(char axis) {
        if (axis == 'x') {
            int xIndex = attrXSelection.getSelectedIndex();
            attrXType.setText(attrXTypes.get(xIndex));
            attrXuserDefinedName.setText(attrXNames.get(xIndex));
        } else if (axis == 'y') {
            int yIndex = attrYSelection.getSelectedIndex();
            attrYType.setText(attrYTypes.get(yIndex));
            attrYuserDefinedName.setText(attrYNames.get(yIndex));
        }
    }
    
    private void updateStreamName() {
        StringBuilder sb = new StringBuilder();
        sb.append(attrYSelection.getSelectedItem());
        sb.append(" over ");
        sb.append(attrXSelection.getSelectedItem());
        nameField.setText(sb.toString());
    }
    
    private void updateFormatInfoPanel(String formatName, ArrayList<EntryAttribute> xAttributes, ArrayList<EntryAttribute> yAttributes) {
//        if (formatName == null || xAttributes == null  || yAttributes == null) {
//            return;
//        }
        
        attrXNames.clear();
        attrXTypes.clear();
        attrYNames.clear();
        attrYTypes.clear();
        
        for (EntryAttribute ea : xAttributes) {
            attrXNames.add(ea.getName());
            attrXTypes.add(ea.getType().toString());
        }
        for (EntryAttribute ea : yAttributes) {
            attrYNames.add(ea.getName());
            attrYTypes.add(ea.getType().toString());
        }
        
        attrXSelection.setSelectedIndex(0);
        attrYSelection.setSelectedIndex(0);
    }
    
    public void tellEntryCount(int entryCount) {
        entryCountField.setText(String.valueOf(entryCount));
    }
    
    public void tellDatastream(ArrayList<DataStream> datastreams) {
        streams = datastreams;
        datastreamCountField.setText(String.valueOf(streams.size()));
    }
    
    public void updateDatastreamsInfo() {
        if (streams != null) {
            datastreamCountField.setText(String.valueOf(streams.size()));
        }
    }

    public void tellFormatCount(int fc) {
        formatCountField.setText(String.valueOf(fc));
    }
    
    public void tellFormatList(ArrayList<String> typesList) {
        int limit = typesList.size();
        formatCountField.setText(String.valueOf(limit));
        
        String[] newTypesList = new String[limit];
        for (int i = 0; i < limit; ++i) {
            newTypesList[i] = typesList.get(i);
        }
        formatsList.setListData(newTypesList);
        formatsList.setSelectedIndex(0);
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void tellCorruptions(int corruptionCount) {
        corruptionCountField.setText(String.valueOf(corruptionCount));
    }

    public void tellFormatMap(String formatID, ArrayList<EntryAttribute> xAttributes, ArrayList<EntryAttribute> yAttributes) {
        updateFormatInfoPanel(formatID, xAttributes, yAttributes);
    }
    
}
