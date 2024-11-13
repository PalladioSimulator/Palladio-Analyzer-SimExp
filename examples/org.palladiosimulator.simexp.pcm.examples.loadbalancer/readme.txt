Probe Specification für PCM; hier muss die measuring point id eingetragen werden, wie diese im UsageScenarioMeasuringPointImpl.java definiert ist


# Monitor: System Response Time
# Measuring point String representation: Usage Scenario: overloadUsageScenario
# PcmMeasurementSpecification unique Measuring point id: Usage Scenario: overloadUsageScenario_Response Time
	# PcmMeasurementSpecBuilder.deriveUniqueId
	
	private static String deriveUniqueId(MeasuringPoint measuringPoint, MetricDescription desc) {
        String stringRepresentation = measuringPoint.getStringRepresentation();
        return String.format("%1s_%2s", stringRepresentation, desc.getName());
    }


# Monitor: System Response Time
# Measuring point: Usage Scenario: EnvironmentPerception; stringRepresentation="Usage Scenario: EnvironmentPerception" aus *.measuringpoint model
