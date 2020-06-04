package org.palladiosimulator.simexp.markovian.config;

import java.util.Optional;

import org.palladiosimulator.simexp.markovian.exploration.EpsilonGreedyStrategy;
import org.palladiosimulator.simexp.markovian.type.Markovian;

public class MarkovianConfig {
	public final int horizon;
	public final Markovian markovian;
	public final Optional<EpsilonGreedyStrategy> eGreedyStrategy;
	
	public MarkovianConfig(int horizon,
						   Markovian markovian,
						   EpsilonGreedyStrategy eGreedyStrategy) {
		this.horizon = horizon;
		this.markovian = markovian;
		this.eGreedyStrategy = Optional.ofNullable(eGreedyStrategy);
	}
	
	public static MarkovianConfig with(Markovian markovian) {
		return new MarkovianConfig(0, markovian, null);
	}
	
}
