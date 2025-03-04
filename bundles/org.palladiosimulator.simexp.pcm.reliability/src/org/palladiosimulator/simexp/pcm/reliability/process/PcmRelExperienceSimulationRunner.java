package org.palladiosimulator.simexp.pcm.reliability.process;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.palladiosimulator.dependability.reliability.uncertainty.UncertaintyRepository;
import org.palladiosimulator.dependability.reliability.uncertainty.solver.api.UncertaintyBasedReliabilityPrediction;
import org.palladiosimulator.dependability.reliability.uncertainty.solver.api.UncertaintyBasedReliabilityPredictionConfig;
import org.palladiosimulator.dependability.reliability.uncertainty.solver.markov.ReliabilityPredictionResult;
import org.palladiosimulator.dependability.reliability.uncertainty.solver.model.DiscreteUncertaintyStateSpace;
import org.palladiosimulator.dependability.reliability.uncertainty.solver.model.DiscreteUncertaintyStateSpace.UncertaintyState;
import org.palladiosimulator.envdyn.api.entity.bn.InputValue;
import org.palladiosimulator.simexp.core.process.ExperienceSimulationRunner;
import org.palladiosimulator.simexp.core.state.StateQuantity;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.reliability.entity.PcmRelSimulatedMeasurementSpec;
import org.palladiosimulator.simexp.pcm.reliability.job.PrepareBlackboardForReliabilityAnalysisJob;
import org.palladiosimulator.simexp.pcm.state.PcmSelfAdaptiveSystemState;

import com.google.common.collect.Lists;

import de.uka.ipd.sdq.workflow.jobs.JobFailedException;
import de.uka.ipd.sdq.workflow.jobs.UserCanceledException;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;
import tools.mdsd.probdist.api.apache.util.IProbabilityDistributionRepositoryLookup;
import tools.mdsd.probdist.api.entity.CategoricalValue;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionFactory;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionRegistry;
import tools.mdsd.probdist.api.parser.ParameterParser;
import tools.mdsd.probdist.api.random.ISeedProvider;

public class PcmRelExperienceSimulationRunner<A, V> implements ExperienceSimulationRunner {

    private final UncertaintyBasedReliabilityPredictionConfig globalConfig;
    private final DiscreteUncertaintyStateSpace uncertaintyStateSpace;
    private final IProbabilityDistributionRegistry<CategoricalValue> probabilityDistributionRegistry;
    private final IProbabilityDistributionFactory<CategoricalValue> probabilityDistributionFactory;
    private final ParameterParser parameterParser;
    private final IProbabilityDistributionRepositoryLookup probDistRepoLookup;
    private final Optional<ISeedProvider> seedProvider;

    public PcmRelExperienceSimulationRunner(UncertaintyBasedReliabilityPredictionConfig globalConfig,
            IProbabilityDistributionRegistry<CategoricalValue> probabilityDistributionRegistry,
            IProbabilityDistributionFactory<CategoricalValue> probabilityDistributionFactory,
            ParameterParser parameterParser, IProbabilityDistributionRepositoryLookup probDistRepoLookup,
            Optional<ISeedProvider> seedProvider) {
        this.globalConfig = globalConfig;
        this.uncertaintyStateSpace = buildUncertaintyStateSpace(globalConfig.getUncertaintyRepository(),
                parameterParser);
        this.probabilityDistributionRegistry = probabilityDistributionRegistry;
        this.probabilityDistributionFactory = probabilityDistributionFactory;
        this.parameterParser = parameterParser;
        this.probDistRepoLookup = probDistRepoLookup;
        this.seedProvider = seedProvider;
    }

    private DiscreteUncertaintyStateSpace buildUncertaintyStateSpace(UncertaintyRepository uncertaintyRepo,
            ParameterParser parameterParser) {
        var stateSpace = uncertaintyRepo.getUncertaintyInducedFailureTypes()
            .stream()
            .flatMap(each -> DiscreteUncertaintyStateSpace.valueSpaceOf(each, parameterParser)
                .stream())
            .collect(toList());
        return DiscreteUncertaintyStateSpace.of(stateSpace);
    }

    @Override
    public void simulate(State state) {
        PcmSelfAdaptiveSystemState<A, V> pcmState = (PcmSelfAdaptiveSystemState<A, V>) state;
        var result = makePrediction(pcmState, probabilityDistributionRegistry, probabilityDistributionFactory,
                parameterParser, probDistRepoLookup);
        retrieveAndSetStateQuantities(pcmState.getQuantifiedState(), result);
    }

    private ReliabilityPredictionResult makePrediction(PcmSelfAdaptiveSystemState<A, V> pcmState,
            IProbabilityDistributionRegistry<CategoricalValue> probabilityDistributionRegistry,
            IProbabilityDistributionFactory<CategoricalValue> probabilityDistributionFactory,
            ParameterParser parameterParser, IProbabilityDistributionRepositoryLookup probDistRepoLookup) {
        var config = deriveConfigFrom(pcmState);
        var uncertaintyStates = deriveUncertaintyStates(pcmState.getPerceivedEnvironmentalState());
        return UncertaintyBasedReliabilityPrediction.predictGiven(uncertaintyStates, config,
                probabilityDistributionRegistry, probabilityDistributionFactory, parameterParser, probDistRepoLookup,
                seedProvider);
    }

    private UncertaintyBasedReliabilityPredictionConfig deriveConfigFrom(PcmSelfAdaptiveSystemState<A, V> pcmState) {
        var blackboard = prepareBlackboardForAnalysis(pcmState);
        return UncertaintyBasedReliabilityPredictionConfig.newBuilder()
            .rebuild(globalConfig, blackboard);
    }

    private MDSDBlackboard prepareBlackboardForAnalysis(PcmSelfAdaptiveSystemState<A, V> pcmState) {
        var pcm = pcmState.getArchitecturalConfiguration()
            .getConfiguration();
        var builderJob = new PrepareBlackboardForReliabilityAnalysisJob(pcm);
        try {
            builderJob.execute(new NullProgressMonitor());
        } catch (JobFailedException | UserCanceledException e) {
            throw new RuntimeException("Something went wrong while preparing the blackboard for analysis.", e);
        }
        return builderJob.getBlackboard();
    }

    private void retrieveAndSetStateQuantities(StateQuantity quantity, ReliabilityPredictionResult result) {
        filterPcmRelMeasurementsSpec(quantity).forEach(spec -> {
            var probOfSuccess = result.filterPredictionResultsFor(spec.getUsageScenario())
                .iterator()
                .next();
            quantity.setMeasurement(probOfSuccess.getConditionalProbabilityOfSuccess(), spec);
        });
    }

    private List<UncertaintyState> deriveUncertaintyStates(PerceivableEnvironmentalState<V> envState) {
        List<UncertaintyState> uncertaintyStateTuple = Lists.newArrayList();
        for (InputValue<CategoricalValue> each : toInputs(envState)) {
            uncertaintyStateSpace.findStateInstantiating(each.getVariable()
                .getInstantiatedTemplate())
                .ifPresent(s -> {
                    UncertaintyState stateToAdd = s.newValuedStateWith(each.getValue());
                    uncertaintyStateTuple.add(stateToAdd);
                });
        }
        return uncertaintyStateTuple;
    }

    private List<InputValue<CategoricalValue>> toInputs(PerceivableEnvironmentalState<V> envState) {
        Object sample = envState.getValue()
            .getValue();
        if (List.class.isInstance(sample)) {
            List<?> inputs = List.class.cast(sample);
            if (inputs.isEmpty() == false) {
                if (InputValue.class.isInstance(inputs.get(0))) {
                    return inputs.stream()
                        .map(InputValue.class::cast)
                        .collect(toList());
                }
            }
        }
        return Lists.newArrayList();
    }

    private List<PcmRelSimulatedMeasurementSpec> filterPcmRelMeasurementsSpec(StateQuantity quantity) {
        return quantity.getMeasurementSpecs()
            .stream()
            .filter(PcmRelSimulatedMeasurementSpec.class::isInstance)
            .map(PcmRelSimulatedMeasurementSpec.class::cast)
            .collect(toList());
    }

}
