package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.envdyn.environment.staticmodel.GroundProbabilisticNetwork;
import org.palladiosimulator.experimentautomation.experiments.Experiment;

public interface IDynamicBayesianBNetworkLoader {
    
    DynamicBayesianNetwork loadDBN();
    
    GroundProbabilisticNetwork loadGroundProbabilisticNetwork();
    
    DynamicBayesianNetwork loadOrGenerateDBN(Experiment exp);

}
