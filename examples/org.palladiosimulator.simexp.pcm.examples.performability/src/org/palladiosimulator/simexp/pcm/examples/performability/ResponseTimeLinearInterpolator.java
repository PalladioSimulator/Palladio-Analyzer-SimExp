package org.palladiosimulator.simexp.pcm.examples.performability;

import org.apache.log4j.Logger;

public class ResponseTimeLinearInterpolator {
    
    private static final Logger LOGGER = Logger.getLogger(ResponseTimeLinearInterpolator.class.getName());    
    
    public static double interpolate(double responseTime, double lowerBoundResponseTime, double upperBoundResponseTime) {
        double interpolatedResponseTime = (1/(upperBoundResponseTime - lowerBoundResponseTime)) * (upperBoundResponseTime - responseTime);
        LOGGER.debug(String.format("ResponseTimeLinearInterpolator: raw response time: %s, interpolated response time: %s", responseTime, interpolatedResponseTime));
        return interpolatedResponseTime;
    }

}
