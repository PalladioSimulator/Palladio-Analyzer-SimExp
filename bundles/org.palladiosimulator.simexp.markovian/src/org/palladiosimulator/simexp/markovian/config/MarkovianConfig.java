package org.palladiosimulator.simexp.markovian.config;

import java.util.Optional;

import org.palladiosimulator.simexp.markovian.exploration.EpsilonGreedyStrategy;
import org.palladiosimulator.simexp.markovian.type.Markovian;

public class MarkovianConfig<S, A, R> {
    public final int horizon;
    public final Markovian<S, A, R> markovian;
    public final Optional<EpsilonGreedyStrategy<T>> eGreedyStrategy;

    public MarkovianConfig(int horizon, Markovian<S, A, R> markovian, EpsilonGreedyStrategy<T> eGreedyStrategy) {
        this.horizon = horizon;
        this.markovian = markovian;
        this.eGreedyStrategy = Optional.ofNullable(eGreedyStrategy);
    }

    public static <S, A, R> MarkovianConfig<S, A, R> with(Markovian<S, A, R> markovian) {
        return new MarkovianConfig<>(0, markovian, null);
    }
}
