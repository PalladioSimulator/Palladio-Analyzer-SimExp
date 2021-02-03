package org.palladiosimulator.simexp.pcm.examples.deltaiot.process;

import static org.palladiosimulator.simexp.pcm.examples.deltaiot.DeltaIoTEnvironemtalDynamics.toInputs;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork.InputValue;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.seff.ProbabilisticBranchTransition;
import org.palladiosimulator.pcm.seff.ServiceEffectSpecification;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismContext;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismSimulatedMeasurementSpec;
import org.palladiosimulator.simexp.pcm.prism.generator.PrismFileUpdateGenerator;
import org.palladiosimulator.simexp.pcm.state.PcmSelfAdaptiveSystemState;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public abstract class DeltaIoTPrismFileUpdater extends PrismFileUpdateGenerator.PrismFileUpdater {

	private static class DeltaIoTPrismReplacementSet {

		private final static Map<String, String> PRISM_SYMBOL_REPLACEMENTS = Maps.newHashMap();
		static {
			// SNR symbols
			PRISM_SYMBOL_REPLACEMENTS.put("Unicast13to11", "%SNR_13_11");
			PRISM_SYMBOL_REPLACEMENTS.put("Unicast14to12", "%SNR_14_12");
			PRISM_SYMBOL_REPLACEMENTS.put("Unicast15to12", "%SNR_15_12");
			PRISM_SYMBOL_REPLACEMENTS.put("Unicast11to7", "%SNR_11_7");
			PRISM_SYMBOL_REPLACEMENTS.put("Unicast12to7", "%SNR_12_7");
			PRISM_SYMBOL_REPLACEMENTS.put("Unicast12to3", "%SNR_12_3");
			PRISM_SYMBOL_REPLACEMENTS.put("Unicast7to3", "%SNR_7_3");
			PRISM_SYMBOL_REPLACEMENTS.put("Unicast7to2", "%SNR_7_2");
			PRISM_SYMBOL_REPLACEMENTS.put("Unicast2to4", "%SNR_2_4");
			PRISM_SYMBOL_REPLACEMENTS.put("Unicast3to1", "%SNR_3_1");
			PRISM_SYMBOL_REPLACEMENTS.put("Unicast8to1", "%SNR_8_1");
			PRISM_SYMBOL_REPLACEMENTS.put("Unicast4to1", "%SNR_4_1");
			PRISM_SYMBOL_REPLACEMENTS.put("Unicast9to1", "%SNR_9_1");
			PRISM_SYMBOL_REPLACEMENTS.put("Unicast6to4", "%SNR_6_4");
			PRISM_SYMBOL_REPLACEMENTS.put("Unicast10to6", "%SNR_10_6");
			PRISM_SYMBOL_REPLACEMENTS.put("Unicast10to5", "%SNR_10_5");
			PRISM_SYMBOL_REPLACEMENTS.put("Unicast5to9", "%SNR_5_9");
			// Mote activation symbols
			PRISM_SYMBOL_REPLACEMENTS.put("PassiveInfraredSensor2", "%LoadMote2");
			PRISM_SYMBOL_REPLACEMENTS.put("TemperatureSensor3", "%LoadMote3");
			PRISM_SYMBOL_REPLACEMENTS.put("PassiveInfraredSensor4", "%LoadMote4");
			PRISM_SYMBOL_REPLACEMENTS.put("RFIDSensor5", "%LoadMote5");
			PRISM_SYMBOL_REPLACEMENTS.put("TemperatureSensor6", "%LoadMote6");
			PRISM_SYMBOL_REPLACEMENTS.put("RFIDSensor7", "%LoadMote7");
			PRISM_SYMBOL_REPLACEMENTS.put("PassiveInfraredSensor8", "%LoadMote8");
			PRISM_SYMBOL_REPLACEMENTS.put("TemperatureSensor9", "%LoadMote9");
			PRISM_SYMBOL_REPLACEMENTS.put("PassiveInfraredSensor10", "%LoadMote10");
			PRISM_SYMBOL_REPLACEMENTS.put("RFIDSensor11", "%LoadMote11");
			PRISM_SYMBOL_REPLACEMENTS.put("RFIDSensor12", "%LoadMote12");
			PRISM_SYMBOL_REPLACEMENTS.put("PassiveInfraredSensor13", "%LoadMote13");
			PRISM_SYMBOL_REPLACEMENTS.put("PassiveInfraredSensor14", "%LoadMote14");
			PRISM_SYMBOL_REPLACEMENTS.put("TemperatureSensor15", "%LoadMote15");
			// Distribution factor symbols
			PRISM_SYMBOL_REPLACEMENTS.put("DualTransmitter3_TransmitToMote2", "%DfMote7_2");
			PRISM_SYMBOL_REPLACEMENTS.put("DualTransmitter3_TransmitFrom7ToMote3", "%DfMote7_3");
			PRISM_SYMBOL_REPLACEMENTS.put("DualTransmitter1_TransmitToMote6", "%DfMote10_6");
			PRISM_SYMBOL_REPLACEMENTS.put("DualTransmitter1_TransmitToMote5", "%DfMote10_5");
			PRISM_SYMBOL_REPLACEMENTS.put("DualTransmitter2_TransmitToMote7", "%DfMote12_7");
			PRISM_SYMBOL_REPLACEMENTS.put("DualTransmitter2_TransmitToMote3", "%DfMote12_3");
			// Transmission power symbols
			PRISM_SYMBOL_REPLACEMENTS.put("TransmissionPower3to1", "%TP_3_1");
			PRISM_SYMBOL_REPLACEMENTS.put("TransmissionPower8to1", "%TP_8_1");
			PRISM_SYMBOL_REPLACEMENTS.put("TransmissionPower9to1", "%TP_9_1");
			PRISM_SYMBOL_REPLACEMENTS.put("TransmissionPower4to1", "%TP_4_1");
			PRISM_SYMBOL_REPLACEMENTS.put("TransmissionPower10to6", "%TP_10_6");
			PRISM_SYMBOL_REPLACEMENTS.put("TransmissionPower10to5", "%TP_10_5");
			PRISM_SYMBOL_REPLACEMENTS.put("TransmissionPower5to9", "%TP_5_9");
			PRISM_SYMBOL_REPLACEMENTS.put("TransmissionPower6to4", "%TP_6_4");
			PRISM_SYMBOL_REPLACEMENTS.put("TransmissionPower7to3", "%TP_7_3");
			PRISM_SYMBOL_REPLACEMENTS.put("TransmissionPower7to2", "%TP_7_2");
			PRISM_SYMBOL_REPLACEMENTS.put("TransmissionPower2to4", "%TP_2_4");
			PRISM_SYMBOL_REPLACEMENTS.put("TransmissionPower11to7", "%TP_11_6");
			PRISM_SYMBOL_REPLACEMENTS.put("TransmissionPower13to11", "%TP_13_11");
			PRISM_SYMBOL_REPLACEMENTS.put("TransmissionPower12to7", "%TP_12_7");
			PRISM_SYMBOL_REPLACEMENTS.put("TransmissionPower12to3", "%TP_12_3");
			PRISM_SYMBOL_REPLACEMENTS.put("TransmissionPower14to12", "%TP_14_12");
			PRISM_SYMBOL_REPLACEMENTS.put("TransmissionPower15to12", "%TP_15_12");
		}

		public static Set<String> getPlaceholders() {
			return PRISM_SYMBOL_REPLACEMENTS.keySet();
		}

		public static String getReplacement(String placeholder) {
			return PRISM_SYMBOL_REPLACEMENTS.get(placeholder);
		}

	}

	private static class DistributionFactorResolver {

		private final BasicComponent transmitter;

		public DistributionFactorResolver(RepositoryComponent transmitter) {
			this.transmitter = BasicComponent.class.cast(transmitter);
		}

		public Optional<ProbabilisticBranchTransition> resolveBranchGiven(String branchIdentifier) {
			for (ServiceEffectSpecification each : transmitter.getServiceEffectSpecifications__BasicComponent()) {
				ProbabilisticBranchTransition result = resolveBranchGiven(each, branchIdentifier);
				if (result != null) {
					return Optional.of(result);
				}
			}
			return Optional.empty();
		}

		private ProbabilisticBranchTransition resolveBranchGiven(ServiceEffectSpecification seff,
				String branchIdentifier) {
			TreeIterator<EObject> iterator = seff.eAllContents();
			while (iterator.hasNext()) {
				EObject next = iterator.next();
				if (ProbabilisticBranchTransition.class.isInstance(next)) {
					ProbabilisticBranchTransition candidate = ProbabilisticBranchTransition.class.cast(next);
					if (branchIdentifier.endsWith(candidate.getEntityName())) {
						return candidate;
					}
				}
			}
			return null;
		}
	}

	public DeltaIoTPrismFileUpdater(PrismSimulatedMeasurementSpec prismSpec) {
		super(prismSpec);
	}

	protected void substituteDistributionFactor(PrismContext prismContext, PcmSelfAdaptiveSystemState sasState) {
		substituteDistributionFactor(prismContext, sasState, null);
	}

	protected void substituteDistributionFactor(PrismContext prismContext, PcmSelfAdaptiveSystemState sasState,
			Function<Double, Integer> factorNormalization) {
		sasState.getArchitecturalConfiguration().getConfiguration().getRepositories().forEach(
				repo -> substituteDistributionFactor(prismContext, repo, Optional.ofNullable(factorNormalization)));
	}

	private void substituteDistributionFactor(PrismContext prismContext, Repository repo,
			Optional<Function<Double, Integer>> factorNormalization) {
		for (RepositoryComponent eachComp : repo.getComponents__Repository()) {
			for (String each : DeltaIoTPrismReplacementSet.getPlaceholders()) {
				if (each.startsWith(eachComp.getEntityName())) {
					Optional<ProbabilisticBranchTransition> branch = new DistributionFactorResolver(eachComp)
							.resolveBranchGiven(each);
					if (branch.isEmpty()) {
						// TODO Exception handling
						throw new RuntimeException("No distribution factor could be resolved.");
					}

					// String replacement = DeltaIoTPrismReplacementSet.getReplacement(each);
					Double branchProb = branch.get().getBranchProbability();
					String normalizedBranchProb = Double.toString(branch.get().getBranchProbability());
					if (factorNormalization.isPresent()) {
						normalizedBranchProb = Integer.toString(factorNormalization.get().apply(branchProb));
					}
					resolveAndSubstitute(prismContext, each, normalizedBranchProb);
				}
			}
		}
	}

	protected void substituteMoteActivations(PrismContext prismContext, PcmSelfAdaptiveSystemState sasState) {
		System system = sasState.getArchitecturalConfiguration().getConfiguration().getSystem();
		for (AssemblyContext each : system.getAssemblyContexts__ComposedStructure()) {
			resolveMAInputValue(each, sasState.getPerceivedEnvironmentalState())
					.ifPresent(value -> substitute(prismContext, each, value));
		}
	}

	private Optional<InputValue> resolveMAInputValue(AssemblyContext context, PerceivableEnvironmentalState state) {
		List<InputValue> values = resolveInputValue(context, state);
		
		if (values.size() != 1) {
			return Optional.empty();
		}
		return Optional.of(values.get(0));
	}

	protected <T extends Entity> List<InputValue> resolveInputValue(T appliedElement,
			PerceivableEnvironmentalState perceivedEnvironmentalState) {
		List<InputValue> values = Lists.newArrayList();
		for (InputValue eachInput : toInputs(perceivedEnvironmentalState.getValue().getValue())) {
			for (EObject eachApplied : eachInput.variable.getAppliedObjects()) {
				if (appliedElement.getClass().isInstance(eachApplied)) {
					if (areEqual(Entity.class.cast(eachApplied), appliedElement)) {
						values.add(eachInput);
					}
				}
			}
		}
		return values;
	}

	private boolean areEqual(Entity first, Entity second) {
		return first.getId().equals(second.getId());
	}

	protected <T extends Entity> void substitute(PrismContext prismContext, T element, InputValue value) {
		if (element.getEntityName().equals("Gateway1Instance")) {
			return;
		}
		resolveAndSubstitute(prismContext, element.getEntityName(), value.value.toString());
	}

	protected void resolveAndSubstitute(PrismContext prismContext, String unresolvedSymbol, String value) {
		String symbolToReplace = DeltaIoTPrismReplacementSet.getReplacement(unresolvedSymbol);
		if (symbolToReplace == null) {
			// TODO exception handling
			throw new RuntimeException("");
		}
		prismContext.moduleFileContent = prismContext.moduleFileContent.replaceAll(symbolToReplace, value);
	}

}
