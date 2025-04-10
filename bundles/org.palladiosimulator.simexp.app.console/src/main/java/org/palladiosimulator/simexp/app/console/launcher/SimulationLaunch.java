package org.palladiosimulator.simexp.app.console.launcher;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.ISourceLocator;
import org.palladiosimulator.simexp.core.simulation.ISimulationResult;

public class SimulationLaunch extends Launch implements ISimulationLaunch {
    private final ISimulationResultProvider simulationResultProvider;

    public SimulationLaunch(ILaunchConfiguration launchConfiguration, String mode, ISourceLocator locator,
            ISimulationResultProvider simulationResultProvider) {
        super(launchConfiguration, mode, locator);
        this.simulationResultProvider = simulationResultProvider;
    }

    @Override
    public ISimulationResult getSimulationResult() {
        return simulationResultProvider.getSimulationResult();
    }
}
