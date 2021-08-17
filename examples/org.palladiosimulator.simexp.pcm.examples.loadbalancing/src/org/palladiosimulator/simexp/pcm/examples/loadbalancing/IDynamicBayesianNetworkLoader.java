package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import org.eclipse.emf.ecore.resource.Resource;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.envdyn.environment.staticmodel.GroundProbabilisticNetwork;

public interface IDynamicBayesianNetworkLoader {
    
    DynamicBayesianNetwork loadDBN();
    
    GroundProbabilisticNetwork loadGroundProbabilisticNetwork();
    
    DynamicBayesianNetwork loadOrGenerateDBN(Resource usageModel);

}
