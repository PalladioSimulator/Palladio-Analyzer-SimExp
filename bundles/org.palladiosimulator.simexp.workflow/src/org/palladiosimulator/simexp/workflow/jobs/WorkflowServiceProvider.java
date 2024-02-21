package org.palladiosimulator.simexp.workflow.jobs;

import static org.palladiosimulator.simexp.service.registry.ServiceEntry.service;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceAccessor;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceCache;
import org.palladiosimulator.simexp.core.store.cache.guava.loader.GuavaSimulatedExperienceCache;
import org.palladiosimulator.simexp.core.store.csv.accessor.CsvAccessor;
import org.palladiosimulator.simexp.distribution.apache.factory.ProbabilityDistributionFactoryAdapter;
import org.palladiosimulator.simexp.distribution.factory.ProbabilityDistributionFactory;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.service.registry.ServiceProvider;
import org.palladiosimulator.simexp.service.registry.ServiceRegistration;

public class WorkflowServiceProvider implements ServiceProvider {
    private static final Logger LOGGER = Logger.getLogger(WorkflowServiceProvider.class);

    @Override
    public void register(ServiceRegistration serviceRegistration) {
//      serviceRegistration.registerAService(service(PcmExperienceSimulationExecutor.class).isProvidedBy(LoadBalancingSimulationExecutor.class));
        serviceRegistration.registerService(
                service(PcmExperienceSimulationExecutor.class).isProvidedBy(PcmExperienceSimulationExecutor.class)); // FIXME

        serviceRegistration.registerService(service(ProbabilityDistributionFactory.class)
            .isProvidedBy(ProbabilityDistributionFactoryAdapter.class));
        serviceRegistration.registerService(
                service(SimulatedExperienceCache.class).isProvidedBy(GuavaSimulatedExperienceCache.class));
        serviceRegistration
            .registerService(service(SimulatedExperienceAccessor.class).isProvidedBy(CsvAccessor.class));

        LOGGER.info("Finishing registration of workflow services.");
    }

}
