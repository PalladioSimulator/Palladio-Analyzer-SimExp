package org.palladiosimulator.simexp.app.console.launcher;

import org.eclipse.debug.core.ILaunch;
import org.palladiosimulator.simexp.core.simulation.ISimulationResult;

public interface ISimulationLaunch extends ILaunch {
    ISimulationResult getSimulationResult();
}
