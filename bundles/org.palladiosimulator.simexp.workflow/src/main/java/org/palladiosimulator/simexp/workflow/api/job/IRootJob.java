package org.palladiosimulator.simexp.workflow.api.job;

import org.palladiosimulator.simexp.core.simulation.ISimulationResult;

import de.uka.ipd.sdq.workflow.jobs.IJob;

public interface IRootJob extends IJob {
    ISimulationResult getRootJobResult();
}
