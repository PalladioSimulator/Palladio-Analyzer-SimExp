package org.palladiosimulator.simexp.markovian.statespace;

import static org.palladiosimulator.simexp.markovian.util.FilterCriterionUtil.withSource;

import java.util.Set;

import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition;

public class PolicyBasedDeductiveNavigator<T> extends DeductiveStateSpaceNavigator<T> {

    private final Policy<Transition<T>> policy;

    public PolicyBasedDeductiveNavigator(MarkovModel<T> markovModel, Policy<Transition<T>> policy) {
        super(markovModel);
        this.policy = policy;
    }

    protected Policy<Transition<T>> getPolicy() {
        return policy;
    }

    @Override
    public State<T> navigate(NavigationContext context) {
        Set<Transition<T>> options = markovModelAccessor.filterTransitions(withSource(context.getSource()));
        Transition<T> select = policy.select(context.getSource(), options);
        return select.getTarget();
    }

}
