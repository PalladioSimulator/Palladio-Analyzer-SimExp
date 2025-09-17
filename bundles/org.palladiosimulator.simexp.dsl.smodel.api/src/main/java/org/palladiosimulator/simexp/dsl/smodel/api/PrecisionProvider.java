package org.palladiosimulator.simexp.dsl.smodel.api;

public class PrecisionProvider implements IPrecisionProvider {

    private final int places;

    public PrecisionProvider(int places) {
        this.places = places;
    }

    @Override
    public int getPlaces() {
        return places;
    }

    @Override
    public double getPrecision() {
        return Math.pow(10, -(places + 1));
    }
}
