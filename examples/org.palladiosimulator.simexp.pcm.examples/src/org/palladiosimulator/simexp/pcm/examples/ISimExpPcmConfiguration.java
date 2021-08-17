package org.palladiosimulator.simexp.pcm.examples;

import org.palladiosimulator.simexp.pcm.examples.executor.IPcmConfiguration;

public interface ISimExpPcmConfiguration extends IPcmConfiguration {
    
    String getEnvironmentalStaticsModelFile();
    
    String getEnvironmentalDynamicsModelFile();
    
}

