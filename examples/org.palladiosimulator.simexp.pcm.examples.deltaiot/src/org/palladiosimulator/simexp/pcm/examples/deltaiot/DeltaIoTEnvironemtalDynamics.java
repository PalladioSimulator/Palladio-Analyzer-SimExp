package org.palladiosimulator.simexp.pcm.examples.deltaiot;

import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.models.PCMInstance;

public class DeltaIoTEnvironemtalDynamics<R> extends DeltaIoTBaseEnvironemtalDynamics<R> {

    public DeltaIoTEnvironemtalDynamics(DynamicBayesianNetwork dbn,
            DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess) {
        super(dbn, modelAccess);
    }

}
