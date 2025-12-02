package org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy;

import org.palladiosimulator.simexp.pcm.config.SimulationParameters;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTModelAccess;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.SystemConfigurationTracker;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.core.models.PCMInstance;

public class DeltaIoTAlwaysAdaptReconfigurationStrategy extends DeltaIoTDefaultReconfigurationStrategy {

    public DeltaIoTAlwaysAdaptReconfigurationStrategy(DeltaIoTReconfigurationParamRepository reconfParamsRepo,
            DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess, SimulationParameters simulationParameters,
            SystemConfigurationTracker systemConfigurationTracker,
            IDeltaIoToReconfCustomizerResolver reconfCustomizerResolver) {
        super(reconfParamsRepo, modelAccess, simulationParameters, systemConfigurationTracker,
                reconfCustomizerResolver);
    }

    @Override
    protected boolean adaptDistributionFactor(boolean powerChanging) {
        return true;
    }
}
