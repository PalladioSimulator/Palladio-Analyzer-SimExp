package org.palladiosimulator.simexp.dsl.ea.optimizer.pareto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.ea.api.IndividualResult;
import org.palladiosimulator.simexp.dsl.smodel.api.IPrecisionProvider;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

import io.jenetics.Optimize;
import io.jenetics.util.ISeq;

public class ParetoSetCollectorTest {
    private static final double EPSILON = 0.0001;

    @Mock
    private IAverageProvider averageProvider;
    @Mock
    private IPrecisionProvider precisionProvider;

    private IndividualResult a;
    private IndividualResult b;
    private IndividualResult c;
    private IndividualResult d;
    private IndividualResult e;
    private IndividualResult f;
    private IndividualResult g;
    private IndividualResult h;
    private IndividualResult i;
    private IndividualResult j;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(precisionProvider.getPrecision()).thenReturn(EPSILON);

        SmodelCreator smodelCreator = new SmodelCreator();
        Optimizable optimizable = smodelCreator.createOptimizable("o", DataType.STRING, null);
        List<OptimizableValue<?>> optimizableValuesA = Collections
            .singletonList(new OptimizableValue<>(optimizable, "a"));
        List<OptimizableValue<?>> optimizableValuesB = Collections
            .singletonList(new OptimizableValue<>(optimizable, "b"));
        List<OptimizableValue<?>> optimizableValuesC = Collections
            .singletonList(new OptimizableValue<>(optimizable, "c"));
        List<OptimizableValue<?>> optimizableValuesD = Collections
            .singletonList(new OptimizableValue<>(optimizable, "d"));
        List<OptimizableValue<?>> optimizableValuesE = Collections
            .singletonList(new OptimizableValue<>(optimizable, "e"));
        List<OptimizableValue<?>> optimizableValuesF = Collections
            .singletonList(new OptimizableValue<>(optimizable, "f"));
        List<OptimizableValue<?>> optimizableValuesG = Collections
            .singletonList(new OptimizableValue<>(optimizable, "g"));
        List<OptimizableValue<?>> optimizableValuesH = Collections
            .singletonList(new OptimizableValue<>(optimizable, "h"));
        List<OptimizableValue<?>> optimizableValuesI = Collections
            .singletonList(new OptimizableValue<>(optimizable, "i"));
        List<OptimizableValue<?>> optimizableValuesJ = Collections
            .singletonList(new OptimizableValue<>(optimizable, "j"));

        // All points:
        // A(1.0, 7.0)
        // B(2.0, 6.0)
        // C(3.0, 5.0)
        // D(4.0, 4.0)
        // E(5.0, 3.0)
        // F(6.0, 2.0)
        // G(7.0, 1.0)
        // H(2.0, 2.0)
        // I(5.0, 5.0)
        // J(0.0, 8.0)
        a = createIndividualResult(1.0, optimizableValuesA);
        b = createIndividualResult(1.0, optimizableValuesB);
        c = createIndividualResult(1.0, optimizableValuesC);
        d = createIndividualResult(1.0, optimizableValuesD);
        e = createIndividualResult(1.0, optimizableValuesE);
        f = createIndividualResult(1.0, optimizableValuesF);
        g = createIndividualResult(1.0, optimizableValuesG);
        h = createIndividualResult(1.0, optimizableValuesH);
        i = createIndividualResult(1.0, optimizableValuesI);
        j = createIndividualResult(1.0, optimizableValuesJ);
        when(averageProvider.getAverages(a)).thenReturn(buildAverages(1, 7));
        when(averageProvider.getAverages(b)).thenReturn(buildAverages(2, 6));
        when(averageProvider.getAverages(c)).thenReturn(buildAverages(3, 5));
        when(averageProvider.getAverages(d)).thenReturn(buildAverages(4, 4));
        when(averageProvider.getAverages(e)).thenReturn(buildAverages(5, 3));
        when(averageProvider.getAverages(f)).thenReturn(buildAverages(6, 2));
        when(averageProvider.getAverages(g)).thenReturn(buildAverages(7, 1));
        when(averageProvider.getAverages(h)).thenReturn(buildAverages(2, 2));
        when(averageProvider.getAverages(i)).thenReturn(buildAverages(5, 5));
        when(averageProvider.getAverages(j)).thenReturn(buildAverages(0, 8));
    }

    @Test
    public void paretoFrontMinimization() {
        // y\x -0- -1- -2- -3- -4- -5- -6- -7-
        // -----------------------------------
        // 8 | [J] --- --- --- --- --- --- ---
        // 7 | --- [A] --- --- --- --- --- ---
        // 6 | --- --- -B- --- --- --- --- ---
        // 5 | --- --- --- -C- --- -I- --- ---
        // 4 | --- --- --- --- -D- --- --- ---
        // 3 | --- --- --- --- --- -E- --- ---
        // 2 | --- --- [H] --- --- --- -F- ---
        // 1 | --- --- --- --- --- --- --- [G]
        // -----------------------------------
        // ___ -0- -1- -2- -3- -4- -5- -6- -7-
        //
        // Pareto front (non-dominated points, minimization):
        // A(1.0, 7.0)
        // J(0.0, 8.0)
        // H(2.0, 2.0)
        // G(7.0, 1.0)
        Stream<IndividualResult> resultStream = buildResultStream(Optimize.MINIMUM);
        Collector<IndividualResult, ?, ISeq<IndividualResult>> collector = ParetoSetCollector.create(precisionProvider,
                averageProvider, s -> Double::compare, Optimize.MINIMUM);

        ISeq<IndividualResult> actualResult = resultStream.collect(collector);

        assertThat(actualResult).containsExactlyInAnyOrder(a, h, j, g);
    }

    @Test
    public void paretoFrontMaximization() {
        // y\x -0- -1- -2- -3- -4- -5- -6- -7-
        // -----------------------------------
        // 8 | [J] --- --- --- --- --- --- ---
        // 7 | --- [A] --- --- --- --- --- ---
        // 6 | --- --- [B] --- --- --- --- ---
        // 5 | --- --- --- -C- --- [I] --- ---
        // 4 | --- --- --- --- -D- --- --- ---
        // 3 | --- --- --- --- --- -E- --- ---
        // 2 | --- --- -H- --- --- --- [F] ---
        // 1 | --- --- --- --- --- --- --- [G]
        // -----------------------------------
        // ___ -0- -1- -2- -3- -4- -5- -6- -7-
        //
        // Pareto front (non-dominated points, maximization):
        // A(1.0, 7.0)
        // B(2.0, 6.0)
        // I(5.0, 5.0)
        // F(6.0, 2.0)
        // G(7.0, 1.0)
        // J(0.0, 8.0)
        Stream<IndividualResult> resultStream = buildResultStream(Optimize.MAXIMUM);
        Collector<IndividualResult, ?, ISeq<IndividualResult>> collector = ParetoSetCollector.create(precisionProvider,
                averageProvider, s -> Double::compare, Optimize.MAXIMUM);

        ISeq<IndividualResult> actualResult = resultStream.collect(collector);

        assertThat(actualResult).containsExactlyInAnyOrder(a, b, i, f, g, j);
    }

    private Stream<IndividualResult> buildResultStream(Optimize optimize) {
        Stream<IndividualResult> resultStream = Stream.of(a, b, c, d, e, f, g, h, i, j);
        return resultStream;
    }

    private Optional<Map<String, Double>> buildAverages(double one, double two) {
        Map<String, Double> averages = new HashMap<>();
        averages.put("qa1", one);
        averages.put("qa2", two);
        return Optional.of(averages);
    }

    private IndividualResult createIndividualResult(double fitness, List<OptimizableValue<?>> optimizableValues) {
        return new IndividualResult(fitness, optimizableValues);
    }
}
