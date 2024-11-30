package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import static io.jenetics.engine.Limits.bySteadyFitness;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator.OptimizableValue;
import org.palladiosimulator.simexp.dsl.ea.api.IEAOptimizer;
import org.palladiosimulator.simexp.dsl.ea.api.IOptimizableProvider;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.smodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Bounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DoubleLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;

import io.jenetics.AnyChromosome;
import io.jenetics.AnyGene;
import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.Mutator;
import io.jenetics.Phenotype;
import io.jenetics.SinglePointCrossover;
import io.jenetics.TournamentSelector;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.engine.InvertibleCodec;
import io.jenetics.util.ISeq;

public class EAOptimizer implements IEAOptimizer {
    private final static Logger LOGGER = Logger.getLogger(EAOptimizer.class);

    @Override
    public void optimize(IOptimizableProvider optimizableProvider, IEAFitnessEvaluator fitnessEvaluator,
            IEAEvolutionStatusReceiver evolutionStatusReceiver) {
        LOGGER.info("EA running...");
        List<CodecOptimizablePair> parsedCodecs = new ArrayList<>();

        for (Optimizable currentOpt : optimizableProvider.getOptimizables()) {
            Bounds optValue = currentOpt.getValues();
            parsedCodecs.add(new CodecOptimizablePair(
                    parseBounds(optValue, optimizableProvider.getExpressionCalculator()), currentOpt));
        }

        Codec<OptimizableChromosome, AnyGene<OptimizableChromosome>> codec = Codec.of(
                Genotype.of(AnyChromosome
                    .of(OptimizableChromosome.getNextChromosomeSupplier(parsedCodecs, fitnessEvaluator))),
                gt -> gt.gene()
                    .allele());

        final Engine<AnyGene<OptimizableChromosome>, Double> engine = Engine.builder(OptimizableChromosome::eval, codec)
            .populationSize(500)
            .selector(new TournamentSelector<>(5))
            .offspringSelector(new TournamentSelector<>(5))
            .alterers(new Mutator<>(0.2), new SinglePointCrossover<>(0.6))
            .build();

        final EvolutionStatistics<Double, ?> statistics = EvolutionStatistics.ofNumber();

        final Phenotype<AnyGene<OptimizableChromosome>, Double> phenotype = engine.stream()
            .limit(bySteadyFitness(20))
            .limit(100)
            .peek(statistics)
            .collect(EvolutionResult.toBestPhenotype());

        OptimizableChromosome phenoChromo = phenotype.genotype()
            .chromosome()
            .gene()
            .allele();

        System.out.println(phenotype);

        System.out.println("PhenoChromo: " + phenoChromo.chromosomes.get(0)
            .second() + " " + OptimizableChromosome.eval(phenoChromo));

        List<OptimizableValue<?>> finalOptimizableValues = new ArrayList();

        for (SingleChromosome singleChromo : phenoChromo.chromosomes) {
            System.out.println(singleChromo.first()
                .apply(singleChromo.second()));
            finalOptimizableValues.add(new IEAFitnessEvaluator.OptimizableValue(singleChromo.third(),
                    new DecoderEncodingPair(singleChromo.first(), singleChromo.second())));
        }

        evolutionStatusReceiver.reportStatus(finalOptimizableValues, phenotype.fitness());

        System.out.println(statistics);
    }

    private Codec parseBounds(Bounds bounds, IExpressionCalculator expressionCalculator) {
        if (bounds instanceof RangeBounds rangeBound) {

            return parseOptimizableRangeDouble(expressionCalculator, rangeBound);

        } else if (bounds instanceof SetBounds setBound) {
            Expression firstExpression = setBound.getValues()
                .get(0);
            if (firstExpression instanceof DoubleLiteral) {
                return parseOptimizableSetDouble(expressionCalculator, setBound);
            } else if (firstExpression instanceof BoolLiteral) {
                return parseOptimizableSetBoolean(expressionCalculator, setBound);
            } else {
                throw new OptimizableProcessingException("Couldn't parse the given optimizable: " + bounds);
            }

        } else {
            throw new OptimizableProcessingException("Couldn't parse the given optimizable: " + bounds);
        }
    }

    private InvertibleCodec<ISeq<Double>, BitGene> parseOptimizableRangeDouble(
            IExpressionCalculator expressionCalculator, RangeBounds rangeBound) {
        double startValue = expressionCalculator.calculateDouble(rangeBound.getStartValue());
        double endValue = expressionCalculator.calculateDouble(rangeBound.getEndValue());
        double stepSize = expressionCalculator.calculateDouble(rangeBound.getStepSize());

        assert (startValue < endValue);

        double currentNum = startValue;
        List<Double> numbersInRange = new ArrayList<>();

        while (currentNum < endValue) {
            numbersInRange.add(currentNum);
            currentNum += stepSize;
        }

        ISeq<Double> seqOfNumbersInRange = ISeq.of(numbersInRange);

        return OneHotEncodingCodecHelper.createCodecOfSubSet(seqOfNumbersInRange, 0.1);

    }

    private InvertibleCodec<ISeq<Double>, BitGene> parseOptimizableSetDouble(IExpressionCalculator expressionCalculator,
            SetBounds setBound) {
        List<Double> elementSet = new ArrayList<>();
        // TODO Hier sollte am besten schon zuvor klar sein, welcher datentyp; oder es sollte
        // ausgeschlossen werden
        // können, dass es mehrere verschiedene Datentypen in der Ergebnisliste gibt
        for (Expression expression : setBound.getValues()) {
            elementSet.add(expressionCalculator.calculateDouble(expression));
        }
        ISeq<Double> seqOfNumbersInSet = ISeq.of(elementSet);
        return OneHotEncodingCodecHelper.createCodecOfSubSet(seqOfNumbersInSet, 0.1);
    }

    private InvertibleCodec<Boolean, BitGene> parseOptimizableSetBoolean(IExpressionCalculator expressionCalculator,
            SetBounds setBound) {
        if (setBound.getValues()
            .size() != 2) {
            throw new RuntimeException("Found a boolean optimization point with less or more than two possible states");
        }

        return InvertibleCodec.of(Genotype.of(BitChromosome.of(1, 0.5)), gt -> gt.chromosome()
            .gene()
            .allele(), val -> {
                if (val) {
                    return Genotype.of(BitChromosome.of(1, 1));
                } else {
                    return Genotype.of(BitChromosome.of(1, 0));
                }
            });
    }

}
