package org.palladiosimulator.simexp.dsl.smodel.tests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.samePropertyValuesAs;

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
                envvar bool varName : staticId "statId" dynamicId "dynId";
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EnvVariable exptectedVariable = SmodelFactory.eINSTANCE.createEnvVariable();
        exptectedVariable.setName("varName");
        exptectedVariable.setDataType(DataType.BOOL);
        exptectedVariable.setStaticId("statId");
        exptectedVariable.setDynamicId("dynId");
        assertThat(model.getEnvVariables(), contains(samePropertyValuesAs(exptectedVariable)));
    }

    @Test
    public void parseSingleInt() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                envvar int varName : staticId "statId" dynamicId "dynId";
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EnvVariable exptectedVariable = SmodelFactory.eINSTANCE.createEnvVariable();
        exptectedVariable.setName("varName");
        exptectedVariable.setDataType(DataType.INT);
        exptectedVariable.setStaticId("statId");
        exptectedVariable.setDynamicId("dynId");
        assertThat(model.getEnvVariables(), contains(samePropertyValuesAs(exptectedVariable)));
    }

    @Test
    public void parseSingleFloat() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                envvar float varName : staticId "statId" dynamicId "dynId";
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EnvVariable exptectedVariable = SmodelFactory.eINSTANCE.createEnvVariable();
        exptectedVariable.setName("varName");
        exptectedVariable.setDataType(DataType.FLOAT);
        exptectedVariable.setStaticId("statId");
        exptectedVariable.setDynamicId("dynId");
        assertThat(model.getEnvVariables(), contains(samePropertyValuesAs(exptectedVariable)));
    }

    @Test
    public void parseSingleString() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                envvar string varName : staticId "statId" dynamicId "dynId";
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EnvVariable exptectedVariable = SmodelFactory.eINSTANCE.createEnvVariable();
        exptectedVariable.setName("varName");
        exptectedVariable.setDataType(DataType.STRING);
        exptectedVariable.setStaticId("statId");
        exptectedVariable.setDynamicId("dynId");
        assertThat(model.getEnvVariables(), contains(samePropertyValuesAs(exptectedVariable)));
    }

    @Test
    public void parseTwoEnvVariables() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                envvar bool varName1 : staticId "statId1" dynamicId "dynId1";
                envvar int varName2 : staticId "statId2" dynamicId "dynId2";
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EnvVariable exptectedVariable1 = SmodelFactory.eINSTANCE.createEnvVariable();
        exptectedVariable1.setName("varName1");
        exptectedVariable1.setDataType(DataType.BOOL);
        exptectedVariable1.setStaticId("statId1");
        exptectedVariable1.setDynamicId("dynId1");
        EnvVariable exptectedVariable2 = SmodelFactory.eINSTANCE.createEnvVariable();
        exptectedVariable2.setName("varName2");
        exptectedVariable2.setDataType(DataType.INT);
        exptectedVariable2.setStaticId("statId2");
        exptectedVariable2.setDynamicId("dynId2");
        assertThat(model.getEnvVariables(),
                contains(samePropertyValuesAs(exptectedVariable1), samePropertyValuesAs(exptectedVariable2)));
    }

    @Test
    public void parseWithoutStaticId() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                envvar bool varName : dynamicId "dynId";
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertErrorMessages(model, 1, "mismatched input 'dynamicId' expecting 'staticId'");
    }

    @Test
    public void parseWithoutDynamicId() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                envvar bool varName : staticId "statId" ;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertErrorMessages(model, 1, "mismatched input ';' expecting 'dynamicId'");
    }

    @Test
    public void parseWithoutIds() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                envvar bool varName : ;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertErrorMessages(model, 1, "mismatched input ';' expecting 'staticId'");
    }
}