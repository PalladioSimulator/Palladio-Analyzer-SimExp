package org.palladiosimulator.simexp.markovian.type;

import java.util.Optional;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Trajectory;

public class MarkovianResult<A, R> {

    public static class MarkovianResultBuilder<A, R> {
        private final Trajectory<A, R> trajToEvaluate;

        private double probability;
        private Optional<Reward<R>> accumulatedReward;

        public MarkovianResultBuilder(Trajectory<A, R> trajToEvaluate) {
            this.trajToEvaluate = trajToEvaluate;
            this.accumulatedReward = Optional.empty();
        }

        public MarkovianResultBuilder<A, R> withProbability(double probability) {
            this.probability = probability;
            return MarkovianResultBuilder.this;
        }

        public MarkovianResultBuilder<A, R> andReward(Reward<R> accumulatedReward) {
            this.accumulatedReward = Optional.of(accumulatedReward);
            return MarkovianResultBuilder.this;
        }

        public MarkovianResult<A, R> build() {
            return new MarkovianResult<>(trajToEvaluate, probability, accumulatedReward);
        }
    }

    private final Trajectory<A, R> trajToEvaluate;
    private final double probability;
    private final Optional<Reward<R>> accumulatedReward;

    public MarkovianResult(Trajectory<A, R> trajToEvaluate, double probability, Optional<Reward<R>> accumulatedReward) {
        this.trajToEvaluate = trajToEvaluate;
        this.probability = probability;
        this.accumulatedReward = accumulatedReward;
    }

    public static <A, R> MarkovianResultBuilder<A, R> of(Trajectory<A, R> trajToAnalyse) {
        return new MarkovianResultBuilder<>(trajToAnalyse);
    }

    public double getProbability() {
        return probability;
    }

    public Trajectory<A, R> getEvaluatedTrajectory() {
        return trajToEvaluate;
    }

    public Optional<Reward<R>> getAccumulatedReward() {
        return accumulatedReward;
    }
}
