package org.palladiosimulator.simexp.workflow.api;

import org.palladiosimulator.simexp.pcm.config.IModelledPrismWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.config.IModelledWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.config.IPrismWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.modelled.simulator.config.IModelledPcmWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.simulator.config.IPCMWorkflowConfiguration;

public interface ISimExpWorkflowConfiguration extends IPCMWorkflowConfiguration, IPrismWorkflowConfiguration,
        IModelledWorkflowConfiguration, IModelledPcmWorkflowConfiguration, IModelledPrismWorkflowConfiguration {

}
