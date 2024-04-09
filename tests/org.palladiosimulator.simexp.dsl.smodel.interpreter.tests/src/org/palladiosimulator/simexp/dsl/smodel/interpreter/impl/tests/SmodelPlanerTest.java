package org.palladiosimulator.simexp.dsl.smodel.interpreter.impl.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.palladiosimulator.simexp.dsl.smodel.test.util.EcoreAssert.assertThat;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.IFieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.ResolvedAction;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.impl.SmodelPlaner;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Action;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ActionArguments;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ActionCall;
import org.palladiosimulator.simexp.dsl.smodel.smodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IfStatement;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Parameter;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ParameterValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelFactory;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

public class SmodelPlanerTest {

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
    public void testPlanWithEmptyModel() throws Exception {
        SmodelPlaner smodelPlaner = new SmodelPlaner(smodel, fvp);

        List<ResolvedAction> actualResults = smodelPlaner.plan();

        assertThat(actualResults).isEmpty();
    }

    @Test
    public void testPlanWithSingleActionWithBoolCondition1() throws Exception {
//      String sb = MODEL_NAME_LINE + """
//      action a();
//      if (true) {
//        a();
//      }
//      """;
        Action expectedAction = smodelCreator.createAction("a");
        smodel.getActions()
            .add(expectedAction);
        IfStatement ifStmt = smodelFactory.createIfStatement();
        BoolLiteral boolLiteral = smodelFactory.createBoolLiteral();
        boolLiteral.setTrue(true);
        Expression boolCond = smodelFactory.createExpression();
        boolCond.setLiteral(boolLiteral);
        ifStmt.setCondition(boolCond);
        ActionCall actionCall = smodelFactory.createActionCall();
        actionCall.setActionRef(expectedAction);
        ifStmt.getThenStatements()
            .add(actionCall);
        smodel.getStatements()
            .add(ifStmt);
        SmodelPlaner smodelPlaner = new SmodelPlaner(smodel, fvp);

        List<ResolvedAction> actualResults = smodelPlaner.plan();

        assertThat(actualResults).size()
            .isEqualTo(1);
        ResolvedAction actualResult = actualResults.get(0);
        assertThat(actualResult.getAction()).isEqualTo(expectedAction);
    }

    @Test
    public void testPlanWithSingleActionWithBoolCondition2() throws Exception {
//      String sb = MODEL_NAME_LINE + """
//      action a();
//      if (false) {
//        a();
//      }
//      """;
        Action expectedAction = smodelCreator.createAction("a");
        smodel.getActions()
            .add(expectedAction);
        IfStatement ifStmt = smodelFactory.createIfStatement();
        BoolLiteral boolLiteral = smodelFactory.createBoolLiteral();
        boolLiteral.setTrue(false);
        Expression boolCond = smodelFactory.createExpression();
        boolCond.setLiteral(boolLiteral);
        ifStmt.setCondition(boolCond);
        ActionCall actionCall = smodelFactory.createActionCall();
        actionCall.setActionRef(expectedAction);
        ifStmt.getThenStatements()
            .add(actionCall);
        smodel.getStatements()
            .add(ifStmt);
        SmodelPlaner smodelPlaner = new SmodelPlaner(smodel, fvp);

        List<ResolvedAction> actualResults = smodelPlaner.plan();

        assertThat(actualResults).isEmpty();
    }

    @Test
    public void testPlanWithSingleActionWithBoolCondition3() throws Exception {
//      String sb = MODEL_NAME_LINE + """
//      action a();
//      if (false) {
//        // do nothing
//      } else {
//        a();
//      }
//      """;
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
        SmodelPlaner smodelPlaner = new SmodelPlaner(smodel, fvp);

        List<ResolvedAction> actualResults = smodelPlaner.plan();

        assertThat(actualResults).size()
            .isEqualTo(1);
        ResolvedAction actualResult = actualResults.get(0);
        assertThat(actualResult.getAction()).isEqualTo(expectedAction);
    }

    @Test
    public void testPlanWithSingleActionWithBoolCondition4() throws Exception {
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
        SmodelPlaner smodelPlaner = new SmodelPlaner(smodel, fvp);

        List<ResolvedAction> actualResults = smodelPlaner.plan();

        assertThat(actualResults).size()
            .isEqualTo(1);
        ResolvedAction actualResult = actualResults.get(0);
        assertThat(actualResult.getAction()).isEqualTo(expectedAction);
    }

    @Test
    public void testPlanWithSingleActionWithBoolCondition5() throws Exception {
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
        ActionCall actionCall1 = smodelFactory.createActionCall();
        actionCall1.setActionRef(expectedAction);
        IfStatement ifStmt = createIfStatement();
        ifStmt.getThenStatements()
            .add(actionCall1);
        IfStatement nestedIfStmt = createIfStatement();
        ActionCall actionCall2 = smodelFactory.createActionCall();
        actionCall2.setActionRef(expectedAction);
        nestedIfStmt.getThenStatements()
            .add(actionCall2);
        ifStmt.getThenStatements()
            .add(nestedIfStmt);
        smodel.getStatements()
            .add(ifStmt);
        SmodelPlaner smodelPlaner = new SmodelPlaner(smodel, fvp);

        List<ResolvedAction> actualResults = smodelPlaner.plan();

        assertThat(actualResults).size()
            .isEqualTo(2);
        ResolvedAction actualResult = actualResults.get(0);
        assertThat(actualResult.getAction()).isEqualTo(expectedAction);
        actualResult = actualResults.get(1);
        assertThat(actualResult.getAction()).isEqualTo(expectedAction);
    }

    @Test
    public void testPlanWithSingleActionWithBoolCondition6() throws Exception {
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
        SmodelPlaner smodelPlaner = new SmodelPlaner(smodel, fvp);

        List<ResolvedAction> actualResults = smodelPlaner.plan();

        assertThat(actualResults).size()
            .isEqualTo(1);
        ResolvedAction actualResult = actualResults.get(0);
        assertThat(actualResult.getAction()).isEqualTo(expectedAction);
        ActionArguments actualArgs = actualResult.getAction()
            .getArguments();
        assertThat(actualArgs.getParameters()
            .size()).isEqualTo(1);
        assertThat(actualArgs.getParameters()
            .get(0)
            .getName()).isEqualTo(expectedParameter.getName());
        assertThat(actualArgs.getParameters()
            .get(0)
            .getDataType()).isEqualTo(expectedParameter.getDataType());
        assertThat(actualArgs.getOptimizables()
            .size()).isEqualTo(0);
    }

    @Test
    public void testPlanWithSingleActionWithBoolCondition7() throws Exception {
//  String sb = MODEL_NAME_LINE + """
//  action a(optimizable int {1} opt);
//  if (true) {
//          a();
//  }
//  """;
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
        SmodelPlaner smodelPlaner = new SmodelPlaner(smodel, fvp);

        List<ResolvedAction> actualResults = smodelPlaner.plan();

        assertThat(actualResults).size()
            .isEqualTo(1);
        ResolvedAction actualResult = actualResults.get(0);
        assertThat(actualResult.getAction()).isEqualTo(expectedAction);
        EList<Parameter> actualParamArgs = actualResult.getAction()
            .getArguments()
            .getParameters();
        assertThat(actualParamArgs.size()).isEqualTo(0);
        EList<Optimizable> actualOptimizableArgs = actualResult.getAction()
            .getArguments()
            .getOptimizables();
        assertThat(actualOptimizableArgs.size()).isEqualTo(1);
        Optimizable actualOptimizable = actualOptimizableArgs.get(0);
        assertThat(actualOptimizable.getName()).isEqualTo(expectedOptimizable.getName());
        assertThat(actualOptimizable.getDataType()).isEqualTo(expectedOptimizable.getDataType());
    }

    @Test
    public void testPlanWithSingleActionWithBoolCondition8() throws Exception {
//  String sb = MODEL_NAME_LINE + """
//  action a(optimizable int[1,3] opt);
//  if (true) {
//          a();
//  }
//  """;
        Action expectedAction = smodelCreator.createAction("a");
        RangeBounds expectedBounds = smodelCreator.createRangeBounds(smodelCreator.createIntLiteral(1),
                smodelCreator.createIntLiteral(3), smodelCreator.createIntLiteral(1));
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
        SmodelPlaner smodelPlaner = new SmodelPlaner(smodel, fvp);

        List<ResolvedAction> actualResults = smodelPlaner.plan();

        assertThat(actualResults).size()
            .isEqualTo(1);
        ResolvedAction actualResult = actualResults.get(0);
        assertThat(actualResult.getAction()).isEqualTo(expectedAction);
        EList<Parameter> actualParamArgs = actualResult.getAction()
            .getArguments()
            .getParameters();
        assertThat(actualParamArgs.size()).isEqualTo(0);
        EList<Optimizable> actualOptimizableArgs = actualResult.getAction()
            .getArguments()
            .getOptimizables();
        assertThat(actualOptimizableArgs.size()).isEqualTo(1);
        Optimizable actualOptimizable = actualOptimizableArgs.get(0);
        assertThat(actualOptimizable.getName()).isEqualTo(expectedOptimizable.getName());
        assertThat(actualOptimizable.getDataType()).isEqualTo(expectedOptimizable.getDataType());
    }

    @Test
    public void testPlanWithSingleActionWithBoolCondition9() throws Exception {
//        String sb = MODEL_NAME_LINE + """
//                action a(param int p, optimizable int {1} opt);
//                if (true) {
//                    a(p=1);
//                }
//                """;
        Action expectedAction = smodelCreator.createAction("a");
        ActionArguments expectedActionArguments = expectedAction.getArguments();
        Parameter expectedParameter = smodelCreator.createParameter("p", DataType.INT);
        expectedActionArguments.getParameters()
            .add(expectedParameter);
        SetBounds expectedBounds = smodelCreator.createSetBounds(smodelCreator.createIntLiteral(1));
        Optimizable expectedOptimizable = smodelCreator.createOptimizable("opt", DataType.INT, expectedBounds);
        expectedActionArguments.getOptimizables()
            .add(expectedOptimizable);
        IfStatement exptectedStatement = createIfStatement();
        ActionCall expectedActionCall = smodelCreator.createActionCall(expectedAction);
        ParameterValue expectedParameterValue = smodelCreator.createParameterValue(expectedParameter,
                smodelCreator.createIntLiteral(1));
        expectedActionCall.getArguments()
            .add(expectedParameterValue);
        exptectedStatement.getThenStatements()
            .add(expectedActionCall);
        smodel.getStatements()
            .add(exptectedStatement);
        SmodelPlaner smodelPlaner = new SmodelPlaner(smodel, fvp);

        List<ResolvedAction> actualResults = smodelPlaner.plan();

        assertThat(actualResults).size()
            .isEqualTo(1);
        ResolvedAction actualResult = actualResults.get(0);
        assertThat(actualResult.getAction()).isEqualTo(expectedAction);
        EList<Parameter> actualParamArgs = actualResult.getAction()
            .getArguments()
            .getParameters();
        assertThat(actualParamArgs.size()).isEqualTo(1);
        Parameter actualParam = actualParamArgs.get(0);
        assertThat(actualParam.getName()).isEqualTo(expectedParameter.getName());
        assertThat(actualParam.getDataType()).isEqualTo(expectedParameter.getDataType());
        EList<Optimizable> actualOptimizableArgs = actualResult.getAction()
            .getArguments()
            .getOptimizables();
        assertThat(actualOptimizableArgs.size()).isEqualTo(1);
        Optimizable actualOptimizable = actualOptimizableArgs.get(0);
        assertThat(actualOptimizable.getName()).isEqualTo(expectedOptimizable.getName());
        assertThat(actualOptimizable.getDataType()).isEqualTo(expectedOptimizable.getDataType());
    }

    private IfStatement createIfStatement() {
        Expression condition = smodelCreator.createLiteralBoolExpression(true);
        IfStatement ifStatement = smodelCreator.createIfStatement(condition);
        return ifStatement;
    }

}
