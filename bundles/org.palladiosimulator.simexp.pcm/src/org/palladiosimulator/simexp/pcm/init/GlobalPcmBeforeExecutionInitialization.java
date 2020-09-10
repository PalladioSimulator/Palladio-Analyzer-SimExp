package org.palladiosimulator.simexp.pcm.init;

import org.palladiosimulator.simexp.core.process.Initializable;
import org.palladiosimulator.simexp.pcm.action.QVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.util.ExperimentProvider;

public class GlobalPcmBeforeExecutionInitialization implements Initializable {

	@Override
	public void initialize() {
		ExperimentProvider.get().initializeExperimentRunner();
		QVToReconfigurationManager.get().resetReconfigurator();
	}

}
