package org.palladiosimulator.simexp.pcm.examples.utils;

import static java.util.stream.Collectors.toList;

import java.util.List;
import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork.InputValue;
import com.google.common.collect.Lists;

public final class EnvironmentalDynamicsUtils {
    
    public static List<InputValue> toInputs(Object sample) {
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


}
