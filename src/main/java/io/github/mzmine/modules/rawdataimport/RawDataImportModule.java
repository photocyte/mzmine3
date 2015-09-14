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
 * MZmine 3; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.modules.rawdataimport;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import io.github.msdk.MSDKException;
import io.github.msdk.datamodel.datapointstore.DataPointStore;
import io.github.msdk.datamodel.datapointstore.DataPointStoreFactory;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.msdk.io.rawdataimport.RawDataFileImportMethod;
import io.github.mzmine.modules.MZmineModuleCategory;
import io.github.mzmine.modules.MZmineProcessingModule;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.project.MZmineProject;
import io.github.mzmine.taskcontrol.MSDKTask;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

/**
 * Raw data import module
 */
public class RawDataImportModule implements MZmineProcessingModule {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private static final String MODULE_NAME = "Raw data import";
    private static final String MODULE_DESCRIPTION = "This module imports raw data into the project.";

    @SuppressWarnings("null")
    @Override
    public @Nonnull String getName() {
        return MODULE_NAME;
    }

    @SuppressWarnings("null")
    @Override
    public @Nonnull String getDescription() {
        return MODULE_DESCRIPTION;
    }

    @Override
    public void runModule(@Nonnull MZmineProject project,
            @Nonnull ParameterSet parameters,
            @Nonnull Collection<Task<?>> tasks) {

        List<File> fileNames = Collections.emptyList();
        // parameters
        // .getParameter(RawDataImportParameters.fileNames).getValue();

        for (File fileName : fileNames) {

            if ((!fileName.exists()) || (!fileName.canRead())) {
                // MZmineCore.getDesktop().displayErrorMessage("Cannot read file
                // "
                // + fileName);
                logger.warning("Cannot read file " + fileName);
                return;
            }

            DataPointStore dataStore;
            MSDKTask newTask = null;
            try {
                dataStore = DataPointStoreFactory.getTmpFileDataPointStore();
                RawDataFileImportMethod method = new RawDataFileImportMethod(
                        fileName, dataStore);
                newTask = new MSDKTask("Importing raw data file",
                        fileName.getName(), method);

                EventHandler<WorkerStateEvent> succesEvent = new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent workerEvent) {
                        RawDataFile rawDataFile = method.getResult();
                        project.addFile(rawDataFile);
                    }
                };
                newTask.setOnSucceeded(succesEvent);
            } catch (MSDKException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (newTask == null) {
                logger.warning(
                        "Cannot determine file type of file " + fileName);
                return;
            }

            tasks.add(newTask);
        }

    }

    @Override
    public @Nonnull MZmineModuleCategory getModuleCategory() {
        return MZmineModuleCategory.RAWDATA;
    }

    private RawDataImportParameters params = new RawDataImportParameters();

    public @Nonnull ParameterSet getParameters() {
        return params;
    }
}
