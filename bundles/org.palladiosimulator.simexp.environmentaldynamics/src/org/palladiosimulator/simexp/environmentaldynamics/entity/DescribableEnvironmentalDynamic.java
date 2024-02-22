package org.palladiosimulator.simexp.environmentaldynamics.entity;

import org.palladiosimulator.simexp.markovian.activity.BasePolicy;
import org.palladiosimulator.simexp.markovian.exploitation.ProbabilityBasedTransitionPolicy;
import org.palladiosimulator.simexp.markovian.exploration.RandomizedStrategy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition;
import org.palladiosimulator.simexp.markovian.statespace.PolicyBasedDeductiveNavigator;

public class DescribableEnvironmentalDynamic<A, Aa extends Action<A>, R> extends PolicyBasedDeductiveNavigator<A, R>
        implements EnvironmentalDynamic {

    /*
     * private static class PolicyController<T> implements Policy<Transition<T>> {
     * 
     * private final static String POLICY_NAME = "PolicyControllerOf";
     * 
     * private final static int EXPLOITATION_INDEX = 0; private final static int EXPLORATION_INDEX =
     * 1;
     */
    /*
     * private final static PolicyController<T> controllerInstance = new PolicyController();
     * 
     * public static <T> PolicyController<T> get() { return controllerInstance; }
     */

    // private final List<Policy<Transition<?>>> strategies;
    // private int chosenStrategyIndex;
    /*
     * private final Policy<Transition<T>> policy;
     * 
     * private PolicyController(boolean isExploration) {
     */
    /*
     * RandomizedStrategy<Transition<T>> randomizedStrategy = new
     * RandomizedStrategy<Transition<T>>(); ProbabilityBasedTransitionPolicy
     * probabilityBasedTransitionPolicy = new ProbabilityBasedTransitionPolicy();
     * 
     * this.strategies = Arrays.asList(randomizedStrategy, probabilityBasedTransitionPolicy);
     * this.chosenStrategyIndex = EXPLOITATION_INDEX;
     */

    /*
     * if (isExploration) { ProbabilityBasedTransitionPolicy probabilityBasedTransitionPolicy = new
     * ProbabilityBasedTransitionPolicy(); policy = probabilityBasedTransitionPolicy; } else { //
     * exploitation RandomizedStrategy<Transition<T>> randomizedStrategy = new
     * RandomizedStrategy<Transition<T>>(); policy = randomizedStrategy; } }
     * 
     * @Override public Transition<T> select(State<Transition<T>> source, Set<Transition<T>>
     * options) { // Policy<Transition<?>> strategy = strategies.get(chosenStrategyIndex); return
     * policy.select(source, options); }
     */

    /*
     * public void pursueExplorationStrategy() { chosenStrategyIndex = EXPLORATION_INDEX; }
     * 
     * public void pursueExploitationStrategy() { chosenStrategyIndex = EXPLOITATION_INDEX; }
     */

    /*
     * @Override public String getId() { /* return String.format("%1s%2sAnd%3s", POLICY_NAME,
     * strategies.get(0) .getId(), strategies.get(1) .getId());
     */
    /*
     * return String.format("%s:%s", POLICY_NAME, policy); }
     * 
     * }
     */

    private final boolean isHiddenProcess;
    // private final PolicyController<T> policy;

    protected DescribableEnvironmentalDynamic(MarkovModel<A, R> markovModel, boolean isHiddenProcess,
            boolean isExploration) {
        super(markovModel, createPolicy(isExploration));
        // this.policy = (PolicyController<T>) getPolicy();
        this.isHiddenProcess = isHiddenProcess;
    }

    private static <A> BasePolicy<Transition<A>> createPolicy(boolean isExploration) {
        /*
         * PolicyController<T> policyController = new PolicyController<T>(isExploration); if
         * (isExploration) { policyController.pursueExplorationStrategy(); } else {
         * policyController.pursueExploitationStrategy(); } return policyController;
         */
        if (isExploration) {
            ProbabilityBasedTransitionPolicy<A> probabilityBasedTransitionPolicy = new ProbabilityBasedTransitionPolicy<>();
            return probabilityBasedTransitionPolicy;
        } else {
            // exploitation
            RandomizedStrategy<Transition<A>> randomizedStrategy = new RandomizedStrategy<>();
            return randomizedStrategy;
        }
    }

    @Override
    public boolean isHiddenProcess() {
        return isHiddenProcess;
    }

    /*
     * @Override public void pursueExplorationStrategy() { // policy.pursueExplorationStrategy();
     * throw new RuntimeException("invalid call, not dynamic"); }
     * 
     * @Override public void pursueExploitationStrategy() { // policy.pursueExploitationStrategy();
     * throw new RuntimeException("invalid call, not dynamic"); }
     */
}
