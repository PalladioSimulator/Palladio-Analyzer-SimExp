package org.palladiosimulator.simexp.dsl.smodel.acceptance.tests;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Constant;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Field;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Probe;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelInjectorProvider;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelTestUtil;

@RunWith(XtextRunner.class)
@InjectWith(SmodelInjectorProvider.class)
public class SmodelAcceptanceFieldTest {
    @Inject
    private ParseHelper<Smodel> parserHelper;
    @Inject
    private ValidationTestHelper validationTestHelper;

    @Test
    public void parseMultipleVariableTypes() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const bool cBool = true;
                probe bool pBool: id "ab11";
                optimizable bool{true,false} vBool;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Constant> constants = model.getConstants();
        assertEquals(1, constants.size());
        Field constant = constants.get(0);
        assertEquals("cBool", constant.getName());
        assertEquals(DataType.BOOL, constant.getDataType());
        EList<Probe> probes = model.getProbes();
        Probe probe = probes.get(0);
        assertEquals("pBool", probe.getName());
        assertEquals(DataType.BOOL, probe.getDataType());
        assertEquals("ab11", probe.getIdentifier());
        EList<Optimizable> variables = model.getOptimizables();
        Optimizable variable = variables.get(0);
        assertEquals("vBool", variable.getName());
        assertEquals(DataType.BOOL, variable.getDataType());
    }

    @Test
    public void parseDuplicateConstantNames() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const bool name = true;
                const int name = 1;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertValidationIssues(validationTestHelper, model, 2, "Duplicate Field 'name'",
                "Duplicate Field 'name'");
    }

    @Test
    public void parseDuplicateProbeNames() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe bool name: id "ax10";
                probe int name: id "ax11";
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertValidationIssues(validationTestHelper, model, 2, "Duplicate Field 'name'",
                "Duplicate Field 'name'");
    }

    @Test
    public void parseDuplicateVariableNames() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable bool{true,false} name;
                optimizable int{1,2] name;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertErrorMessages(model, 1, "mismatched input ']' expecting '}'");
    }

    @Test
    public void parseDuplicateConstantEnvVariableName() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const bool name = true;
                envvar bool name : staticId = "statId" dynamicId = "dynId";
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertValidationIssues(validationTestHelper, model, 2, "Duplicate Field 'name'",
                "Duplicate Field 'name'");
    }

    @Test
    public void parseDuplicateProbeAddressing() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe bool name1: id "ax10";
                probe bool name2: id "ax10";
                if (name1 && name2) {}
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, model.eClass(), null, "probe 'name2' duplicate addressing.");
    }

    @Test
    public void parseDuplicateEnvVariableAddressing() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                envvar bool name1 : staticId "statId" dynamicId "dynId";
                envvar bool name2 : staticId "statId" dynamicId "dynId";
                if (name1 && name2) {}
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, model.eClass(), null, "envvar 'name2' duplicate addressing.");
    }
}
