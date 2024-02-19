package org.palladiosimulator.simexp.pcm.util;

import java.util.List;
import java.util.Map;

import org.palladiosimulator.envdyn.api.entity.bn.InputValue;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedSelectedInputValues;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedValue;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public class BayesianModelUtil {

    public static PerceivedValue<List<InputValue<CategoricalValue>>> asPerceivedValue(
            List<InputValue<CategoricalValue>> sample, Map<String, String> attributeMap) {
        PerceivedSelectedInputValues perceivedValue = new PerceivedSelectedInputValues(sample, attributeMap);
        return perceivedValue;
    }

}
