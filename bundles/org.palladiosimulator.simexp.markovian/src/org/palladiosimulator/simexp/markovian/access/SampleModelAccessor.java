package org.palladiosimulator.simexp.markovian.access;

import static org.palladiosimulator.simexp.markovian.util.MarkovProcessConstants.STARTING_TIME;

import java.util.List;
import java.util.Optional;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.SampleModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.SampleModelFactory;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Trajectory;

public class SampleModelAccessor<S, A, R> {
    private final static SampleModelFactory sampleModelFactory = SampleModelFactory.eINSTANCE;
    private final SampleModel<S, A, R, State<S>> sampleModel;

    public SampleModelAccessor(Optional<SampleModel<S, A, R, State<S>>> sampleModel) {
        this.sampleModel = sampleModel.orElse(sampleModelFactory.createSampleModel());
    }

    public void addNewTrajectory(Sample<S, A, R, State<S>> initial) {
        Trajectory<S, A, R, State<S>> traj = sampleModelFactory.createTrajectory();
        traj.getSamplePath()
            .add(initial);
        sampleModel.getTrajectories()
            .add(traj);
    }

    public Trajectory<S, A, R, State<S>> getCurrentTrajectory() {
        int index = sampleModel.getTrajectories()
            .size() - 1;
        return sampleModel.getTrajectories()
            .get(index);
    }

    public void addSample(Sample<S, A, R, State<S>> sample) {
        getCurrentTrajectory().getSamplePath()
            .add(sample);
    }

    public SampleModel<S, A, R, State<S>> getSampleModel() {
        return sampleModel;
    }

    public Sample<S, A, R, State<S>> getCurrentSample() {
        List<Sample<S, A, R, State<S>>> samplePath = getCurrentTrajectory().getSamplePath();
        return samplePath.get(samplePath.size() - 1);
    }

    public Sample<S, A, R, State<S>> createTemplateSampleBy(Sample<S, A, R, State<S>> ref) {
        if (isInitialSample(ref)) {
            return ref;
        }
        return createSampleBy(ref.getNext(), ref.getPointInTime() + 1);
    }

    private boolean isInitialSample(Sample<S, A, R, State<S>> sample) {
        return sample.getPointInTime() == STARTING_TIME && sample.getNext() == null;
    }

    public static <S, A, R> Sample<S, A, R, State<S>> createSampleBy(State<S> current) {
        Sample<S, A, R, State<S>> newSample = sampleModelFactory.createSample();
        newSample.setCurrent(current);
        return newSample;
    }

    public static <S, A, R> Sample<S, A, R, State<S>> createSampleBy(State<S> current, int pointInTime) {
        Sample<S, A, R, State<S>> newSample = createSampleBy(current);
        newSample.setPointInTime(pointInTime);
        return newSample;
    }

    public static <S, A, R> Sample<S, A, R, State<S>> createInitialSample(State<S> initial) {
        return createSampleBy(initial, STARTING_TIME);
    }

}
