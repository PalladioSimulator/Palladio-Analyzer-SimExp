package org.palladiosimulator.simexp.pcm.config;

import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.simexp.commons.constants.model.SimulationEngine;
import org.palladiosimulator.simexp.commons.constants.model.SimulatorType;

public interface IWorkflowConfiguration {
    SimulatorType getSimulatorType();

    SimulationEngine getSimulationEngine();

    URI getExperimentsURI();

    URI getStaticModelURI();

    URI getDynamicModelURI();

    SimulationParameters getSimulationParameters();

    Set<String> getTransformationNames();

}