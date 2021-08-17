package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import org.eclipse.emf.ecore.resource.Resource;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;

public interface IDynamicBayesianNetworkGenerator {

    DynamicBayesianNetwork generateDBN(Resource usageModel);
    
}
