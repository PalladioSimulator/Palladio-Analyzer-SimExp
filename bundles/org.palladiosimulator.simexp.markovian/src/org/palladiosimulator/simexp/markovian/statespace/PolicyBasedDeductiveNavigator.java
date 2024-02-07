package org.palladiosimulator.simexp.markovian.statespace;

import static org.palladiosimulator.simexp.markovian.util.FilterCriterionUtil.withSource;

import java.util.Set;

import org.palladiosimulator.simexp.markovian.activity.BasePolicy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition;

public class PolicyBasedDeductiveNavigator<A, R> extends DeductiveStateSpaceNavigator<A, R> {

    private final BasePolicy<Transition<A>> policy;

    public PolicyBasedDeductiveNavigator(MarkovModel<A, R> markovModel, BasePolicy<Transition<A>> policy) {
        super(markovModel);
        this.policy = policy;
    }

    protected BasePolicy<Transition<A>> getPolicy() {
        return policy;
    }

    @Override
    public State navigate(NavigationContext<A> context) {
        Set<Transition<A>> options = markovModelAccessor.filterTransitions(withSource(context.getSource()));
        Transition<A> select = policy.select(context.getSource(), options);
        return select.getTarget();
    }

}
