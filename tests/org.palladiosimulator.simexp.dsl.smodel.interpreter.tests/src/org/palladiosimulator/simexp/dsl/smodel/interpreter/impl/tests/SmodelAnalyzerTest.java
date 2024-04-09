package org.palladiosimulator.simexp.dsl.smodel.interpreter.impl.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

import org.eclipse.emf.common.util.EList;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.IFieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.impl.SmodelAnalyzer;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Action;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ActionArguments;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ActionCall;
import org.palladiosimulator.simexp.dsl.smodel.smodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IfStatement;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Parameter;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelFactory;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

public class SmodelAnalyzerTest {

    private final SmodelFactory smodelFactory = SmodelFactory.eINSTANCE;

    private SmodelCreator smodelCreator;
    @Mock
    IFieldValueProvider fvp;

    @Before
    public void setUp() {
        initMocks(this);
        smodelCreator = new SmodelCreator();
    }

    @Test
    public void testAnalyzeWithEmptyModel() throws Exception {
        Smodel smodel = smodelFactory.createSmodel();
        SmodelAnalyzer smodelAnalyzer = new SmodelAnalyzer(smodel, fvp);

        boolean actualResult = smodelAnalyzer.analyze();

        assertThat(actualResult).isFalse();
    }

    @Test
    public void testAnalyzeWithSingleActionWithBoolCondition1() throws Exception {
//        String sb = MODEL_NAME_LINE + """
//                action a();
//                if (true) {
//                  a();
//                }
//                """;
        Smodel smodel = smodelFactory.createSmodel();
        Action expectedAction = smodelCreator.createAction("a");
        smodel.getActions()
            .add(expectedAction);
        IfStatement ifStmt = createIfStatement();
        smodel.getStatements()
            .add(ifStmt);
        SmodelAnalyzer smodelAnalyzer = new SmodelAnalyzer(smodel, fvp);

        boolean actualResult = smodelAnalyzer.analyze();

        assertThat(actualResult).isTrue();
    }

    @Test
    public void testAnalyzeWithSingleActionWithBoolCondition2() throws Exception {
//        String sb = MODEL_NAME_LINE + """
//                action a();
//                if (false) {
//                  a();
//                }
//                """;
        Smodel smodel = smodelFactory.createSmodel();
        Action expectedAction = smodelCreator.createAction("a");
        smodel.getActions()
            .add(expectedAction);
        IfStatement ifStmt = smodelFactory.createIfStatement();
        BoolLiteral boolLiteral = smodelFactory.createBoolLiteral();
        boolLiteral.setTrue(false);
        Expression boolCond = smodelFactory.createExpression();
        boolCond.setLiteral(boolLiteral);
        ifStmt.setCondition(boolCond);
        smodel.getStatements()
            .add(ifStmt);
        SmodelAnalyzer smodelAnalyzer = new SmodelAnalyzer(smodel, fvp);

        boolean actualResult = smodelAnalyzer.analyze();

        assertThat(actualResult).isFalse();
    }

    @Test
    public void testAnalyzeWithSingleActionWithBoolCondition3() throws Exception {
//        String sb = MODEL_NAME_LINE + """
//                action a();
//                if (false) {
//                  // do nothing
//                } else {
//                  a();
//                }
//                """;
        Smodel smodel = smodelFactory.createSmodel();
        Action expectedAction = smodelCreator.createAction("a");
        smodel.getActions()
            .add(expectedAction);
        IfStatement ifStmt = smodelFactory.createIfStatement();
        BoolLiteral boolLiteral = smodelFactory.createBoolLiteral();
        boolLiteral.setTrue(false);
        Expression boolCond = smodelFactory.createExpression();
        boolCond.setLiteral(boolLiteral);
        ifStmt.setCondition(boolCond);
        ActionCall elseActionCall = smodelFactory.createActionCall();
        elseActionCall.setActionRef(expectedAction);
        ifStmt.getElseStatements()
            .add(elseActionCall);
        smodel.getStatements()
            .add(ifStmt);
        SmodelAnalyzer smodelAnalyzer = new SmodelAnalyzer(smodel, fvp);

        boolean actualResult = smodelAnalyzer.analyze();

        assertThat(actualResult).isTrue();
    }

    @Test
    public void testAnalyzeWithSingleActionWithBoolCondition4() throws Exception {
//        String sb = MODEL_NAME_LINE + """
//                action a();
//                if (true) {
//                    if (true) {
//                        a();
//                    }
//                }
//                """;
        Smodel smodel = smodelFactory.createSmodel();
        Action expectedAction = smodelCreator.createAction("a");
        smodel.getActions()
            .add(expectedAction);
        IfStatement ifStmt = createIfStatement();
        IfStatement nestedIfStmt = createIfStatement();
        ActionCall actionCall = smodelFactory.createActionCall();
        nestedIfStmt.getThenStatements()
            .add(actionCall);
        ifStmt.getThenStatements()
            .add(nestedIfStmt);
        smodel.getStatements()
            .add(nestedIfStmt);
        SmodelAnalyzer smodelAnalyzer = new SmodelAnalyzer(smodel, fvp);

        boolean actualResult = smodelAnalyzer.analyze();

        assertThat(actualResult).isTrue();
    }

    @Test
    public void testAnalyzeWithSingleActionWithBoolCondition5() throws Exception {
//        String sb = MODEL_NAME_LINE + """
//                action a();
//                if (true) {
//                    a();
//                    if (true) {
//                        a();
//                    }
//                }
//                """;
        Smodel smodel = smodelFactory.createSmodel();
        Action expectedAction = smodelCreator.createAction("a");
        smodel.getActions()
            .add(expectedAction);
        ActionCall actionCall = smodelFactory.createActionCall();
        IfStatement ifStmt = createIfStatement();
        ifStmt.getThenStatements()
            .add(actionCall);
        IfStatement nestedIfStmt = createIfStatement();
        nestedIfStmt.getThenStatements()
            .add(actionCall);
        ifStmt.getThenStatements()
            .add(nestedIfStmt);
        smodel.getStatements()
            .add(nestedIfStmt);
        SmodelAnalyzer smodelAnalyzer = new SmodelAnalyzer(smodel, fvp);

        boolean actualResult = smodelAnalyzer.analyze();

        assertThat(actualResult).isTrue();
    }

    @Test
    public void testAnalyzeWithSingleActionWithBoolCondition6() throws Exception {
//        String sb = MODEL_NAME_LINE + """
//                action a(param int p);
//                if (true) {
//                    a(p=1);
//                }
//                """;
        Smodel smodel = smodelFactory.createSmodel();
        Action expectedAction = smodelCreator.createAction("a");
        Parameter param = smodelFactory.createParameter();
        param.setDataType(DataType.INT);
        param.setName("p");
        ActionArguments actionArgs = smodelFactory.createActionArguments();
        EList<Parameter> parameters = actionArgs.getParameters();
        parameters.add(param);
        expectedAction.setArguments(actionArgs);
        smodel.getActions()
            .add(expectedAction);
        ActionCall actionCall = smodelFactory.createActionCall();
        IfStatement ifStmt = createIfStatement();
        ifStmt.getThenStatements()
            .add(actionCall);
        smodel.getStatements()
            .add(ifStmt);
        SmodelAnalyzer smodelAnalyzer = new SmodelAnalyzer(smodel, fvp);

        boolean actualResult = smodelAnalyzer.analyze();

        assertThat(actualResult).isTrue();
    }

    @Test
    public void testAnalyzeWithSingleActionWithBoolCondition7() throws Exception {
//        String sb = MODEL_NAME_LINE + """
//                action a(param int p, optimizable int {1,2} opt);
//                if (true) {
//                    a(p=1);
//                }
//                """;
        Smodel smodel = smodelFactory.createSmodel();
        Action expectedAction = smodelCreator.createAction("a");
        Parameter param = smodelFactory.createParameter();
        param.setDataType(DataType.INT);
        param.setName("p");
        ActionArguments actionArgs = smodelFactory.createActionArguments();
        EList<Parameter> parameters = actionArgs.getParameters();
        parameters.add(param);
        Optimizable opt = smodelFactory.createOptimizable();
        opt.setDataType(DataType.INT);
        opt.setName("opt");
        SetBounds bounds = smodelFactory.createSetBounds();
        IntLiteral literal1 = smodelFactory.createIntLiteral();
        literal1.setValue(1);
        IntLiteral literal2 = smodelFactory.createIntLiteral();
        literal2.setValue(2);
        bounds.getValues()
            .add(0, literal1);
        bounds.getValues()
            .add(1, literal2);
        opt.setValues(bounds);
        EList<Optimizable> optimizables = actionArgs.getOptimizables();
        optimizables.add(opt);
        expectedAction.setArguments(actionArgs);
        smodel.getActions()
            .add(expectedAction);
        ActionCall actionCall = smodelFactory.createActionCall();
        IfStatement ifStmt = createIfStatement();
        ifStmt.getThenStatements()
            .add(actionCall);
        smodel.getStatements()
            .add(ifStmt);
        SmodelAnalyzer smodelAnalyzer = new SmodelAnalyzer(smodel, fvp);

        boolean actualResult = smodelAnalyzer.analyze();

        assertThat(actualResult).isTrue();
    }

    private IfStatement createIfStatement() {
        Expression condition = smodelCreator.createLiteralBoolExpression(true);
        IfStatement ifStatement = smodelCreator.createIfStatement(condition);
        return ifStatement;
    }

}
