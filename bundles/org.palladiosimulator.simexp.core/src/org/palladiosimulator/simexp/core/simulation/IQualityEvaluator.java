package org.palladiosimulator.simexp.core.simulation;

import java.util.List;
import java.util.Map;

public interface IQualityEvaluator {
    List<Map<String, List<Double>>> getQualityAttributes();
}
