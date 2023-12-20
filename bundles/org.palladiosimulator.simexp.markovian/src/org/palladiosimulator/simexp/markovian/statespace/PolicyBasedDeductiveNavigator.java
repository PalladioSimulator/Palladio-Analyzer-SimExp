package org.palladiosimulator.simexp.markovian.statespace;

import static org.palladiosimulator.simexp.markovian.util.FilterCriterionUtil.withSource;

import java.util.Set;

import org.palladiosimulator.simexp.markovian.activity.BasePolicy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition;

public class PolicyBasedDeductiveNavigator<S, A, R> extends DeductiveStateSpaceNavigator<S, A, R> {

    private final BasePolicy<S, Transition<S, A>> policy;

    public PolicyBasedDeductiveNavigator(MarkovModel<S, A, R> markovModel, BasePolicy<S, Transition<S, A>> policy) {
        super(markovModel);
        this.policy = policy;
    }

    protected BasePolicy<S, Transition<S, A>> getPolicy() {
        return policy;
    }

    @Override
    public State<S> navigate(NavigationContext<S, A> context) {
        Set<Transition<S, A>> options = markovModelAccessor.filterTransitions(withSource(context.getSource()));
        Transition<S, A> select = policy.select(context.getSource(), options);
        return select.getTarget();
    }

}
