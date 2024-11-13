package org.palladiosimulator.simexp.dsl.smodel.interpreter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.impl.SmodelAnalyzer;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.impl.SmodelDumper;
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
    private static final Logger LOGGER = Logger.getLogger(SmodelInterpreter.class);

    private final SmodelAnalyzer smodelAnalyzer;
    private final SmodelPlaner smodelPlaner;

    public SmodelInterpreter(Smodel model, IFieldValueProvider probeValueProvider,
            IFieldValueProvider envVariableValueProvider) {
        IFieldValueProvider optimizableValueProvider = new OptimizableValueProvider();
        ISmodelConfig smodelConfig = new ISmodelConfig() {

            @Override
            public double getEpsilon() {
                return 0.0001;
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
        List<ResolvedAction> actions = smodelPlaner.plan();
        if (LOGGER.isDebugEnabled()) {
            SmodelDumper dumper = new SmodelDumper(null);
            for (ResolvedAction resolvedAction : actions) {
                StringBuilder sb = new StringBuilder();
                sb.append("resolved action: ");
                sb.append(resolvedAction.getAction()
                    .getName());
                sb.append(" with: ");
                List<String> entries = new ArrayList<>();
                for (Map.Entry<String, Object> entry : resolvedAction.getArguments()
                    .entrySet()) {
                    StringBuilder sbEntry = new StringBuilder();
                    sbEntry.append(entry.getKey());
                    sbEntry.append("=");
                    Object value = entry.getValue();
                    sbEntry.append(dumper.formatValue(value));
                    entries.add(sbEntry.toString());
                }
                sb.append(StringUtils.join(entries, ","));
                LOGGER.debug(sb.toString());
            }
        }
        return actions;
    }

}
