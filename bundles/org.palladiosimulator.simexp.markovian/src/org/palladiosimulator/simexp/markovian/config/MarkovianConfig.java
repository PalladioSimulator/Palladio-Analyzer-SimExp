package org.palladiosimulator.simexp.markovian.config;

import java.util.Optional;

import org.palladiosimulator.simexp.markovian.exploration.EpsilonGreedyStrategy;
import org.palladiosimulator.simexp.markovian.type.Markovian;

public class MarkovianConfig<S, A, R, O> {
    public final int horizon;
    public final Markovian<S, A, R, O> markovian;
    public final Optional<EpsilonGreedyStrategy<S, A>> eGreedyStrategy;

    public MarkovianConfig(int horizon, Markovian<S, A, R, O> markovian, EpsilonGreedyStrategy<S, A> eGreedyStrategy) {
        this.horizon = horizon;
        this.markovian = markovian;
        this.eGreedyStrategy = Optional.ofNullable(eGreedyStrategy);
    }

    public static <S, A, R, O> MarkovianConfig<S, A, R, O> with(Markovian<S, A, R, O> markovian) {
        return new MarkovianConfig<>(0, markovian, null);
    }
}
