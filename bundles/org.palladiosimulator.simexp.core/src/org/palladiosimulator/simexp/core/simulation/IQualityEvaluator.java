package org.palladiosimulator.simexp.core.simulation;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public interface IQualityEvaluator {
    public class Run {
        private final Map<String, List<Double>> qualityAttributes;

        public Run(Map<String, List<Double>> qualityAttributes) {
            this.qualityAttributes = Collections.unmodifiableMap(qualityAttributes);
        }

        public Map<String, List<Double>> getQualityAttributes() {
            return qualityAttributes;
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37).append(qualityAttributes)
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
            Run rhs = (Run) obj;
            return new EqualsBuilder().append(qualityAttributes, rhs.qualityAttributes)
                .isEquals();
        }
    }

    public class QualityMeasurements {
        private final List<Run> runs;

        public QualityMeasurements(List<Run> runs) {
            this.runs = Collections.unmodifiableList(runs);
        }

        public List<Run> getRuns() {
            return runs;
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(11, 37).append(runs)
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
            QualityMeasurements rhs = (QualityMeasurements) obj;
            return new EqualsBuilder().append(runs, rhs.runs)
                .isEquals();
        }
    }

    QualityMeasurements getQualityMeasurements();
}
