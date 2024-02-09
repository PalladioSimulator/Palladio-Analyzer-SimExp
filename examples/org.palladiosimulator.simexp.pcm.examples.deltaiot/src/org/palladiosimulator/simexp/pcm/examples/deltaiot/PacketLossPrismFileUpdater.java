package org.palladiosimulator.simexp.pcm.examples.deltaiot;

import java.util.List;
import java.util.Optional;

import org.palladiosimulator.envdyn.api.entity.bn.InputValue;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismContext;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismSimulatedMeasurementSpec;
import org.palladiosimulator.simexp.pcm.state.PcmSelfAdaptiveSystemState;

//TODO implement XTend prism code generator...
public class PacketLossPrismFileUpdater<A> extends DeltaIoTPrismFileUpdater<A> {

    private static final String SNR_VARIABLE = "SignalToNoiseRatio";

    public PacketLossPrismFileUpdater(PrismSimulatedMeasurementSpec prismSpec) {
        super(prismSpec);
    }

    @Override
    public PrismContext apply(PcmSelfAdaptiveSystemState<A, List<InputValue>> sasState) {
        PrismContext prismContext = new PrismContext(stringify(prismSpec.getModuleFile()),
                stringify(prismSpec.getPropertyFile()));
        substituteMoteActivations(prismContext, sasState);
        substituteSNR(prismContext, sasState);
        substituteDistributionFactor(prismContext, sasState);
        return prismContext;
    }

    private void substituteSNR(PrismContext prismContext, PcmSelfAdaptiveSystemState<A, List<InputValue>> sasState) {
        ResourceEnvironment resEnv = sasState.getArchitecturalConfiguration()
            .getConfiguration()
            .getResourceEnvironment();
        for (LinkingResource each : resEnv.getLinkingResources__ResourceEnvironment()) {
            resolveSNRInputValue(each, sasState.getPerceivedEnvironmentalState())
                .ifPresent(value -> substitute(prismContext, each, value));
        }
    }

    private Optional<InputValue> resolveSNRInputValue(LinkingResource linkingRes,
            PerceivableEnvironmentalState<List<InputValue>> state) {
        List<InputValue> values = resolveInputValue(linkingRes, state);
        if (values.isEmpty()) {
            return Optional.empty();
        }
        return values.stream()
            .filter(each -> each.getVariable().getInstantiatedTemplate()
                .getEntityName()
                .equals(SNR_VARIABLE))
            .findFirst();
    }

}
