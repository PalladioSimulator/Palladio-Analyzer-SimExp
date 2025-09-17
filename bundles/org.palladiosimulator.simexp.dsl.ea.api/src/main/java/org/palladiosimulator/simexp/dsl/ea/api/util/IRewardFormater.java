package org.palladiosimulator.simexp.dsl.ea.api.util;

public interface IRewardFormater {
    double getPrecision();

    String asString(double reward);
}
