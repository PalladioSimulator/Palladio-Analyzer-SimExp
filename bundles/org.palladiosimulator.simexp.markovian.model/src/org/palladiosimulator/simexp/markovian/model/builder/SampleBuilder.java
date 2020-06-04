package org.palladiosimulator.simexp.markovian.model.builder;

public class SampleBuilder {
	
	private final static SampleBuilder INSTANCE = new SampleBuilder();
	
	private SampleBuilder() {
		
	}
	
	public static SampleBuilder get() {
		return INSTANCE;
	}
	
	
	
}
