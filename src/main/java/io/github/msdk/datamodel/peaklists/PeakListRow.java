/* 
 * (C) Copyright 2015 by MSDK Development Team
 *
 * This software is dual-licensed under either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */

package io.github.msdk.datamodel.peaklists;

import java.util.List;

import javax.annotation.Nonnull;

/* 
 * WARNING: the interfaces in this package are still under construction
 */

/**
 * 
 */
public interface PeakListRow {

    /**
     * @return
     */
    @Nonnull
    PeakList getParentPeakList();

    /**
     * Return ID of this row
     */
    int getId();

    /**
     * Return parent ID of this row
     */
    int getParentId();

    /**
     * Return m/z of this row
     */
    int getMz();

    /**
     * Return RT of this row
     */
    int getRt();

    /**
     * Return number of features assigned to this row
     */
    int getNumberOfFeatures();

    /**
     * Return features assigned to this row
     */
    <DataType> List<PeakListColumn<DataType>> getFeatures();

    /**
     * Return data assigned to this row
     */
    <DataType> DataType getData(PeakListColumn<DataType> column);

}