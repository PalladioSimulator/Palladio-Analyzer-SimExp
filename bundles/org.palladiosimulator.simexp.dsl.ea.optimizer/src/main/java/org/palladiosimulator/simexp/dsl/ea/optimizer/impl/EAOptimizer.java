package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import static io.jenetics.engine.Limits.bySteadyFitness;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.IEAOptimizer;
import org.palladiosimulator.simexp.dsl.ea.api.IOptimizableProvider;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Bounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DoubleLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;

import io.jenetics.AnyChromosome;
import io.jenetics.AnyGene;
import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.Chromosome;
import io.jenetics.DoubleChromosome;
import io.jenetics.Genotype;
import io.jenetics.Mutator;
import io.jenetics.Phenotype;
import io.jenetics.SinglePointCrossover;
import io.jenetics.TournamentSelector;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Codecs;
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
        List<Codec> parsedCodecs = new ArrayList<>();

        Collection<Optimizable> optimizables = optimizableProvider.getOptimizables();
        for (Optimizable currentOpt : optimizables) {
            Bounds optValue = currentOpt.getValues();
            parsedCodecs.add(parseBounds(optValue, optimizableProvider.getExpressionCalculator()));
        }

        Codec<OptimizableChromosome, AnyGene<OptimizableChromosome>> codec = Codec.of(
                Genotype.of(AnyChromosome.of(OptimizableChromosome.getNextChromosomeSupplier(parsedCodecs))),
                gt -> gt.gene()
                    .allele());

        final Engine<AnyGene<OptimizableChromosome>, Double> engine = Engine.builder(OptimizableChromosome::eval, codec)
            .populationSize(500)
            .selector(new TournamentSelector<>(5))
            .offspringSelector(new TournamentSelector<>(5))
            .alterers(new Mutator<>(0.4), new SinglePointCrossover<>(0.8))
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

        System.out.println(statistics);

        for (Pair geno : phenoChromo.chromosomes) {
            Chromosome chromosome = ((Genotype) geno.second()).chromosome();

            if (chromosome instanceof BitChromosome) {
                System.out.println((((Function) geno.first()).apply(geno.second())));

            } else if (chromosome instanceof DoubleChromosome doubleChromo) {
                System.out.println(((Function) geno.first()).apply(geno.second()));
            } else {
                throw new RuntimeException("Unknown chromosome type specified: " + chromosome.getClass());
            }

        }
//      for(phenoChromo.chromosomes)
//      System.out.println("Chromosome: " + phenoChromo.intValue + " " + phenoChromo.boolValue);
//      

        // TODO Auto-generated method stub

    }

    private Codec parseBounds(Bounds bounds, IExpressionCalculator expressionCalculator) {
        if (bounds instanceof RangeBounds rangeBound) {

            return parseOptimizableRangeDouble(expressionCalculator, rangeBound);

        } else if (bounds instanceof SetBounds) {
            // TODO implement
            throw new RuntimeException("not implemented yet");
//            return Codecs.ofScalar(DoubleRange.of(-10.0, 10.0));

        } else {
            throw new OptimizableProcessingException("Couldn't parse the given optimizable: " + bounds);
        }
    }

    private InvertibleCodec<ISeq<Double>, BitGene> parseOptimizableRangeDouble(
            IExpressionCalculator expressionCalculator, RangeBounds rangeBound) {
        double startValue;
        if (rangeBound.getStartValue() instanceof DoubleLiteral literal) {
            startValue = literal.getValue();
        } else {
            startValue = expressionCalculator.calculateDouble(rangeBound.getStartValue());
        }

        double endValue;
        if (rangeBound.getEndValue() instanceof DoubleLiteral literal) {
            endValue = literal.getValue();
        } else {
            endValue = expressionCalculator.calculateDouble(rangeBound.getEndValue());
        }

        double stepSize;
        if (rangeBound.getStepSize() instanceof DoubleLiteral literal) {
            stepSize = literal.getValue();
        } else {
            stepSize = expressionCalculator.calculateDouble(rangeBound.getStepSize());
        }

        assert (startValue < endValue);

        double currentNum = startValue;
        List<Double> numbersInRange = new ArrayList<>();

        while (currentNum < endValue) {
            numbersInRange.add(currentNum);
            currentNum += stepSize;
        }

        ISeq<Double> seqOfNumbersInRange = ISeq.of(numbersInRange);

        return Codecs.ofSubSet(seqOfNumbersInRange);
    }

}
