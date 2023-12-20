package org.palladiosimulator.simexp.markovian.exploration;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;

public class RandomizedStrategy<S> implements Policy<S, Double, Action<Double>> {

    private final static String STRATEGY_ID = "RandomizedSelectionStrategy";

    private final Random random = new Random(System.currentTimeMillis());

    @Override
    public Action<Double> select(State<S> source, Set<Action<Double>> options) {
        return selectRandomly(options);
    }

    private Action<Double> selectRandomly(Set<Action<Double>> values) {
        int randomizedIndex = random.nextInt(values.size());
        // TODO: order of set entries is not guaranteed
        // but on each call the same order is implied
        // to get value randomly by index.
        return new ArrayList<>(values).get(randomizedIndex);
    }

    @Override
    public String getId() {
        return STRATEGY_ID;
    }
}
