package org.palladiosimulator.simexp.dsl.smodel.interpreter.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.smodel.SmodelStandaloneSetup;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.IFieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Constant;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Variable;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

public class SmodelDumperTest {
    private static final String MODEL_NAME_LINE = "modelName = \"name\";";

    private SmodelDumper dumper;

    private ParseHelper<Smodel> parserHelper;

    @Mock
    private IFieldValueProvider fieldValueProvider;

    @Before
    public void setUp() {
        initMocks(this);

        Injector injector = new SmodelStandaloneSetup().createInjectorAndDoEMFRegistration();
        parserHelper = injector.getInstance(Key.get(new TypeLiteral<ParseHelper<Smodel>>() {
        }));

        dumper = new SmodelDumper(fieldValueProvider);
    }

    @Test
    public void testBoolExpression1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = true;
                """;
        Smodel model = parserHelper.parse(sb);
        Constant constant = getFirstConstant(model);

        String actualDump = dumper.doSwitch(constant.getValue());

        assertThat(actualDump).isEqualTo("true");
    }

    @Test
    public void testBoolOrExpression2() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = true || false;
                """;
        Smodel model = parserHelper.parse(sb);
        Constant constant = getFirstConstant(model);

        String actualDump = dumper.doSwitch(constant.getValue());

        assertThat(actualDump).isEqualTo("true || false");
    }

    @Test
    public void testBoolComplementExpression1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = !false;
                """;
        Smodel model = parserHelper.parse(sb);
        Constant constant = getFirstConstant(model);

        String actualDump = dumper.doSwitch(constant.getValue());

        assertThat(actualDump).isEqualTo("!false");
    }

    @Test
    public void testBoolEqualExpression1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = true == true;
                """;
        Smodel model = parserHelper.parse(sb);
        Constant constant = getFirstConstant(model);

        String actualDump = dumper.doSwitch(constant.getValue());

        assertThat(actualDump).isEqualTo("true == true");
    }

    @Test
    public void testBoolEqualExpressionDouble1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = 1.0 == 1.0;
                """;
        Smodel model = parserHelper.parse(sb);
        Constant constant = getFirstConstant(model);

        String actualDump = dumper.doSwitch(constant.getValue());

        assertThat(actualDump).isEqualTo("1.000000000000000000 == 1.000000000000000000");
    }

    @Test
    public void testBoolEqualExpressionString1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = "a" == "a";
                """;
        Smodel model = parserHelper.parse(sb);
        Constant constant = getFirstConstant(model);

        String actualDump = dumper.doSwitch(constant.getValue());

        assertThat(actualDump).isEqualTo("\"a\" == \"a\"");
    }

    @Test
    public void testStringExpressionVarRef() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int cvalue = 2;
                var int value = cvalue;
                """;
        Smodel model = parserHelper.parse(sb);
        Constant constant = getFirstConstant(model);
        Variable variable = model.getVariables()
            .get(0);
        when(fieldValueProvider.getIntegerValue(constant)).thenReturn(((IntLiteral) constant.getValue()
            .getLiteral()).getValue());

        String actualDump = dumper.doSwitch(variable.getValue());

        assertThat(actualDump).isEqualTo("<cvalue>2");
    }

    private Constant getFirstConstant(Smodel model) {
        EList<Constant> constants = model.getConstants();
        Constant constant = constants.get(0);
        return constant;
    }
}
