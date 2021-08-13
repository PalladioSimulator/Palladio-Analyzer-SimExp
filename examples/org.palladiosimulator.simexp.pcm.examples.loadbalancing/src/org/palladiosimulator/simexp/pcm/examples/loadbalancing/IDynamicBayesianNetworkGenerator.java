package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.experimentautomation.experiments.Experiment;

public interface IDynamicBayesianNetworkGenerator {

    DynamicBayesianNetwork generateDBN(Experiment exp);
    
}
