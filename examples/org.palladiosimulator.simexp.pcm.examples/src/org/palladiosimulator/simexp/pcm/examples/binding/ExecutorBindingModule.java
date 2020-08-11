package org.palladiosimulator.simexp.pcm.examples.binding;

import org.palladiosimulator.simexp.pcm.examples.deltaiot.DeltaIoTSimulationExecutor;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;

import com.google.inject.AbstractModule;

public class ExecutorBindingModule extends AbstractModule {

	@Override
	protected void configure() {
		//bind(PcmExperienceSimulationExecutor.class).to(LoadBalancingSimulationExecutor.class);
		bind(PcmExperienceSimulationExecutor.class).to(DeltaIoTSimulationExecutor.class);
	}

}
