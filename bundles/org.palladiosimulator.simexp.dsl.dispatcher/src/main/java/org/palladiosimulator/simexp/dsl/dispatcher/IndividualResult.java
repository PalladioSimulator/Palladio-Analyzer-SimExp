package org.palladiosimulator.simexp.dsl.dispatcher;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.palladiosimulator.simexp.dsl.dispatcher.util.OptimizableValueToString;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public class IndividualResult {
    private final double reward;
    private final List<OptimizableValue<?>> optimizableValues;
    private final String id;

    public IndividualResult(double reward, List<OptimizableValue<?>> optimizableValues, String id) {
        this.reward = reward;
        this.optimizableValues = Collections.unmodifiableList(optimizableValues);
        this.id = id;
    }

    public double getReward() {
        return reward;
    }

    public List<OptimizableValue<?>> getOptimizableValues() {
        return optimizableValues;
    }

    @Override
    public String toString() {
        OptimizableValueToString optimizableValueToString = new OptimizableValueToString();
        String values = optimizableValueToString.asString(optimizableValues);
        return String.format("IR %.3f: %s", reward, values);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(19, 37).append(reward)
            .append(optimizableValues)
            .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        IndividualResult rhs = (IndividualResult) obj;
        return new EqualsBuilder().append(reward, rhs.reward)
            .append(optimizableValues, rhs.optimizableValues)
            .isEquals();
    }

    public String getId() {
        return id;
    }
}