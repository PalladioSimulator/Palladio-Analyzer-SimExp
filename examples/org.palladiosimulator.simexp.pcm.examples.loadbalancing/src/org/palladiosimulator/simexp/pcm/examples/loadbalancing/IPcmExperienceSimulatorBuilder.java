package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.pcm.examples.ISimExpSimulationConfiguration;

public interface IPcmExperienceSimulatorBuilder {
    
    ExperienceSimulator createSimulator(ISimExpSimulationConfiguration simExpSimulationConfiguration, Experiment experiment,DynamicBayesianNetwork dbn);

}
