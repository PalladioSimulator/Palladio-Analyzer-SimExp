package org.palladiosimulator.simexp.dsl.smodel.interpreter.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.palladiosimulator.simexp.dsl.smodel.test.util.EcoreAssert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.IFieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.ResolvedAction;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.SmodelInterpreter;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Action;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ActionCall;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IfStatement;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelFactory;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

public class SModelInterpreterTest {

    private SmodelInterpreter interpreter;
    private Smodel smodel;
    private SmodelCreator smodelCreator;

    @Mock
    private IFieldValueProvider fvp;
    @Mock
    private IFieldValueProvider optimizableValueProvider;

    @Before
    public void setUp() {
        initMocks(this);
        smodel = SmodelFactory.eINSTANCE.createSmodel();
        smodelCreator = new SmodelCreator();
        interpreter = new SmodelInterpreter(smodel, fvp, optimizableValueProvider);
    }

    @Test
    public void testAnalyzeWithSingleActionWithBoolCondition1() throws Exception {
//      String sb = MODEL_NAME_LINE + """
//      action a();
//      if (true) {
//        a();
//      }
//      """;
        Action expectedAction = smodelCreator.createAction("a");
        smodel.getActions()
            .add(expectedAction);
        Expression condition = smodelCreator.createLiteralBoolExpression(true);
        IfStatement ifStmt = smodelCreator.createIfStatement(condition);
        ActionCall expectedActionCall = smodelCreator.createActionCall(expectedAction);
        ifStmt.getThenStatements()
            .add(expectedActionCall);
        smodel.getStatements()
            .add(ifStmt);

        boolean actualResult = interpreter.analyze();

        assertThat(actualResult).isTrue();
    }

    @Test
    public void testAnalyzeWithSingleActionWithBoolCondition2() throws Exception {
//      String sb = MODEL_NAME_LINE + """
//      action a();
//      if (false) {
//        a();
//      }
//      """;
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
        boolean actualResult = interpreter.analyze();

        assertThat(actualResult).isFalse();
    }

    @Test
    public void testPlanWithSingleActionWithBoolCondition() throws Exception {
//      String sb = MODEL_NAME_LINE + """
//      action a();
//      if (true) {
//        a();
//      }
//      """;
        Action expectedAction = smodelCreator.createAction("a");
        smodel.getActions()
            .add(expectedAction);
        Expression condition = smodelCreator.createLiteralBoolExpression(true);
        IfStatement ifStmt = smodelCreator.createIfStatement(condition);
        ActionCall actionCall = smodelCreator.createActionCall(expectedAction);
        actionCall.setActionRef(expectedAction);
        ifStmt.getThenStatements()
            .add(actionCall);
        smodel.getStatements()
            .add(ifStmt);

        List<ResolvedAction> actualResults = interpreter.plan();

        assertThat(actualResults).size()
            .isEqualTo(1);
        ResolvedAction actualResult = actualResults.get(0);
        assertThat(actualResult.getAction()).isEqualTo(expectedAction);
    }

    @Test
    public void testPlanWithMultipleActionsWithBoolCondition() throws Exception {
//      String sb = MODEL_NAME_LINE + """
//      action a();
//      if (true) {
//        a();
//        a();
//      }
//      """;
        Action expectedAction = smodelCreator.createAction("a");
        smodel.getActions()
            .add(expectedAction);
        Expression condition = smodelCreator.createLiteralBoolExpression(true);
        IfStatement ifStmt = smodelCreator.createIfStatement(condition);
        ActionCall actionCall1 = smodelCreator.createActionCall(expectedAction);
        actionCall1.setActionRef(expectedAction);
        ifStmt.getThenStatements()
            .add(actionCall1);
        ActionCall actionCall2 = smodelCreator.createActionCall(expectedAction);
        actionCall2.setActionRef(expectedAction);
        ifStmt.getThenStatements()
            .add(actionCall2);
        smodel.getStatements()
            .add(ifStmt);

        List<ResolvedAction> actualResults = interpreter.plan();

        assertThat(actualResults.size()).isEqualTo(2);
        assertThat(actualResults.get(0)
            .getAction()).isEqualTo(expectedAction);
        assertThat(actualResults.get(1)
            .getAction()).isEqualTo(expectedAction);
    }

}
