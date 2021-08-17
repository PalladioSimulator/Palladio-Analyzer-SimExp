package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import org.palladiosimulator.simexp.pcm.examples.ISimExpPcmConfiguration;

public class LoadBalancingSimexpPcmConfiguration implements ISimExpPcmConfiguration {
    

    
    private final static String ENVIRONMENTAL_MODELS_BASE_PATH = "/org.palladiosimulator.simexp.pcm.examples.loadbalancer";
    public final static String ENVIRONMENTAL_STATICS_MODEL_FILE = String.format("%1s/%2s.%3s", ENVIRONMENTAL_MODELS_BASE_PATH,
          "LoadBalancerNonTemporalEnvironment", DynamicBayesianNetworkLoader.STATIC_MODEL_EXTENSION);
    public final static String ENVIRONMENTAL_DYNAMICS_MODEL_FILE = String.format("%1s/%2s.%3s", ENVIRONMENTAL_MODELS_BASE_PATH,
          "LoadBalancerEnvironmentalDynamics", DynamicBayesianNetworkLoader.DYNAMIC_MODEL_EXTENSION);
    public final static String EXPERIMENT_FILE = "/org.palladiosimulator.simexp.pcm.examples.loadbalancer/elasticity.experiments";
    

    
    
    private final String environmentalStaticsModelFile;
    private final String EnvironmentalDynamicsModelFile; 
    private final String experimentFile;

    
//    private LoadBalancingPcmMeasurementSpecificationBuilder pcmMeasurementSpecBuilder;

    public LoadBalancingSimexpPcmConfiguration(String experimentModelFile, String environmentalStaticsModelFile, String environmentalDynamicsModelFile
            ) {
        // FIXME: replace by constructor parameters instead of using constants
        this.experimentFile = EXPERIMENT_FILE;
        this.environmentalStaticsModelFile = ENVIRONMENTAL_STATICS_MODEL_FILE;
        this.EnvironmentalDynamicsModelFile = ENVIRONMENTAL_DYNAMICS_MODEL_FILE;
        
//        EList<Monitor> monitors = experiment.getInitialModel().getMonitorRepository().getMonitors();
//        this.pcmMeasurementSpecBuilder = new LoadBalancingPcmMeasurementSpecificationBuilder();
//        this.pcmSpecs = Arrays.asList(
//                pcmMeasurementSpecBuilder.buildResponseTimeSpec(monitors),
//                pcmMeasurementSpecBuilder.buildCpuUtilizationSpecOf(monitors, LoadBalancingPcmMeasurementSpecificationBuilder.CPU_SERVER_1_MONITOR),
//                pcmMeasurementSpecBuilder.buildCpuUtilizationSpecOf(monitors, LoadBalancingPcmMeasurementSpecificationBuilder.CPU_SERVER_2_MONITOR));

    }

    @Override
    public String getExperimentFile() {
        return experimentFile;
    }


    @Override
    public String getEnvironmentalStaticsModelFile() {
        return environmentalStaticsModelFile;
    }

    @Override
    public String getEnvironmentalDynamicsModelFile() {
        return EnvironmentalDynamicsModelFile;
    }

    
}
