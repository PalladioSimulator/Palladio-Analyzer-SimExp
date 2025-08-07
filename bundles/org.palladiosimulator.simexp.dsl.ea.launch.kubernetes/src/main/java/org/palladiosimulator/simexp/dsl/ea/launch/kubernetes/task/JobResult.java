package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.palladiosimulator.simexp.core.simulation.IQualityEvaluator.QualityMeasurements;

public class JobResult {
    public enum Status {
        START, COMPLETE, ABORT;
    };

    public String id;
    public String executor_id;
    public int tag;
    public boolean redelivered;
    public int delivery_count;
    public Status status;
    public Double reward;
    public QualityMeasurements qualityMeasurements;
    public String error;
    public Integer return_code;

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id)
            .append(executor_id)
            .append(tag)
            .append(redelivered)
            .append(delivery_count)
            .append(status)
            .append(reward)
            .append(qualityMeasurements)
            .append(error)
            .append(return_code)
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
        JobResult rhs = (JobResult) obj;
        return new EqualsBuilder().append(id, rhs.id)
            .append(executor_id, rhs.executor_id)
            .append(tag, rhs.tag)
            .append(redelivered, rhs.redelivered)
            .append(delivery_count, rhs.delivery_count)
            .append(status, rhs.status)
            .append(reward, rhs.reward)
            .append(qualityMeasurements, rhs.qualityMeasurements)
            .append(error, rhs.error)
            .append(return_code, rhs.return_code)
            .isEquals();
    }

}