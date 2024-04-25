package org.palladiosimulator.simexp.pcm.config;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.simexp.commons.constants.model.QualityObjective;
import org.palladiosimulator.simexp.commons.constants.model.SimulationEngine;
import org.palladiosimulator.simexp.commons.constants.model.SimulatorType;

public interface IPCMWorkflowConfiguration {
    SimulatorType getSimulatorType();

    SimulationEngine getSimulationEngine();

    QualityObjective getQualityObjective();

    URI getExperimentsURI();

    URI getStaticModelURI();

    URI getDynamicModelURI();

    URI getSmodelURI();

    SimulationParameters getSimulationParameters();

    List<String> getMonitorNames();

    List<URI> getModuleFiles();

    List<URI> getPropertyFiles();

}
