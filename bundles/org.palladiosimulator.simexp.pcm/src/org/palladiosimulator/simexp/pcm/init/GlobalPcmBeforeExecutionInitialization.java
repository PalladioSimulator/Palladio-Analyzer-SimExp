package org.palladiosimulator.simexp.pcm.init;

import org.palladiosimulator.simexp.core.process.Initializable;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;

public class GlobalPcmBeforeExecutionInitialization implements Initializable {
	
	private final IExperimentProvider experimentProvider;
	private final IQVToReconfigurationManager qvtoReconfigurationManager;
	
	public GlobalPcmBeforeExecutionInitialization(IExperimentProvider experimentProvider, IQVToReconfigurationManager qvtoReconfigurationManager) {
		this.experimentProvider = experimentProvider;
		this.qvtoReconfigurationManager = qvtoReconfigurationManager;
	}
	

	@Override
	public void initialize() {
		experimentProvider.initializeExperimentRunner();
		qvtoReconfigurationManager.resetReconfigurator();
	}

}
