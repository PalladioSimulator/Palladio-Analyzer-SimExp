package org.palladiosimulator.simexp.pcm.datasource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.palladiosimulator.simexp.core.util.Pair;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;

public class MeasurementSeriesResult {
	
	public static class MeasurementSeries {
		
		private final List<Pair<Number, Double>> measurements;
		
		public MeasurementSeries(List<Pair<Number, Double>> measurements) {
			this.measurements = measurements;
		}
		
		public Stream<Pair<Number, Double>> asStream() {
			return measurements.stream();
		}
		
		public List<Pair<Number, Double>> asList() {
			return measurements;
		}
		
		public Stream<Number> asStreamOfValues() {
			return asListOfValues().stream();
		}
		
		public List<Number> asListOfValues() {
			return measurements.stream().map(each -> each.getFirst()).collect(Collectors.toList());
		}
		
	}
	
	private final Map<PcmMeasurementSpecification, MeasurementSeries> results;
	
	public MeasurementSeriesResult() {
		this.results = new HashMap<>();
	}
	
	public void addMeasurementSeries(PcmMeasurementSpecification spec, List<Pair<Number, Double>> measurements) {
		results.put(spec, new MeasurementSeries(measurements));
	}
	
	public Optional<MeasurementSeries> getMeasurementsSeries(PcmMeasurementSpecification spec) {
		return Optional.ofNullable(results.get(spec));
	}
}
