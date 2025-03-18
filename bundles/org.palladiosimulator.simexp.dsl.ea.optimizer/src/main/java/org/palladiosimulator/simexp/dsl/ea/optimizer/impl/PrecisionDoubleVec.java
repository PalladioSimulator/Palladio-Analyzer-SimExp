package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import static java.lang.String.format;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.commons.math3.util.Precision;

import io.jenetics.ext.moea.ElementComparator;
import io.jenetics.ext.moea.ElementDistance;
import io.jenetics.ext.moea.Pareto;
import io.jenetics.ext.moea.Vec;

public class PrecisionDoubleVec implements Vec<double[]>, Serializable {
    private static final long serialVersionUID = 2L;

    private final double epsilon;
    private final double[] _data;

    public PrecisionDoubleVec(double epsilon, double[] data) {
        this.epsilon = epsilon;
        _data = data;
    }

    @Override
    public double[] data() {
        return _data;
    }

    @Override
    public int length() {
        return _data.length;
    }

    @Override
    public ElementComparator<double[]> comparator() {
        return this::cmp;
    }

    private int cmp(final double[] u, final double[] v, final int i) {
        return compareDouble(u[i], v[i]);
    }

    @Override
    public ElementDistance<double[]> distance() {
        return PrecisionDoubleVec::dist;
    }

    private static double dist(final double[] u, final double[] v, final int i) {
        return u[i] - v[i];
    }

    @Override
    public Comparator<double[]> dominance() {
        return this::dominance;
    }

    private int dominance(final double[] u, final double[] v) {
        checkLength(u.length, v.length);

        return Pareto.dominance(u, v, u.length, (a, b, i) -> compareDouble(a[i], b[i]));
    }

    private int compareDouble(double a, double b) {
        return Precision.compareTo(a, b, epsilon);
    }

    private void checkLength(final int i, final int j) {
        if (i != j) {
            throw new IllegalArgumentException(format("Length are not equals: %d != %d.", i, j));
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(_data);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj == this
                || obj instanceof PrecisionDoubleVec && Arrays.equals(((PrecisionDoubleVec) obj)._data, _data);
    }

    @Override
    public String toString() {
        return Arrays.toString(_data);
    }
}
