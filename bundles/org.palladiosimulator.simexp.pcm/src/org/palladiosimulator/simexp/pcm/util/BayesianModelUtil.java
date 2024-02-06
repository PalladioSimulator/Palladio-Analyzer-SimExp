package org.palladiosimulator.simexp.pcm.util;

import java.util.List;
import java.util.Map;

import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork.InputValue;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedValue;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedSelectedInputValues;

public class BayesianModelUtil {

    public static PerceivedValue<List<InputValue>> asPerceivedValue(List<InputValue> sample,
            Map<String, String> attributeMap) {
        PerceivedSelectedInputValues perceivedValue = new PerceivedSelectedInputValues(sample, attributeMap);
        return perceivedValue;
    }

}
