package org.palladiosimulator.simexp.markovian.config;

import java.util.Optional;

import org.palladiosimulator.simexp.markovian.exploration.EpsilonGreedyStrategy;
import org.palladiosimulator.simexp.markovian.type.Markovian;

public class MarkovianConfig<T> {
    public final int horizon;
    public final Markovian<T> markovian;
    public final Optional<EpsilonGreedyStrategy<T>> eGreedyStrategy;

    public MarkovianConfig(int horizon, Markovian<T> markovian, EpsilonGreedyStrategy<T> eGreedyStrategy) {
        this.horizon = horizon;
        this.markovian = markovian;
        this.eGreedyStrategy = Optional.ofNullable(eGreedyStrategy);
    }

    public static <T> MarkovianConfig<T> with(Markovian<T> markovian) {
        return new MarkovianConfig<>(0, markovian, null);
    }
}
