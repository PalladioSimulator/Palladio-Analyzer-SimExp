package org.palladiosimulator.simexp.pcm.examples.executor;

import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;
import org.palladiosimulator.simexp.pcm.util.SimulationParameterConfiguration;

public abstract class KmodelSimulationExecutor extends PcmExperienceSimulationExecutor {
    private final Kmodel     kmodel;
    
    protected KmodelSimulationExecutor(Kmodel kmodel, Experiment experiment, SimulationParameterConfiguration simulationParameters) {
    	super(experiment, simulationParameters);
        this.kmodel = kmodel;
    }

    protected Kmodel getKmodel() {
        return kmodel;
    }
}
