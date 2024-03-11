package org.palladiosimulator.simexp.dsl.kmodel.tests;

import javax.inject.Inject;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Constant;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Field;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Probe;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Variable;
import org.palladiosimulator.simexp.dsl.kmodel.tests.util.KmodelInjectorProvider;

@RunWith(XtextRunner.class)
@InjectWith(KmodelInjectorProvider.class)
public class KmodelFieldParsingJavaTest {
    @Inject
    private ParseHelper<Kmodel> parserHelper;

    @Inject
    private ValidationTestHelper validationTestHelper;

    @Test
    public void parseAllDifferentFieldTypes() throws Exception {
        String sb = String.join("\n", "var bool{true, false} condition;", "const int one = 1;",
                "probe float aliasName : \"someId\";");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        EList<Field> fields = model.getFields();
        Assert.assertEquals(3, fields.size());
        Field firstField = fields.get(0);
        Assert.assertTrue(firstField instanceof Variable);
        Field secondField = fields.get(1);
        Assert.assertTrue(secondField instanceof Constant);
        Field thirdField = fields.get(2);
        Assert.assertTrue(thirdField instanceof Probe);
    }

    @Test
    public void parseFieldWithInvalidName() throws Exception {
        String sb = String.join("\n", "const int 123 = 123;");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertErrorMessages(model, 1, "mismatched input '123' expecting RULE_ID");
    }

    @Test
    public void parseFieldWithTokenAsName() throws Exception {
        String sb = String.join("\n", "const int const = 1;");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertErrorMessages(model, 3, "mismatched input 'const' expecting RULE_ID",
                "no viable alternative at input '='", "mismatched input ';' expecting RULE_ID");
    }

    @Test
    public void parseSameFieldsWithSameName() throws Exception {
        String sb = String.join("\n", "const int number = 1;", "const int number = 1;");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 2, "Duplicate Field 'number'",
                "Duplicate Field 'number'");
    }

    @Test
    public void parseDifferentFieldsWithSameName() throws Exception {
        String sb = String.join("\n", "var string{\"word\"} word;", "probe string word : \"someId\";");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 2, "Duplicate Field 'word'",
                "Duplicate Field 'word'");
    }

    @Test
    public void parseLocalField() throws Exception {
        String sb = String.join("\n", "if(true){", "var int variable = {1};", "}");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertErrorMessages(model, 1, "mismatched input 'var' expecting '}'");
    }
}
