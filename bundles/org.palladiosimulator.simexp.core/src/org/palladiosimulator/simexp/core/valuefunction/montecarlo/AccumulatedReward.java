package org.palladiosimulator.simexp.core.valuefunction.montecarlo;

import java.util.Objects;

public class AccumulatedReward {

    private double total;
    private int accummulations;
    
    public AccumulatedReward() {
        this(0.0, 1);
    }
    
    AccumulatedReward(double total, int accummulations) {
        this.total = total;
        this.accummulations = accummulations;
    }

    public void append(double reward) {
        total += reward;
        accummulations++;
    }

    public void mergeWith(AccumulatedReward accReward) {
        total += accReward.total;
        accummulations += accReward.accummulations;
    }

    public double calculateAverage() {
        return total / accummulations;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accummulations, total);
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
        AccumulatedReward other = (AccumulatedReward) obj;
        return accummulations == other.accummulations
                && Double.doubleToLongBits(total) == Double.doubleToLongBits(other.total);
    }
}
