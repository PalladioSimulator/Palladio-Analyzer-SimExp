package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import org.eclipse.emf.ecore.resource.Resource;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.simexp.pcm.examples.ISimExpPcmConfiguration;

public interface IEnvironmentalDynamicsModelProvider {
    
    DynamicBayesianNetwork getEnvironmentalDynamicsModel(ISimExpPcmConfiguration simExpPcmConfiguration, Resource usageModel);

}
