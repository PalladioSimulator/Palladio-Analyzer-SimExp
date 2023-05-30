package org.palladiosimulator.simexp.pcm.init;

import org.palladiosimulator.simexp.core.process.Initializable;
import org.palladiosimulator.simexp.pcm.action.QVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;

public class GlobalPcmBeforeExecutionInitialization implements Initializable {
	
	private final IExperimentProvider experimentProvider;
	
	public GlobalPcmBeforeExecutionInitialization(IExperimentProvider experimentProvider) {
		this.experimentProvider = experimentProvider;
	}
	

	@Override
	public void initialize() {
		experimentProvider.initializeExperimentRunner();
		QVToReconfigurationManager.get().resetReconfigurator();
	}

}
