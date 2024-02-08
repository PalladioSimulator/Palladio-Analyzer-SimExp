package org.palladiosimulator.simexp.markovian.access;

import static org.palladiosimulator.simexp.markovian.util.MarkovProcessConstants.STARTING_TIME;

import java.util.List;
import java.util.Optional;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.SampleModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.SampleModelFactory;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Trajectory;

public class SampleModelAccessor<A, R> {
    private final static SampleModelFactory sampleModelFactory = SampleModelFactory.eINSTANCE;
    private final SampleModel<A, R> sampleModel;

    public SampleModelAccessor(Optional<SampleModel<A, R>> sampleModel) {
        this.sampleModel = sampleModel.orElse(sampleModelFactory.createSampleModel());
    }

    public void addNewTrajectory(Sample<A, R> initial) {
        Trajectory<A, R> traj = sampleModelFactory.createTrajectory();
        traj.getSamplePath()
            .add(initial);
        sampleModel.getTrajectories()
            .add(traj);
    }

    public Trajectory<A, R> getCurrentTrajectory() {
        int index = sampleModel.getTrajectories()
            .size() - 1;
        return sampleModel.getTrajectories()
            .get(index);
    }

    public void addSample(Sample<A, R> sample) {
        getCurrentTrajectory().getSamplePath()
            .add(sample);
    }

    public SampleModel<A, R> getSampleModel() {
        return sampleModel;
    }

    public Sample<A, R> getCurrentSample() {
        List<Sample<A, R>> samplePath = getCurrentTrajectory().getSamplePath();
        return samplePath.get(samplePath.size() - 1);
    }

    public Sample<A, R> createTemplateSampleBy(Sample<A, R> ref) {
        if (isInitialSample(ref)) {
            return ref;
        }
        return createSampleBy(ref.getNext(), ref.getPointInTime() + 1);
    }

    private boolean isInitialSample(Sample<A, R> sample) {
        return sample.getPointInTime() == STARTING_TIME && sample.getNext() == null;
    }

    public static <A, R> Sample<A, R> createSampleBy(State current) {
        Sample<A, R> newSample = sampleModelFactory.createSample();
        newSample.setCurrent(current);
        return newSample;
    }

    public static <A, R> Sample<A, R> createSampleBy(State current, int pointInTime) {
        Sample<A, R> newSample = createSampleBy(current);
        newSample.setPointInTime(pointInTime);
        return newSample;
    }

    public static <A, R> Sample<A, R> createInitialSample(State initial) {
        return createSampleBy(initial, STARTING_TIME);
    }

}
