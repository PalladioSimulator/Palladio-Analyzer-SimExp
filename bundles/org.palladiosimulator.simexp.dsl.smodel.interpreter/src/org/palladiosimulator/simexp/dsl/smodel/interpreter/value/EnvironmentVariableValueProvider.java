package org.palladiosimulator.simexp.dsl.smodel.interpreter.value;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.envdyn.api.entity.bn.InputValue;
import org.palladiosimulator.envdyn.environment.staticmodel.GroundProbabilisticNetwork;
import org.palladiosimulator.envdyn.environment.staticmodel.GroundRandomVariable;
import org.palladiosimulator.envdyn.environment.staticmodel.LocalProbabilisticNetwork;
import org.palladiosimulator.envdyn.environment.staticmodel.ProbabilisticModelRepository;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.IFieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.EnvVariable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Field;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public class EnvironmentVariableValueProvider implements IFieldValueProvider, PerceivedEnvironmentalStateValueInjector {

    private final ProbabilisticModelRepository staticEnvDynModel;

    // private List<InputValue<CategoricalValue>> values;
    private final Map<GroundRandomVariable, CategoricalValue> valueMap;

    public EnvironmentVariableValueProvider(ProbabilisticModelRepository staticEnvDynModel) {
        this.staticEnvDynModel = staticEnvDynModel;
        // this.values = Collections.emptyList();
        Comparator<GroundRandomVariable> comparator = Comparator.comparing(GroundRandomVariable::getId);
        this.valueMap = new TreeMap<>(comparator);
    }

    @Override
    public Boolean getBoolValue(Field field) {
        throw new UnsupportedOperationException("Boolean Field not supported");
    }

    @Override
    public Double getDoubleValue(Field field) {
        EnvVariable envVariable = (EnvVariable) field;
        GroundRandomVariable groundRandomVariable = findEnvironmentVariable(envVariable);
        if (groundRandomVariable == null) {
            return null;
        }
        String stringCategoricalValue = retrieveCategoricalValue(groundRandomVariable);
        if (stringCategoricalValue == null) {
            return null;
        }
        // TODO: Fehlerbehandlung Value Konvertierung
        Double valueOf = Double.valueOf(stringCategoricalValue);
        return valueOf;
    }

    @Override
    public Integer getIntegerValue(Field field) {
        EnvVariable envVariable = (EnvVariable) field;
        GroundRandomVariable groundRandomVariable = findEnvironmentVariable(envVariable);
        if (groundRandomVariable == null) {
            return null;
        }
        String stringCategoricalValue = retrieveCategoricalValue(groundRandomVariable);
        if (stringCategoricalValue == null) {
            return null;
        }
        Integer valueOf = Integer.valueOf(stringCategoricalValue);
        return valueOf;
    }

    @Override
    public String getStringValue(Field field) {
        EnvVariable envVariable = (EnvVariable) field;
        GroundRandomVariable groundRandomVariable = findEnvironmentVariable(envVariable);
        if (groundRandomVariable == null) {
            return null;
        }
        String stringCategoricalValue = retrieveCategoricalValue(groundRandomVariable);
        if (stringCategoricalValue == null) {
            return null;
        }
        return stringCategoricalValue;
    }

    private GroundRandomVariable findEnvironmentVariable(EnvVariable envVar) {
        EList<GroundProbabilisticNetwork> gpns = staticEnvDynModel.getModels();
        for (GroundProbabilisticNetwork gpn : gpns) {
            EList<LocalProbabilisticNetwork> localProbabilisticNeworks = gpn.getLocalProbabilisticModels();
            for (LocalProbabilisticNetwork localProbNetwork : localProbabilisticNeworks) {
                EList<GroundRandomVariable> groundRandomVariables = localProbNetwork.getGroundRandomVariables();
                for (GroundRandomVariable gvr : groundRandomVariables) {
                    String gvrId = gvr.getId();
                    if (gvrId.equals(envVar.getVariableId())) {
                        return gvr;
                    }
                }
            }
        }
        return null;
    }

    private String retrieveCategoricalValue(GroundRandomVariable groundRandomVariable) {
        CategoricalValue categoricalValue = valueMap.get(groundRandomVariable);
        if (categoricalValue == null) {
            return null;
        }
        /*
         * categoricalValue: {valueString, probability} -> example: String:
         * {available,0.9};{unavailable,0.1} Double:
         * {0.2,0.1};{0.225,0.1};{0.25,0.1};{0.275,0.1};{0.3,0.1};{0.325,0.1};{0.35,0.1};{0.375,0.1}
         * ;{0.4,0.1};{0.425,0.1}
         */
        String stringValue = categoricalValue.toString();
        return stringValue;
    }

    @Override
    public void injectPerceivedEnvironmentStateValues(List<InputValue<CategoricalValue>> currentValues) {
        valueMap.clear();
        for (InputValue<CategoricalValue> value : currentValues) {
            GroundRandomVariable variable = value.getVariable();
            CategoricalValue variableValue = value.getValue();
            valueMap.put(variable, variableValue);
        }
    }

}
