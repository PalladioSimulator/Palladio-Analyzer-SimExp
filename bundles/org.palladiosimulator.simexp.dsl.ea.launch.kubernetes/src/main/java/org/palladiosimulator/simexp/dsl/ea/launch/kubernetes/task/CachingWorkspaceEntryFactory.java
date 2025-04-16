package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.JobTask.WorkspaceEntry;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public class CachingWorkspaceEntryFactory implements IWorkspaceEntryFactory {
    private final IWorkspaceEntryFactory delegate;

    private final Map<Path, WorkspaceEntry> pathCache;
    private final Map<List<OptimizableValue<?>>, WorkspaceEntry> optimizableCache;

    public CachingWorkspaceEntryFactory(IWorkspaceEntryFactory delegate) {
        this.delegate = delegate;
        this.pathCache = new HashMap<>();
        this.optimizableCache = new HashMap<>();
    }

    @Override
    public WorkspaceEntry createOptimizableFile(List<OptimizableValue<?>> optimizableValues) throws IOException {
        WorkspaceEntry workspaceEntry = optimizableCache.get(optimizableValues);
        if (workspaceEntry == null) {
            workspaceEntry = delegate.createOptimizableFile(optimizableValues);
            optimizableCache.put(optimizableValues, workspaceEntry);
        }
        return workspaceEntry;
    }

    @Override
    public WorkspaceEntry createFile(Path path) throws IOException {
        WorkspaceEntry workspaceEntry = pathCache.get(path);
        if (workspaceEntry == null) {
            workspaceEntry = delegate.createFile(path);
            pathCache.put(path, workspaceEntry);
        }
        return workspaceEntry;
    }

    @Override
    public WorkspaceEntry createProjectArchive(Path projectFolder) throws IOException {
        WorkspaceEntry workspaceEntry = pathCache.get(projectFolder);
        if (workspaceEntry == null) {
            workspaceEntry = delegate.createProjectArchive(projectFolder);
            pathCache.put(projectFolder, workspaceEntry);
        }
        return workspaceEntry;
    }
}
