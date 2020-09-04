package org.palladiosimulator.simexp.pcm.reliability.process;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.palladiosimulator.dependability.reliability.uncertainty.UncertaintyRepository;
import org.palladiosimulator.dependability.reliability.uncertainty.solver.api.UncertaintyBasedReliabilityPredictionConfig;
import org.palladiosimulator.dependability.reliability.uncertainty.solver.api.UncertaintyBasedReliabilityPredictor;
import org.palladiosimulator.dependability.reliability.uncertainty.solver.markov.ReliabilityPredictionResult;
import org.palladiosimulator.dependability.reliability.uncertainty.solver.model.DiscreteUncertaintyStateSpace;
import org.palladiosimulator.dependability.reliability.uncertainty.solver.model.DiscreteUncertaintyStateSpace.UncertaintyState;
import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork.InputValue;
import org.palladiosimulator.simexp.core.process.ExperienceSimulationRunner;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.state.StateQuantity;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.pcm.action.QVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.reliability.entity.PcmRelSimulatedMeasurementSpec;
import org.palladiosimulator.simexp.pcm.reliability.job.PrepareBlackboardForReliabilityAnalysisJob;
import org.palladiosimulator.simexp.pcm.state.PcmSelfAdaptiveSystemState;
import org.palladiosimulator.simexp.pcm.util.ExperimentProvider;

import com.google.common.collect.Lists;

import de.uka.ipd.sdq.workflow.jobs.JobFailedException;
import de.uka.ipd.sdq.workflow.jobs.UserCanceledException;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

public class PcmRelExperienceSimulationRunner implements ExperienceSimulationRunner {

	private final UncertaintyBasedReliabilityPredictionConfig globalConfig;
	private final DiscreteUncertaintyStateSpace uncertaintyStateSpace;

	public PcmRelExperienceSimulationRunner(UncertaintyBasedReliabilityPredictionConfig globalConfig) {
		this.globalConfig = globalConfig;
		this.uncertaintyStateSpace = buildUncertaintyStateSpace(globalConfig.getUncertaintyRepository());
	}

	private DiscreteUncertaintyStateSpace buildUncertaintyStateSpace(UncertaintyRepository uncertaintyRepo) {
		var stateSpace = uncertaintyRepo.getUncertaintyInducedFailureTypes().stream()
				.flatMap(each -> DiscreteUncertaintyStateSpace.valueSpaceOf(each).stream()).collect(toList());
		return DiscreteUncertaintyStateSpace.of(stateSpace);
	}

	@Override
	public void initSimulationRun() {
		ExperimentProvider.get().initializeExperimentRunner();
		QVToReconfigurationManager.get().resetReconfigurator();
	}

	@Override
	public void simulate(SelfAdaptiveSystemState<?> sasState) {
		if (PcmSelfAdaptiveSystemState.class.isInstance(sasState) == false) {
			// TODO Exception handling
			throw new RuntimeException();
		}

		ReliabilityPredictionResult result = makePrediction((PcmSelfAdaptiveSystemState) sasState);
		retrieveAndSetStateQuantities(sasState.getQuantifiedState(), result);
	}

	private ReliabilityPredictionResult makePrediction(PcmSelfAdaptiveSystemState pcmState) {
		var config = deriveConfigFrom(pcmState);
		var uncertaintyStates = deriveUncertaintyStates(pcmState.getPerceivedEnvironmentalState());
		return UncertaintyBasedReliabilityPredictor.predictGiven(uncertaintyStates, config);
	}

	private UncertaintyBasedReliabilityPredictionConfig deriveConfigFrom(PcmSelfAdaptiveSystemState pcmState) {
		var blackboard = prepareBlackboardForAnalysis(pcmState);
		return UncertaintyBasedReliabilityPredictionConfig.newBuilder().rebuild(globalConfig, blackboard);
	}

	private MDSDBlackboard prepareBlackboardForAnalysis(PcmSelfAdaptiveSystemState pcmState) {
		var pcm = pcmState.getArchitecturalConfiguration().getConfiguration();
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
			var probOfSuccess = result.getProbabilityOfSuccess(spec.getUsageScenario());
			quantity.setMeasurement(probOfSuccess, spec);
		});
	}

	private List<UncertaintyState> deriveUncertaintyStates(PerceivableEnvironmentalState envState) {
		List<UncertaintyState> uncertaintyStateTuple = Lists.newArrayList();
		for (InputValue each : toInputs(envState)) {
			uncertaintyStateSpace.findStateInstantiating(each.variable.getInstantiatedTemplate()).ifPresent(s -> {
				UncertaintyState stateToAdd = s.newValuedStateWith(each.asCategorical());
				uncertaintyStateTuple.add(stateToAdd);
			});
		}
		return uncertaintyStateTuple;
	}

	private List<InputValue> toInputs(PerceivableEnvironmentalState envState) {
		Object sample = envState.getValue().getValue();
		if (List.class.isInstance(sample)) {
			List<?> inputs = List.class.cast(sample);
			if (inputs.isEmpty() == false) {
				if (InputValue.class.isInstance(inputs.get(0))) {
					return inputs.stream().map(InputValue.class::cast).collect(toList());
				}
			}
		}
		return Lists.newArrayList();
	}

	private List<PcmRelSimulatedMeasurementSpec> filterPcmRelMeasurementsSpec(StateQuantity quantity) {
		return quantity.getMeasurementSpecs().stream().filter(PcmRelSimulatedMeasurementSpec.class::isInstance)
				.map(PcmRelSimulatedMeasurementSpec.class::cast).collect(toList());
	}

}
