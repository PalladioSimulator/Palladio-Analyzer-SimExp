package org.palladiosimulator.simexp.markovian.type;

import java.util.Optional;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Trajectory;

public class MarkovianResult<S, A, R, O> {

    public static class MarkovianResultBuilder<S, A, R, O> {
        private final Trajectory<S, A, R, O> trajToEvaluate;

        private double probability;
        private Optional<Reward<R>> accumulatedReward;

        public MarkovianResultBuilder(Trajectory<S, A, R, O> trajToEvaluate) {
            this.trajToEvaluate = trajToEvaluate;
            this.accumulatedReward = Optional.empty();
        }

        public MarkovianResultBuilder<S, A, R, O> withProbability(double probability) {
            this.probability = probability;
            return MarkovianResultBuilder.this;
        }

        public MarkovianResultBuilder<S, A, R, O> andReward(Reward<R> accumulatedReward) {
            this.accumulatedReward = Optional.of(accumulatedReward);
            return MarkovianResultBuilder.this;
        }

        public MarkovianResult<S, A, R, O> build() {
            return new MarkovianResult<>(trajToEvaluate, probability, accumulatedReward);
        }
    }

    private final Trajectory<S, A, R, O> trajToEvaluate;
    private final double probability;
    private final Optional<Reward<R>> accumulatedReward;

    public MarkovianResult(Trajectory<S, A, R, O> trajToEvaluate, double probability,
            Optional<Reward<R>> accumulatedReward) {
        this.trajToEvaluate = trajToEvaluate;
        this.probability = probability;
        this.accumulatedReward = accumulatedReward;
    }

    public static <S, A, R, O> MarkovianResultBuilder<S, A, R, O> of(Trajectory<S, A, R, O> trajToAnalyse) {
        return new MarkovianResultBuilder<>(trajToAnalyse);
    }

    public double getProbability() {
        return probability;
    }

    public Trajectory<S, A, R, O> getEvaluatedTrajectory() {
        return trajToEvaluate;
    }

    public Optional<Reward<R>> getAccumulatedReward() {
        return accumulatedReward;
    }
}
