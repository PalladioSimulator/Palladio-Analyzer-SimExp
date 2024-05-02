package org.palladiosimulator.simexp.pcm.examples.deltaiot.environment;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.Predicate;

import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.envdyn.api.entity.bn.InputValue;
import org.palladiosimulator.envdyn.environment.staticmodel.GroundRandomVariable;
import org.palladiosimulator.envdyn.environment.staticmodel.LocalProbabilisticNetwork;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedInputValues;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedValue;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.DeltaIoTBaseEnvironemtalDynamics;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTModelAccess;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.models.PCMInstance;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public class DeltaIoTEnvironemtalDynamics<R> extends DeltaIoTBaseEnvironemtalDynamics<R> {
    private final static String SNR_TEMPLATE = "SignalToNoiseRatio";
    private final static String MA_TEMPLATE = "MoteActivation";

    public DeltaIoTEnvironemtalDynamics(DynamicBayesianNetwork<CategoricalValue> dbn,
            DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess) {
        super(dbn, modelAccess);
    }

    @Override
    protected PerceivedValue<List<InputValue<CategoricalValue>>> toPerceivedValue(
            List<InputValue<CategoricalValue>> sample) {
        return new PerceivedInputValues(sample) {

            @Override
            public String toString() {
                var wiValues = sample.stream()
                    .filter(each -> isWITemplate().test(each.getVariable()))
                    .collect(toList());
                var snrValues = sample.stream()
                    .filter(each -> isSNRTemplate(each.getVariable()))
                    .collect(toList());
                var maValues = sample.stream()
                    .filter(each -> isMATemplate().test(each.getVariable()))
                    .collect(toList());

                StringBuilder builder = new StringBuilder();
                wiValues.forEach(input -> builder.append(String.format("(Variable: %1s, Value: %2s),",
                        input.getVariable()
                            .getEntityName(),
                        input.getValue()
                            .toString())));
                snrValues.forEach(input -> builder.append(String.format("(Variable: %1s, Value: %2s),",
                        input.getVariable()
                            .getEntityName(),
                        input.getValue()
                            .toString())));
                maValues.forEach(input -> builder.append(String.format("(Variable: %1s, Value: %2s),",
                        input.getVariable()
                            .getEntityName(),
                        input.getValue()
                            .toString())));

                String stringValues = builder.toString();
                return String.format("Environmental states:%s", stringValues.substring(0, stringValues.length() - 1));
            }

        };
    }

    public static GroundRandomVariable findWirelessInterferenceVariable(LocalProbabilisticNetwork localNetwork) {
        return localNetwork.getGroundRandomVariables()
            .stream()
            .filter(isWITemplate())
            .findFirst()
            .orElseThrow(() -> new RuntimeException("There is no wireless interference template."));
    }

    public static Predicate<GroundRandomVariable> isWITemplate() {
        return isMATemplate().or(v -> isSNRTemplate(v))
            .negate();
    }

    public static Predicate<GroundRandomVariable> isMATemplate() {
        return v -> v.getInstantiatedTemplate()
            .getEntityName()
            .equals(MA_TEMPLATE);
    }

    public static boolean isSNRTemplate(GroundRandomVariable variable) {
        return variable.getInstantiatedTemplate()
            .getEntityName()
            .equals(SNR_TEMPLATE);
    }
}
