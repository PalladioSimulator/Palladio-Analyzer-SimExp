package org.palladiosimulator.simexp.dsl.smodel.interpreter.util;

import org.palladiosimulator.envdyn.environment.staticmodel.ProbabilisticModelRepository;
import org.palladiosimulator.envdyn.environment.templatevariable.TemplateVariableDefinitions;

public class EnvironmentalDynamicsTestModels {

    private final String simulationKind;
    private final ProbabilisticModelRepository staticEnvModel;
    private final TemplateVariableDefinitions templateVarsModel;

    public EnvironmentalDynamicsTestModels(String simulationKind, TemplateVariableDefinitions templateVarsModel,
            ProbabilisticModelRepository staticEnvModel) {
        this.simulationKind = simulationKind;
        this.templateVarsModel = templateVarsModel;
        this.staticEnvModel = staticEnvModel;
    }

    public String getSimulationKind() {
        return simulationKind;
    }

    public ProbabilisticModelRepository getStaticEnvModel() {
        return staticEnvModel;
    }

    public TemplateVariableDefinitions getTemplateVarsModel() {
        return templateVarsModel;
    }

}
