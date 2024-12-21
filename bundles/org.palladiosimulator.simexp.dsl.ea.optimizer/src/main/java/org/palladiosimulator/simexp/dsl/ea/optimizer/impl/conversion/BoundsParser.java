package org.palladiosimulator.simexp.dsl.ea.optimizer.impl.conversion;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.OptimizableProcessingException;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Bounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;

import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.engine.Codec;
import io.jenetics.engine.InvertibleCodec;
import io.jenetics.util.ISeq;

public class BoundsParser {

    private CodecCreator codecCreator;

    public BoundsParser(CodecCreator codecCreator) {
        this.codecCreator = codecCreator;
    }

    public Codec parseBounds(Bounds bounds, IExpressionCalculator expressionCalculator, DataType dataType) {
        if (bounds instanceof RangeBounds rangeBound) {
            switch (dataType) {
            case DOUBLE:
                return parseOptimizableRangeDouble(expressionCalculator, rangeBound);
            case INT:
                return parseOptimizableRangeInteger(expressionCalculator, rangeBound);
            default:
                throw new OptimizableProcessingException("Couldn't parse the given optimizable: " + bounds);
            }

        } else if (bounds instanceof SetBounds setBound) {
            Expression firstExpression = setBound.getValues()
                .get(0);
            switch (dataType) {
            case DOUBLE:
                return parseOptimizableSetDouble(expressionCalculator, setBound);
            case INT:
                return parseOptimizableSetInteger(expressionCalculator, setBound);
            case BOOL:
                return parseOptimizableSetBoolean(expressionCalculator, setBound);
            default:
                throw new OptimizableProcessingException("Couldn't parse the given optimizable: " + bounds);
            }

        } else {
            throw new OptimizableProcessingException("Couldn't parse the given optimizable: " + bounds);
        }
    }

    public InvertibleCodec<ISeq<Double>, BitGene> parseOptimizableRangeDouble(
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

        return codecCreator.createCodecOfSubSet(seqOfNumbersInRange, 0.1);
    }

    public InvertibleCodec<ISeq<Integer>, BitGene> parseOptimizableRangeInteger(
            IExpressionCalculator expressionCalculator, RangeBounds rangeBound) {
        int startValue = expressionCalculator.calculateInteger(rangeBound.getStartValue());
        int endValue = expressionCalculator.calculateInteger(rangeBound.getEndValue());
        int stepSize = expressionCalculator.calculateInteger(rangeBound.getStepSize());

        assert (startValue < endValue);

        int currentNum = startValue;
        List<Integer> numbersInRange = new ArrayList<>();

        while (currentNum < endValue) {
            numbersInRange.add(currentNum);
            currentNum += stepSize;
        }

        ISeq<Integer> seqOfNumbersInRange = ISeq.of(numbersInRange);

        return codecCreator.createCodecOfSubSet(seqOfNumbersInRange, 0.1);
    }

    public InvertibleCodec<ISeq<Double>, BitGene> parseOptimizableSetDouble(IExpressionCalculator expressionCalculator,
            SetBounds setBound) {
        List<Double> elementSet = new ArrayList<>();
        for (Expression expression : setBound.getValues()) {
            elementSet.add(expressionCalculator.calculateDouble(expression));
        }
        ISeq<Double> seqOfNumbersInSet = ISeq.of(elementSet);
        return codecCreator.createCodecOfSubSet(seqOfNumbersInSet, 0.1);
    }

    private InvertibleCodec<ISeq<Boolean>, BitGene> parseOptimizableSetBoolean(
            IExpressionCalculator expressionCalculator, SetBounds setBound) {
        if (setBound.getValues()
            .size() != 2) {
            throw new RuntimeException("Found a boolean optimization point with less or more than two possible states");
        }

        return InvertibleCodec.of(Genotype.of(BitChromosome.of(1, 0.5)), gt -> ISeq.of(gt.chromosome()
            .gene()
            .allele()), val -> {
                if (val.size() > 1) {
                    throw new RuntimeException("Not allowed to carry more than one value in bool chromosome");
                }

                if (val.get(0)) {
                    return Genotype.of(BitChromosome.of(1, 1));
                } else {
                    return Genotype.of(BitChromosome.of(1, 0));
                }
            });
    }

    public InvertibleCodec<ISeq<Integer>, BitGene> parseOptimizableSetInteger(
            IExpressionCalculator expressionCalculator, SetBounds setBound) {
        List<Integer> elementSet = new ArrayList<>();
        for (Expression expression : setBound.getValues()) {
            elementSet.add(expressionCalculator.calculateInteger(expression));
        }
        ISeq<Integer> seqOfNumbersInSet = ISeq.of(elementSet);
        return codecCreator.createCodecOfSubSet(seqOfNumbersInSet, 0.1);
    }
}
