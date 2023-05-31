package org.palladiosimulator.simexp.pcm.examples.hri;

import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.process.Initializable;
import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.init.GlobalPcmBeforeExecutionInitialization;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;

public class RobotCognitionBeforeExecutionInitialization extends GlobalPcmBeforeExecutionInitialization {
	private final ReconfigurationStrategy<? extends Reconfiguration<?>> reconfigurationStrategy;
	
	public RobotCognitionBeforeExecutionInitialization(ReconfigurationStrategy<? extends Reconfiguration<?>> reconfigurationStrategy, IExperimentProvider experimentProvider, IQVToReconfigurationManager qvtoReconfigurationManager) {
		super(experimentProvider, qvtoReconfigurationManager);
		this.reconfigurationStrategy = reconfigurationStrategy;
	}
	
	@Override
	public void initialize() {
		super.initialize();
		
		if (reconfigurationStrategy instanceof Initializable) {
			Initializable.class.cast(reconfigurationStrategy).initialize();
		}
	}
}
