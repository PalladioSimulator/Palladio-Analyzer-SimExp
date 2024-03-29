package org.palladiosimulator.simexp.dsl.smodel.tests;

import static org.palladiosimulator.simexp.dsl.smodel.tests.util.EcoreAssert.assertThat;

import javax.inject.Inject;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.EnvVariable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelFactory;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelPackage;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelInjectorProvider;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelTestUtil;

@RunWith(XtextRunner.class)
@InjectWith(SmodelInjectorProvider.class)
public class SmodelEnvVariableParsingTest {
    @Inject
    private ParseHelper<Smodel> parserHelper;
    @Inject
    private ValidationTestHelper validationTestHelper;

    @Test
    public void parseSingleBool() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                envvar bool varName : variableId = "statId";
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EnvVariable exptectedVariable = SmodelFactory.eINSTANCE.createEnvVariable();
        exptectedVariable.setName("varName");
        exptectedVariable.setDataType(DataType.BOOL);
        exptectedVariable.setVariableId("statId");
        assertThat(model.getEnvVariables()).containsExactlyInAnyOrder(exptectedVariable);
    }

    @Test
    public void parseSingleInt() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                envvar int varName : variableId = "statId";
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EnvVariable exptectedVariable = SmodelFactory.eINSTANCE.createEnvVariable();
        exptectedVariable.setName("varName");
        exptectedVariable.setDataType(DataType.INT);
        exptectedVariable.setVariableId("statId");
        assertThat(model.getEnvVariables()).containsExactlyInAnyOrder(exptectedVariable);
    }

    @Test
    public void parseSingleFloat() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                envvar float varName : variableId = "statId";
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EnvVariable exptectedVariable = SmodelFactory.eINSTANCE.createEnvVariable();
        exptectedVariable.setName("varName");
        exptectedVariable.setDataType(DataType.FLOAT);
        exptectedVariable.setVariableId("statId");
        assertThat(model.getEnvVariables()).containsExactlyInAnyOrder(exptectedVariable);
    }

    @Test
    public void parseSingleString() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                envvar string varName : variableId = "statId";
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EnvVariable exptectedVariable = SmodelFactory.eINSTANCE.createEnvVariable();
        exptectedVariable.setName("varName");
        exptectedVariable.setDataType(DataType.STRING);
        exptectedVariable.setVariableId("statId");
        assertThat(model.getEnvVariables()).containsExactlyInAnyOrder(exptectedVariable);
    }

    @Test
    public void parseTwoEnvVariables() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                envvar bool varName1 : variableId = "statId1";
                envvar int varName2 : variableId = "statId2";
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EnvVariable exptectedVariable1 = SmodelFactory.eINSTANCE.createEnvVariable();
        exptectedVariable1.setName("varName1");
        exptectedVariable1.setDataType(DataType.BOOL);
        exptectedVariable1.setVariableId("statId1");
        EnvVariable exptectedVariable2 = SmodelFactory.eINSTANCE.createEnvVariable();
        exptectedVariable2.setName("varName2");
        exptectedVariable2.setDataType(DataType.INT);
        exptectedVariable2.setVariableId("statId2");
        assertThat(model.getEnvVariables()).containsExactlyInAnyOrder(exptectedVariable1, exptectedVariable2);
    }

    @Test
    public void parseUnusedInt() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                envvar int one : variableId = "1";
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertWarning(model, SmodelPackage.Literals.SMODEL, null,
                "The EnvVariable 'one' is never used.");
    }

    @Test
    public void parseWithoutId() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                envvar bool varName : ;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertErrorMessages(model, 1, "mismatched input ';' expecting 'variableId'");
    }
}
