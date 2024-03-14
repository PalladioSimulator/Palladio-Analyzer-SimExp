package org.palladiosimulator.simexp.dsl.smodel.interpreter.tests;

import static org.junit.Assert.assertEquals;

import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.simexp.dsl.smodel.SmodelStandaloneSetup;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.KnowledgeLookup;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Constant;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Kmodel;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Runtime;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

public class KnowledgeLookupTest {
    public static final String MODEL_NAME_LINE = "modelName = \"name\";";

    private ParseHelper<Kmodel> parserHelper;

    @Before
    public void setUp() {
        Injector injector = new SmodelStandaloneSetup().createInjectorAndDoEMFRegistration();
        parserHelper = injector.getInstance(Key.get(new TypeLiteral<ParseHelper<Kmodel>>() {
        }));
    }

    @Test
    public void testLookupFieldWithIndex() throws Exception {
        String sbTest = MODEL_NAME_LINE + """
                const int a = 2;
                const int b = 1;
                """;
        Kmodel testModel = parserHelper.parse(sbTest);
        assertEquals(2, testModel.getConstants()
            .size());
        Constant b = testModel.getConstants()
            .get(1);
        String sb = MODEL_NAME_LINE + """
                runtime int rint: simple: constants[1];
                """;
        Kmodel model = parserHelper.parse(sb);
        Runtime runtime = model.getRuntimes()
            .get(0);
        KnowledgeLookup rvp = new KnowledgeLookup(testModel);

        Object value = rvp.getValue(runtime);

        Assert.assertEquals(b, value);
    }

    @Test
    public void testLookupFieldWithPredicate() throws Exception {
        String sbTest = MODEL_NAME_LINE + """
                const int a = 2;
                const int b = 1;
                """;
        Kmodel testModel = parserHelper.parse(sbTest);
        assertEquals(2, testModel.getConstants()
            .size());
        Constant b = testModel.getConstants()
            .get(1);
        String sb = MODEL_NAME_LINE + """
                runtime int rint: simple: constants{name="b"};
                """;
        Kmodel model = parserHelper.parse(sb);
        Runtime runtime = model.getRuntimes()
            .get(0);
        KnowledgeLookup rvp = new KnowledgeLookup(testModel);

        Object value = rvp.getValue(runtime);

        Assert.assertEquals(b, value);
    }

    @Test
    public void testLookupFieldName() throws Exception {
        String sbTest = MODEL_NAME_LINE + """
                const int a = 1;
                """;
        Kmodel testModel = parserHelper.parse(sbTest);
        assertEquals(1, testModel.getConstants()
            .size());
        String sb = MODEL_NAME_LINE + """
                runtime int rint: simple: constants[0].name;
                """;
        Kmodel model = parserHelper.parse(sb);
        Runtime runtime = model.getRuntimes()
            .get(0);
        KnowledgeLookup rvp = new KnowledgeLookup(testModel);

        Object value = rvp.getValue(runtime);

        Assert.assertEquals("a", value);
    }
}
