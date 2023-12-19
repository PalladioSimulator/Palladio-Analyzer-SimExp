package org.palladiosimulator.simexp.markovian.access;

import static org.palladiosimulator.simexp.markovian.util.MarkovProcessConstants.STARTING_TIME;

import java.util.List;
import java.util.Optional;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.SampleModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.SampleModelFactory;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Trajectory;

public class SampleModelAccessor<T> {
    private final static SampleModelFactory sampleModelFactory = SampleModelFactory.eINSTANCE;
    private final SampleModel<T> sampleModel;

    public SampleModelAccessor(Optional<SampleModel<T>> sampleModel) {
        this.sampleModel = sampleModel.orElse(sampleModelFactory.createSampleModel());
    }

    public void addNewTrajectory(Sample<T> initial) {
        Trajectory<T> traj = sampleModelFactory.createTrajectory();
        traj.getSamplePath()
            .add(initial);
        sampleModel.getTrajectories()
            .add(traj);
    }

    public Trajectory<T> getCurrentTrajectory() {
        int index = sampleModel.getTrajectories()
            .size() - 1;
        return sampleModel.getTrajectories()
            .get(index);
    }

    public void addSample(Sample<T> sample) {
        getCurrentTrajectory().getSamplePath()
            .add(sample);
    }

    public SampleModel<T> getSampleModel() {
        return sampleModel;
    }

    public Sample<T> getCurrentSample() {
        List<Sample<T>> samplePath = getCurrentTrajectory().getSamplePath();
        return samplePath.get(samplePath.size() - 1);
    }

    public Sample<T> createTemplateSampleBy(Sample<T> ref) {
        if (isInitialSample(ref)) {
            return ref;
        }
        return createSampleBy(ref.getNext(), ref.getPointInTime() + 1);
    }

    private boolean isInitialSample(Sample<T> sample) {
        return sample.getPointInTime() == STARTING_TIME && sample.getNext() == null;
    }

    public static <T> Sample<T> createSampleBy(State<T> current) {
        Sample<T> newSample = sampleModelFactory.createSample();
        newSample.setCurrent(current);
        return newSample;
    }

    public static <T> Sample<T> createSampleBy(State<T> current, int pointInTime) {
        Sample<T> newSample = createSampleBy(current);
        newSample.setPointInTime(pointInTime);
        return newSample;
    }

    public static <T> Sample<T> createInitialSample(State<T> initial) {
        return createSampleBy(initial, STARTING_TIME);
    }

}
