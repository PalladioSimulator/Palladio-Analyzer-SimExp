package org.palladiosimulator.simexp.dsl.kmodel.tests;

import javax.inject.Inject;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;
import org.palladiosimulator.simexp.dsl.kmodel.tests.util.KmodelInjectorProvider;

@RunWith(XtextRunner.class)
@InjectWith(KmodelInjectorProvider.class)
public class KmodelParsingTest {
    @Inject
    private ParseHelper<Kmodel> parserHelper;
    @Inject
    private ValidationTestHelper validationTestHelper;

    @Test
    public void parseWithModelName() throws Exception {
        String sb = String.join("\n", "modelName \"name\";", "if (true) {}");

        Kmodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoIssues(model);
    }

    @Test
    public void parseWithoutModelName() throws Exception {
        String sb = String.join("\n", "if (true) {}");

        Kmodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        validationTestHelper.assertWarning(model, model.eClass(), null, "No modelName given.");
    }

    @Test
    public void parseEmptyModelName() throws Exception {
        String sb = String.join("\n", "modelName \"\";", "if (true) {}");

        Kmodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        validationTestHelper.assertWarning(model.getModelName(), model.getModelName()
            .eClass(), null, "Empty modelName.");
    }
}
