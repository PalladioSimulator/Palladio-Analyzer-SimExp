package org.palladiosimulator.simexp.pcm.simulator.provider;

import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.pcm.datasource.MeasurementSeriesResult.MeasurementSeries;
import org.palladiosimulator.simexp.pcm.datasource.MeasurementSeriesResult.MeasurementValue;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;

public class SystemExecutionResultTypeMeasurementAggregator implements PcmMeasurementSpecification.MeasurementAggregator {
    
    private static final Logger LOGGER = Logger.getLogger(SystemExecutionResultTypeMeasurementAggregator.class.getName());
    
    private static final String SUCCESS = "SUCCESS";        // refers to literal MetricDescriptionConstants.EXECUTION_RESULT_TYPE_SUCCESS
    

    @Override
    public double aggregate(MeasurementSeries measurements) {
        double numberOfSuccess = 0;
        double successRate = 0; // relative frequency of successful usage scenario executions
        
        List<MeasurementValue> measurementsAsNumbers = measurements.asListOfValues();
        if (measurementsAsNumbers.isEmpty()) {
            LOGGER.error("No measurements available for Metric 'SystemExecutionResultType' from simulation.");
            return Double.NaN;
        }
        for (MeasurementValue number : measurementsAsNumbers) {
            String value = (String) number.getValue();
            if (SUCCESS.equals(value)) {
                numberOfSuccess++;
            }
        }
        
        int overall = measurementsAsNumbers.size();
        successRate = numberOfSuccess / overall;
        LOGGER.debug(String.format("Statistics - Metric 'EXECUTION_RESULT_TYPE_TUPLE': overall: %s, success rate: %s, failure rate: %s"
                , overall, successRate, overall - numberOfSuccess / overall));
        return successRate;
    }

}

