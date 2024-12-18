package org.palladiosimulator.simexp.pcm.examples.deltaiot.environment;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;

import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.envdyn.api.entity.bn.InputValue;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedInputValues;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedValue;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.DeltaIoTBaseEnvironemtalDynamics;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTModelAccess;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.models.PCMInstance;

import tools.mdsd.probdist.api.entity.CategoricalValue;
import tools.mdsd.probdist.api.random.ISeedProvider;

public class DeltaIoTEnvironemtalDynamics<R> extends DeltaIoTBaseEnvironemtalDynamics<R> {
    public DeltaIoTEnvironemtalDynamics(DynamicBayesianNetwork<CategoricalValue> dbn,
            DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess, Optional<ISeedProvider> seedProvider) {
        super(dbn, modelAccess, seedProvider);
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
}
