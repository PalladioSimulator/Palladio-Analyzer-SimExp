package org.palladiosimulator.simexp.markovian.access;

import static org.palladiosimulator.simexp.markovian.util.MarkovProcessConstants.STARTING_TIME;

import java.util.List;
import java.util.Optional;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.SampleModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.SampleModelFactory;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Trajectory;

public class SampleModelAccessor {
	private final static SampleModelFactory sampleModelFactory = SampleModelFactory.eINSTANCE;
	private final SampleModel sampleModel;
	
	public SampleModelAccessor(Optional<SampleModel> sampleModel) {
		this.sampleModel = sampleModel.orElse(sampleModelFactory.createSampleModel());
	}
	
	public void addNewTrajectory(Sample initial) {
		Trajectory traj = sampleModelFactory.createTrajectory();
		traj.getSamplePath().add(initial);
		sampleModel.getTrajectories().add(traj);
	}
	
	public Trajectory getCurrentTrajectory() {
		int index = sampleModel.getTrajectories().size() - 1;
		return sampleModel.getTrajectories().get(index);
	}
	
	public void addSample(Sample sample) {
		getCurrentTrajectory().getSamplePath().add(sample);
	}
	
	public SampleModel getSampleModel() {
		return sampleModel;
	}
	
	public Sample getCurrentSample() {
		List<Sample> samplePath = getCurrentTrajectory().getSamplePath();
		return samplePath.get(samplePath.size() - 1);
	}
	
	public Sample createTemplateSampleBy(Sample ref) {
		if (isInitialSample(ref)) {
			return ref;
		}
		return createSampleBy(ref.getNext(), ref.getPointInTime() + 1);
	}
	
	private boolean isInitialSample(Sample sample) {
		return sample.getPointInTime() == STARTING_TIME && sample.getNext() == null;
	}

	public static Sample createSampleBy(State current) {
		Sample newSample = sampleModelFactory.createSample();
		newSample.setCurrent(current);
		return newSample;
	}
	
	public static Sample createSampleBy(State current, int pointInTime) {
		Sample newSample = createSampleBy(current);
		newSample.setPointInTime(pointInTime);
		return newSample;
	}
	
	public static Sample createInitialSample(State initial) {
		return createSampleBy(initial, STARTING_TIME);
	}
	
}
