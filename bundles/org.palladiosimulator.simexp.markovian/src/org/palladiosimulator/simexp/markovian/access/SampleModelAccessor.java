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
    private final SampleModel<S, A, R> sampleModel;

    public SampleModelAccessor(Optional<SampleModel<S, A, R>> sampleModel) {
        this.sampleModel = sampleModel.orElse(sampleModelFactory.createSampleModel());
    }

    public void addNewTrajectory(Sample<S, A, R> initial) {
        Trajectory<S, A, R> traj = sampleModelFactory.createTrajectory();
        traj.getSamplePath()
            .add(initial);
        sampleModel.getTrajectories()
            .add(traj);
    }

    public Trajectory<S, A, R> getCurrentTrajectory() {
        int index = sampleModel.getTrajectories()
            .size() - 1;
        return sampleModel.getTrajectories()
            .get(index);
    }

    public void addSample(Sample<S, A, R> sample) {
        getCurrentTrajectory().getSamplePath()
            .add(sample);
    }

    public SampleModel<S, A, R> getSampleModel() {
        return sampleModel;
    }

    public Sample<S, A, R> getCurrentSample() {
        List<Sample<S, A, R>> samplePath = getCurrentTrajectory().getSamplePath();
        return samplePath.get(samplePath.size() - 1);
    }

    public Sample<S, A, R> createTemplateSampleBy(Sample<S, A, R> ref) {
        if (isInitialSample(ref)) {
            return ref;
        }
        return createSampleBy(ref.getNext(), ref.getPointInTime() + 1);
    }

    private boolean isInitialSample(Sample<S, A, R> sample) {
        return sample.getPointInTime() == STARTING_TIME && sample.getNext() == null;
    }

    public static <S, A, R> Sample<S, A, R> createSampleBy(State<S> current) {
        Sample<S, A, R> newSample = sampleModelFactory.createSample();
        newSample.setCurrent(current);
        return newSample;
    }

    public static <S, A, R> Sample<S, A, R> createSampleBy(State<S> current, int pointInTime) {
        Sample<S, A, R> newSample = createSampleBy(current);
        newSample.setPointInTime(pointInTime);
        return newSample;
    }

    public static <S, A, R> Sample<S, A, R> createInitialSample(State<S> initial) {
        return createSampleBy(initial, STARTING_TIME);
    }

}
