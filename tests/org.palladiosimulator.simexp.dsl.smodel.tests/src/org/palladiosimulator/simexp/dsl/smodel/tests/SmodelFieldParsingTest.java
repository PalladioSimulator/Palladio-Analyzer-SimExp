package org.palladiosimulator.simexp.dsl.smodel.tests;

import javax.inject.Inject;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Constant;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Kmodel;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Probe;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelInjectorProvider;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelTestUtil;

@RunWith(XtextRunner.class)
@InjectWith(SmodelInjectorProvider.class)
public class SmodelFieldParsingTest {
    @Inject
    private ParseHelper<Kmodel> parserHelper;

    @Inject
    private ValidationTestHelper validationTestHelper;

    @Test
    public void parseAllDifferentFieldTypes() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable bool{true, false} condition;
                const int one = 1;
                probe float aliasName : id "someId";
                """;

        Kmodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        EList<Optimizable> variables = model.getOptimizables();
        Assert.assertEquals(1, variables.size());
        EList<Constant> constants = model.getConstants();
        Assert.assertEquals(1, constants.size());
        EList<Probe> probes = model.getProbes();
        Assert.assertEquals(1, probes.size());
    }

    @Test
    public void parseFieldWithInvalidName() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const int 123 = 123;
                """;

        Kmodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertErrorMessages(model, 1, "mismatched input '123' expecting RULE_ID");
    }

    @Test
    public void parseFieldWithTokenAsName() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const int const = 1;
                """;

        Kmodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertErrorMessages(model, 3, "mismatched input 'const' expecting RULE_ID",
                "no viable alternative at input '='", "mismatched input '<EOF>' expecting RULE_ID");
    }

    @Test
    public void parseSameFieldsWithSameName() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const int number = 1;
                const int number = 2;
                """;

        Kmodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertValidationIssues(validationTestHelper, model, 2, "Duplicate Field 'number'",
                "Duplicate Field 'number'");
    }

    @Test
    public void parseDifferentFieldsWithSameName() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable string{"word"} word;
                probe string word : id "someId";
                """;

        Kmodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertValidationIssues(validationTestHelper, model, 2, "Duplicate Field 'word'",
                "Duplicate Field 'word'");
    }

    @Test
    public void parseLocalField() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                if(true){
                    optimizable int variable = {1};
                }
                """;

        Kmodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertErrorMessages(model, 1, "mismatched input 'optimizable' expecting '}'");
    }
}
