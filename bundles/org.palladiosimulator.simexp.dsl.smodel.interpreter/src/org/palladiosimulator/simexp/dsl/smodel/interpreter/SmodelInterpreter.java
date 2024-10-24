package org.palladiosimulator.simexp.dsl.smodel.interpreter;

import java.util.List;

import org.apache.commons.math3.util.Precision;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.impl.SmodelAnalyzer;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.impl.SmodelPlaner;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.mape.Analyzer;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.mape.Planner;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.value.ConstantValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.value.FieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.value.OptimizableValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.value.SaveFieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.value.VariableValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;

public class SmodelInterpreter implements Analyzer, Planner {

    private final SmodelAnalyzer smodelAnalyzer;
    private final SmodelPlaner smodelPlaner;

    public SmodelInterpreter(Smodel model, IFieldValueProvider probeValueProvider,
            IFieldValueProvider envVariableValueProvider) {
        IFieldValueProvider optimizableValueProvider = new OptimizableValueProvider();
        ISmodelConfig smodelConfig = new ISmodelConfig() {

            @Override
            public double getEpsilon() {
                return Precision.EPSILON;
            }
        };
        IFieldValueProvider constantValueProvider = new ConstantValueProvider(smodelConfig);
        VariableValueProvider variableValueProvider = new VariableValueProvider(smodelConfig, constantValueProvider,
                probeValueProvider, optimizableValueProvider, envVariableValueProvider);
        IFieldValueProvider fieldValueProvider = new FieldValueProvider(constantValueProvider, variableValueProvider,
                probeValueProvider, optimizableValueProvider, envVariableValueProvider);
        IFieldValueProvider saveFieldValueProvider = new SaveFieldValueProvider(fieldValueProvider);
        this.smodelAnalyzer = new SmodelAnalyzer(model, smodelConfig, saveFieldValueProvider);
        this.smodelPlaner = new SmodelPlaner(model, smodelConfig, saveFieldValueProvider, variableValueProvider);
    }

    @Override
    public boolean analyze() {
        return smodelAnalyzer.analyze();
    }

    @Override
    public List<ResolvedAction> plan() {
        return smodelPlaner.plan();
    }

}
