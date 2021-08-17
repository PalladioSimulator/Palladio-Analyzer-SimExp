package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import java.util.HashSet;
import java.util.Set;

import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.pcm.action.QVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.builder.PcmExperienceSimulationBuilder;
import org.palladiosimulator.simexp.pcm.examples.ISimExpSimulationConfiguration;
import org.palladiosimulator.simexp.pcm.init.GlobalPcmBeforeExecutionInitialization;
import org.palladiosimulator.simexp.pcm.process.PcmExperienceSimulationRunner;

import com.google.common.collect.Sets;

public class LoadBalancingSimulationExperienceBuilder implements IPcmExperienceSimulatorBuilder {
    

    @Override
    public ExperienceSimulator createSimulator(ISimExpSimulationConfiguration simExpSimulationConfiguration, Experiment experiment,
            DynamicBayesianNetwork dbn) {
        return PcmExperienceSimulationBuilder.newBuilder()
                .makeGlobalPcmSettings()
                    .withInitialExperiment(experiment)
                    .andSimulatedMeasurementSpecs(Sets.newHashSet(simExpSimulationConfiguration.getPcmSpecs()))
                    .addExperienceSimulationRunner(new PcmExperienceSimulationRunner())
                    .done()
                .createSimulationConfiguration()
                    .withSimulationID(simExpSimulationConfiguration.getSimulationId())
                    .withNumberOfRuns(3) //500
                    .andNumberOfSimulationsPerRun(5) //100
                    .andOptionalExecutionBeforeEachRun(new GlobalPcmBeforeExecutionInitialization())
                    .done()
                .specifySelfAdaptiveSystemState()
                    .asEnvironmentalDrivenProcess(VaryingInterarrivelRateProcess.get(dbn))
                    .done()
                .createReconfigurationSpace()
                    .addReconfigurations(getAllReconfigurations())
                    .andReconfigurationStrategy(simExpSimulationConfiguration.getReconfigurationPolicy())
                    .done()
                .specifyRewardHandling()
                    .withRewardEvaluator(simExpSimulationConfiguration.getRewardEvaluator())
                    .done()
                .build();
    }
    
    private Set<Reconfiguration<?>> getAllReconfigurations() {
        return new HashSet<Reconfiguration<?>>(QVToReconfigurationManager.get().loadReconfigurations());
    }

}
