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

    public <T> State<T> createState(String name) {
        State<T> newState = entityFactory.createState();
        newState.setName(name);
        return newState;
    }

    public <T, A> Transition<T, A> createTransitionBetween(State<T> source, State<T> target) {
        Transition<T, A> newTransition = entityFactory.createTransition();
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

    public <O> Observation<O> createObservation(O value) {
        Observation<O> obs = entityFactory.createObservation();
        // obs.setValue(value);
        return obs;
    }

}
