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
import org.palladiosimulator.simexp.dsl.smodel.interpreter.value.OptimizableValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.value.impl.FieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.value.impl.SaveFieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.value.impl.StaticValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.value.impl.VariableValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;

public class SmodelInterpreter implements Analyzer, Planner, IResettable {
    private static final Logger LOGGER = Logger.getLogger(SmodelInterpreter.class);

    private final SmodelAnalyzer smodelAnalyzer;
    private final SmodelPlaner smodelPlaner;
    private final VariableValueProvider variableValueProvider;
    private final StaticValueProvider staticProbeValueProvider;
    private final StaticValueProvider staticEnvVariableValueProvider;
    private final StaticValueProvider staticOptimizableValueProvider;

    public SmodelInterpreter(Smodel model, IFieldValueProvider probeValueProvider,
            IFieldValueProvider envVariableValueProvider, OptimizableValueProvider optimizableValueProvider) {
        ISmodelConfig smodelConfig = new DefaultSmodelConfig();
        IFieldValueProvider constantValueProvider = new ConstantValueProvider(smodelConfig);
        this.staticProbeValueProvider = new StaticValueProvider(probeValueProvider, model.getProbes());
        this.staticEnvVariableValueProvider = new StaticValueProvider(envVariableValueProvider,
                model.getEnvVariables());
        this.staticOptimizableValueProvider = new StaticValueProvider(optimizableValueProvider,
                model.getOptimizables());
        variableValueProvider = new VariableValueProvider(smodelConfig, constantValueProvider, staticProbeValueProvider,
                staticOptimizableValueProvider, staticEnvVariableValueProvider);
        IFieldValueProvider fieldValueProvider = new FieldValueProvider(constantValueProvider, variableValueProvider,
                staticProbeValueProvider, staticOptimizableValueProvider, staticEnvVariableValueProvider);
        IFieldValueProvider saveFieldValueProvider = new SaveFieldValueProvider(fieldValueProvider);
        this.smodelAnalyzer = new SmodelAnalyzer(model, smodelConfig, saveFieldValueProvider);
        this.smodelPlaner = new SmodelPlaner(model, smodelConfig, saveFieldValueProvider, variableValueProvider);
    }

    @Override
    public void reset() {
        LOGGER.info("reset strategy variables");
        variableValueProvider.reset();
    }

    @Override
    public boolean analyze() {
        // Ensure dynamic values keep their values during an analyze/plan cycle
        staticProbeValueProvider.assignStatic();
        staticEnvVariableValueProvider.assignStatic();
        staticOptimizableValueProvider.assignStatic();
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
