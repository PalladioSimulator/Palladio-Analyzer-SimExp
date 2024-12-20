package org.palladiosimulator.simexp.pcm.state;

import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;

import de.uka.ipd.sdq.scheduler.resources.active.IResourceTableManager;

public interface IPCMReconfigurationExecutor {
    void execute(IExperimentProvider experimentProvider, IResourceTableManager resourceTableManager);
}
