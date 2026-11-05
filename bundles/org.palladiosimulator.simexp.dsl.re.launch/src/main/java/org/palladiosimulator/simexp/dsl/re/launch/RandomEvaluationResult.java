package org.palladiosimulator.simexp.dsl.re.launch;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import org.palladiosimulator.simexp.dsl.dispatcher.IndividualResult;

public class RandomEvaluationResult {
    private final BigInteger problemSpaceSize;
    private final IndividualResult bestConfiguration;
    private final List<IndividualResult> evaluatedConfigurations;

    public RandomEvaluationResult(BigInteger problemSpaceSize, IndividualResult bestConfiguration,
            List<IndividualResult> evaluatedConfigurations) {
        this.problemSpaceSize = problemSpaceSize;
        this.bestConfiguration = bestConfiguration;
        this.evaluatedConfigurations = Collections.unmodifiableList(evaluatedConfigurations);
    }

    public BigInteger getProblemSpaceSize() {
        return problemSpaceSize;
    }

    public IndividualResult getBestConfiguration() {
        return bestConfiguration;
    }

    public List<IndividualResult> getEvaluatedConfigurations() {
        return evaluatedConfigurations;
    }
}
