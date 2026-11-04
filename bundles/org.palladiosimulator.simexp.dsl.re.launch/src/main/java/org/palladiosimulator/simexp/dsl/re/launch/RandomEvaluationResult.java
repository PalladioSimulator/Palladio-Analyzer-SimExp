package org.palladiosimulator.simexp.dsl.re.launch;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import org.palladiosimulator.simexp.dsl.dispatcher.IndividualResult;

public class RandomEvaluationResult {
    private final BigInteger problemSpaceSize;
    private final IndividualResult bestIndividual;
    private final List<IndividualResult> evaluatedIndividuals;

    public RandomEvaluationResult(BigInteger problemSpaceSize, IndividualResult bestIndividual,
            List<IndividualResult> evaluatedIndividuals) {
        this.problemSpaceSize = problemSpaceSize;
        this.bestIndividual = bestIndividual;
        this.evaluatedIndividuals = Collections.unmodifiableList(evaluatedIndividuals);
    }

    public BigInteger getProblemSpaceSize() {
        return problemSpaceSize;
    }

    public IndividualResult getBest() {
        return bestIndividual;
    }

    public List<IndividualResult> getEvaluatedIndividuals() {
        return evaluatedIndividuals;
    }
}
