package org.palladiosimulator.simexp.markovian.sampling;

import java.util.Optional;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.markovian.access.SampleModelAccessor;
import org.palladiosimulator.simexp.markovian.config.MarkovianConfig;
import org.palladiosimulator.simexp.markovian.exploration.EpsilonGreedyStrategy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Trajectory;
import org.palladiosimulator.simexp.markovian.type.Markovian;

public class MarkovSampling<A, R> {
    private static final Logger LOGGER = Logger.getLogger(MarkovSampling.class);

    private static class SampleLoop {

        private final int horizon;

        private int sampleIndex = 0;

        public SampleLoop(int horizon) {
            this.horizon = horizon;
        }

        public boolean stillSamplesToIterate() {
            return sampleIndex != horizon;
        }

        public void incrementSampleIndex() {
            sampleIndex++;
        }

        public boolean isInitial() {
            return sampleIndex == 0;
        }

        public int getIterationIndex() {
            return sampleIndex;
        }

    }

    private final int horizon;
    private final Markovian<A, R> markovian;
    private final SampleModelAccessor<A, R> sampleModelAccessor;
    private final Optional<EpsilonGreedyStrategy<A>> eGreedy;

    public MarkovSampling(MarkovianConfig<A, R> config) {
        this.horizon = config.horizon;
        this.sampleModelAccessor = new SampleModelAccessor<>(Optional.empty());
        this.markovian = config.markovian;
        this.eGreedy = config.eGreedyStrategy;
    }

    public Trajectory<A, R> sampleTrajectory() {
        SampleLoop sampleLoop = new SampleLoop(horizon);
        while (sampleLoop.stillSamplesToIterate()) {
            LOGGER.info(String.format("Markov sample: %d/%d", sampleLoop.getIterationIndex() + 1, horizon));

            if (sampleLoop.isInitial()) {
                Sample<A, R> initialSample = drawInitialSample();
                sampleModelAccessor.addNewTrajectory(initialSample);
            } else {
                drawSample();
            }

            sampleLoop.incrementSampleIndex();

            eGreedy.ifPresent(e -> e.adjust(sampleLoop.getIterationIndex()));
        }

        return sampleModelAccessor.getCurrentTrajectory();
    }

    public Sample<A, R> drawSampleGiven(Sample<A, R> last) {
        Sample<A, R> newSample = sampleModelAccessor.createTemplateSampleBy(last);
        markovian.drawSample(newSample);
        return newSample;
    }

    private void drawSample() {
        Sample<A, R> currentSample = sampleModelAccessor.getCurrentSample();
        Sample<A, R> result = drawSampleGiven(currentSample);
        sampleModelAccessor.addSample(result);
    }

    public Sample<A, R> drawInitialSample() {
        return markovian.determineInitialState();
    }
}
