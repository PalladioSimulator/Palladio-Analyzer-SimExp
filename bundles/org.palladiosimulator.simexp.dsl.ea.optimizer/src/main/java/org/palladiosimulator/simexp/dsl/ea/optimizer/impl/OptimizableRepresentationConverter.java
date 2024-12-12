package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator.OptimizableValue;
import org.palladiosimulator.simexp.dsl.ea.api.IOptimizableProvider;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Bounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

import io.jenetics.AnyGene;
import io.jenetics.Phenotype;
import io.jenetics.engine.Codec;

public class OptimizableRepresentationConverter {

    private final static Logger LOGGER = Logger.getLogger(EAOptimizer.class);

    private BoundsParser parser = new BoundsParser();

    public List<CodecOptimizablePair> parseOptimizables(IOptimizableProvider optimizableProvider) {
        List<CodecOptimizablePair> parsedCodecs = new ArrayList<>();

        for (Optimizable currentOpt : optimizableProvider.getOptimizables()) {
            Codec<?, ?> genotype = toGenotype(currentOpt, optimizableProvider.getExpressionCalculator());
            parsedCodecs.add(new CodecOptimizablePair(genotype, currentOpt));
        }
        return parsedCodecs;
    }

    // Type to Type
    public Codec<?, ?> toGenotype(Optimizable optimizable, IExpressionCalculator expressionCalculator) {
        DataType dataType = optimizable.getDataType();
        Bounds optValue = optimizable.getValues();
        return parser.parseBounds(optValue, expressionCalculator, dataType);
    }

    public List<OptimizableValue<?>> toPhenoValue(Phenotype<AnyGene<OptimizableChromosome>, Double> phenotype) {
        return toPhenoValue(phenotype, null);
    }

    // Value to Value
    public List<OptimizableValue<?>> toPhenoValue(Phenotype<AnyGene<OptimizableChromosome>, Double> phenotype,
            OptimizableChromosomeFactory chromoCreator) {
        OptimizableChromosome phenoChromo = phenotype.genotype()
            .chromosome()
            .gene()
            .allele();

        if (chromoCreator != null) {
            LOGGER.info("PhenoChromo: " + phenoChromo.chromosomes.get(0)
                .genotype() + " " + chromoCreator.eval(phenoChromo));
        }

        List<OptimizableValue<?>> finalOptimizableValues = new ArrayList();

        // review-finding: SingleChromosom violates OO; only data, no methods; should have a
        // getValue()-method and not expose its data
        for (SingleChromosome singleChromo : phenoChromo.chromosomes) {
            LOGGER.info(singleChromo.getPhenotype());
            finalOptimizableValues.add(chromoToValue(singleChromo));
        }

        return finalOptimizableValues;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public OptimizableValue<?> chromoToValue(SingleChromosome singleChromo) {
        return new IEAFitnessEvaluator.OptimizableValue(singleChromo.optimizable(), singleChromo.getPhenotype());
    }

}
