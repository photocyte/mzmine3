/*
 * Copyright 2006-2015 The MZmine 3 Development Team
 * 
 * This file is part of MZmine 3.
 * 
 * MZmine 3 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 3 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 3; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.modules.plots.msspectrum;

import java.text.NumberFormat;

import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;

import io.github.msdk.datamodel.msspectra.MsSpectrum;
import io.github.msdk.datamodel.msspectra.MsSpectrumType;
import io.github.msdk.datamodel.rawdata.MsScan;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.msdk.util.MsSpectrumUtil;
import io.github.mzmine.main.MZmineCore;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

/**
 * MS spectrum data set. Implements IntervalXYDataset for centroid spectra
 * support (rendered by XYBarRenderer).
 */
public class MsSpectrumDataSet extends AbstractXYDataset
        implements XYItemLabelGenerator, XYToolTipGenerator, IntervalXYDataset {

    private final double mzValues[];
    private final float intensityValues[];
    private final float topIndensity;
    private final int numOfDataPoints;

    private final StringProperty name = new SimpleStringProperty(this, "name",
            "MS spectrum");
    private final DoubleProperty mzShift = new SimpleDoubleProperty(this,
            "mzShift", 0.0);
    private final DoubleProperty intensityScale = new SimpleDoubleProperty(this,
            "intensityScale", 0.0);
    private final IntegerProperty lineThickness = new SimpleIntegerProperty(
            this, "lineThickness", 1);
    private final ObjectProperty<MsSpectrumType> renderingType = new SimpleObjectProperty<>(
            this, "renderingType", MsSpectrumType.CENTROIDED);
    private final ObjectProperty<Color> color = new SimpleObjectProperty<>(this,
            "color", Color.BLUE);
    private final BooleanProperty showDataPoints = new SimpleBooleanProperty(
            this, "showDataPoints", false);

    public String getName() {
        return name.get();
    }

    public void setName(String newName) {
        name.set(newName);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public Double getMzShift() {
        return mzShift.get();
    }

    public void setMzShift(Double newMzShift) {
        mzShift.set(newMzShift);
    }

    public DoubleProperty mzShiftProperty() {
        return mzShift;
    }

    public Double getIntensityScale() {
        return intensityScale.get();
    }

    public void setIntensityScale(Double newIntensityScale) {
        intensityScale.set(newIntensityScale);
    }

    public DoubleProperty intensityScaleProperty() {
        return intensityScale;
    }

    public Integer getLineThickness() {
        return lineThickness.get();
    }

    public void setMzShift(Integer newLineThickness) {
        lineThickness.set(newLineThickness);
    }

    public IntegerProperty lineThicknessProperty() {
        return lineThickness;
    }

    public Boolean getShowDataPoints() {
        return showDataPoints.get();
    }

    public void setMzShift(Boolean newShowDataPoints) {
        showDataPoints.set(newShowDataPoints);
    }

    public BooleanProperty showDataPointsProperty() {
        return showDataPoints;
    }

    public MsSpectrumType getRenderingType() {
        return renderingType.get();
    }

    public void setRenderingType(MsSpectrumType newType) {
        renderingType.set(newType);
    }

    public ObjectProperty<MsSpectrumType> renderingTypeProperty() {
        return renderingType;
    }

    public Color getColor() {
        return color.get();
    }

    public void setColor(Color newColor) {
        color.set(newColor);
    }

    public ObjectProperty<Color> colorProperty() {
        return color;
    }

    MsSpectrumDataSet(MsSpectrum spectrum) {

        String spectrumTitle = "MS spectrum";
        if (spectrum instanceof MsScan) {
            MsScan scan = (MsScan) spectrum;
            RawDataFile dataFile = scan.getRawDataFile();
            if (dataFile != null)
                spectrumTitle += " " + dataFile.getName();
            spectrumTitle += "#" + scan.getScanNumber();
        }
        setName(spectrumTitle);

        this.mzValues = spectrum.getMzValues();
        this.intensityValues = spectrum.getIntensityValues();
        this.numOfDataPoints = spectrum.getNumberOfDataPoints();

        this.topIndensity = MsSpectrumUtil.getMaxIntensity(intensityValues,
                numOfDataPoints);
        setIntensityScale((double) topIndensity);
        intensityScale.addListener(e -> {
            fireDatasetChanged();
        });
        name.addListener(e -> {
            fireDatasetChanged();
        });
    }

    @Override
    public int getItemCount(int series) {
        return numOfDataPoints;
    }

    @Override
    public Number getX(int series, int index) {
        return mzValues[index];
    }

    @Override
    public Number getY(int series, int index) {
        return intensityValues[index] * (getIntensityScale() / topIndensity);
    }

    @Override
    public int getSeriesCount() {
        return 1;
    }

    @Override
    public Comparable getSeriesKey(int series) {
        return getName();
    }

    @Override
    public String generateLabel(XYDataset ds, int series, int index) {
        NumberFormat mzFormat = MZmineCore.getConfiguration().getMZFormat();
        final double mz = mzValues[index];
        String label = mzFormat.format(mz);
        return label;
    }

    @Override
    public String generateToolTip(XYDataset ds, int series, int index) {
        NumberFormat mzFormat = MZmineCore.getConfiguration().getMZFormat();
        final double mz = mzValues[index];
        String label = mzFormat.format(mz);
        return label;

    }

    @Override
    public Number getStartX(int series, int item) {
        return getX(series, item);
    }

    @Override
    public double getStartXValue(int series, int item) {
        return getXValue(series, item);
    }

    @Override
    public Number getEndX(int series, int item) {
        return getX(series, item);
    }

    @Override
    public double getEndXValue(int series, int item) {
        return getXValue(series, item);
    }

    @Override
    public Number getStartY(int series, int item) {
        return getY(series, item);
    }

    @Override
    public double getStartYValue(int series, int item) {
        return getYValue(series, item);
    }

    @Override
    public Number getEndY(int series, int item) {
        return getY(series, item);
    }

    @Override
    public double getEndYValue(int series, int item) {
        return getYValue(series, item);
    }

}