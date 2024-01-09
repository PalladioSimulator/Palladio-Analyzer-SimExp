package org.palladiosimulator.simexp.environmentaldynamics.builder;

import java.util.function.Predicate;

import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.markovian.model.builder.BasicMarkovModelBuilder;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;

public class EnvironmentModelBuilder<S, A, R> extends BasicMarkovModelBuilder<S, A, R> {

    // TODO: singleton
    private static EnvironmentModelBuilder builderInstance = null;

    private boolean isHidden = false;

    private EnvironmentModelBuilder() {
        super();
    }

    public static <S, A, R> EnvironmentModelBuilder<S, A, R> get() {
        if (builderInstance == null) {
            builderInstance = new EnvironmentModelBuilder<>();
        }
        return builderInstance;
    }

    @Override
    public MarkovModel<S, A, R> build() {
        MarkovModel<S, A, R> build = super.build();
        checkValidity(build);
        return build;
    }

    private void checkValidity(MarkovModel<S, A, R> build) {
        isHidden = isHidden(build.getStateSpace()
            .get(0));
        if (isNotConsistent(build)) {
            // TODO exception handling
            throw new RuntimeException("");
        }
    }

    private boolean isNotConsistent(MarkovModel<S, A, R> build) {
        return !build.getStateSpace()
            .stream()
            .allMatch(consistencyCondition());
    }

    private Predicate<State<S>> consistencyCondition() {
        return state -> isHidden(state) == isHidden;
    }

    private boolean isHidden(State<S> state) {
        return PerceivableEnvironmentalState.class.cast(state)
            .isHidden();
    }

}
