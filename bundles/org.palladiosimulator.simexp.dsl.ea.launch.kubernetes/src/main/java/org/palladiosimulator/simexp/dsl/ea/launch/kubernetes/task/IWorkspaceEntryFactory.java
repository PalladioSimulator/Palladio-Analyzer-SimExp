package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.JobTask.WorkspaceEntry;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public interface IWorkspaceEntryFactory {

    WorkspaceEntry createOptimizableFile(List<OptimizableValue<?>> optimizableValues) throws IOException;

    WorkspaceEntry createFile(Path path) throws IOException;

    WorkspaceEntry createProjectArchive(Path projectFolder) throws IOException;

}