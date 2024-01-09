package org.palladiosimulator.simexp.pcm.examples.hri;

import org.palladiosimulator.simexp.core.process.Initializable;
import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.init.GlobalPcmBeforeExecutionInitialization;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;

public class RobotCognitionBeforeExecutionInitialization<S> extends GlobalPcmBeforeExecutionInitialization {
    private final ReconfigurationStrategy<S, QVTOReconfigurator, QVToReconfiguration> reconfigurationStrategy;

    public RobotCognitionBeforeExecutionInitialization(
            ReconfigurationStrategy<S, QVTOReconfigurator, QVToReconfiguration> reconfigurationStrategy,
            IExperimentProvider experimentProvider, IQVToReconfigurationManager qvtoReconfigurationManager) {
        super(experimentProvider, qvtoReconfigurationManager);
        this.reconfigurationStrategy = reconfigurationStrategy;
    }

    @Override
    public void initialize() {
        super.initialize();

        if (reconfigurationStrategy instanceof Initializable) {
            Initializable.class.cast(reconfigurationStrategy)
                .initialize();
        }
    }
}
