package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.monitorrepository.MeasurementSpecification;
import org.palladiosimulator.monitorrepository.Monitor;
import org.palladiosimulator.simexp.core.util.Pair;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.pcm.datasource.MeasurementSeriesResult.MeasurementSeries;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification.MeasurementAggregator;

public class LoadBalancingPcmMeasurementSpecificationBuilder  implements IPcmMeasurementSpecificationBuilder {
    
    // Monitor specification
    private final static String RESPONSE_TIME_MONITOR = "System Response Time";
    public final static String CPU_SERVER_1_MONITOR = "cpuServer1";
    public final static String CPU_SERVER_2_MONITOR = "cpuServer2";

    private final static Threshold STEADY_STATE_EVALUATOR = Threshold.lessThan(0.1);
    

    public PcmMeasurementSpecification buildResponseTimeSpec(EList<Monitor> monitors) {
        Monitor rtMonitor = findMonitor(monitors, RESPONSE_TIME_MONITOR);
        MeasurementSpecification rtSpec = rtMonitor.getMeasurementSpecifications().get(0);
        return PcmMeasurementSpecification.newBuilder()
                .withName(rtMonitor.getEntityName())
                .measuredAt(rtMonitor.getMeasuringPoint())
                .withMetric(rtSpec.getMetricDescription())
                .useDefaultMeasurementAggregation()
                .withOptionalSteadyStateEvaluator(STEADY_STATE_EVALUATOR)
                .build();
    }
    
    public PcmMeasurementSpecification buildCpuUtilizationSpecOf(EList<Monitor> monitors, String monitorName) {
        Monitor monitor = findMonitor(monitors, monitorName);
        MeasurementSpecification spec = monitor.getMeasurementSpecifications().get(1);
        return PcmMeasurementSpecification.newBuilder()
                .withName(monitor.getEntityName())
                .measuredAt(monitor.getMeasuringPoint())
                .withMetric(spec.getMetricDescription())
                .aggregateMeasurementsBy(getUtilizationAggregator())
                .build();
    }
    
    private Monitor findMonitor(EList<Monitor> monitors, String monitorName) {
        return monitors.stream().filter(m -> m.getEntityName().equals(monitorName))
                       .findFirst()
                       .orElseThrow(() -> new RuntimeException("There is no monitor."));
    }
    
    private MeasurementAggregator getUtilizationAggregator() {
        return new PcmMeasurementSpecification.MeasurementAggregator() {
            
            @Override
            public double aggregate(MeasurementSeries series) {
                double utilization = 0;
                
                List<Pair<Number, Double>> measurements = series.asList();
                for (int i = 0; i < measurements.size() - 1; i++) {
                    Pair<Number, Double> current = measurements.get(i);
                    Pair<Number, Double> next = measurements.get(i + 1);
                    if (isActive(current, next) || isIdle(current, next)) {
                        utilization += getTimeInstant(next) - getTimeInstant(current);
                    }
                }
                
                return computeUtilization(utilization, getTotalTime(measurements));
            }

            private Double getTotalTime(List<Pair<Number, Double>> measurements) {
                int last = measurements.size() - 1;
                return getTimeInstant(measurements.get(last));
            }

            private double computeUtilization(double utilization, Double totalTime) {
                return utilization / totalTime;
            }

            private boolean isActive(Pair<Number, Double> current, Pair<Number, Double> next) {
                return getResourceState(current) > 0 && getResourceState(next) > 0;
            }

            private boolean isIdle(Pair<Number, Double> current, Pair<Number, Double> next) {
                return getResourceState(current) > 0 && getResourceState(next) == 0;
            }
            
            private Integer getResourceState(Pair<Number, Double> measurement) {
                return measurement.getFirst().intValue();
            }
            
            private Double getTimeInstant(Pair<Number, Double> measurement) {
                return measurement.getSecond();
            }
            
        };
    }


}
