package org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.palladiosimulator.pcm.seff.ProbabilisticBranchTransition;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurement;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactorValue;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPowerValue;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.DistributionFactorReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.TransmissionPowerReconfiguration;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismSimulatedMeasurementSpec;
import org.palladiosimulator.simexp.pcm.state.PcmSelfAdaptiveSystemState;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.uka.ipd.sdq.stoex.VariableReference;

public abstract class DeltaIoTReconfigurationStrategy implements Policy<Action<?>> {

	public static class DeltaIoTReconfigurationStrategyBuilder {

		private final DeltaIoTReconfigurationStrategy strategy;

		protected DeltaIoTReconfigurationStrategyBuilder(DeltaIoTReconfigurationStrategy strategy) {
			this.strategy = strategy;
		}

		public DeltaIoTReconfigurationStrategyBuilder withReconfigurationParams(
				DeltaIoTReconfigurationParamRepository params) {
			strategy.reconfParamsRepo = params;
			return this;
		}

		public DeltaIoTReconfigurationStrategyBuilder andPacketLossSpec(PrismSimulatedMeasurementSpec packetLossSpec) {
			strategy.packetLossSpec = packetLossSpec;
			return this;
		}

		public DeltaIoTReconfigurationStrategyBuilder andEnergyConsumptionSpec(
				PrismSimulatedMeasurementSpec energyConsumptionSpec) {
			strategy.energyConsumptionSpec = energyConsumptionSpec;
			return this;
		}

		public DeltaIoTReconfigurationStrategy build() {
			requireNonNull(strategy.reconfParamsRepo, "Reconfiguration params are missing");
			requireNonNull(strategy.packetLossSpec, "Packet loss spec is missing");
			requireNonNull(strategy.energyConsumptionSpec, "Energy consumption spec is missing");

			return strategy;
		}

	}

	private final static String BRANCH_10_TO_6 = "TransmitToMote6";
	private final static String BRANCH_10_TO_5 = "TransmitToMote5";
	private final static String BRANCH_12_TO_3 = "TransmitToMote3";
	private final static String BRANCH_12_TO_7 = "TransmitToMote7";
	private final static String BRANCH_7_TO_3 = "TransmitFrom7ToMote3";
	private final static String BRANCH_7_TO_2 = "TransmitToMote2";
	private final static double DISTRIBUTION_FACTOR_INCREMENT = 0.1;
	private final static int TRANSMISSION_POWER_INCREMENT = 1;

	protected final static String VARIABLE_REFERENCE_3_TO_1 = "TransmissionPower3to1";
	protected final static String VARIABLE_REFERENCE_8_TO_1 = "TransmissionPower8to1";
	protected final static String VARIABLE_REFERENCE_9_TO_1 = "TransmissionPower9to1";
	protected final static String VARIABLE_REFERENCE_4_TO_1 = "TransmissionPower4to1";
	protected final static String VARIABLE_REFERENCE_10_TO_6 = "TransmissionPower10to6";
	protected final static String VARIABLE_REFERENCE_10_TO_5 = "TransmissionPower10to5";
	protected final static String VARIABLE_REFERENCE_5_TO_9 = "TransmissionPower5to9";
	protected final static String VARIABLE_REFERENCE_6_TO_4 = "TransmissionPower6to4";
	protected final static String VARIABLE_REFERENCE_7_TO_3 = "TransmissionPower7to3";
	protected final static String VARIABLE_REFERENCE_7_TO_2 = "TransmissionPower7to2";
	protected final static String VARIABLE_REFERENCE_2_TO_4 = "TransmissionPower2to4";
	protected final static String VARIABLE_REFERENCE_11_TO_7 = "TransmissionPower11to7";
	protected final static String VARIABLE_REFERENCE_13_TO_11 = "TransmissionPower13to11";
	protected final static String VARIABLE_REFERENCE_12_TO_7 = "TransmissionPower12to7";
	protected final static String VARIABLE_REFERENCE_12_TO_3 = "TransmissionPower12to3";
	protected final static String VARIABLE_REFERENCE_14_TO_12 = "TransmissionPower14to12";
	protected final static String VARIABLE_REFERENCE_15_TO_12 = "TransmissionPower15to12";
	protected final static List<String> VARIABLE_REFERENCES = Lists.newArrayList();
	static {
		VARIABLE_REFERENCES.add(VARIABLE_REFERENCE_3_TO_1);
		VARIABLE_REFERENCES.add(VARIABLE_REFERENCE_8_TO_1);
		VARIABLE_REFERENCES.add(VARIABLE_REFERENCE_9_TO_1);
		VARIABLE_REFERENCES.add(VARIABLE_REFERENCE_4_TO_1);
		VARIABLE_REFERENCES.add(VARIABLE_REFERENCE_10_TO_6);
		VARIABLE_REFERENCES.add(VARIABLE_REFERENCE_10_TO_5);
		VARIABLE_REFERENCES.add(VARIABLE_REFERENCE_5_TO_9);
		VARIABLE_REFERENCES.add(VARIABLE_REFERENCE_6_TO_4);
		VARIABLE_REFERENCES.add(VARIABLE_REFERENCE_7_TO_3);
		VARIABLE_REFERENCES.add(VARIABLE_REFERENCE_7_TO_2);
		VARIABLE_REFERENCES.add(VARIABLE_REFERENCE_2_TO_4);
		VARIABLE_REFERENCES.add(VARIABLE_REFERENCE_11_TO_7);
		VARIABLE_REFERENCES.add(VARIABLE_REFERENCE_13_TO_11);
		VARIABLE_REFERENCES.add(VARIABLE_REFERENCE_12_TO_7);
		VARIABLE_REFERENCES.add(VARIABLE_REFERENCE_12_TO_3);
		VARIABLE_REFERENCES.add(VARIABLE_REFERENCE_14_TO_12);
		VARIABLE_REFERENCES.add(VARIABLE_REFERENCE_15_TO_12);
	}

	public final static Threshold LOWER_PACKET_LOSS = Threshold.lessThan(0.1);
	public final static Threshold LOWER_ENERGY_CONSUMPTION = Threshold.lessThan(0.4);

	protected DeltaIoTReconfigurationParamRepository reconfParamsRepo;
	protected PrismSimulatedMeasurementSpec packetLossSpec;
	protected PrismSimulatedMeasurementSpec energyConsumptionSpec;

	protected DeltaIoTReconfigurationStrategy() {

	}

	@Override
	public Action<?> select(State source, Set<Action<?>> options) {
		retrieveDistributionFactorReconfiguration(options).setDistributionFactorValuesToDefaults();		
		
		PcmSelfAdaptiveSystemState state = PcmSelfAdaptiveSystemState.class.cast(source);

		SimulatedMeasurement packetLoss = state.getQuantifiedState().findMeasurementWith(packetLossSpec)
				.orElseThrow(() -> new RuntimeException(
						String.format("There is no simulated measurement for spec %s", packetLossSpec.getName())));
		if (packetLossIsViolated(packetLoss)) {
			return handlePacketLoss(state, packetLoss, options);
		}

		SimulatedMeasurement energyConsumtption = state.getQuantifiedState().findMeasurementWith(energyConsumptionSpec)
				.orElseThrow(() -> new RuntimeException(String.format("There is no simulated measurement for spec %s",
						energyConsumptionSpec.getName())));
		if (energyConsumptionIsViolated(energyConsumtption)) {
			return handleEnergyConsumption(state, energyConsumtption, options);
		}

		return QVToReconfiguration.empty();
	}

	private boolean packetLossIsViolated(SimulatedMeasurement packetLoss) {
		return LOWER_PACKET_LOSS.isNotSatisfied(packetLoss.getValue());
	}

	private boolean energyConsumptionIsViolated(SimulatedMeasurement energyConsumtption) {
		return LOWER_ENERGY_CONSUMPTION.isNotSatisfied(energyConsumtption.getValue());
	}

	protected abstract Action<?> handlePacketLoss(PcmSelfAdaptiveSystemState state, SimulatedMeasurement packetLoss,
			Set<Action<?>> options);

	protected abstract Action<?> handleEnergyConsumption(PcmSelfAdaptiveSystemState state,
			SimulatedMeasurement energyConsumtption, Set<Action<?>> options);

	protected DistributionFactorReconfiguration retrieveDistributionFactorReconfiguration(Set<Action<?>> options) {
		return retrieveReconfiguration(DistributionFactorReconfiguration.class, options)
				.orElseThrow(() -> new RuntimeException("There is no distribution factor reconfiguration registered."));
	}

	protected TransmissionPowerReconfiguration retrieveTransmissionPowerReconfiguration(Set<Action<?>> options) {
		return retrieveReconfiguration(TransmissionPowerReconfiguration.class, options)
				.orElseThrow(() -> new RuntimeException("There is no distribution factor reconfiguration registered."));
	}

	private <T extends QVToReconfiguration> Optional<T> retrieveReconfiguration(Class<T> reconfClass,
			Set<Action<?>> options) {
		return options.stream().filter(reconfClass::isInstance).map(reconfClass::cast).findFirst();
	}

	protected boolean increaseDistributionFactor(ProbabilisticBranchTransition branchToIncrease,
			ProbabilisticBranchTransition branchToDecrease, DistributionFactorReconfiguration reconf) {
		Map<ProbabilisticBranchTransition, Double> factors = Maps.newHashMap();
		factors.put(findBranchWith(branchToDecrease.getEntityName()), DISTRIBUTION_FACTOR_INCREMENT * (-1));
		factors.put(findBranchWith(branchToIncrease.getEntityName()), DISTRIBUTION_FACTOR_INCREMENT);
		if (DistributionFactorReconfiguration.isValid(factors)) {
			reconf.adjustDistributionFactors(factors);
			return true;
		}
		return false;
	}

	protected boolean increaseDistributionFactorOfMote7(DistributionFactorReconfiguration reconf) {
		Map<ProbabilisticBranchTransition, Double> factors = Maps.newHashMap();
		factors.put(findBranchWith(BRANCH_7_TO_3), DISTRIBUTION_FACTOR_INCREMENT * (-1));
		factors.put(findBranchWith(BRANCH_7_TO_2), DISTRIBUTION_FACTOR_INCREMENT);
		if (Boolean.logicalAnd(reconf.canBeIncreased(factors), DistributionFactorReconfiguration.isValid(factors))) {
			reconf.adjustDistributionFactors(factors);
			return true;
		}
		return false;
	}

	protected boolean decreaseDistributionFactorOfMote7(DistributionFactorReconfiguration reconf) {
		Map<ProbabilisticBranchTransition, Double> factors = Maps.newHashMap();
		factors.put(findBranchWith(BRANCH_7_TO_3), DISTRIBUTION_FACTOR_INCREMENT);
		factors.put(findBranchWith(BRANCH_7_TO_2), DISTRIBUTION_FACTOR_INCREMENT * (-1));
		if (Boolean.logicalAnd(reconf.canBeDecreased(factors), DistributionFactorReconfiguration.isValid(factors))) {
			reconf.adjustDistributionFactors(factors);
			return true;
		}
		return false;
	}

	protected boolean increaseDistributionFactorOfMote10(DistributionFactorReconfiguration reconf) {
		Map<ProbabilisticBranchTransition, Double> factors = Maps.newHashMap();
		factors.put(findBranchWith(BRANCH_10_TO_6), DISTRIBUTION_FACTOR_INCREMENT * (-1));
		factors.put(findBranchWith(BRANCH_10_TO_5), DISTRIBUTION_FACTOR_INCREMENT);
		if (Boolean.logicalAnd(reconf.canBeIncreased(factors), DistributionFactorReconfiguration.isValid(factors))) {
			reconf.adjustDistributionFactors(factors);
			return true;
		}
		return false;
	}

	protected boolean decreaseDistributionFactorOfMote10(DistributionFactorReconfiguration reconf) {
		Map<ProbabilisticBranchTransition, Double> factors = Maps.newHashMap();
		factors.put(findBranchWith(BRANCH_10_TO_6), DISTRIBUTION_FACTOR_INCREMENT);
		factors.put(findBranchWith(BRANCH_10_TO_5), DISTRIBUTION_FACTOR_INCREMENT * (-1));
		if (Boolean.logicalAnd(reconf.canBeDecreased(factors), DistributionFactorReconfiguration.isValid(factors))) {
			reconf.adjustDistributionFactors(factors);
			return true;
		}
		return false;
	}

	protected boolean increaseDistributionFactorOfMote12(DistributionFactorReconfiguration reconf) {
		Map<ProbabilisticBranchTransition, Double> factors = Maps.newHashMap();
		factors.put(findBranchWith(BRANCH_12_TO_3), DISTRIBUTION_FACTOR_INCREMENT * (-1));
		factors.put(findBranchWith(BRANCH_12_TO_7), DISTRIBUTION_FACTOR_INCREMENT);
		if (Boolean.logicalAnd(reconf.canBeIncreased(factors), DistributionFactorReconfiguration.isValid(factors))) {
			reconf.adjustDistributionFactors(factors);
			return true;
		}
		return false;
	}

	protected boolean decreaseDistributionFactorOfMote12(DistributionFactorReconfiguration reconf) {
		Map<ProbabilisticBranchTransition, Double> factors = Maps.newHashMap();
		factors.put(findBranchWith(BRANCH_12_TO_3), DISTRIBUTION_FACTOR_INCREMENT);
		factors.put(findBranchWith(BRANCH_12_TO_7), DISTRIBUTION_FACTOR_INCREMENT * (-1));
		if (Boolean.logicalAnd(reconf.canBeDecreased(factors), DistributionFactorReconfiguration.isValid(factors))) {
			reconf.adjustDistributionFactors(factors);
			return true;
		}
		return false;
	}

	protected boolean increaseTransmissionPower(String varRef, TransmissionPowerReconfiguration reconf) {
		Map<VariableReference, Integer> powerSettings = Maps.newHashMap();
		powerSettings.put(findVariableReferenceWith(varRef), TRANSMISSION_POWER_INCREMENT);
		if (reconf.canBeAdjusted(powerSettings)) {
			reconf.adjustPowerSetting(powerSettings);
			return true;
		}
		return false;
	}

	protected boolean decreaseTransmissionPower(String varRef, TransmissionPowerReconfiguration reconf) {
		Map<VariableReference, Integer> powerSettings = Maps.newHashMap();
		powerSettings.put(findVariableReferenceWith(varRef), TRANSMISSION_POWER_INCREMENT * (-1));
		if (reconf.canBeAdjusted(powerSettings)) {
			reconf.adjustPowerSetting(powerSettings);
			return true;
		}
		return false;
	}

	private ProbabilisticBranchTransition findBranchWith(String branchName) {
		return reconfParamsRepo.getDistributionFactors().stream().flatMap(each -> each.getFactorValues().stream())
				.filter(factorValuesWithBranch(branchName)).map(DistributionFactorValue::getAppliedBranch).findFirst()
				.orElseThrow(() -> new RuntimeException(String.format("There is no branch with name %s", branchName)));
	}

	private Predicate<DistributionFactorValue> factorValuesWithBranch(String branchName) {
		return value -> value.getAppliedBranch().getEntityName().equals(branchName);
	}

	private VariableReference findVariableReferenceWith(String referenceName) {
		return reconfParamsRepo.getTransmissionPower().stream()
				.flatMap(each -> each.getTransmissionPowerValues().stream())
				.filter(powerValuesWithVariable(referenceName)).map(TransmissionPowerValue::getVariableRef).findFirst()
				.orElseThrow(() -> new RuntimeException(
						String.format("There is no variable reference with name %s", referenceName)));
	}

	private Predicate<TransmissionPowerValue> powerValuesWithVariable(String referenceName) {
		return value -> value.getVariableRef().getReferenceName().equals(referenceName);
	}

}
