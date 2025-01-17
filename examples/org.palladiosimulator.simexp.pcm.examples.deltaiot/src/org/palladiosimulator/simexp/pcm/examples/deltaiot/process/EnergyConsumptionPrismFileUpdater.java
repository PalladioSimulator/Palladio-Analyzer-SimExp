package org.palladiosimulator.simexp.pcm.examples.deltaiot.process;

import java.util.List;

import org.palladiosimulator.envdyn.api.entity.bn.InputValue;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.parameter.VariableUsage;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismContext;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismSimulatedMeasurementSpec;
import org.palladiosimulator.simexp.pcm.state.PcmSelfAdaptiveSystemState;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public class EnergyConsumptionPrismFileUpdater<A> extends DeltaIoTPrismFileUpdater<A> {

    public EnergyConsumptionPrismFileUpdater(PrismSimulatedMeasurementSpec prismSpec) {
        super(prismSpec);
    }

    @Override
    public PrismContext apply(PcmSelfAdaptiveSystemState<A, List<InputValue<CategoricalValue>>> sasState) {
        PrismContext prismContext = new PrismContext("EnergyConsumption", stringify(prismSpec.getModuleFile()),
                stringify(prismSpec.getPropertyFile()));
        substituteMoteActivations(prismContext, sasState);
        substituteTransmissionPower(prismContext, sasState);
        substituteDistributionFactor(prismContext, sasState, f -> Double.valueOf(f * 100)
            .intValue());
        return prismContext;
    }

    private void substituteTransmissionPower(PrismContext prismContext,
            PcmSelfAdaptiveSystemState<A, List<InputValue<CategoricalValue>>> sasState) {
        System system = sasState.getArchitecturalConfiguration()
            .getConfiguration()
            .getSystem();
        for (AssemblyContext each : system.getAssemblyContexts__ComposedStructure()) {
            if (isInstantiatedSensorMote(each, sasState)) {
                substituteTransmissionPower(prismContext, each.getConfigParameterUsages__AssemblyContext());
            }
        }
    }

    private void substituteTransmissionPower(PrismContext prismContext, List<VariableUsage> configParameter) {
        configParameter.forEach(param -> substituteTransmissionPower(prismContext, param));
    }

    private void substituteTransmissionPower(PrismContext prismContext, VariableUsage configParameter) {
        String unresolvedSymbol = configParameter.getNamedReference__VariableUsage()
            .getReferenceName();
        String value = configParameter.getVariableCharacterisation_VariableUsage()
            .get(0)
            .getSpecification_VariableCharacterisation()
            .getSpecification();
        resolveAndSubstitute(prismContext, unresolvedSymbol, value);
    }

    private boolean isInstantiatedSensorMote(AssemblyContext each,
            PcmSelfAdaptiveSystemState<A, List<InputValue<CategoricalValue>>> sasState) {
        return resolveInputValue(each, sasState.getPerceivedEnvironmentalState()).isEmpty() == false;
    }

}
