package org.palladiosimulator.simexp.markovian.model.factory;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovEntityFactory;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Observation;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition;

public class MarkovModelFactory {

    private final MarkovEntityFactory entityFactory = MarkovEntityFactory.eINSTANCE;

    public MarkovModelFactory() {
    }

    public State createState(String name) {
        State newState = entityFactory.createState();
        newState.setName(name);
        return newState;
    }

    public <A> Transition<A> createTransitionBetween(State source, State target) {
        Transition<A> newTransition = entityFactory.createTransition();
        newTransition.setSource(source);
        newTransition.setTarget(target);
        return newTransition;
    }

    public <T> Reward<T> createRewardSignal() {
        return entityFactory.createReward();
    }

    public <T> Reward<T> createRewardSignal(T value) {
        Reward<T> reward = entityFactory.createReward();
        reward.setValue(value);
        return reward;
    }

    public Observation createObservation() {
        Observation obs = entityFactory.createObservation();
        // obs.setValue(value);
        return obs;
    }

}
