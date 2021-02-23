package org.palladiosimulator.simexp.pcm.examples.deltaiot.process;

import java.util.List;
import java.util.Optional;

import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork.InputValue;
import org.palladiosimulator.envdyn.environment.staticmodel.LocalProbabilisticNetwork;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.environment.DeltaIoTEnvironemtalDynamics;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTModelAccess;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismContext;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismSimulatedMeasurementSpec;
import org.palladiosimulator.simexp.pcm.state.PcmSelfAdaptiveSystemState;

//TODO implement XTend prism code generator...
public class PacketLossPrismFileUpdater extends DeltaIoTPrismFileUpdater {

	private static final String SNR_VARIABLE = "SignalToNoiseRatio";

	public PacketLossPrismFileUpdater(PrismSimulatedMeasurementSpec prismSpec) {
		super(prismSpec);
	}

	@Override
	public PrismContext apply(PcmSelfAdaptiveSystemState sasState) {
		PrismContext prismContext = new PrismContext(stringify(prismSpec.getModuleFile()),
				stringify(prismSpec.getPropertyFile()));
		substituteMoteActivations(prismContext, sasState);
		substituteSNR(prismContext, sasState);
		substituteDistributionFactor(prismContext, sasState);
		return prismContext;
	}

	private void substituteSNR(PrismContext prismContext, PcmSelfAdaptiveSystemState sasState) {
		ResourceEnvironment resEnv = sasState.getArchitecturalConfiguration().getConfiguration()
				.getResourceEnvironment();
		for (LinkingResource each : resEnv.getLinkingResources__ResourceEnvironment()) {
			var snrValue = resolveSNRInputValue(each, sasState.getPerceivedEnvironmentalState());
					//.ifPresent(value -> substitute(prismContext, each, value));
			if (snrValue.isPresent()) {
				var localNetwork = LocalProbabilisticNetwork.class
						.cast(snrValue.get().variable.eContainer());
				var wiVariable = DeltaIoTEnvironemtalDynamics.findWirelessInterferenceVariable(localNetwork);
				var wirelessInterference = DeltaIoTModelAccess.get().retrieveWirelessInterference(wiVariable, 
						sasState.getPerceivedEnvironmentalState());
				var newSnrValue = Double.valueOf(snrValue.get().asCategorical().get()) + wirelessInterference;
				substitute(prismContext, each, Double.toString(newSnrValue));
			}
		}
	}

	private Optional<InputValue> resolveSNRInputValue(LinkingResource linkingRes, PerceivableEnvironmentalState state) {
		List<InputValue> values = resolveInputValue(linkingRes, state);
		if (values.isEmpty()) {
			return Optional.empty();
		}
		return values.stream()
				.filter(each -> each.variable.getInstantiatedTemplate().getEntityName().equals(SNR_VARIABLE))
				.findFirst();
	}

}
