package org.palladiosimulator.simexp.dsl.bf.launch;

import java.util.Collections;
import java.util.List;

import org.palladiosimulator.simexp.dsl.dispatcher.IndividualResult;

public class BruteForceResult {
    private final IndividualResult bestIndividual;
    private final List<IndividualResult> evaluatedIndividuals;

    public BruteForceResult(IndividualResult bestIndividual, List<IndividualResult> evaluatedIndividuals) {
        this.bestIndividual = bestIndividual;
        this.evaluatedIndividuals = Collections.unmodifiableList(evaluatedIndividuals);
    }

    public IndividualResult getBest() {
        return bestIndividual;
    }

    public List<IndividualResult> getEvaluatedIndividuals() {
        return evaluatedIndividuals;
    }
}
