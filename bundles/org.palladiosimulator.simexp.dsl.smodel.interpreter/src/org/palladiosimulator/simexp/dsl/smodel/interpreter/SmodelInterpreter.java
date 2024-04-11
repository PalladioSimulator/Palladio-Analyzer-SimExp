package org.palladiosimulator.simexp.dsl.smodel.interpreter;

import java.util.List;

import org.palladiosimulator.simexp.dsl.smodel.interpreter.impl.SmodelAnalyzer;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.impl.SmodelPlaner;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.mape.Analyzer;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.mape.Planner;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.value.ConstantValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.value.FieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.value.SaveFieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;

public class SmodelInterpreter implements Analyzer, Planner {

    private final SmodelAnalyzer smodelAnalyzer;
    private final SmodelPlaner smodelPlaner;

    public SmodelInterpreter(Smodel model, IFieldValueProvider probeValueProvider,
            IFieldValueProvider optimizableValueProvider) {
        IFieldValueProvider constantValueProvider = new ConstantValueProvider();
        IFieldValueProvider fieldValueProvider = new FieldValueProvider(probeValueProvider, optimizableValueProvider,
                constantValueProvider);
        IFieldValueProvider saveFieldValueProvider = new SaveFieldValueProvider(fieldValueProvider);
        this.smodelAnalyzer = new SmodelAnalyzer(model, saveFieldValueProvider);
        this.smodelPlaner = new SmodelPlaner(model, saveFieldValueProvider);
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
