package org.palladiosimulator.simexp.pcm.config;

import java.util.Optional;

import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.simexp.commons.constants.model.SimulationEngine;
import org.palladiosimulator.simexp.commons.constants.model.SimulatorType;

import tools.mdsd.probdist.api.random.ISeedProvider;

public interface IWorkflowConfiguration extends ITransformationConfiguration {
    SimulatorType getSimulatorType();

    SimulationEngine getSimulationEngine();

    URI getExperimentsURI();

    URI getStaticModelURI();

    URI getDynamicModelURI();

    SimulationParameters getSimulationParameters();

    Optional<ISeedProvider> getSeedProvider();

}