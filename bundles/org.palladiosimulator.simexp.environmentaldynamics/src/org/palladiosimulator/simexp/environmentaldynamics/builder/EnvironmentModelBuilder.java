package org.palladiosimulator.simexp.environmentaldynamics.builder;

import java.util.function.Predicate;

import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.markovian.model.builder.BasicMarkovModelBuilder;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;

public class EnvironmentModelBuilder<A, R> extends BasicMarkovModelBuilder<A, R> {

    private boolean isHidden = false;

    @Override
    public MarkovModel<A, R> build() {
        MarkovModel<A, R> build = super.build();
        checkValidity(build);
        return build;
    }

    private void checkValidity(MarkovModel<A, R> build) {
        isHidden = isHidden(build.getStateSpace()
            .get(0));
        if (isNotConsistent(build)) {
            // TODO exception handling
            throw new RuntimeException("");
        }
    }

    private boolean isNotConsistent(MarkovModel<A, R> build) {
        return !build.getStateSpace()
            .stream()
            .allMatch(consistencyCondition());
    }

    private Predicate<State> consistencyCondition() {
        return state -> isHidden(state) == isHidden;
    }

    private boolean isHidden(State state) {
        return PerceivableEnvironmentalState.class.cast(state)
            .isHidden();
    }

}
