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

import com.mbwkarl.analysistoolgui.utils.RangeSlider;
import com.mbwkarl.analysistoolgui.utils.DataStreamListModel;
import com.mbwkarl.analysistool.model.DataStream;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author Karl Birch
 */
public class Graph2D extends javax.swing.JPanel implements ComponentListener, ListDataListener {
    
    private Color[] colors = {
        Color.BLUE,
        Color.RED,
        Color.MAGENTA,
        Color.GREEN
    };
    
    private final int GRAPH_MARGIN = 10;
    private final int AXIS_MARGIN = 70;
    private final int CHARACTER_WIDTH = 4;
    private boolean visible = false;
    private final DataStreamListModel selectedDataStreams;
    private int focusedDataStreamIndex = 0;
    private final RangeSlider rangeSlider;
    
    private boolean timeCompatibility = true;
    
    // private BigDecimal lowestTime;
    // private BigDecimal highestTime;
    // private BigDecimal diffTime;
    
    public Graph2D(DataStreamListModel dslm, RangeSlider rslider) {
        rangeSlider = rslider;
        selectedDataStreams = dslm;
        selectedDataStreams.addListDataListener(this);
        repaint();
    }
    
    public void init(GraphingTab tab) {
        tab.addComponentListener(this);
    }
    
    // <editor-fold defaultstate="collapsed" desc="Paint/Draw methods">
    @Override
    public void paint(Graphics g) {
        if (!visible) {
            return;
        }
        
        timeCompatibility = selectedDataStreams.areTimeCompatible();
        
        
        //BigDecimal lowerMostKey, upperMostKey;

//        lowerMostKey = selectedDataStreams.getLowestKey();
//        upperMostKey = selectedDataStreams.getHighestKey();
//
//        BigDecimal diffKey = upperMostKey.subtract(lowerMostKey);
//        BigDecimal interval = diffKey.divide(new BigDecimal(rangeSlider.getMaximum()), 2, RoundingMode.HALF_UP);
//        lowestTime = lowerMostKey.add(interval.multiply(new BigDecimal(rangeSlider.getValue())));
//        highestTime = lowerMostKey.add(interval.multiply(new BigDecimal(rangeSlider.getUpperValue())));
//        diffTime = highestTime.subtract(lowestTime);
        
        clearCanvas(g);
        drawAxes(g);
        drawAxesPoints(g);
        drawPlot(g);
    }
    
    @Override
    public void update(Graphics g) {
        paint(g);
    }
    
    private void clearCanvas(Graphics g) {
        g.clearRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.WHITE);
        g.fillRect(getGraphLeft(), getGraphTop(), getGraphWidth(), getGraphHeight());
    }
    
    private void drawAxes(Graphics g) {
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(getAxisLeft(), getAxisTop(), getAxisWidth(), getAxisHeight());
        g.setColor(Color.BLACK);
        g.drawLine(getAxisLeft(), getAxisBottom(), getAxisLeft(), getAxisTop());
        g.drawLine(getAxisLeft(), getAxisBottom(), getAxisRight(), getAxisBottom());
    }
    
    private void drawAxesPoints(Graphics g) {
        // X Axis
        
        if (selectedDataStreams.isEmpty()) {
            g.drawString("Drop a Datastream in to begin", getAxisLeft(), getAxisBottom());
            return;
        }
        
        drawXAxis(g);
        drawYAxis(g);
    }
    
    private void drawXAxis(Graphics g) {
        int offset = 20;
        
        String lowStr;
        String hiStr;
        
        if (timeCompatibility) {
            BigDecimal lowerMostKey, upperMostKey, lowestTime, highestTime, diffTime;
            
            lowerMostKey = selectedDataStreams.getLowestKey();
            upperMostKey = selectedDataStreams.getHighestKey();

            BigDecimal diffKey = upperMostKey.subtract(lowerMostKey);
            BigDecimal interval = diffKey.divide(new BigDecimal(rangeSlider.getMaximum()), 2, RoundingMode.HALF_UP);
            lowestTime = lowerMostKey.add(interval.multiply(new BigDecimal(rangeSlider.getValue())));
            highestTime = lowerMostKey.add(interval.multiply(new BigDecimal(rangeSlider.getUpperValue())));
            diffTime = highestTime.subtract(lowestTime);
        
            lowStr = lowestTime.toPlainString();
            hiStr = highestTime.toPlainString();
        } else {
            lowStr = "MIN";
            hiStr = "MAX";
        }
        
        g.drawString(lowStr, getAxisLeft() - (lowStr.length() * CHARACTER_WIDTH), getAxisBottom() + offset);
        g.drawString(hiStr, getAxisRight() - (hiStr.length() * CHARACTER_WIDTH), getAxisBottom() + offset);
        
        int limit = getNumXPoints();
        int interval = getXAxisInterval(limit);
            
        for (int i = 1; i < limit; ++i) {
            int xCoord = getAxisLeft() + (i * interval);
            g.setColor(Color.WHITE);
            g.drawLine(xCoord, getAxisBottom() + 5, xCoord, getAxisTop());
            g.setColor(Color.BLACK);
            g.drawLine(xCoord, getAxisBottom() - 5, xCoord, getAxisBottom() + 5);
        }
    }
    
    private void drawYAxis(Graphics g) {
        int offset = 30;
        int yPoints = getNumYPoints();
        int interval = getYAxisInterval();
        
        DataStream ds = selectedDataStreams.getElementAt(focusedDataStreamIndex);
        
        BigDecimal lowestVal = ds.getLowestValue();
        BigDecimal highestVal = ds.getHighestValue();
        BigDecimal diffVal = highestVal.subtract(lowestVal);
        BigDecimal intervalVal = diffVal.divide(BigDecimal.valueOf(yPoints - 1), 2, RoundingMode.HALF_UP);
        BigDecimal[] values = new BigDecimal[yPoints];

        values[0] = lowestVal;
        int lastYIndex = yPoints - 1;
        values[lastYIndex] = highestVal;
        for (int i = 1; i < lastYIndex; ++i) {
            values[i] = values[i - 1].add(intervalVal);
        }

        g.setColor(colors[focusedDataStreamIndex]);
        for (int i = 0; i < yPoints; ++i) {
            String valStr = values[i].toPlainString();
            g.drawString(valStr,
                    getAxisLeft() - offset - (valStr.length() * CHARACTER_WIDTH),
                    getAxisBottom() - (interval * i) + 5);
        }
        
        for (int i = 1; i < yPoints; ++i) {
            int yCoord = getAxisBottom() - (interval * i);
            g.setColor(Color.WHITE);
            g.drawLine(getAxisLeft() + 5, yCoord, getAxisRight(), yCoord);
            g.setColor(Color.BLACK);
            g.drawLine(getAxisLeft() - 5, yCoord, getAxisLeft() + 5, yCoord);
        }
        
    }
    
    private BigDecimal[] getTimePoints(int numberOfPoints) {
        BigDecimal lowerMostKey, upperMostKey, lowestTime, highestTime, diffTime;

        lowerMostKey = selectedDataStreams.getLowestKey();
        upperMostKey = selectedDataStreams.getHighestKey();

        BigDecimal diffKey = upperMostKey.subtract(lowerMostKey);
        BigDecimal interval = diffKey.divide(new BigDecimal(rangeSlider.getMaximum()), 2, RoundingMode.HALF_UP);
        lowestTime = lowerMostKey.add(interval.multiply(new BigDecimal(rangeSlider.getValue())));
        highestTime = lowerMostKey.add(interval.multiply(new BigDecimal(rangeSlider.getUpperValue())));
        diffTime = highestTime.subtract(lowestTime);
        
        BigDecimal intervalTime = diffTime.divide(new BigDecimal(numberOfPoints), 2, RoundingMode.HALF_UP);
        BigDecimal[] timePoints = new BigDecimal[numberOfPoints];
        
        timePoints[0] = lowestTime;
        int lastXIndex = numberOfPoints - 1;
        timePoints[lastXIndex] = highestTime;
        for (int i = 1; i < lastXIndex; ++i) {
            timePoints[i] = timePoints[i - 1].add(intervalTime);
        }
        
        return timePoints;
    }
    
    private BigDecimal[] getPositionalTimePoints(int numberOfPoints, DataStream stream) {
        BigDecimal lowerMostKey, upperMostKey, lowestTime, highestTime, diffTime, interval;

        lowerMostKey = stream.getLowestKey();
        upperMostKey = stream.getHighestKey();

        BigDecimal diffKey = upperMostKey.subtract(lowerMostKey);
        BigDecimal diffInterval = diffKey.divide(new BigDecimal(rangeSlider.getMaximum()), 2, RoundingMode.HALF_UP);
        lowestTime = lowerMostKey.add(diffInterval.multiply(new BigDecimal(rangeSlider.getValue())));
        highestTime = lowerMostKey.add(diffInterval.multiply(new BigDecimal(rangeSlider.getUpperValue())));
        diffTime = highestTime.subtract(lowestTime);
        interval = diffTime.divide(BigDecimal.valueOf(numberOfPoints), 2, RoundingMode.HALF_UP);
        
        BigDecimal[] timePoints = new BigDecimal[numberOfPoints];
        timePoints[0] = lowestTime;
        int lastXIndex = numberOfPoints - 1;
        timePoints[lastXIndex] = highestTime;
        for (int i = 1; i < lastXIndex; ++i) {
            timePoints[i] = timePoints[i - 1].add(interval);
        }
        
        return timePoints;
    }
    
    private void drawPlot(Graphics g) {
//        if (highestTime.compareTo(lowestTime) == 0) {
//            return;
//        }
        
        Graphics2D g2d = (Graphics2D) g.create();
        
        int colorcycle = 0;
        int colorcount = colors.length;
        int numPoints = getNumPlotPoints();
        BigDecimal[] plotTimes = getTimePoints(numPoints);;
        
        for (DataStream ds : selectedDataStreams) {
            if (!timeCompatibility) {
                plotTimes = getPositionalTimePoints(numPoints, ds);
            }
            
            int[] points = new int[numPoints];
            BigDecimal minVal = ds.getLowestValue();
            BigDecimal maxVal = ds.getHighestValue();
            for (int i = 0; i < numPoints; ++i) {
                BigDecimal pointValue = ds.getValueAtTime(plotTimes[i]);
                points[i] = yDataToYCoord(pointValue, minVal, maxVal);
            }

            g2d.setColor(colors[colorcycle++]);
            
            int[] xpoints = getXPointsForPlotting();
            boolean lastDrawable = false;
            for (int i = 0; i < numPoints; ++i) {
                if (points[i] >= getAxisBottom()) {
                    lastDrawable = false;
                } else {
                    if (lastDrawable) {
                        g2d.drawLine(xpoints[i - 1], points[i - 1], xpoints[i], points[i]);
                    } else {
                        lastDrawable = true;
                    }
                }
            }
            
            if (colorcycle >= colorcount) {
                colorcycle = 0;
            }
        }
        
        g2d.dispose();
    }
    // </editor-fold>
    
    // <editor-fold desc="Graph Scaling">
    
    private int[] getXPointsForPlotting() {
        int[] xPoints = new int[getNumPlotPoints()];
        
        for (int i = 0; i < xPoints.length; ++i) {
            xPoints[i] = getXPosFromTimePoint(i, xPoints.length);
        }
        
        return xPoints;
    }
    
    private int getXPosFromTimePoint(int i, int pointCount) {
        return getAxisLeft() + (getXAxisInterval(pointCount) * i);
    }
    
    private int yDataToYCoord(BigDecimal yData, BigDecimal min, BigDecimal max) {
        BigDecimal diffMaxMin = max.subtract(min);
        if (diffMaxMin.compareTo(BigDecimal.ZERO) < 0) {
            diffMaxMin = diffMaxMin.negate();
        }
        BigDecimal diffDataMin = yData.subtract(min);
        if (diffDataMin.compareTo(BigDecimal.ZERO) < 0) {
            diffDataMin = diffDataMin.negate();
        }
        BigDecimal ratio;
        
        if (diffMaxMin.compareTo(BigDecimal.ZERO) == 0) {
            ratio = BigDecimal.ZERO;
        } else {
            ratio = diffDataMin.divide(diffMaxMin, 2, RoundingMode.HALF_UP);
        }
        
        BigDecimal yPos = ratio.multiply(new BigDecimal(getAxisHeight()));
        int yCoord = getAxisBottom() - yPos.intValue();
        return yCoord;
    }
    
    private int getNumPlotPoints() {
        return getAxisWidth(); // 2;
    }
    
    private int getNumXPoints() {
        return 11;
    }
    
    private int getNumYPoints() {
        return 11;
    }
    
    private int calculateInterval(int range, int numPoints) {
        return (range / (numPoints - 1));
    }
    
    private int getXAxisInterval(int pointCount) {
        return calculateInterval(getAxisWidth(), pointCount);
    }
    
    private int getYAxisInterval() {
        return calculateInterval(getAxisHeight(), getNumYPoints());
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Axis Bounds">
    private int getAxisRight() {
        int initRight = getGraphRight() - AXIS_MARGIN;
        int initWidth = initRight - getAxisLeft();
        int newWidth = (initWidth / (getNumXPoints() - 1)) * (getNumXPoints() - 1);
        int newRight = getAxisLeft() + newWidth;
        return newRight;
        // return getGraphRight() - AXIS_MARGIN;
    }
    
    private int getAxisLeft() {
        return getGraphLeft() + AXIS_MARGIN;
    }
    
    private int getAxisBottom() {
        return getGraphBottom() - AXIS_MARGIN;
    }
    
    private int getAxisTop() {
        int initTop = getGraphTop() + AXIS_MARGIN;
        int initHeight = getAxisBottom() - initTop;
        int newHeight = (initHeight / (getNumYPoints() - 1)) * (getNumYPoints() - 1);
        int newTop = getAxisBottom() - newHeight;
        return newTop;
        //return getGraphTop() + AXIS_MARGIN;
    }
    
    private int getAxisWidth() {
        //return getGraphWidth() - (2 * AXIS_MARGIN);
        return getAxisRight() - getAxisLeft();
    }
    
    private int getAxisHeight() {
        //return getGraphHeight() - (2 * AXIS_MARGIN);
        return getAxisBottom() - getAxisTop();
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Graph Bounds">
    private int getGraphRight() {
        return getWidth() - GRAPH_MARGIN;
    }
    
    private int getGraphLeft() {
        return GRAPH_MARGIN;
    }
    
    private int getGraphBottom() {
        return getHeight() - GRAPH_MARGIN;
    }
    
    private int getGraphTop() {
        return GRAPH_MARGIN;
    }
    
    private int getGraphWidth() {
        return getWidth() - (2 * GRAPH_MARGIN);
    }
    
    private int getGraphHeight() {
        return getHeight() - (2 * GRAPH_MARGIN);
    }
    // </editor-fold>
    
    // <editor-fold desc="Component Listener Functions">
    @Override
    public void componentResized(ComponentEvent e) {
        repaint();
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        repaint();
    }

    @Override
    public void componentShown(ComponentEvent e) {
        visible = true;
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        visible = false;
    } // </editor-fold>

    @Override
    public void intervalAdded(ListDataEvent e) {
        repaint();
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
        repaint();
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        repaint();
    }
    
    protected void focusDataStream(int index) {
        focusedDataStreamIndex = index;
    }
    
}
