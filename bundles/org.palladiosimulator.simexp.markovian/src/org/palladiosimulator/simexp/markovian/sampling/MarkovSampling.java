package org.palladiosimulator.simexp.markovian.sampling;

import java.util.Optional;

import org.palladiosimulator.simexp.markovian.access.SampleModelAccessor;
import org.palladiosimulator.simexp.markovian.config.MarkovianConfig;
import org.palladiosimulator.simexp.markovian.exploration.EpsilonGreedyStrategy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Trajectory;
import org.palladiosimulator.simexp.markovian.type.Markovian;

public class MarkovSampling<S, A, R, O> {

    private class SampleLoop {

        private int sampleIndex = 0;

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
    private final Markovian<S, A, R, O> markovian;
    private final SampleModelAccessor<S, A, R, O> sampleModelAccessor;
    private final Optional<EpsilonGreedyStrategy<S, A>> eGreedy;

    public MarkovSampling(MarkovianConfig<S, A, R, O> config) {
        this.horizon = config.horizon;
        this.sampleModelAccessor = new SampleModelAccessor<>(Optional.empty());
        this.markovian = config.markovian;
        this.eGreedy = config.eGreedyStrategy;
    }

    public Trajectory<S, A, R, O> sampleTrajectory() {
        SampleLoop sampleLoop = new SampleLoop();
        while (sampleLoop.stillSamplesToIterate()) {
            if (sampleLoop.isInitial()) {
                sampleModelAccessor.addNewTrajectory(markovian.determineInitialState());
            } else {
                drawSample();
            }

            sampleLoop.incrementSampleIndex();

            eGreedy.ifPresent(e -> e.adjust(sampleLoop.getIterationIndex()));
        }

        return sampleModelAccessor.getCurrentTrajectory();
    }

    public Sample<S, A, R, O> drawSampleGiven(Sample<S, A, R, O> last) {
        Sample<S, A, R, O> newSample = sampleModelAccessor.createTemplateSampleBy(last);
        markovian.drawSample(newSample);
        return newSample;
    }

    private void drawSample() {
        Sample<S, A, R, O> result = drawSampleGiven(sampleModelAccessor.getCurrentSample());
        sampleModelAccessor.addSample(result);
    }

    public Sample<S, A, R, O> drawInitialSample() {
        return markovian.determineInitialState();
    }

    public int getHorizon() {
        return horizon;
    }
}
