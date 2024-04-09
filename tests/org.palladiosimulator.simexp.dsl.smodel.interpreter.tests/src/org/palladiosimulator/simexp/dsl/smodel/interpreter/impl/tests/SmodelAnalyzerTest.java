package org.palladiosimulator.simexp.dsl.smodel.interpreter.impl.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.IFieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.impl.SmodelAnalyzer;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Action;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ActionArguments;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ActionCall;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IfStatement;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Parameter;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ParameterValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelFactory;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

public class SmodelAnalyzerTest {

    private final SmodelFactory smodelFactory = SmodelFactory.eINSTANCE;

    private SmodelCreator smodelCreator;
    private Smodel smodel;
    @Mock
    IFieldValueProvider fvp;

    @Before
    public void setUp() {
        initMocks(this);
        smodel = smodelFactory.createSmodel();
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
        Action expectedAction = smodelCreator.createAction("a");
        smodel.getActions()
            .add(expectedAction);
        IfStatement ifStmt = createIfStatement();
        ActionCall expectedActionCall = smodelCreator.createActionCall(expectedAction);
        ifStmt.getThenStatements()
            .add(expectedActionCall);
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
        Action expectedAction = smodelCreator.createAction("a");
        smodel.getActions()
            .add(expectedAction);
        Expression condition = smodelCreator.createLiteralBoolExpression(false);
        IfStatement ifStmt = smodelCreator.createIfStatement(condition);
        ActionCall expectedActionCall = smodelCreator.createActionCall(expectedAction);
        ifStmt.getThenStatements()
            .add(expectedActionCall);
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
        Action expectedAction = smodelCreator.createAction("a");
        smodel.getActions()
            .add(expectedAction);
        Expression condition = smodelCreator.createLiteralBoolExpression(false);
        IfStatement ifStmt = smodelCreator.createIfStatement(condition);
        ActionCall expectedActionCall = smodelCreator.createActionCall(expectedAction);
        ifStmt.getElseStatements()
            .add(expectedActionCall);
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
        Action expectedAction = smodelCreator.createAction("a");
        smodel.getActions()
            .add(expectedAction);
        IfStatement ifStmt = createIfStatement();
        IfStatement nestedIfStmt = createIfStatement();
        ActionCall expectedActionCall = smodelCreator.createActionCall(expectedAction);
        nestedIfStmt.getThenStatements()
            .add(expectedActionCall);
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
        Action expectedAction = smodelCreator.createAction("a");
        smodel.getActions()
            .add(expectedAction);
        ActionCall expectedActionCall = smodelCreator.createActionCall(expectedAction);
        IfStatement ifStmt = createIfStatement();
        ifStmt.getThenStatements()
            .add(expectedActionCall);
        IfStatement nestedIfStmt = createIfStatement();
        nestedIfStmt.getThenStatements()
            .add(expectedActionCall);
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
        Action expectedAction = smodelCreator.createAction("a");
        ActionArguments expectedActionArguments = expectedAction.getArguments();
        Parameter expectedParameter = smodelCreator.createParameter("pd", DataType.INT);
        expectedActionArguments.getParameters()
            .add(expectedParameter);
        IfStatement exptectedStatement = createIfStatement();
        ActionCall expectedActionCall = smodelCreator.createActionCall(expectedAction);
        ParameterValue expectedParameterValue = smodelCreator.createParameterValue(expectedParameter,
                smodelCreator.createDoubleLiteral(1.0));
        expectedActionCall.getArguments()
            .add(expectedParameterValue);
        exptectedStatement.getThenStatements()
            .add(expectedActionCall);
        smodel.getStatements()
            .add(exptectedStatement);

        SmodelAnalyzer smodelAnalyzer = new SmodelAnalyzer(smodel, fvp);

        boolean actualResult = smodelAnalyzer.analyze();

        assertThat(actualResult).isTrue();
    }

    @Test
    public void testAnalyzeWithSingleActionWithBoolCondition7() throws Exception {
        // String sb = MODEL_NAME_LINE + """
        // action a(optimizable int {1} opt);
        // if (true) {
//              a();
        // }
        // """;
        Action expectedAction = smodelCreator.createAction("a");
        SetBounds expectedBounds = smodelCreator.createSetBounds(smodelCreator.createIntLiteral(1));
        Optimizable expectedOptimizable = smodelCreator.createOptimizable("opt", DataType.INT, expectedBounds);
        ActionArguments expectedActionArguments = expectedAction.getArguments();
        expectedActionArguments.getOptimizables()
            .add(expectedOptimizable);
        IfStatement exptectedStatement = createIfStatement();
        ActionCall expectedActionCall = smodelCreator.createActionCall(expectedAction);
        exptectedStatement.getThenStatements()
            .add(expectedActionCall);
        smodel.getStatements()
            .add(exptectedStatement);
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
