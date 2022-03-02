package org.palladiosimulator.simexp.workflow.jobs;

import static org.palladiosimulator.simexp.service.registry.ServiceEntry.service;
import static org.palladiosimulator.simexp.service.registry.ServiceRegistry.registerService;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceAccessor;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceCache;
import org.palladiosimulator.simexp.core.store.cache.guava.loader.GuavaSimulatedExperienceCache;
import org.palladiosimulator.simexp.core.store.csv.accessor.CsvAccessor;
import org.palladiosimulator.simexp.distribution.apache.factory.ProbabilityDistributionFactoryAdapter;
import org.palladiosimulator.simexp.distribution.factory.ProbabilityDistributionFactory;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.examples.loadbalancing.LoadBalancingSimulationExecutor;
//import org.palladiosimulator.simexp.pcm.prism.service.PrismService;
import org.palladiosimulator.simexp.pcm.examples.performability.loadbalancing.FaultTolerantLoadBalancingSimulationExecutor;

//import de.fzi.srp.simulatedexperience.prism.wrapper.service.PrismInvocationService;
import de.uka.ipd.sdq.workflow.jobs.CleanupFailedException;
import de.uka.ipd.sdq.workflow.jobs.IJob;
import de.uka.ipd.sdq.workflow.jobs.JobFailedException;
import de.uka.ipd.sdq.workflow.jobs.UserCanceledException;

public class SimExpServiceRegistrationJob implements IJob {

	private static final Logger LOGGER = Logger.getLogger(PcmExperienceSimulationJob.class.getName());
	
	@Override
	public void execute(IProgressMonitor monitor) throws JobFailedException, UserCanceledException {
		LOGGER.info("Start registration of the required services.");
		
//		registerService(service(PcmExperienceSimulationExecutor.class).isProvidedBy(LoadBalancingSimulationExecutor.class));
		registerService(service(PcmExperienceSimulationExecutor.class).isProvidedBy(FaultTolerantLoadBalancingSimulationExecutor.class));

		//FIXME: Prism service registration should be customized
//		registerService(service(PrismService.class).isProvidedBy(PrismInvocationService.class));
		registerService(service(ProbabilityDistributionFactory.class).isProvidedBy(ProbabilityDistributionFactoryAdapter.class));
		registerService(service(SimulatedExperienceCache.class).isProvidedBy(GuavaSimulatedExperienceCache.class));
		registerService(service(SimulatedExperienceAccessor.class).isProvidedBy(CsvAccessor.class));
	
		LOGGER.info("Finishing registration of the required services.");
	}

	@Override
	public void cleanup(IProgressMonitor monitor) throws CleanupFailedException {
		/**
         * 
         * nothing to do here
         * 
         * */ 
	}

	@Override
	public String getName() {
		return this.getClass().getCanonicalName();
	}

}
