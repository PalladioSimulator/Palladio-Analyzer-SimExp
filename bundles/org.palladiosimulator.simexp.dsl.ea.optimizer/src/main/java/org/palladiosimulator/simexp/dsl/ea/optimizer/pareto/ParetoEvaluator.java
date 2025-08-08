package org.palladiosimulator.simexp.dsl.ea.optimizer.pareto;

import java.util.List;

import org.palladiosimulator.simexp.dsl.ea.api.IQualityAttributeProvider;
import org.palladiosimulator.simexp.dsl.ea.api.EAResult.IndividualResult;

public class ParetoEvaluator {
    private final IQualityAttributeProvider qualityAttributeProvider;

    public ParetoEvaluator(IQualityAttributeProvider qualityAttributeProvider) {
        this.qualityAttributeProvider = qualityAttributeProvider;
    }

    public List<IndividualResult> buildParetoFront(List<IndividualResult> population) {
        return null;
    }
}
