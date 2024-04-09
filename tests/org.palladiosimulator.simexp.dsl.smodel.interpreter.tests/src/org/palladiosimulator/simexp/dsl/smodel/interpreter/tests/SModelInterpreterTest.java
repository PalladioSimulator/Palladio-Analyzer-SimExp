package org.palladiosimulator.simexp.dsl.smodel.interpreter.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.palladiosimulator.simexp.dsl.smodel.test.util.EcoreAssert.assertThat;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.smodel.SmodelStandaloneSetup;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.IFieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.ResolvedAction;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.SmodelInterpreter;
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
import org.palladiosimulator.simexp.dsl.smodel.smodel.ParameterValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelFactory;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

public class SModelInterpreterTest {
    public static final String MODEL_NAME_LINE = "modelName = \"name\";";

    private final SmodelFactory smodelFactory = SmodelFactory.eINSTANCE;

    private ParseHelper<Smodel> parserHelper;
    private SmodelInterpreter interpreter;

    private SmodelCreator smodelCreator;
    @Mock
    IFieldValueProvider fvp;

//    @Inject
//    private ValidationTestHelper validationTestHelper;

    @Before
    public void setUp() {
        initMocks(this);
        smodelCreator = new SmodelCreator();
        Injector injector = new SmodelStandaloneSetup().createInjectorAndDoEMFRegistration();
        injector.injectMembers(this);
        parserHelper = injector.getInstance(Key.get(new TypeLiteral<ParseHelper<Smodel>>() {
        }));
    }

    ///
    @Test
    public void testAnalyzeWithEmptyModel() throws Exception {
        Smodel smodel = smodelFactory.createSmodel();
        SmodelInterpreter interpreter = new SmodelInterpreter(smodel, fvp);

        boolean actualResult = interpreter.analyze();

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
        Action expectedAction = createSimpleAction("a");
        smodel.getActions()
            .add(expectedAction);
        IfStatement ifStmt = smodelFactory.createIfStatement();
        BoolLiteral boolLiteral = smodelFactory.createBoolLiteral();
        boolLiteral.setTrue(true);
        Expression boolCond = smodelFactory.createExpression();
        boolCond.setLiteral(boolLiteral);
        ifStmt.setCondition(boolCond);
        smodel.getStatements()
            .add(ifStmt);
        SmodelInterpreter interpreter = new SmodelInterpreter(smodel, fvp);

        boolean actualResult = interpreter.analyze();

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
        Action expectedAction = createSimpleAction("a");
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
        SmodelInterpreter interpreter = new SmodelInterpreter(smodel, fvp);

        boolean actualResult = interpreter.analyze();

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
        Action expectedAction = createSimpleAction("a");
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
        SmodelInterpreter interpreter = new SmodelInterpreter(smodel, fvp);

        boolean actualResult = interpreter.analyze();

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
        Action expectedAction = createSimpleAction("a");
        smodel.getActions()
            .add(expectedAction);
        IfStatement ifStmt = smodelFactory.createIfStatement();
        BoolLiteral boolLiteral = smodelFactory.createBoolLiteral();
        boolLiteral.setTrue(true);
        Expression boolCond = smodelFactory.createExpression();
        boolCond.setLiteral(boolLiteral);
        ifStmt.setCondition(boolCond);
        IfStatement nestedIfStmt = smodelFactory.createIfStatement();
        BoolLiteral boolLiteral2 = smodelFactory.createBoolLiteral();
        boolLiteral2.setTrue(true);
        Expression boolCond2 = smodelFactory.createExpression();
        boolCond2.setLiteral(boolLiteral2);
        nestedIfStmt.setCondition(boolCond2);
        ActionCall actionCall = smodelFactory.createActionCall();
        nestedIfStmt.getThenStatements()
            .add(actionCall);
        ifStmt.getThenStatements()
            .add(nestedIfStmt);
        smodel.getStatements()
            .add(nestedIfStmt);

        SmodelInterpreter interpreter = new SmodelInterpreter(smodel, fvp);
        boolean actualResult = interpreter.analyze();

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
        Action expectedAction = createSimpleAction("a");
        smodel.getActions()
            .add(expectedAction);
        ActionCall actionCall = smodelFactory.createActionCall();
        IfStatement ifStmt = smodelFactory.createIfStatement();
        BoolLiteral boolLiteral = smodelFactory.createBoolLiteral();
        boolLiteral.setTrue(true);
        Expression boolCond = smodelFactory.createExpression();
        boolCond.setLiteral(boolLiteral);
        ifStmt.setCondition(boolCond);
        ifStmt.getThenStatements()
            .add(actionCall);
        IfStatement nestedIfStmt = smodelFactory.createIfStatement();
        BoolLiteral boolLiteral2 = smodelFactory.createBoolLiteral();
        boolLiteral2.setTrue(true);
        Expression boolCond2 = smodelFactory.createExpression();
        boolCond2.setLiteral(boolLiteral2);
        nestedIfStmt.setCondition(boolCond2);
        nestedIfStmt.getThenStatements()
            .add(actionCall);
        ifStmt.getThenStatements()
            .add(nestedIfStmt);
        smodel.getStatements()
            .add(nestedIfStmt);

        SmodelInterpreter interpreter = new SmodelInterpreter(smodel, fvp);
        boolean actualResult = interpreter.analyze();

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
        Action expectedAction = createSimpleAction("a");
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
        IfStatement ifStmt = smodelFactory.createIfStatement();
        BoolLiteral boolLiteral = smodelFactory.createBoolLiteral();
        boolLiteral.setTrue(true);
        Expression boolCond = smodelFactory.createExpression();
        boolCond.setLiteral(boolLiteral);
        ifStmt.setCondition(boolCond);
        ifStmt.getThenStatements()
            .add(actionCall);
        smodel.getStatements()
            .add(ifStmt);

        SmodelInterpreter interpreter = new SmodelInterpreter(smodel, fvp);
        boolean actualResult = interpreter.analyze();

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
        Action expectedAction = createSimpleAction("a");
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
        IfStatement ifStmt = smodelFactory.createIfStatement();
        BoolLiteral boolLiteral = smodelFactory.createBoolLiteral();
        boolLiteral.setTrue(true);
        Expression boolCond = smodelFactory.createExpression();
        boolCond.setLiteral(boolLiteral);
        ifStmt.setCondition(boolCond);
        ifStmt.getThenStatements()
            .add(actionCall);
        smodel.getStatements()
            .add(ifStmt);

        SmodelInterpreter interpreter = new SmodelInterpreter(smodel, fvp);
        boolean actualResult = interpreter.analyze();

        assertThat(actualResult).isTrue();
    }

    // parameter ????

    @Test
    public void testAnalyzeDebug() throws Exception {
//        String sb = MODEL_NAME_LINE + """
//                action a();
//                if (true) {
//                    if (true) {
//                        a();
//                    }
//                }
//                """;
        String sb = MODEL_NAME_LINE + """
                action a(param int p);
                if (true) {
                    a(p=1);
                }
                """;
        Smodel model = parserHelper.parse(sb);
//        validationTestHelper.assertNoIssues(model);

        interpreter = new SmodelInterpreter(model, fvp);

        SmodelInterpreter interpreter = new SmodelInterpreter(model, fvp);
        boolean actualResult = interpreter.analyze();

        assertThat(actualResult).isTrue();

    }

    //// PLAN

    @Test
    public void testPlanWithEmptyModel() throws Exception {
        Smodel smodel = smodelFactory.createSmodel();
        SmodelInterpreter interpreter = new SmodelInterpreter(smodel, fvp);

        List<ResolvedAction> actualResult = interpreter.plan();

        assertThat(actualResult).isEmpty();
    }

    @Test
    public void testPlanWithSingleActionWithBoolCondition1() throws Exception {
//      String sb = MODEL_NAME_LINE + """
//      action a();
//      if (true) {
//        a();
//      }
//      """;
        Smodel smodel = smodelFactory.createSmodel();
        Action expectedAction = createSimpleAction("a");
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
        SmodelInterpreter interpreter = new SmodelInterpreter(smodel, fvp);

        List<ResolvedAction> actualResults = interpreter.plan();

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
        Smodel smodel = smodelFactory.createSmodel();
        Action expectedAction = createSimpleAction("a");
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
        SmodelInterpreter interpreter = new SmodelInterpreter(smodel, fvp);

        List<ResolvedAction> actualResult = interpreter.plan();

        assertThat(actualResult).isEmpty();
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

        Smodel smodel = smodelFactory.createSmodel();
        Action expectedAction = createSimpleAction("a");
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
        SmodelInterpreter interpreter = new SmodelInterpreter(smodel, fvp);

        List<ResolvedAction> actualResults = interpreter.plan();

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
        Smodel smodel = smodelFactory.createSmodel();
        Action expectedAction = createSimpleAction("a");
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
        SmodelInterpreter interpreter = new SmodelInterpreter(smodel, fvp);

        List<ResolvedAction> actualResults = interpreter.plan();

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
        Smodel smodel = smodelFactory.createSmodel();
        Action expectedAction = createSimpleAction("a");
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
        SmodelInterpreter interpreter = new SmodelInterpreter(smodel, fvp);

        List<ResolvedAction> actualResults = interpreter.plan();

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
        Smodel smodel = smodelFactory.createSmodel();
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
        SmodelInterpreter interpreter = new SmodelInterpreter(smodel, fvp);

        List<ResolvedAction> actualResults = interpreter.plan();

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
        Smodel smodel = smodelFactory.createSmodel();
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
        SmodelInterpreter interpreter = new SmodelInterpreter(smodel, fvp);

        List<ResolvedAction> actualResults = interpreter.plan();

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
        Smodel smodel = smodelFactory.createSmodel();
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
        SmodelInterpreter interpreter = new SmodelInterpreter(smodel, fvp);

        List<ResolvedAction> actualResults = interpreter.plan();

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
        Smodel smodel = smodelFactory.createSmodel();
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
        SmodelInterpreter interpreter = new SmodelInterpreter(smodel, fvp);

        List<ResolvedAction> actualResults = interpreter.plan();

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

        // resolved parameter = 1
        // resolved opt = 2
    }

//    @Test
//    public void testAnalyzeWithSecondTrueCondition() throws Exception {
//        String sb = MODEL_NAME_LINE + """
//                action adapt(param int parameter);
//                if (false) {
//                    adapt(parameter=1);
//                }
//                if (true) {
//                    adapt(parameter=1);
//                }
//                """;
//        Smodel model = parserHelper.parse(sb);
//        validationTestHelper.assertNoIssues(model);
//        /*
//         * interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);
//         * 
//         * Assert.assertTrue(interpreter.analyze());
//         */
//    }
//
//    @Test
//    public void testAnalyzeWithIfElseStatement() throws Exception {
//        String sb = MODEL_NAME_LINE + """
//                action adapt(param int parameter);
//                if (false) {} else {
//                    adapt(parameter=1);
//                }
//                """;
//        Smodel model = parserHelper.parse(sb);
//        validationTestHelper.assertNoIssues(model);
//        /*
//         * interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);
//         * 
//         * Assert.assertTrue(interpreter.analyze());
//         */
//    }
//
//    @Test
//    public void testAnalyzeWithInnerTrueCondition() throws Exception {
//        String sb = MODEL_NAME_LINE + """
//                action adapt(param int parameter);
//                if (false) {
//                    if (true) {
//                        adapt(parameter=1);
//                    }
//                }
//                """;
//        Smodel model = parserHelper.parse(sb);
//        validationTestHelper.assertNoIssues(model);
//        /*
//         * interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);
//         * 
//         * Assert.assertFalse(interpreter.analyze());
//         */
//    }
//
//    @Test
//    public void testAnalyzeWithNoTrueCondition() throws Exception {
//        String sb = MODEL_NAME_LINE + """
//                action adapt(param int parameter);
//                if (false) {
//                    adapt(parameter=1);
//                }
//                """;
//
//        Smodel model = parserHelper.parse(sb);
//        validationTestHelper.assertNoIssues(model);
//        /*
//         * interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);
//         * 
//         * Assert.assertFalse(interpreter.analyze());
//         */
//    }
//
//    @Test
//    public void testAnalyzeWithoutCondition() throws Exception {
//        String sb = MODEL_NAME_LINE + """
//                action adapt(param int parameter);
//                """;
//
//        Smodel model = parserHelper.parse(sb);
//        validationTestHelper.assertNoErrors(model);
//        validationTestHelper.assertWarning(model, model.eClass(), null, "The action 'adapt' is never used.");
//        /*
//         * interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);
//         * 
//         * Assert.assertFalse(interpreter.analyze());
//         */
//    }
//
//    @Test
//    public void testPlanWithOneActionCall() throws Exception {
//        String sb = MODEL_NAME_LINE + """
//                action adapt(param int parameter);
//                if (true) {
//                    adapt(parameter=1);
//                }
//                """;
//        Smodel model = parserHelper.parse(sb);
//        validationTestHelper.assertNoIssues(model);
//        /*
//         * interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);
//         * 
//         * List<ResolvedAction> resolvedActions = interpreter.plan(); Assert.assertEquals(1,
//         * resolvedActions.size());
//         * 
//         * Action adapt = model.getActions() .get(0); ResolvedAction resolvedAdapt =
//         * resolvedActions.get(0); assertResolvedAction(adapt, resolvedAdapt, 1);
//         */
//    }
//
//    @Test
//    public void testPlanWithTwoActionCalls() throws Exception {
//        String sb = MODEL_NAME_LINE + """
//                action adapt(param int parameter);
//                action adapt2(param int parameter);
//                if (true) {
//                    adapt(parameter=1);
//                    adapt2(parameter=2);
//                }
//                """;
//        Smodel model = parserHelper.parse(sb);
//        validationTestHelper.assertNoIssues(model);
//        /*
//         * interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);
//         * 
//         * List<ResolvedAction> resolvedActions = interpreter.plan(); Assert.assertEquals(2,
//         * resolvedActions.size());
//         * 
//         * Action adapt = model.getActions() .get(0); ResolvedAction resolvedAdapt =
//         * resolvedActions.get(0); assertResolvedAction(adapt, resolvedAdapt, 1);
//         * 
//         * Action adapt2 = model.getActions() .get(1); ResolvedAction resolvedAdapt2 =
//         * resolvedActions.get(1); assertResolvedAction(adapt2, resolvedAdapt2, 2);
//         */
//    }
//
//    @Test
//    public void testPlanWithTwoActionCallsOfSameAction() throws Exception {
//        String sb = MODEL_NAME_LINE + """
//                action adapt(param int parameter);
//                if (true) {
//                    adapt(parameter=1);
//                    adapt(parameter=2);
//                }
//                """;
//        Smodel model = parserHelper.parse(sb);
//        validationTestHelper.assertNoIssues(model);
//        /*
//         * interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);
//         * 
//         * List<ResolvedAction> resolvedActions = interpreter.plan(); Assert.assertEquals(2,
//         * resolvedActions.size()); Action adapt = model.getActions() .get(0); ResolvedAction
//         * resolvedAdapt = resolvedActions.get(0); assertResolvedAction(adapt, resolvedAdapt, 1);
//         * ResolvedAction resolvedAdapt2 = resolvedActions.get(1); assertResolvedAction(adapt,
//         * resolvedAdapt2, 2);
//         */
//    }
//
//    @Test
//    public void testPlanWithNestedActionCalls() throws Exception {
//        String sb = MODEL_NAME_LINE + """
//                action adapt(param int parameter);
//                action adapt2(param int parameter);
//                if (true) {
//                    adapt(parameter=2);
//                    if (true) {
//                        adapt2(parameter=1);
//                    }
//                }
//                """;
//        Smodel model = parserHelper.parse(sb);
//        validationTestHelper.assertNoIssues(model);
//        /*
//         * interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);
//         * 
//         * List<ResolvedAction> resolvedActions = interpreter.plan(); Assert.assertEquals(2,
//         * resolvedActions.size());
//         * 
//         * Action adapt = model.getActions() .get(0); ResolvedAction resolvedAdapt =
//         * resolvedActions.get(0); assertResolvedAction(adapt, resolvedAdapt, 2);
//         * 
//         * Action adapt2 = model.getActions() .get(1); ResolvedAction resolvedAdapt2 =
//         * resolvedActions.get(1); assertResolvedAction(adapt2, resolvedAdapt2, 1);
//         */
//    }
//
//    @Test
//    public void testPlanWithIfElseStatement() throws Exception {
//        String sb = MODEL_NAME_LINE + """
//                action adapt(param int parameter);
//                if (false) {} else {
//                    adapt(parameter=1);
//                }
//                """;
//        Smodel model = parserHelper.parse(sb);
//        validationTestHelper.assertNoIssues(model);
//        /*
//         * interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);
//         * 
//         * List<ResolvedAction> resolvedActions = interpreter.plan(); Assert.assertEquals(1,
//         * resolvedActions.size());
//         * 
//         * Action adapt = model.getActions() .get(0); ResolvedAction resolvedAdapt =
//         * resolvedActions.get(0); assertResolvedAction(adapt, resolvedAdapt, 1);
//         */
//    }
//
//    @Test
//    public void testPlanWithFalseOuterCondition() throws Exception {
//        String sb = MODEL_NAME_LINE + """
//                action adapt(param int parameter);
//                action adapt2(param int parameter);
//                if (false) {
//                    adapt(parameter=1);
//                    if (true) {
//                        adapt2(parameter=1);
//                    }
//                }
//                """;
//        Smodel model = parserHelper.parse(sb);
//        validationTestHelper.assertNoIssues(model);
//        /*
//         * interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);
//         * 
//         * List<ResolvedAction> resolvedActions = interpreter.plan(); Assert.assertEquals(0,
//         * resolvedActions.size());
//         */
//    }
//
//    @Test
//    public void testPlanWithFalseInnerCondition() throws Exception {
//        String sb = MODEL_NAME_LINE + """
//                action adapt(param int parameter);
//                action adapt2(param int parameter);
//                if (true) {
//                    adapt(parameter=1);
//                    if (false) {
//                        adapt2(parameter=1);
//                    }
//                }
//                """;
//        Smodel model = parserHelper.parse(sb);
//        validationTestHelper.assertNoIssues(model);
//        /*
//         * interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);
//         * 
//         * List<ResolvedAction> resolvedActions = interpreter.plan(); Assert.assertEquals(1,
//         * resolvedActions.size());
//         * 
//         * Action adapt = model.getActions() .get(0); ResolvedAction resolvedAdapt =
//         * resolvedActions.get(0); assertResolvedAction(adapt, resolvedAdapt, 1);
//         */
//    }
//
//    @Test
//    public void testPlanWithoutActionCalls() throws Exception {
//        String sb = MODEL_NAME_LINE + """
//                action adapt(param int parameter);
//                """;
//        Smodel model = parserHelper.parse(sb);
//        validationTestHelper.assertNoErrors(model);
//        validationTestHelper.assertWarning(model, model.eClass(), null, "The action 'adapt' is never used.");
//        /*
//         * interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);
//         * 
//         * List<ResolvedAction> resolvedActions = interpreter.plan(); Assert.assertEquals(0,
//         * resolvedActions.size());
//         */
//    }
//
//    @Test
//    public void testPlanWithActionWithoutParameters() throws Exception {
//        String sb = MODEL_NAME_LINE + """
//                action adapt();
//                if (true) {
//                    adapt();
//                }
//                """;
//        Smodel model = parserHelper.parse(sb);
//        validationTestHelper.assertNoIssues(model);
//        /*
//         * interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);
//         * 
//         * List<ResolvedAction> resolvedActions = interpreter.plan(); Assert.assertEquals(1,
//         * resolvedActions.size());
//         * 
//         * Action adapt = model.getActions() .get(0); ResolvedAction resolvedAdapt =
//         * resolvedActions.get(0); assertResolvedAction(adapt, resolvedAdapt);
//         */
//    }
//
//    @Test
//    public void testPlanWithActionWithSingleParameter() throws Exception {
//        String sb = MODEL_NAME_LINE + """
//                action adapt(param int parameter);
//                if (true) {
//                    adapt(parameter=1);
//                }
//                """;
//        Smodel model = parserHelper.parse(sb);
//        validationTestHelper.assertNoIssues(model);
//        /*
//         * interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);
//         * 
//         * List<ResolvedAction> resolvedActions = interpreter.plan(); Assert.assertEquals(1,
//         * resolvedActions.size());
//         * 
//         * Action adapt = model.getActions() .get(0); ResolvedAction resolvedAdapt =
//         * resolvedActions.get(0); assertResolvedAction(adapt, resolvedAdapt, 1);
//         */
//    }
//
//    @Test
//    public void testPlanWithActionWithMultipleParameters() throws Exception {
//        String sb = MODEL_NAME_LINE + """
//                action adapt(param int parameter, param int parameter2, param int parameter3);
//                if (true) {
//                    adapt(parameter=1, parameter2=2, parameter3=3);
//                }
//                """;
//        Smodel model = parserHelper.parse(sb);
//        validationTestHelper.assertNoIssues(model);
//        /*
//         * interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);
//         * 
//         * List<ResolvedAction> resolvedActions = interpreter.plan(); Assert.assertEquals(1,
//         * resolvedActions.size());
//         * 
//         * Action adapt = model.getActions() .get(0); ResolvedAction resolvedAdapt =
//         * resolvedActions.get(0); assertResolvedAction(adapt, resolvedAdapt, 1, 2, 3);
//         */
//    }
//
//    @Test
//    public void testPlanWithActionWithSingleVariable() throws Exception {
//        String sb = MODEL_NAME_LINE + """
//                action adapt(optimizable int{5, 4, 3, 2, 1} variable);
//                if (true) {
//                    adapt();
//                }
//                """;
//        Smodel model = parserHelper.parse(sb);
//        validationTestHelper.assertNoIssues(model);
//        /*
//         * interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);
//         * 
//         * List<ResolvedAction> resolvedActions = interpreter.plan(); Assert.assertEquals(1,
//         * resolvedActions.size());
//         * 
//         * Action adapt = model.getActions() .get(0); ResolvedAction resolvedAdapt =
//         * resolvedActions.get(0); assertResolvedAction(adapt, resolvedAdapt, 5);
//         */
//    }
//
//    @Test
//    public void testPlanWithActionWithMultipleVariables() throws Exception {
//        String sb = MODEL_NAME_LINE + """
//                action adapt(optimizable int{5, 4, 3, 2, 1} variable, optimizable double{0.1, 0.2, 0.3} variable2);
//                if (true) {
//                    adapt();
//                }
//                """;
//        Smodel model = parserHelper.parse(sb);
//        validationTestHelper.assertNoIssues(model);
//        /*
//         * interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);
//         * 
//         * List<ResolvedAction> resolvedActions = interpreter.plan(); Assert.assertEquals(1,
//         * resolvedActions.size());
//         * 
//         * Action adapt = model.getActions() .get(0); ResolvedAction resolvedAdapt =
//         * resolvedActions.get(0); assertResolvedAction(adapt, resolvedAdapt, 5, 0.1f);
//         */
//    }
//
//    @Test
//    public void testPlanWithActionWithParameterAndVariable() throws Exception {
//        String sb = MODEL_NAME_LINE + """
//                action adapt(param int parameter, optimizable double{0.1, 0.2, 0.3} variable);
//                if (true) {
//                    adapt(parameter=3);
//                }
//                """;
//        Smodel model = parserHelper.parse(sb);
//        validationTestHelper.assertNoIssues(model);
//        /*
//         * interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);
//         * 
//         * List<ResolvedAction> resolvedActions = interpreter.plan(); Assert.assertEquals(1,
//         * resolvedActions.size());
//         * 
//         * Action adapt = model.getActions() .get(0); ResolvedAction resolvedAdapt =
//         * resolvedActions.get(0); assertResolvedAction(adapt, resolvedAdapt, 3, 0.1f);
//         */
//    }
//
//    @Test
//    public void testComplexAnalyzeAndPlan() throws Exception {
//        String sb = MODEL_NAME_LINE + """
//                const int constant = 1;
//                const int anotherConstant = constant * 2;
//                optimizable double{1.5, 2.5, 3.5} adaptationFactor;
//                probe double someProbe : id = "abc123";
//                probe double someRuntime: id = "b";
//
//                action adapt(param int parameter, param double factor);
//                action adapt2(param double parameter, optimizable double[1,5,1] someRange);
//                action adapt3();
//
//                if (someProbe > 0 || someProbe <= -10 && someRuntime == 0.0) {
//                    adapt(parameter=anotherConstant, factor=0.5);
//                    if (someProbe > 0 && someProbe < 10) {
//                        adapt2(parameter=adaptationFactor);
//                    }
//                }
//                if (someProbe == 100) {
//                    adapt3();
//                }
//                """;
//        Smodel model = parserHelper.parse(sb);
//        validationTestHelper.assertNoIssues(model);
//        /*
//         * interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);
//         * 
//         * List<ResolvedAction> resolvedActions = interpreter.plan(); Assert.assertEquals(2,
//         * resolvedActions.size());
//         * 
//         * Action adapt = model.getActions() .get(0); ResolvedAction resolvedAdapt =
//         * resolvedActions.get(0); assertResolvedAction(adapt, resolvedAdapt, 2, 0.5f);
//         * 
//         * Action adapt2 = model.getActions() .get(1); ResolvedAction resolvedAdapt2 =
//         * resolvedActions.get(1); assertResolvedAction(adapt2, resolvedAdapt2, 1.5f, 1);
//         */
//    }
//
//    private void assertResolvedAction(Action action, ResolvedAction resolvedAction, Object... arguments) {
//        // Check, if the expected & actual action of the resolved action match.
//        Assert.assertEquals(action, resolvedAction.getAction());
//
//        List<Parameter> parameters = action.getArguments()
//            .getParameters();
//        List<Optimizable> optimizables = action.getArguments()
//            .getOptimizables();
//        Map<String, Object> resolvedArguments = resolvedAction.getArguments();
//
//        // Check, if the expected & actual amount of resolved arguments match.
//        Assert.assertEquals(parameters.size() + optimizables.size(), resolvedArguments.size());
//
//        // Check if the expected & actual names & values for parameter arguments match.
//        for (int i = 0; i < parameters.size(); i++) {
//            Parameter parameter = parameters.get(i);
//            Assert.assertTrue(resolvedArguments.containsKey(parameter.getName()));
//
//            Object value = resolvedArguments.get(parameter.getName());
//            Assert.assertEquals(arguments[i], value);
//        }
//
//        // Check if the expected & actual names & values for variable arguments match.
//        for (int i = 0; i < optimizables.size(); i++) {
//            Field variable = optimizables.get(i);
//            Assert.assertTrue(resolvedArguments.containsKey(variable.getName()));
//
//            Object value = resolvedArguments.get(variable.getName());
//            Assert.assertEquals(arguments[i + parameters.size()], value);
//        }
//    }

    private IfStatement createIfStatement() {
        Expression condition = smodelCreator.createLiteralBoolExpression(true);
        IfStatement ifStatement = smodelCreator.createIfStatement(condition);
        return ifStatement;
    }

    private Action createSimpleAction(String name) {
        Action a = smodelFactory.createAction();
        a.setName("a");
        ActionArguments actionArguments = smodelFactory.createActionArguments();
        a.setArguments(actionArguments);
        return a;
    }

    public Action createAction(String name) {
        Action action = SmodelFactory.eINSTANCE.createAction();
        action.setName(name);
        ActionArguments actionArguments = SmodelFactory.eINSTANCE.createActionArguments();
        action.setArguments(actionArguments);
        return action;
    }

}
