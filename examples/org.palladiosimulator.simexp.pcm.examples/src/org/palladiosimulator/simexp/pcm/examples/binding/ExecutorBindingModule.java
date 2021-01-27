package org.palladiosimulator.simexp.pcm.examples.binding;

import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.examples.loadbalancing.LoadBalancingSimulationExecutor;

import com.google.inject.AbstractModule;

public class ExecutorBindingModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(PcmExperienceSimulationExecutor.class).to(LoadBalancingSimulationExecutor.class);
	}

}
