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

import com.mbwkarl.analysistool.model.DataStream;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

/**
 *
 * @author Karl Birch
 */
class ModelScene extends JPanel {
    
    private static final float RADIUS = 3.5f;
    private static final BigDecimal coordMin = new BigDecimal(-RADIUS),
            coordMax = new BigDecimal(RADIUS),
            coordRange = coordMax.subtract(coordMin);
    private static final float SPHERE_SIZE = 0.05f;
    private static final float GROUND_SIZE = 8f;
    private static final float GROUND_WIDTH = 0.1f;
    private static final BigDecimal ALT_RANGE = BigDecimal.valueOf(500);
    private static final float GROUND_Z = -3 - (GROUND_WIDTH / 2);
    
    private Canvas3D c3d = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
    private SimpleUniverse universe = new SimpleUniverse(c3d);
    private BranchGroup mainGroup = new BranchGroup();
    
    private DataStream longitudeStream, latitudeStream, altitudeStream;
    private BigDecimal lowestLat, highestLat, lowestLon, highestLon;
    private BigDecimal latLonSquareRange = BigDecimal.ZERO;
    
    public ModelScene() {
        lowestLat = highestLat = lowestLon = highestLon = BigDecimal.ZERO;
        
        setLayout(new GridLayout(1, 1));
        add(c3d);
        setupScene();
    }
    
    // <editor-fold desc="Resource Gathering" defaultstate="collapsed">
    private BigDecimal[] getTimePoints(int numPoints) {
        if (!latLonStreamsActive()) {
            return new BigDecimal[] { BigDecimal.ZERO };
        }
        
        ArrayList<DataStream> streamsToQuery = new ArrayList<>();
        streamsToQuery.add(latitudeStream);
        streamsToQuery.add(longitudeStream);
        
        if (altitudeStream != null) {
            streamsToQuery.add(altitudeStream);
        }
        
        BigDecimal[] timeRange = DataStream.getCommonTimeRange(
                streamsToQuery.toArray(new DataStream[streamsToQuery.size()]));
        
        BigDecimal diffTimes = timeRange[1].subtract(timeRange[0]);
        BigDecimal interval = diffTimes.divide(new BigDecimal(numPoints), 2, RoundingMode.HALF_DOWN);
        
        BigDecimal[] times = new BigDecimal[numPoints];
        times[0] = timeRange[0];
        for (int i = 1; i < times.length - 1; ++i) {
            times[i] = times[i - 1].add(interval);
        }
        times[times.length - 1] = timeRange[1];
        
        return times;
    }
    
    private BigDecimal[] createPointsFromStream(DataStream stream, BigDecimal[] times) {
        if (times.length <= 2) {
            return new BigDecimal[] { BigDecimal.ZERO };
        }
        
        int limit = times.length;
        BigDecimal[] points = new BigDecimal[limit];
        for (int i = 0; i < limit; ++i) {
            points[i] = stream.getValueAtTime(times[i]);
        }
        return points;
    }
    // </editor-fold>
    
    // <editor-fold desc="Positioning" defaultstate="collapsed">
    private BigDecimal getCoordForVal(BigDecimal value, BigDecimal low, BigDecimal high, BigDecimal drawRange) {
        BigDecimal range = high.subtract(low);

        BigDecimal scaleDown = coordRange.divide(range, 10, RoundingMode.HALF_UP);
        BigDecimal diffMinVal = value.subtract(low);
        BigDecimal position = coordMin.add(diffMinVal.multiply(scaleDown));
        
        return position;
    }
    
    private BigDecimal updateLatLonSquareSize() {
        BigDecimal latRange = latitudeStream.getHighestValue()
                .subtract(latitudeStream.getLowestValue());
        
        if (latRange.compareTo(BigDecimal.ZERO) < 0) {
            latRange = latRange.negate();
        }
        BigDecimal lonRange = longitudeStream.getHighestValue()
                .subtract(longitudeStream.getLowestValue());
        if (latRange.compareTo(BigDecimal.ZERO) < 0) {
            latRange = latRange.negate();
        }
        BigDecimal largest = latRange.compareTo(lonRange) > 0 ? latRange : lonRange;
        return largest;
    }
    
    private BigDecimal getXForLon(BigDecimal longitude) {
        return getCoordForVal(longitude, lowestLon, highestLon, latLonSquareRange);
    }
    
    private BigDecimal getYForLat(BigDecimal latitude) {
        return getCoordForVal(latitude, lowestLat, highestLat, latLonSquareRange);
    }
    
    private BigDecimal getZForAlt(BigDecimal altitude) {
        return getCoordForVal(altitude, BigDecimal.ZERO, ALT_RANGE, ALT_RANGE);
    }
    // </editor-fold>
    
    public void setupScene() {
        // universe = new SimpleUniverse(c3d);
        drawScene();
        mainGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        mainGroup.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        mainGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        universe.addBranchGraph(mainGroup);

        OrbitBehavior orbit = new OrbitBehavior(c3d, OrbitBehavior.STOP_ZOOM | OrbitBehavior.REVERSE_ROTATE | OrbitBehavior.DISABLE_TRANSLATE);
        orbit.setSchedulingBounds(new BoundingSphere());
        orbit.RotationCenter(new Object[] { new Point3d(0, 0, -2.0) });
        
        c3d.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                switch (e.getKeyChar()) {
                    case 'r':
                        resetView();
                        break;
                }
                
                
            }
        });
        
        resetView();
        universe.getViewingPlatform().setViewPlatformBehavior(orbit);
    }
    
    private void resetView() {
        universe.getViewingPlatform().setNominalViewingTransform();
        Transform3D viewTransform = new Transform3D();
        viewTransform.set(new Vector3d(0d, 0d, 20d));
        universe.getViewingPlatform().getViewPlatformTransform().setTransform(viewTransform);
    }
    
    private void updateScene() {
        clearScene();
        drawScene();
    }
    
    private void clearScene() {
        if (mainGroup.numChildren() != 0) {
            mainGroup.removeAllChildren();
        }
    }
    
    public void drawScene() {
        BranchGroup group = new BranchGroup();
        group.setCapability(BranchGroup.ALLOW_DETACH);
        
        group.addChild(drawGround());
        
        if (latLonStreamsActive()) {
            updateLatLonSquareSize();
            drawPath(100, group);
        }

        Color3f light1Color = new Color3f(.1f, 1.1f, .1f); // green light
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
        Vector3f light1Direction = new Vector3f(0.1f, -0.1f, -12.0f);
        DirectionalLight light1 = new DirectionalLight(light1Color, light1Direction);
        light1.setInfluencingBounds(bounds);
        group.addChild(light1);
        
        mainGroup.addChild(group);
    }
    
    private void drawPath(int numPoints, BranchGroup group) {
        
        BigDecimal[] times = getTimePoints(numPoints);
        BigDecimal[] longitudes = createPointsFromStream(longitudeStream, times);
        BigDecimal[] latitudes = createPointsFromStream(latitudeStream, times);
        
        lowestLon = longitudeStream.getLowestValue();
        highestLon = longitudeStream.getHighestValue();
        
        lowestLat = latitudeStream.getLowestValue();
        highestLat = latitudeStream.getHighestValue();
        
        BigDecimal[] altitudes = new BigDecimal[] { BigDecimal.ZERO };
        
        if (altitudeStream != null) {
            altitudes = createPointsFromStream(altitudeStream, times);
        }
        
        // first iteration
        float x, y, z, lastX, lastY, lastZ;
        x = getXForLon(longitudes[0]).floatValue();
        y = getYForLat(latitudes[0]).floatValue();
        z = (altitudeStream == null ? .5f : getZForAlt(altitudes[0]).floatValue());
        drawSphereAtPoint(x, y, z);
        
        // rest
        for (int i = 1; i < numPoints; ++i) {
            lastX = x;
            lastY = y;
            lastZ = z;
            x = getXForLon(longitudes[i]).floatValue();
            y = getYForLat(latitudes[i]).floatValue();
            z = (altitudeStream == null ? .5f : getZForAlt(altitudes[i]).floatValue());
            group.addChild(drawToNextPoint(x, y, z, lastX, lastY, lastZ));
        }
        
        drawSphereAtPoint(x, y, z);
    }
    
    private TransformGroup drawGround() {
        Appearance ap = new Appearance();
        ap.setColoringAttributes(new ColoringAttributes(0f, 0f, 1f, ColoringAttributes.FASTEST));
        
        Box box = new Box(GROUND_SIZE, GROUND_SIZE, GROUND_WIDTH, ap);
        
        TransformGroup tg = new TransformGroup();
        Transform3D transform = new Transform3D();
        
        Vector3f vector = new Vector3f(0f, 0f, GROUND_Z);
        transform.setTranslation(vector);
        tg.setTransform(transform);
        tg.addChild(box);
        return tg;
    }
    
    private TransformGroup drawSphereAtPoint(float x, float y, float z) {
        Appearance ap = new Appearance();
        ap.setColoringAttributes(new ColoringAttributes(1f, 0f, 0f, ColoringAttributes.FASTEST));
        
        Sphere sphere = new Sphere(.05f, ap);
        
        TransformGroup tg = new TransformGroup();
        Transform3D transform = new Transform3D();

        Vector3f vector = new Vector3f(x, y, z);
        transform.setTranslation(vector);
        tg.setTransform(transform);
        tg.addChild(sphere);
        return tg;
    }
    
    private TransformGroup drawToNextPoint(float x, float y, float z, float lastX, float lastY, float lastZ) {
        Appearance ap = new Appearance();
        ap.setColoringAttributes(new ColoringAttributes(.1f, 1.4f, .1f, ColoringAttributes.NICEST));
        Sphere sphere = new Sphere(SPHERE_SIZE);
        
        TransformGroup tg = new TransformGroup();
        Transform3D transform = new Transform3D();

        Vector3f vector = new Vector3f(x, y, z);
        transform.setTranslation(vector);
        tg.setTransform(transform);
        tg.addChild(sphere);
        return tg;
    }
    
    // <editor-fold desc="Extern" defaultstate="collapsed">
    public void setLongitudeStream(DataStream stream) {
        longitudeStream = stream;
        updateScene();
    }
    
    public void setLatitudeStream(DataStream stream) {
        latitudeStream = stream;
        updateScene();
    }
    
    public void setAltitudeStream(DataStream stream) {
        altitudeStream = stream;
        updateScene();
    }
    // </editor-fold>
    
    private boolean latLonStreamsActive() {
        return (latitudeStream != null && longitudeStream != null);
    }
    
}
