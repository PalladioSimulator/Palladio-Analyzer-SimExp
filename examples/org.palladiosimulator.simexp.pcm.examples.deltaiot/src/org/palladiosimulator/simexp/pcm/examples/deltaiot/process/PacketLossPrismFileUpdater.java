package org.palladiosimulator.simexp.pcm.examples.deltaiot.process;

import java.util.List;
import java.util.Optional;

import org.palladiosimulator.envdyn.api.entity.bn.InputValue;
import org.palladiosimulator.envdyn.environment.staticmodel.LocalProbabilisticNetwork;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.DeltaIoTPartiallyEnvDynamics;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTModelAccess;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismContext;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismSimulatedMeasurementSpec;
import org.palladiosimulator.simexp.pcm.state.PcmSelfAdaptiveSystemState;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.core.models.PCMInstance;

import tools.mdsd.probdist.api.entity.CategoricalValue;

//TODO implement XTend prism code generator...
public class PacketLossPrismFileUpdater<A> extends DeltaIoTPrismFileUpdater<A> {

    private static final String SNR_VARIABLE = "SignalToNoiseRatio";

    private final DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess;

    public PacketLossPrismFileUpdater(PrismSimulatedMeasurementSpec prismSpec,
            DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess) {
        super(prismSpec);
        this.modelAccess = modelAccess;
    }

    @Override
    public PrismContext apply(PcmSelfAdaptiveSystemState<A, List<InputValue<CategoricalValue>>> sasState) {
        PrismContext prismContext = new PrismContext("PacketLoss", stringify(prismSpec.getModuleFile()),
                stringify(prismSpec.getPropertyFile()));
        substituteMoteActivations(prismContext, sasState);
        substituteSNR(prismContext, sasState);
        substituteDistributionFactor(prismContext, sasState);
        return prismContext;
    }

    private void substituteSNR(PrismContext prismContext,
            PcmSelfAdaptiveSystemState<A, List<InputValue<CategoricalValue>>> sasState) {
        ResourceEnvironment resEnv = sasState.getArchitecturalConfiguration()
            .getConfiguration()
            .getResourceEnvironment();
        for (LinkingResource each : resEnv.getLinkingResources__ResourceEnvironment()) {
            Optional<InputValue<CategoricalValue>> snrValue = resolveSNRInputValue(each,
                    sasState.getPerceivedEnvironmentalState());
            // .ifPresent(value -> substitute(prismContext, each, value));
            if (snrValue.isPresent()) {
                InputValue<CategoricalValue> inputValue = snrValue.get();
                var localNetwork = LocalProbabilisticNetwork.class.cast(inputValue.getVariable()
                    .eContainer());
                var wiVariable = DeltaIoTPartiallyEnvDynamics.findWirelessInterferenceVariable(localNetwork);
                var wirelessInterference = modelAccess.retrieveWirelessInterference(wiVariable,
                        sasState.getPerceivedEnvironmentalState());
                var newSnrValue = Double.valueOf(inputValue.getValue()
                    .get()) + wirelessInterference;
                substitute(prismContext, each, Double.toString(newSnrValue));
            }
        }
    }

    private Optional<InputValue<CategoricalValue>> resolveSNRInputValue(LinkingResource linkingRes,
            PerceivableEnvironmentalState<List<InputValue<CategoricalValue>>> state) {
        List<InputValue<CategoricalValue>> values = resolveInputValue(linkingRes, state);
        if (values.isEmpty()) {
            return Optional.empty();
        }
        return values.stream()
            .filter(each -> each.getVariable()
                .getInstantiatedTemplate()
                .getEntityName()
                .equals(SNR_VARIABLE))
            .findFirst();
    }

}
