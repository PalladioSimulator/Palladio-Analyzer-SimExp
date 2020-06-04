package de.fzi.srp.core.process.config;

import java.util.Optional;

import de.fzi.srp.core.process.exploration.EpsilonGreedyStrategy;
import de.fzi.srp.core.process.markovian.Markovian;

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
