package org.palladiosimulator.simexp.markovian.config;

import java.util.Optional;

import org.palladiosimulator.simexp.markovian.exploration.EpsilonGreedyStrategy;
import org.palladiosimulator.simexp.markovian.type.Markovian;

public class MarkovianConfig<A, R> {
    public final int horizon;
    public final Markovian<A, R> markovian;
    public final Optional<EpsilonGreedyStrategy<A>> eGreedyStrategy;

    public MarkovianConfig(int horizon, Markovian<A, R> markovian, EpsilonGreedyStrategy<A> eGreedyStrategy) {
        this.horizon = horizon;
        this.markovian = markovian;
        this.eGreedyStrategy = Optional.ofNullable(eGreedyStrategy);
    }

    public static <A, R> MarkovianConfig<A, R> with(Markovian<A, R> markovian) {
        return new MarkovianConfig<>(0, markovian, null);
    }
}
