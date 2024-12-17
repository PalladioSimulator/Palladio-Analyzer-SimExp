package org.palladiosimulator.simexp.dsl.ea.optimizer.impl.conversion;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator.OptimizableValue;
import org.palladiosimulator.simexp.dsl.ea.api.IOptimizableProvider;
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.CodecOptimizablePair;
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.EAOptimizer;
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.OptimizableChromosome;
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.OptimizableChromosomeFactory;
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.SingleOptimizableChromosome;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Bounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

import io.jenetics.AnyGene;
import io.jenetics.Phenotype;
import io.jenetics.engine.Codec;

public abstract class AbstractConverter {

    protected static final Logger LOGGER = Logger.getLogger(EAOptimizer.class);

    protected BoundsParser parser;

    protected AbstractConverter(CodecCreator codecCreator) {
        this.parser = new BoundsParser(codecCreator);
    }

    public List<CodecOptimizablePair> parseOptimizables(IOptimizableProvider optimizableProvider) {
        List<CodecOptimizablePair> parsedCodecs = new ArrayList<>();

        for (Optimizable currentOpt : optimizableProvider.getOptimizables()) {
            Codec<?, ?> genotype = toGenotype(currentOpt, optimizableProvider.getExpressionCalculator());
            parsedCodecs.add(new CodecOptimizablePair(genotype, currentOpt));
        }
        return parsedCodecs;
    }

    public Codec<?, ?> toGenotype(Optimizable optimizable, IExpressionCalculator expressionCalculator) {
        DataType dataType = optimizable.getDataType();
        Bounds optValue = optimizable.getValues();
        return parser.parseBounds(optValue, expressionCalculator, dataType);
    }

    public List<OptimizableValue<?>> toPhenoValue(Phenotype<AnyGene<OptimizableChromosome>, Double> phenotype) {
        return toPhenoValue(phenotype, null);
    }

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

        for (SingleOptimizableChromosome singleChromo : phenoChromo.chromosomes) {
            LOGGER.info(singleChromo.getPhenotype());
            finalOptimizableValues.add(singleChromo.toOptimizableValue());
        }

        return finalOptimizableValues;
    }

}
