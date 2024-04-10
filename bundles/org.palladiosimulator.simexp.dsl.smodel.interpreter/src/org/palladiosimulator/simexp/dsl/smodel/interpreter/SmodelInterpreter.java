package org.palladiosimulator.simexp.dsl.smodel.interpreter;

import java.util.List;

import org.palladiosimulator.simexp.dsl.smodel.interpreter.impl.SmodelAnalyzer;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.impl.SmodelPlaner;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.mape.Analyzer;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.mape.Planner;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;

public class SmodelInterpreter implements Analyzer, Planner {

    private final SmodelAnalyzer smodelAnalyzer;
    private final SmodelPlaner smodelPlaner;

    public SmodelInterpreter(Smodel model, IFieldValueProvider fieldValueProvider) {
        this.smodelAnalyzer = new SmodelAnalyzer(model, fieldValueProvider);
        this.smodelPlaner = new SmodelPlaner(model, fieldValueProvider);
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
