package org.palladiosimulator.simexp.pcm.examples.executor;

import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;

public abstract class KmodelSimulationExecutor extends PcmExperienceSimulationExecutor {
    private final Kmodel     kmodel;
    
    protected KmodelSimulationExecutor(Kmodel kmodel) {
        this.kmodel = kmodel;
    }

    protected Kmodel getKmodel() {
        return kmodel;
    }
}
