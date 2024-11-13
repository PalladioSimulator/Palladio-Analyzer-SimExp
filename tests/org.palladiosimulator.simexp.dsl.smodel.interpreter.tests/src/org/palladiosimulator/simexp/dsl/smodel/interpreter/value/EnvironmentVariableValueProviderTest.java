package org.palladiosimulator.simexp.dsl.smodel.interpreter.value;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.envdyn.api.entity.bn.InputValue;
import org.palladiosimulator.envdyn.environment.staticmodel.GroundProbabilisticModel;
import org.palladiosimulator.envdyn.environment.staticmodel.GroundProbabilisticNetwork;
import org.palladiosimulator.envdyn.environment.staticmodel.GroundRandomVariable;
import org.palladiosimulator.envdyn.environment.staticmodel.LocalProbabilisticNetwork;
import org.palladiosimulator.envdyn.environment.staticmodel.ProbabilisticModelRepository;
import org.palladiosimulator.envdyn.environment.staticmodel.StaticmodelFactory;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.EnvVariable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelFactory;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public class EnvironmentVariableValueProviderTest {

    private SmodelCreator smodelCreator;

    @Before
    public void setUp() throws Exception {
        smodelCreator = new SmodelCreator();
    }

    @Test
    public void testGetBoolValueNotSupported() throws Exception {
        EnvironmentVariableValueProvider valueProvider = new EnvironmentVariableValueProvider(null);

        EnvVariable expectedField = SmodelFactory.eINSTANCE.createEnvVariable();
        expectedField.setDataType(DataType.BOOL);
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> {
            valueProvider.getBoolValue(expectedField);
        })
            .withMessage("Boolean Field not supported");
    }

    @Test
    public void testGetDoubleValueForUnknownGVR() throws Exception {
        String expectedGVRId = "_QZ8DcAFHEe6baZPxrKywYw";
        GroundRandomVariable expectedGRV = createGroundRandomVariable(expectedGVRId, "grv");
        ProbabilisticModelRepository staticEnvModelRepo = createStaticEnvModelWith(expectedGRV);
        EnvVariable expectedEnvVar = smodelCreator.createEnvVariable("testEnvVar", DataType.STRING, "unknownGRVId");
        EnvironmentVariableValueProvider valueProvider = new EnvironmentVariableValueProvider(staticEnvModelRepo);
        double expectedEnvVarValue = 0.2;
        InputValue<CategoricalValue> expectedInputValue = createInputValue(expectedGRV,
                String.valueOf(expectedEnvVarValue));
        List<InputValue<CategoricalValue>> expectedValues = new ArrayList<>();
        expectedValues.add(expectedInputValue);
        valueProvider.injectPerceivedEnvironmentStateValues(expectedValues);

        Double actualResult = valueProvider.getDoubleValue(expectedEnvVar);

        assertThat(actualResult).isNull();
    }

    @Test
    public void testGetDoubleValueForUnknownEnvVar() throws Exception {
        GroundRandomVariable grv = createGroundRandomVariable("testId", "gvr");
        ProbabilisticModelRepository staticEnvModelRepo = createStaticEnvModelWith(grv);
        String expectedGVRId = "_QZ8DcAFHEe6baZPxrKywYw";
        GroundRandomVariable expectedGRV = createGroundRandomVariable(expectedGVRId, "expectedGRV");
        EnvVariable expectedEnvVar = smodelCreator.createEnvVariable("testEnvVar", DataType.STRING, expectedGVRId);
        EnvironmentVariableValueProvider valueProvider = new EnvironmentVariableValueProvider(staticEnvModelRepo);
        double expectedEnvVarValue = 0.2;
        InputValue<CategoricalValue> expectedInputValue = createInputValue(expectedGRV,
                String.valueOf(expectedEnvVarValue));
        List<InputValue<CategoricalValue>> expectedValues = new ArrayList<>();
        expectedValues.add(expectedInputValue);
        valueProvider.injectPerceivedEnvironmentStateValues(expectedValues);

        Double actualResult = valueProvider.getDoubleValue(expectedEnvVar);

        assertThat(actualResult).isNull();
    }

    @Test
    public void testGetDoubleValueForSingleGRV() throws Exception {
        String expectedGVRId = "_QZ8DcAFHEe6baZPxrKywYw";
        GroundRandomVariable expectedGRV = createGroundRandomVariable(expectedGVRId, "grv");
        ProbabilisticModelRepository staticEnvModelRepo = createStaticEnvModelWith(expectedGRV);
        EnvVariable expectedEnvVar = smodelCreator.createEnvVariable("testEnvVar", DataType.STRING, expectedGVRId);
        EnvironmentVariableValueProvider valueProvider = new EnvironmentVariableValueProvider(staticEnvModelRepo);
        // CategoricalValue as Double: {0.2,0.1};{0.225,0.9}
        double expectedEnvVarValue = 0.2;
        InputValue<CategoricalValue> expectedInputValue = createInputValue(expectedGRV,
                String.valueOf(expectedEnvVarValue));
        List<InputValue<CategoricalValue>> expectedValues = new ArrayList<>();
        expectedValues.add(expectedInputValue);
        valueProvider.injectPerceivedEnvironmentStateValues(expectedValues);

        Double actualResult = valueProvider.getDoubleValue(expectedEnvVar);

        assertThat(actualResult).isEqualTo(expectedEnvVarValue);
    }

    @Test
    public void testGetIntegerValueForUnknownGVR() throws Exception {
        String expectedGVRId = "_QZ8DcAFHEe6baZPxrKywYw";
        GroundRandomVariable expectedGRV = createGroundRandomVariable(expectedGVRId, "grv");
        ProbabilisticModelRepository staticEnvModelRepo = createStaticEnvModelWith(expectedGRV);
        EnvVariable expectedEnvVar = smodelCreator.createEnvVariable("testEnvVar", DataType.STRING, "unknownGRVId");
        EnvironmentVariableValueProvider valueProvider = new EnvironmentVariableValueProvider(staticEnvModelRepo);
        // CategoricalValue as Integer: {1,0.1};{2,0.9}
        int expectedEnvVarValue = 1;
        InputValue<CategoricalValue> expectedInputValue = createInputValue(expectedGRV,
                String.valueOf(expectedEnvVarValue));
        List<InputValue<CategoricalValue>> expectedValues = new ArrayList<>();
        expectedValues.add(expectedInputValue);
        valueProvider.injectPerceivedEnvironmentStateValues(expectedValues);

        Integer actualResult = valueProvider.getIntegerValue(expectedEnvVar);

        assertThat(actualResult).isNull();
    }

    @Test
    public void testGetIntegerValueForUnknownEnvVar() throws Exception {
        GroundRandomVariable grv = createGroundRandomVariable("testId", "grv");
        ProbabilisticModelRepository staticEnvModelRepo = createStaticEnvModelWith(grv);
        String expectedGVRId = "_QZ8DcAFHEe6baZPxrKywYw";
        GroundRandomVariable expectedGRV = createGroundRandomVariable(expectedGVRId, "expectedGRV");
        EnvVariable expectedEnvVar = smodelCreator.createEnvVariable("testEnvVar", DataType.STRING, expectedGVRId);
        EnvironmentVariableValueProvider valueProvider = new EnvironmentVariableValueProvider(staticEnvModelRepo);
        // CategoricalValue as Integer: {1,0.1};{2,0.9}
        int expectedEnvVarValue = 1;
        InputValue<CategoricalValue> expectedInputValue = createInputValue(expectedGRV,
                String.valueOf(expectedEnvVarValue));
        List<InputValue<CategoricalValue>> expectedValues = new ArrayList<>();
        expectedValues.add(expectedInputValue);
        valueProvider.injectPerceivedEnvironmentStateValues(expectedValues);

        Integer actualResult = valueProvider.getIntegerValue(expectedEnvVar);

        assertThat(actualResult).isNull();
    }

    @Test
    public void testGetIntValueForSingleGRV() throws Exception {
        String expectedGVRId = "_QZ8DcAFHEe6baZPxrKywYw";
        GroundRandomVariable expectedGRV = createGroundRandomVariable(expectedGVRId, "grv");
        ProbabilisticModelRepository staticEnvModelRepo = createStaticEnvModelWith(expectedGRV);
        EnvVariable expectedEnvVar = smodelCreator.createEnvVariable("testEnvVar", DataType.STRING, expectedGVRId);
        EnvironmentVariableValueProvider valueProvider = new EnvironmentVariableValueProvider(staticEnvModelRepo);
        // CategoricalValue as Integer: {1,0.1};{2,0.9}
        int expectedEnvVarValue = 1;
        InputValue<CategoricalValue> expectedInputValue = createInputValue(expectedGRV,
                String.valueOf(expectedEnvVarValue));
        List<InputValue<CategoricalValue>> expectedValues = new ArrayList<>();
        expectedValues.add(expectedInputValue);
        valueProvider.injectPerceivedEnvironmentStateValues(expectedValues);

        Integer actualResult = valueProvider.getIntegerValue(expectedEnvVar);

        assertThat(actualResult).isEqualTo(expectedEnvVarValue);
    }

    @Test
    public void testGetStringValueForUnknownGVR() throws Exception {
        String expectedGVRId = "_QZ8DcAFHEe6baZPxrKywYw";
        GroundRandomVariable expectedGRV = createGroundRandomVariable(expectedGVRId, "grv");
        ProbabilisticModelRepository staticEnvModelRepo = createStaticEnvModelWith(expectedGRV);
        EnvVariable expectedEnvVar = smodelCreator.createEnvVariable("testEnvVar", DataType.STRING, "unknownGRVId");
        EnvironmentVariableValueProvider valueProvider = new EnvironmentVariableValueProvider(staticEnvModelRepo);
        // CategoricalValue as String: {"available",0.1};{"unavailable",0.9}
        String expectedEnvVarValue = "available";
        InputValue<CategoricalValue> expectedInputValue = createInputValue(expectedGRV, expectedEnvVarValue);
        List<InputValue<CategoricalValue>> expectedValues = new ArrayList<>();
        expectedValues.add(expectedInputValue);
        valueProvider.injectPerceivedEnvironmentStateValues(expectedValues);

        String actualResult = valueProvider.getStringValue(expectedEnvVar);

        assertThat(actualResult).isNull();
    }

    @Test
    public void testGetStringValueForUnknownEnvVar() throws Exception {
        GroundRandomVariable grv = createGroundRandomVariable("testId", "grv");
        ProbabilisticModelRepository staticEnvModelRepo = createStaticEnvModelWith(grv);
        String expectedGVRId = "_QZ8DcAFHEe6baZPxrKywYw";
        GroundRandomVariable expectedGRV = createGroundRandomVariable(expectedGVRId, "expectedGRV");
        EnvVariable expectedEnvVar = smodelCreator.createEnvVariable("testEnvVar", DataType.STRING, expectedGVRId);
        EnvironmentVariableValueProvider valueProvider = new EnvironmentVariableValueProvider(staticEnvModelRepo);
        // CategoricalValue as String: {"available",0.1};{"unavailable",0.9}
        String expectedEnvVarValue = "available";
        InputValue<CategoricalValue> expectedInputValue = createInputValue(expectedGRV, expectedEnvVarValue);
        List<InputValue<CategoricalValue>> expectedValues = new ArrayList<>();
        expectedValues.add(expectedInputValue);
        valueProvider.injectPerceivedEnvironmentStateValues(expectedValues);

        String actualResult = valueProvider.getStringValue(expectedEnvVar);

        assertThat(actualResult).isNull();
    }

    @Test
    public void testGetStringValueForSingleGRV() throws Exception {
        String expectedGVRId = "_QZ8DcAFHEe6baZPxrKywYw";
        GroundRandomVariable expectedGRV = createGroundRandomVariable(expectedGVRId, "grv");
        ProbabilisticModelRepository staticEnvModelRepo = createStaticEnvModelWith(expectedGRV);
        EnvVariable expectedEnvVar = smodelCreator.createEnvVariable("testEnvVar", DataType.STRING, expectedGVRId);
        EnvironmentVariableValueProvider valueProvider = new EnvironmentVariableValueProvider(staticEnvModelRepo);
        // CategoricalValue as String: {"available",0.1};{"unavailable",0.9}
        String expectedEnvVarValue = "available";
        InputValue<CategoricalValue> expectedInputValue = createInputValue(expectedGRV, expectedEnvVarValue);
        List<InputValue<CategoricalValue>> expectedValues = new ArrayList<>();
        expectedValues.add(expectedInputValue);
        valueProvider.injectPerceivedEnvironmentStateValues(expectedValues);

        String actualResult = valueProvider.getStringValue(expectedEnvVar);

        assertThat(actualResult).isEqualTo(expectedEnvVarValue);
    }

    // Environment model
    private GroundRandomVariable createGroundRandomVariable(String id, String name) {
        GroundRandomVariable grv = StaticmodelFactory.eINSTANCE.createGroundRandomVariable();
        grv.setId(id);
        grv.setEntityName(name);
        return grv;
    }

    private ProbabilisticModelRepository createStaticEnvModelWith(GroundRandomVariable grv) {
        ProbabilisticModelRepository staticEnvironmentModel = StaticmodelFactory.eINSTANCE
            .createProbabilisticModelRepository();
        GroundProbabilisticNetwork gpn = StaticmodelFactory.eINSTANCE.createGroundProbabilisticNetwork();
        GroundProbabilisticModel pgModel = StaticmodelFactory.eINSTANCE.createGroundProbabilisticModel();
        LocalProbabilisticNetwork localNetwork = StaticmodelFactory.eINSTANCE.createLocalProbabilisticNetwork();
        gpn.getLocalModels()
            .add(pgModel);
        localNetwork.getGroundRandomVariables()
            .add(grv);
        gpn.getLocalProbabilisticModels()
            .add(localNetwork);
        staticEnvironmentModel.getModels()
            .add(gpn);
        return staticEnvironmentModel;
    }

    // others
    private InputValue<CategoricalValue> createInputValue(GroundRandomVariable grv, String value) {
        CategoricalValue expectedCategoricalValue = CategoricalValue.create(value);
        InputValue<CategoricalValue> inputValue = InputValue.create(expectedCategoricalValue, grv);
        return inputValue;
    }

}
