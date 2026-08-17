package org.palladiosimulator.simexp.dsl.ea.api;

import java.util.Collections;
import java.util.List;

public class EAResult {
    private final IndividualResult fittestIndividual;
    private final List<IndividualParetoResult> paretoFront;
    private final List<IndividualResult> initialPopulation;
    private final List<IndividualResult> finalPopulation;

    public EAResult(IndividualResult fittestIndividual, List<IndividualParetoResult> paretoFront,
            List<IndividualResult> initialPopulation, List<IndividualResult> finalPopulation) {
        this.fittestIndividual = fittestIndividual;
        this.paretoFront = Collections.unmodifiableList(paretoFront);
        this.initialPopulation = Collections.unmodifiableList(initialPopulation);
        this.finalPopulation = Collections.unmodifiableList(finalPopulation);
    }

    public IndividualResult getFittest() {
        return fittestIndividual;
    }

    public List<IndividualParetoResult> getParetoFront() {
        return paretoFront;
    }

    public List<IndividualResult> getInitialPopulation() {
        return initialPopulation;
    }

    public List<IndividualResult> getFinalPopulation() {
        return finalPopulation;
    }
}
